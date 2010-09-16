/** Small class for basic 3D vector handling.
  */
public class Vec3 {
	
	/** vector components */
	public float x,y,z;

	public Vec3(float a, float b, float c) { x=a; y=b; z=c; }
	public Vec3(Vec3 a) { x=a.x; y=a.y; z=a.z; }

	/** return the vector length */
	public float length() { return (float)Math.sqrt(x*x+y*y+z*z); }

	/** Normalizes the vector. Divides each vector component by the length. */
	public void norm() { 
		float f = 1/length();
		x = x*f; y = y*f; z = z*f;
	}

	/** vector addition. */
	public void add(Vec3 a) { x += a.x; y += a.y; z += a.z; }

	/** vector substraction. */
	public void sub(Vec3 a) { x -= a.x; y -= a.y; z -= a.z; }

	/** Element per element multiplication. */
	public void mul(Vec3 a) { x *= a.x; y *= a.y; z *= a.z; }

	/** scalar multiplication. Multiply every element by f. */
	public void mul(float f) { x *= f; y *= f; z *= f; }

	/** dot product. */
	public float dotprod(Vec3 a) { return x*a.x + y*a.y + z*a.z; }
	
	/** simple copy. */
	public void copy(Vec3 a) { x=a.x; y=a.y; z=a.z; }

	/** Replace the vector by the cross product of a and b. */
	public void crossProd(Vec3 a, Vec3 b) 
	{
		x = a.y * b.z - a.z * b.y;
		y = a.z * b.x - a.x * b.z;
		z = a.x * b.y - a.y * b.x;
	}

};


