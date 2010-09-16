import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;
import java.util.Arrays;
import Jama.*;
/**
 * This plugin just copy an image or a region of interest.
 */
public class Rotate_ implements PlugInFilter {
	ImagePlus imp;
	private static final boolean interpolate_bilinear = true;
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return NO_CHANGES | DOES_RGB;
	}

	public void run(ImageProcessor ip) {
		// read the region of interest (ROI) to zoom out
		// (If the user did not select any ROI, the default ROI is the
		// whole image)
		Rectangle roi = ip.getRoi(); 
		int w = roi.width;
	        int h = roi.height;

		// the image width (not the ROI width) tells us how many pixel
		// to skip to reach next line in the pixel array.
		int nextLine = ip.getWidth();

		// create a new image
		ColorProcessor r = new ColorProcessor(w,h);

		// get access to pixels. One pixel contains 32 bits in a int,
		// usually only 3*8 bits are used for red, green and blue
		// channels. The last 8 bits might contain an alpha channel.
		int[] srcpix = (int []) ip.getPixels();
		int[] src2 = (int []) r.getPixels();
      	
		// iterate over destination pixels
		for (int y= 0; y<h; y++) {

			// precompute pixel offsets of the line to read
			int srcline = (roi.y + y)*nextLine + roi.x;

			// same thing for the line to write
			int dstline = y*w;

			for (int x=0; x<w; x++) {
				// read the pixel from source and copy it into destination
				src2[dstline + x] = srcpix[srcline + x];
			}
		}

		double angle = askForAngle()*Math.PI/180;
		int rw = w;
		int rh = h;
		ColorProcessor rr = new ColorProcessor(rw,rh);
		int[] dstpix = (int []) rr.getPixels();
		Arrays.fill(dstpix,0);
		
		Point center = new Point(w/2, h/2);
		//Point center = new Point(0, 0);
		Matrix invM = getRotationMatrix(angle, center).inverse();
		
		for(int i=0; i<dstpix.length; i++)
		{
			int dstx = i%w;
			int dsty = i/w;
			Matrix dstv = new Matrix(new double[]{dstx,dsty,1.0}, 3);
			Matrix srcv = invM.times(dstv);
			int srcx = (int)Math.round(srcv.get(0,0));
			int srcy = (int)Math.round(srcv.get(1,0));
			if(srcx < 0 || srcx >= w || srcy < 0 || srcy >= h) {
				dstpix[i] = 0;
			} else {
            	if(interpolate_bilinear) {
                    Bilinear bilinear = new Bilinear(src2, w, h);
                    dstpix[i] = bilinear.interpolate(srcx, srcy);
                } else {
                	int j = (int)(srcx + srcy*w);
					dstpix[i] = src2[j];
				}
			}
		}
		
		new ImagePlus("Image rotation", rr).show();
	}
	
	private double askForAngle()
	{
		return IJ.getNumber("Enter the angle of rotation", 0.0);
	}
	
	public static Matrix getRotationMatrix(double angle, Point p)
	{
		final double[][] R = {	{Math.cos(angle), -Math.sin(angle), 0},
								{Math.sin(angle),  Math.cos(angle), 0},
								{		0	    ,		0	      , 1}};
		final double[][] T1 = { {1, 0, -p.x},
								{0, 1, -p.y},
								{0, 0,   1 }};
		final double[][] T2 = { {1, 0, p.x},
								{0, 1, p.y},
								{0, 0,  1 }};
		Matrix RM = new Matrix(R);
		Matrix TM1 = new Matrix(T1);
		Matrix TM2 = new Matrix(T2);
		return TM2.times(RM).times(TM1);
	}
	
}
