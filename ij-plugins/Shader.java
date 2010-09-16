
/** Shader callback interface.
  This interface specifies the shader callback interface for the class RayTracer.
  When RayTracer.traceRay() finds an intersection with an object, it calls shade().
*/
public interface Shader {
	/**
	 * Computes light coming from a surface point given:
	 *  - a surface point (point); 
	 *  - a vector normal to the surface (normal);
	 *  - the observer position (viewPos) ;
	 *  - a diffuse albedo (diffuse);
	 *  - a specular albedo (specular);
	 *  - a light position and color (lightPos, lightColor).
	 */
	public Color shade(Vec3 point, Vec3 normal, Vec3 viewPos, Color diffuse, 
			Color specular, Vec3 lightPos, Color lightColor);
}
