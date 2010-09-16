import java.util.Arrays;
import java.lang.Math;

public class Bicubic {

    int[] pixels; int width; int height;

    Bicubic(int[] pixels, int width, int height) {
        this.pixels = pixels;
        this.width = width;
        this.height = height;
    }

    // extract a channel value from a RGB 'int' packed color and convert it
    // to double.
    double channel(int c, int i) {
        return (double) ((c >> (8*i)) & 0xFF);
    }

    // Bicubic interpolation for the given position
    public int interpolate(double x, double y) {

        int ix = (int)x;
        int iy = (int)y;
        double p = x - ix; // sub-pixel offset in the x axis
        double q = y - iy; // sub-pixel offset in the y axis

        if ((ix < 2) || (iy < 2) || (ix >= width-3) || (iy >= height-3))
            return 0; // if pixel or neighbors are out of the image

        // Pixel neighborhood N;
        int dimN = 4;
        int[] N = new int[dimN*dimN];
        int offset = (iy - 1) * width + (ix - 1); // offset to the top-left neighbor

        /**** 1. FILL IN THE NEIGHBOR SET "N[0] to N[15]" HERE ****/
        for(int i=0; i<dimN; i++)
        {
        	for(int j=0; j<dimN; j++)
        	{
        		N[i+j*(i+1)] = pixels[(int)(offset + j*p + i*q*width)];
			}
		}
 		
        int result = 0;
        // loop over channels; r,g,b
        for (int i=0; i<3; ++i) {
            // interpolate in the x direction using the neighbor set N
            /**** 2. COMPUTE THE INTERPOLATION RESULTS IN THE X-DIRECTION HERE ****/
            // interpolate each line
            double[] L = new double[dimN];
            for(int j=0; j<dimN; j++)
            {
            	L[j] += interpolate_1D(	channel(N[j*dimN],i),
            							channel(N[j*dimN+1],i),
            							channel(N[j*dimN+2],i),
            							channel(N[j*dimN+3],i),p) ;
            }
            // interpolate the x-axis results in the y direction and add to "result"
            double interp_res = interpolate_1D(L[0], L[1], L[2], L[3], q) ;/**** 3. FILL IN THE INTERPOLATION RESULT IN THE Y-DIRECTION****/

            result += (int)interp_res << (i*8);
        }
        return result;
    }

    public double interpolate_1D(double N0, double N1, double N2, double N3, double t) {
        // cubic interpolation using four pixels

        /**** 4. COMPUTE THE INTERPOLATION COEFFICIENTS HERE ****/
		double c0 = 2*N1;
		double c1 = N2-N0;
		double c2 = 2*N0-5*N1+4*N2-N3;
		double c3 = -N0+3*N1-3*N2+N3;
		/**** 5. FILL IN THE CUBIC RESULT HERE ****/
		double res = (c3*t*t*t + c2*t*t + c1*t + c0)/2;
        res = Math.round(res); 

        // Check for oveflow and underflow in computations
        if(res < 0) res = 0;
        if(res > 255) res = 255;

        return res;
    }
}
