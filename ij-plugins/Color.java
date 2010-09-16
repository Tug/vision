
/** Represent a color in floating point. */
public class Color {

	/** Red component */
	float r;
	/** Green component */
	float g;
	/** Blue component */
	float b;

	/** Integer constructor: decompose 8 bits/channel into 3 floats. */
	public Color(int c) {
		r= (c & 0xFF) / 255.0f;
		g = ((c>>8)&0xFF)/ 255.0f;
		b = ((c>>16)&0xFF)/255.0f;
	}
	
	public Color(float _r, float _g, float _b) {
		r= _r; g = _g; b= _b;
	}

	public Color(Color c) {
		r=c.r; g=c.g; b=c.b;
	}
	
	/** convert to an int */
	public int toInt() {
		int ir = (int)(r*255.0f);
		int ig = (int)(g*255.0f);
		int ib = (int)(b*255.0f);
		if (ir<0) ir=0;
		if (ig<0) ig=0;
		if (ib<0) ib=0;
		if (ir>255) ir=255;
		if (ig>255) ig=255;
		if (ib>255) ib=255;
		return (ir + (ig<<8) + (ib<<16));
	}

	/** Sum color components. */
	public void add(Color c) {
		r += c.r;
		g += c.g;
		b += c.b;
	}

	/** Component per component multiplication. 
	  This function allows to modulate the color by an albedo.
	  */
	public void mul(Color c) {
		r *= c.r;
		g *= c.g;
		b *= c.b;
	}

	/** Scalar multiplication.
	  Multiply each channel by f.
	  */
	public void mul(float f) {
		r *=f;
		g *=f;
		b *=f;
	}
}

