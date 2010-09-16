import java.awt.*;
import ij.*;
import ij.gui.*;
import ij.process.*;
import ij.plugin.PlugIn;

/** This plugin will generate an image using the RayTracer class */
public class RayTracer_ implements PlugIn, Shader {

	/*
	 * Camera class. This camera will always be located at origin, projecting on the (X,Y,f) plane.
	 */ 
	class Camera {
		float f; // focal length
		float px, py; // principal point. Described in the same document.

		Camera(float _px, float _py, float _f) {
			px = _px; py = _py; f =_f;
			pos = new Vec3(0,0,0);
			z = new Vec3(0,0,1);
			u = new Vec3(1,0,0);
			v = new Vec3(0,1,0);
		}

		Vec3 pos, z, u, v;

		void setTarget(Vec3 target, Vec3 pos)  {
			this.pos.copy(pos);
			z = new Vec3(target);
			z.sub(pos);
			z.norm();
			u.crossProd(new Vec3(0,1,0) ,z);
		        v.crossProd(z, u);	
		}

	};

	Camera camera;

	RayTracer tracer;

	/* Trace a ray for each pixel. A ray is a unit vector starting from
	 * origin and pointing towards the pixel center direction.
	 */
	void tracePixelRays(ColorProcessor cp) {
		int[] pixels = (int[])cp.getPixels();
		int w = cp.getWidth();
		int h = cp.getHeight();
		int i = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {

				// compute the ray corresponding to pixel (x,y) here.
				// make sure that direction norm is 1.
				// Ray r = new Ray( direction, origin );

				// this 3D vector is the 3D center of the pixel
				// located at (x,y).
				Vec3 direction = new Vec3(camera.z);
				direction.mul(camera.f);
				Vec3 u = new Vec3(camera.u);
				u.mul((float)(x - camera.px));
				Vec3 v = new Vec3(camera.v);
				v.mul((float)(y - camera.py));
				direction.add(u);
				direction.add(v);

				// the direction has to be a unit vector
				direction.norm();

				// trace the ray to compute the pixel color.
				pixels[i++] = tracer.traceRay(camera.pos, direction, this ).toInt();
			}
		}
	}

	public void run(String arg) {
		int w = 640, h = 480;
		ColorProcessor ip = new ColorProcessor(w, h);

		// setup projection
		camera = new Camera (w/2, h/2, 1000);

		camera.setTarget(new Vec3(0,0,2000), new Vec3(100, -1500, -100));

		tracer = new RayTracer();

		tracePixelRays(ip);

		new ImagePlus("Sphere tracer", ip).show();
	}

	/**
	 * Computes light coming from a surface point given:
	 *  a surface point (point); 
	 *  a vector normal to the surface (normal);
	 *  the observer position (viewPos) ;
	 *  a diffuse albedo (diffuse);
	 *  a specular albedo (specular);
	 *  a light position and color (lightPos, lightColor).
	 */ 
	public Color shade(Vec3 point, Vec3 normal, Vec3 viewPos, 
			Color diffuse, Color specular, Vec3 lightPos, Color lightColor)
	{
		// Using the 2 above methods and the Phong illumination model,
		// compute the color for this point. Remember that the camera is at origin.

		// Lambertian component
		Vec3 lightDir = new Vec3(lightPos);
		lightDir.sub(point);
		lightDir.norm();
		float theta = lightDir.dotprod(normal);
		if (theta <0) theta = 0;

		// specular component
		Vec3 Vr = new Vec3(normal);
		Vr.mul(2*theta);
		Vr.sub(lightDir);

		// Ve = camera center - intersection
		Vec3 Ve = new Vec3(viewPos);
		Ve.sub(point);
		Ve.norm();
		float sigma =Ve.dotprod(Vr);
		float spec = (float)Math.pow(sigma, 8);

		if (sigma <0) {
			sigma = 0;
			spec = 0;
		}

		Color albDif = new Color(diffuse);
		Color albSpec = new Color(specular);
		
		albDif.mul(lightColor);
		albDif.mul(theta);
		albSpec.mul(lightColor);
		albSpec.mul(spec);

		albDif.add(albSpec);
		return  albDif;
	}
}

