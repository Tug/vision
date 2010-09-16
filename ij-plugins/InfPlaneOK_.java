import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.measure.*;
import java.awt.*;
import java.util.Arrays;
import ij.plugin.filter.*;
import Jama.*;


// projective geometry demo 
// how to find the feature points

// subpixel accuracy : taylor expansion
// bayesian formula P(A|B) = P(B|A)*P(A)/P(B)
/*
 * The goal of this class is to transform an infinite plane made of a repeated
 * image with respect to a given homography. A naive approach will lead to
 * unwanted artifacts, due to a bad sampling scale.
 *
 * We propose here the use of a gaussian pyramid to avoid these artifacts.
 */
public class InfPlaneOK_ implements PlugInFilter {
	ImagePlus imp;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return NO_CHANGES | DOES_RGB;
	}

	// setup a linear system to look for a transformation
	// that maps (srcX[i], srcX[i]) to (dstX[i], dstY[i])
	//
	// Pass the result to the transform() to transform any point. 
	double[] homographyFrom4Correspondences(double[] srcX, double[] srcY, double[] dstX, double dstY[])
	{
		double[][] linSystem = new double[8][8];
		double[][] result = new double[8][1];

		for (int i=0; i<4; ++i) {
			linSystem[i*2][0] = srcX[i];
			linSystem[i*2][1] = srcY[i];
			linSystem[i*2][2] = 1;
			linSystem[i*2][3] = 0;
			linSystem[i*2][4] = 0;
			linSystem[i*2][5] = 0;
			linSystem[i*2][6] = -srcX[i]*dstX[i];
			linSystem[i*2][7] = -srcY[i]*dstX[i];

			linSystem[i*2+1][0] = 0;
			linSystem[i*2+1][1] = 0;
			linSystem[i*2+1][2] = 0;
			linSystem[i*2+1][3] = srcX[i];
			linSystem[i*2+1][4] = srcY[i];
			linSystem[i*2+1][5] = 1;
			linSystem[i*2+1][6] = -srcX[i]*dstY[i];
			linSystem[i*2+1][7] = -srcY[i]*dstY[i];

			result[i*2][0] = dstX[i];
			result[i*2+1][0] = dstY[i];
		}

		Matrix A = new Matrix(linSystem);
		Matrix b = new Matrix(result);
		Matrix x = A.solve(b);
		return x.getColumnPackedCopy();
	}

	// homography transformation. Compute T using
	// homographyFrom4Correspondences().
	void transform(double[] T, double x, double y, double[] r) {
		double a = T[0]*x + T[1]*y + T[2];
		double b = T[3]*x + T[4]*y + T[5];
		double c = T[6]*x + T[7]*y + 1;

		r[0] =a/c;
		r[1] = b/c;
	}

	/* This class represent a Gaussian Pyramid, i.e. a certain amount of
	 * blured images. Each image has half the resolution of the previous one.
	 */ 
	class Pyramid {

		ColorProcessor[] images;
		int nbLev;

		// Build the pyramid, given a base image and a number of levels.
		Pyramid(ColorProcessor img, int nbLev) {
			this.nbLev = nbLev;
			images = new ColorProcessor[nbLev];

			images[0] = img;

			int w = img.getWidth();
			int h = img.getHeight();

			for (int i=1; i<nbLev; ++i) {
				// create the new image
				images[i] = new ColorProcessor(w,h);

				// blur it
				GaussianBlur.apply((int[])images[i-1].getPixels(), 
						(int[])images[i].getPixels(), w, h);

				// and downscale it.
				w /= 2;
				h /= 2;
				if (w < 1) w = 1;
				if (h < 1) h = 1;
				images[i] = (ColorProcessor) images[i].resize(w, h);
			}
		}

		/* Sample a point on the pyramid. The (x,y) coordinates are
		 * always given in level 0 coordinates.
		 * The image is repeated, i.e. x and y can take any value and
		 * are not restricted to [0, width-1] x [0, height-1] domain. 
		 */ 
		int sample(double x, double y, int level) {
			int l = level;
			if (l< 0) l=0;
			if (l>= nbLev) l=nbLev-1;

			double f = (double)(1<<l);
			int ix = (int)Math.round(x/f) % images[l].getWidth();
			int iy = (int)Math.round(y/f) % images[l].getHeight();

			if (ix<0) ix+=images[l].getWidth();
			if (iy<0) iy+=images[l].getHeight();

			return images[l].getPixel(ix, iy);
		}
	}

	public void run(ImageProcessor ip) {

		int newW = 800;
		int newH = 600;
		int w=ip.getWidth()-1;
		int h=ip.getHeight()-1;
		double[] srcX = { 0, w, 0, w };
		double[] srcY = { 0, 0, h, h };

		double[] dstX = { newW/4, 3*newW/4, -1, -1 };
		double[] dstY = { 3*newH/4, 3*newH/4, -1, -1};

		int n = (int) IJ.getNumber("Number of Pyramid levels", 8);

		// Vanishing point coordinates
		double cx = newW *.45;
		double cy = newH *.27; 

		double l = 3;
		dstX[2] = l*(dstX[0]-cx) + cx;
		dstY[2] = l*(dstY[0]-cy) + cy;
		dstX[3] = l*(dstX[1]-cx) + cx;
		dstY[3] = l*(dstY[1]-cy) + cy;
		
		// H is the homography we want to transform the originial image
		// with. H transforms destination coordinates to original image
		// coordinates.
		double[] H = homographyFrom4Correspondences(dstX, dstY, srcX, srcY);

		// create a new image
		ColorProcessor r = new ColorProcessor(newW, newH);

		// Build a pyramid with the source image, and transform it with
		// H to fill the destination image.
		warp(H, new Pyramid((ColorProcessor)ip, n), r);

		// display the result.
		new ImagePlus("Infinite plane", r).show();
	}

	// return the integer base 2 logarithm. 
	int intlog2(int a) {
		int r=0;

		while((a>>r) != 0) {
			r++;
		}
		return r;
	}

	// Homographic transformation with Gaussian Pyramid sampling.
	void warp(double[] T, Pyramid p, ColorProcessor dst) {

		int offset;
		int width = dst.getWidth();
		int height = dst.getHeight();
		int[] pixels = (int[])dst.getPixels();

		double[] t = new double[2];
		double[] t2 = new double[2];

		for (int y=0; y<height; y++) {
			offset = y*width;
			for (int x=0; x<width; x++) {
				// compute the source coordinate for pixel (x,y)
				transform(T, (double)x, (double)y, t);

				// now, we have to compute the correct scale to
				// sample with.
				// Let A be the pixel we want to compute the
				// scale for, in destination coordinates. Let B
				// be a point such as dist(A,B) = 1.
				// 
				// The result should take dist(T(A), T(B))
				// pixels of the source image into account.
				//
				// log2( dist(T(A), T(B)) ) gives the scale, or
				// equivalently:
				// (1/2) * log2( dist(T(A), T(B))^2 )
				transform(T, (double)x+.707, (double)y+.707, t2);

				// squared distance in texture space
				double d = (t[0]-t2[0])*(t[0]-t2[0]) + 
					(t[1]-t2[1])*(t[1]-t2[1]);

				// finally we have the level!
				int level = intlog2((int)d)/2;

				// copy the pixel.
				pixels[offset + x] = p.sample(t[0], t[1], level);
			}
		}
	}	
}
