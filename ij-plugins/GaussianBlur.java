import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;

/**
 * This plugin just copy an image or a region of interest.
 */
public class GaussianBlur implements PlugInFilter {
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
        int[] dstpix = (int []) r.getPixels();

        blur(srcpix, dstpix, w, h);

        // display the result
        new ImagePlus("Image copy", r).show();
    }
    
    static void blur(int[] srcpix, int[] dstpix, int w, int h)
    {
        int[] tempix = new int[srcpix.length];
        double[] kernel1D = new double[]{ 0.9/16, 3.9/16, 6.4/16, 3.9/16, 0.9/16 };
        int kernelSize = kernel1D.length;
        int pad = kernelSize / 2;
        for (int y=0; y<h; y++)
        {
            int line = y*w;
            for(int x=pad; x<h-pad; x++)
            {
                int curpix = line + x;
                double dstred = 0;
                double dstgreen = 0;
                double dstblue = 0;
                for(int i=0; i<kernelSize; i++)
                {
                    int ppix = curpix + i - pad;
                    int pixel = srcpix[ppix];
                    double kern = kernel1D[i];
                    int srcred = (pixel >> 16) & 0xFF;
                    int srcgreen = (pixel >> 8) & 0xFF;
                    int srcblue = pixel & 0xFF;
                    dstred += kern * srcred;
                    dstgreen += kern * srcgreen;
                    dstblue += kern * srcblue;
                }
                int dstintred = ((int)dstred) & 0xFF;
                int dstintgreen = ((int)dstgreen) & 0xFF;
                int dstintblue = ((int)dstblue) & 0xFF;
                tempix[curpix] = (dstintred << 16) | (dstintgreen << 8) | dstintblue;
            }
        }
        
        for (int x=0; x<w; x++)
        {
            for(int y=pad; y<w-pad; y++)
            {
                int curpix = x + y*w;
                double dstred = 0;
                double dstgreen = 0;
                double dstblue = 0;
                for(int i=0; i<kernelSize; i++)
                {
                    int ppix = curpix + (i - pad)*w;
                    int pixel = tempix[ppix];
                    double kern = kernel1D[i];
                    int srcred = (pixel >> 16) & 0xFF;
                    int srcgreen = (pixel >> 8) & 0xFF;
                    int srcblue = pixel & 0xFF;
                    dstred += kern * srcred;
                    dstgreen += kern * srcgreen;
                    dstblue += kern * srcblue;
                }
                int dstintred = ((int)dstred) & 0xFF;
                int dstintgreen = ((int)dstgreen) & 0xFF;
                int dstintblue = ((int)dstblue) & 0xFF;
                dstpix[curpix] = (dstintred << 16) | (dstintgreen << 8) | dstintblue;
            }
        }
    }

}
