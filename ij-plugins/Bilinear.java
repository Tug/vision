import java.util.Arrays;
import java.lang.Math;

public class Bilinear {

	int[] pixels; int width; int height;

	Bilinear(int[] pixels, int width, int height) {
		this.pixels = pixels;
		this.width = width;
		this.height = height;
	}

	// extract a channel value from a RGB 'int' packed color and convert it
	// to double.
	double channel(int c, int i) {
		return (double) ((c >> (8*i)) & 0xFF);
	}

	// Bilinear interpolation for the given position
	public int interpolate(double x, double y) {
        int ix = (int)x;
        int iy = (int)y;
        double p = x - ix; // sub-pixel offset in the x axis
        double q = y - iy; // sub-pixel offset in the y axis

        if ((ix < 1) || (iy < 1) || (ix >= width-1) || (iy >= height-1))
            return 0; // if pixel or neighbors are out of the image
		
        int offset = iy * width + ix; // offset fro the first neighbor
        // List of neighbors
        int a = pixels[offset];	
        int b = pixels[offset + 1];
        int c = pixels[offset + width];
        int d = pixels[offset + width + 1];

        int result = 0;
        for (int i=0; i<3; ++i) {
            double Iab = interpolate_1D(channel(a,i), channel(b,i), p); // interpolate first  set of neighbors in the x axis
            double Icd = interpolate_1D(channel(c,i), channel(d,i), p); // interpolate second set of neighbors in the x axis
            result += (int)interpolate_1D(Iab, Icd, q) << (i*8);        // interpolate the x-axis results in the y direction
        }
        return result;
    }

    public double interpolate_1D(double I0, double I1, double t) {
        // linear interpolation between two pixels
        return (1-t)*I0 + t*I1;
    }
}
