import java.awt.*;
import ij.*;
import ij.gui.*;
import ij.process.*;
import ij.plugin.PlugIn;

/** This a prototype ImageJ plug-in. */
public class RayTracer {
	
	
	interface Object {
		Color getDiffuse(Intersection hit);	// surface color
		Color getSpecular();	// specularity color
		Intersection intersect(Ray r);
	}

	/** 
	 * Represent a ray. The ray is defined by all points lying on
	 * origin + l*direction, for any l.
	 */
	class Ray {
		Vec3 origin;
		Vec3 direction;

		Ray(Vec3 d) {
			direction = new Vec3(d);
			origin = new Vec3(0,0,0);
		}
		Ray(Vec3 d, Vec3 o) {
			direction = new Vec3(d);
			origin = new Vec3(o);
		}
	};


	Object[] world; // store the world
	Light[] lights;
	Color background;

	// constructor: setup world.
	RayTracer() {
		world = new Object[5];
		world[0] = new Sphere(300,0,2400,300, 0x749723, 0xaFaFaF);
	       	world[1] = new Sphere(-300,-50,2000,150, 0xD73849, 0x663333);
		world[2] = new Sphere(0,-300,2200,100, 0x102FA0, 0);
		world[3] = new Sphere(-120,250,1800,110, 0x102FA0, 0x303030);
		world[4] = new Plane(new Vec3(0,-1,0), 360, 0xffffff, 0x7f7f7f);
		lights = new Light[2];
		lights[0] = new Light(new Vec3(-2000, -100, 2000), 0xeeeeee);
		lights[1] = new Light(new Vec3(1000, -2500, 100), 0xff9977);
		background = new Color(0);
	}
	
	class Intersection {
		Intersection() {
			normal = new Vec3(0,0,0);
			pos = new Vec3(0,0,0);
		}
		Object obj;
		Vec3 normal;
		Vec3 pos;
		Ray r;
		float dist;
	}

	class Light {
		Light(Vec3 p, int c) { pos = p; col= new Color(c); }
		Vec3 pos;
		Color col;
	}

	// Represent a sphere in the raytraced world
	class Sphere implements Object {
		Color albedo,specular;
		public Color getDiffuse(Intersection hit) { return albedo; }
		public Color getSpecular() { return specular; }

		Vec3 pos;	// 3D sphere center
		float radius;	// sphere radius

		Sphere(float x, float y, float z, float r, int albedo, int spec) {
			this.albedo = new Color(albedo);
			pos = new Vec3(x,y,z);
			radius = r;
			specular = new Color(spec);
		}
			
		/* if the half line defined by the Ray r hits the sphere,
		 * return the intersection. Otherwise returns null.
		 */ 
		public Intersection intersect(Ray r) {
			// line / sphere intersection. See 
			// http://www.cs.fit.edu/wds/classes/adv-graphics/raytrace/raytrace.html
			// for details.

			Vec3 tmp = new Vec3(r.origin);
			tmp.sub(pos);
			float A = r.direction.dotprod(r.direction);
			float B = 2*r.direction.dotprod(tmp);
			float C = tmp.dotprod(tmp) - radius*radius;
			float det = B*B - 4*A*C;

			float dist = 2e30f;
			if ( det <= 0) return null;

			float t = (float)(-B - Math.sqrt(det)) / (2*A);

			if (t <= 0) return null;

			Intersection hit = new Intersection();

			hit.pos.copy( r.direction);
			hit.pos.mul(t);
			hit.pos.add(r.origin);

			hit.normal.copy(hit.pos);
			hit.normal.sub(pos);
			hit.normal.mul(1/radius);

			hit.obj = this;
			hit.dist = t;

			return hit;
		}
	}

	class Plane implements Object {

		Vec3 normal;
		float a;

		Plane(Vec3 n, float _a, int alb, int spec) {
			normal = n;
			normal.norm();
			a = _a;
			albedo = new Color(alb);
			specular = new Color(spec);
		}

