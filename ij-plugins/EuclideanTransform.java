import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.measure.*;
import java.awt.*;
import java.util.Arrays;
import java.lang.Math;
import ij.plugin.filter.*;
import Jama.*;

public class EuclideanTransform implements PlugInFilter {
    ImagePlus imp;

    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        return NO_CHANGES | DOES_RGB;
    }

    /**
     * Compute a Euclidean transformation matrix doing the inverse of what follows:
     * - rotate alpha radian arround (cx, cy)
     * - translate the result to (tx, ty) 
     */
    public void makeEuclideanMatrix(double[] m, double alpha, double cx, double cy, double tx, double ty)
    {
        double ca = Math.cos(alpha);
        double sa = Math.sin(alpha);
        m[0] = ca;
        m[1] = sa;
        m[2] = -(sa*ty+ca*tx-cx);
        m[3] = -sa;
        m[4] = ca;
        m[5] = -(ca*ty-sa*tx-cy);
    }
	
    public void run(ImageProcessor ip) {
        Rectangle roi = ip.getRoi();

        // ask transformation parameters to the user
        double angle = IJ.getNumber("Rotation angle", 0);
        if (angle == IJ.CANCELED) return;

        // compute the transformation matrix
        double [] matrix = new double[6];
        makeEuclideanMatrix(matrix, angle*2*Math.PI/360, 
                            roi.x + roi.width/2,
                            roi.y + roi.height/2,
                            roi.width/2,
                            roi.height/2);

        // create a new image
        ColorProcessor r = new ColorProcessor(roi.width, roi.height);

        // transform the source image into the new image
        warp(matrix, (ColorProcessor)ip, r, true);

        // display the result.
        new ImagePlus("Euclidean transform result", r).show();
    }

    // compute matrix/vector multiplication.  
    void transform(double[] T, double x, double y, double[] r) {
        r[0] = T[0]*x + T[1]*y + T[2];
        r[1] = T[3]*x + T[4]*y + T[5];
    }
		
    void warp(double[] T, ColorProcessor src, ColorProcessor dst, boolean interpolate_bilinear) {

        int offset;
        int width = dst.getWidth();
        int height = dst.getHeight();
        int[] pixels = (int[])dst.getPixels();
        int[] srcPix = (int[])src.getPixels();

        int sw = src.getWidth();
        int sh = src.getHeight();

        double[] t = new double[2];

        // for each destination pixel ...
        for (int y=0; y<height; y++) {
            offset = y*width;
            for (int x=0; x<width; x++) {

                // compute the source coordinates
                transform(T, (double)x, (double)y, t);

                if(interpolate_bilinear) {
                    Bilinear bilinear = new Bilinear(srcPix, sw, sh);

                    pixels[offset + x] = bilinear.interpolate(t[0], t[1]);
                }
                else {
                    int rx = (int)Math.round(t[0]);
                    int ry = (int)Math.round(t[1]);

                    if ((rx>= 0) && (rx< sw) && (ry>=0) && (ry < sh))
                        pixels[offset + x] = srcPix[ ry*sw + rx];
                    else
                        pixels[offset + x] = 0;
                }
            }
        }
    }	
}
