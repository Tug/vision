import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;

/**
 * This plugin just copy an image or a region of interest.
 */
public class PixelZoomOut implements PlugInFilter {
	ImagePlus imp;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return NO_CHANGES | DOES_RGB;
	}

	public void run(ImageProcessor ip) {
		// read the region of interest (ROI) to zoom out
		// (If the user did not select any ROI, the default ROI is the
		// whole image)
		Rectangle roi = ip.getRoi(); 
		int w = roi.width/2;
	        int h = roi.height/2;

		// the image width (not the ROI width) tells us how many pixel
		// to skip to reach next line in the pixel array.
		int nextLine = ip.getWidth();

		// create a new image
		ColorProcessor r = new ColorProcessor(w,h);

		// get access to pixels. One pixel contains 32 bits in a int,
		// usually only 3*8 bits are used for red, green and blue
		// channels. The last 8 bits might contain an alpha channel.
		int[] srcpix = (int []) ip.getPixels();
		int[] dstpix = (int []) r.getPixels();

		// iterate over destination pixels
		for (int y= 0; y<h; y++) {

			// precompute pixel offsets of the line to read
			int srcline = (roi.y + y)*nextLine + roi.x;

			// same thing for the line to write
			int dstline = y*w;

			for (int x=0; x<w; x++) {
				// read the pixel from source and copy it into destination
				dstpix[dstline + x] = srcpix[(dstline + x)*2];
			}
		}

		// display the result
		new ImagePlus("Image copy", r).show();
	}

}