		Color albedo, specular;
		public Color getDiffuse(Intersection hit) { 
			int a = (int)(hit.pos.x);
			int b = (int)(hit.pos.z);

			if (((a&0x100)^(b&0x100)) == 0) 
				return albedo; 
			else
				return new Color(0,0,0);
		}
		public Color getSpecular() { return specular; }

		public Intersection intersect(Ray r) {
			float on = r.origin.dotprod(normal);
			float dn = r.direction.dotprod(normal);
			float lambda = (-a - on) / dn;
			if (lambda <=0.001) return null;
			
			Intersection hit = new Intersection();

			hit.obj= this;
			hit.dist = lambda;
			hit.pos.copy(r.direction);
			hit.pos.mul(lambda);
			hit.pos.add(r.origin);

			hit.normal.copy(normal);
			hit.r = r;

			return hit;
		}
	}
	
	/*
	 * Trace a ray in the world.
	 */ 
	Intersection traceRay(Ray r, Object skip) {
		Intersection best = null;

		// locate the nearest sphere
		for (int i=0; i<world.length; ++i) {

			if( world[i] != skip) {
				// does the ray intersect this sphere ?
				Intersection hit = world[i].intersect(r);

				if ((hit != null) && ((best ==null) || (hit.dist < best.dist))) {
					// yes, and it is the nearest one for now.
					best = hit;
				}
			}
		}
		return best;
	}

	/** Trace a ray in the world.
	  This function traces a ray from 'origin' towards 'direction' and
	  computes the corresponding color by calling 'shader.shade()' as many
	  times as necessary.

	  'direction' has to be a normal vector.

	  Returns a color.
	*/
	public Color traceRay(Vec3 origin, Vec3 direction, Shader shader) {
		Ray ray = new Ray(direction, origin);
		return traceRayRec(ray, shader, 0, null);
	}

	Color traceRayRec(Ray ray, Shader shader, int level, Object skip) {

		Intersection hit = traceRay(ray, skip);
		if (hit == null) return background;

		// the ray touched a surface point.
		// To compute lighting: sum over all the visible lights.
		Color color = new Color(0,0,0);
		for (int i=0; i<lights.length; ++i) {
			if (isLightVisible(lights[i].pos, hit))
				color.add(shader.shade(
							hit.pos, hit.normal,
							ray.origin,
							hit.obj.getDiffuse(hit),
							hit.obj.getSpecular(),
							lights[i].pos,
							lights[i].col));
		}

		// now the reflection ray
		if (level < 3) {
			Ray reflect = reflectionRay(ray, hit);

			Vec3 virtualPos = new Vec3(reflect.direction);
			virtualPos.mul(100);
			virtualPos.add(reflect.origin);
			color.add(shader.shade(
						hit.pos, hit.normal,
						ray.origin,
						new Color(0,0,0),
						hit.obj.getSpecular(),
						virtualPos,
						traceRayRec(reflect, shader, level+1, hit.obj)));
		}
		return color;
	}

	Ray reflectionRay(Ray ray, Intersection hit) {
		Vec3 v = new Vec3(hit.normal);
		v.mul(-2*ray.direction.dotprod(hit.normal));
		v.add(ray.direction);
		Ray r = new Ray(v);
		r.origin.copy(hit.pos);
		return r;
	}

	boolean isLightVisible(Vec3 lightPos, Intersection hit) {
		if (hit==null) return true;

		Ray lightRay = new Ray(lightPos);
		lightRay.direction.sub(hit.pos);
		float lightDist = lightRay.direction.length();
		lightRay.direction.mul(1/lightDist);
		lightRay.origin.copy(hit.pos);
		Intersection intr = traceRay(lightRay, hit.obj);
		if (intr == null)return true;
		return intr.dist > lightDist;
	}

	int sumColor(int c1, int c2) {
		int r = c1 & 0xFF;	
		int g = (c1>>8) & 0xFF;	
		int b = (c1>>16) & 0xFF;	
		r += c2 & 0xFF;	
		g += (c2>>8) & 0xFF;	
		b += (c2>>16) & 0xFF;	
		if (r>255) r=255;
		if (g>255) g=255;
		if (b>255) b=255;
		return (r + (g<<8) + (b<<16));
	}
}

