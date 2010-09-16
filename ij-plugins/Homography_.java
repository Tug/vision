import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.measure.*;
import java.awt.*;
import java.util.Arrays;
import ij.plugin.filter.*;
import Jama.*;
import java.lang.Math;
import ij.io.*;



public class Homography_ implements PlugInFilter {
	ImagePlus imp;
 

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return NO_CHANGES | DOES_RGB;
	}

	public void run(ImageProcessor ip) {

		float im1X[] = new float[9];
		float im1Y[] = new float[9];
		float im2X[] = new float[9];
		float im2Y[] = new float[9];

		// correspondences
		im2X[0] = 246;
		im2X[1] = 500;
		im2X[2] = 254;
		im2X[3] = 535;
		im2X[4] = 242;
		im2X[5] = 502;
		im2X[6] = 254;
		im2X[7] = 496;
		im2X[8] = 523;
		

		im2Y[0] = 102;
		im2Y[1] = 100;
		im2Y[2] = 180;
		im2Y[3] = 106;
		im2Y[4] = 27;
		im2Y[5] = 22;
		im2Y[6] = 341;
		im2Y[7] = 360;
		im2Y[8] = 278;


		im1X[0] = 68;
		im1X[1] = 334;
		im1X[2] = 81;
		im1X[3] = 366;
		im1X[4] = 61;
		im1X[5] = 334;
		im1X[6] = 90;
		im1X[7] = 343;
		im1X[8] = 361;

		im1Y[0] = 100;
		im1Y[1] = 104;
		im1Y[2] = 185;
		im1Y[3] = 110;
		im1Y[4] = 16;
		im1Y[5] = 30;
		im1Y[6] = 362;
		im1Y[7] = 350;
		im1Y[8] = 271;


		// setup a linear system to look for a transformation
		double[][] linSystem = new double[18][8];
		double[][] result = new double[18][1];

		for  (int i=0; i<9; ++i) {
			linSystem[i*2][0] = im2X[i];
			linSystem[i*2][1] = im2Y[i];
			linSystem[i*2][2] = 1;
			linSystem[i*2][3] = 0;
			linSystem[i*2][4] = 0;
			linSystem[i*2][5] = 0;
			linSystem[i*2][6] = -im2X[i]*im1X[i];
			linSystem[i*2][7] = -im2Y[i]*im1X[i];

			linSystem[i*2+1][0] = 0;
			linSystem[i*2+1][1] = 0;
			linSystem[i*2+1][2] = 0;
			linSystem[i*2+1][3] = im2X[i];
			linSystem[i*2+1][4] = im2Y[i];
			linSystem[i*2+1][5] = 1;
			linSystem[i*2+1][6] = -im2X[i]*im1Y[i];
			linSystem[i*2+1][7] = -im2Y[i]*im1Y[i];

			result[i*2][0] = im1X[i];
			result[i*2+1][0] = im1Y[i];
		}

		// solve the system
		Matrix A = new Matrix(linSystem);
		//A.print(java.text.NumberFormat.getInstance(),8);
		Matrix b = new Matrix(result);
		Matrix x = A.solve(b);
		//x.print(java.text.NumberFormat.getInstance(),8);
		
		
		// just to output and see the H matrix ..
		/* 
		double[][] arrayX = x.getArray();
		double[][] vals = {{arrayX[0][0],arrayX[1][0],arrayX[2][0]},{arrayX[3][0],arrayX[4][0],arrayX[5][0]},{arrayX[6][0],arrayX[7][0],1}};
		Matrix H = new Matrix(vals);
		H.print(java.text.NumberFormat.getInstance(),8);
		*/


		// create a new image
		ColorProcessor r = new ColorProcessor(ip.getWidth()*2, ip.getHeight()*2);
		int[] pixels = (int[])ip.getPixels();
		Bilinear bil = new	Bilinear(pixels, ip.getWidth(), ip.getHeight());

		// transform the source image into the new image
		warp(x.getColumnPackedCopy(), (ColorProcessor)ip, r,bil);
   	
		new ImagePlus("Panorama", r).show();
		//FileSaver fs = new FileSaver(new ImagePlus("Panorama", r)); 
		//fs.saveAsJpeg("im2_result_half.jpg");
	     
	}

	void transform(double[] T, double x, double y, double[] r) {
		double a = T[0]*x + T[1]*y + T[2];
		double b = T[3]*x + T[4]*y + T[5];
		double c = T[6]*x + T[7]*y + 1;

		r[0] = a/c;
		r[1] = b/c;
	}

   void warp(double[] T, ColorProcessor src, ColorProcessor dst,Bilinear bil) {

		int offset;
		int width = dst.getWidth();
		int height = dst.getHeight();
		int[] pixels = (int[])dst.getPixels();


		double[] t = new double[2];

		for (int y=0; y<height; y++) {
			offset = y*width;
			for (int x=0; x<width; x++) {
				transform(T, (double)x, (double)y, t);
				pixels[offset + x] = bil.interpolate(t[0],t[1]);
			}
		}
	}
   
}
	 
 
