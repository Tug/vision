import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.measure.*;
import java.awt.*;
import java.util.Arrays;
import ij.plugin.filter.*;

/* This plugin draws epipolar lines.
 * for details, see http://in3www.epfl.ch/~jpilet/serie8/
 */
public class Epipolar_ implements PlugInFilter {
   ImagePlus imp;

   /**
    * Camera class.
    * Internal parameters: f,px,py
    * External parameters: pos, u,v,z
    */
   class Camera {
      double f; // the scalar described in http://cvlab.epfl.ch/%7Efua/courses/vision/math/notes/Cameras.pdf
      double px, py; // principal point. Described in the same document.

      Camera(double _px, double _py, double _f) {
         px = _px; py = _py; f =_f;
         pos = new Vec3(0,0,0);
         z = new Vec3(0,0,1);
         u = new Vec3(1,0,0);
         v = new Vec3(0,1,0);
      }

      /** The 3D position of the camera projection center. */
      Vec3 pos;

      /** The camera 'z' 3d axis, going from projection center towards
       * the image plane. It is always a unit vector. */
      Vec3 z;

      /** The camera u and v 3d axis. Both are unit vectors. 'u'
       * represent the 3D displacement to go from one pixel to its
       * right neightbor.
       */
      Vec3 u, v;

      /** Moves the camera projection center to 'pos', and make it
       * look towards 'target'
       */
      void setTarget(Vec3 target, Vec3 pos)  {
         this.pos.copy(pos);
         z = new Vec3(target);
         z.sub(pos);
         z.norm();
         u.crossProd(new Vec3(0,1,0) ,z);
         u.norm();
         v.crossProd(z, u);
         v.norm();
      }

      Vec3 find_3d_point( double x, double y, double d)
         {
            Vec3 qu = new Vec3(u);
            qu.mul( x - px );

            Vec3 qv = new Vec3(v);
            qv.mul( y - py );

            Vec3 qz = new Vec3(z);
            qz.mul(f);

            Vec3 pnt = new Vec3(qz);

            pnt.add(qu);
            pnt.add(qv);

            Vec3 pnt3d = new Vec3(pnt);
            pnt3d.mul(d);
            pnt3d.add(pos);
            return pnt3d;
         }

      Vec3 project( Vec3 pnt3d )  {

         Vec3 Q = new Vec3( pnt3d );
         Q.sub( pos );

         Vec3 Qp = new Vec3( Q.dotprod(u), Q.dotprod(v), Q.dotprod(z) );

         Vec3 uv = new Vec3(0,0,1);

         uv.x = f * Qp.x / Qp.z + px;
         uv.y = f * Qp.y / Qp.z + py;
         
         return uv;

// intersection of a line segment and the image plane solution
//
//          double w0 = - z.dotprod(pos) - f * z.dotprod(z);
//          Vec3 line = new Vec3(pnt3d);
//          line.sub(pos);
//          line.norm();
//          double t = f * z.dotprod(z) / z.dotprod( line );
//          Vec3 point_on_plane = new Vec3(line);
//          point_on_plane.mul(t);
//          point_on_plane.add(pos);
//          Vec3 prince = new Vec3(z);
//          prince.mul(f);
//          prince.add(pos);
//          point_on_plane.sub(prince);
//          double x = point_on_plane.dotprod( u ) + px;
//          double y = point_on_plane.dotprod( v ) + py;
//          Vec3 ret_point = new Vec3(0,0,1);
//          ret_point.x = x;
//          ret_point.y = y;
//          return ret_point;
      }

   };

   /** Draws epipolar lines.
    * Given camera parameters for two images, and a set of point on the
    * image take by camera 1, this method draws on the second image, taken
    * by camera 2, epipolar lines corresponding to selected points.
    */
   void epipolarLines(Camera c1, Camera c2, Polygon p, ImageProcessor ip2)
      {
         String s = "Selected points: \n";

         Vec3 q = new Vec3(c1.pos);

         Vec3 pnt2d = c2.project( q );
         double ex = pnt2d.x;
         double ey = pnt2d.y;

         for (int i=0; i< p.npoints; i++)  {
            /*
             *
             * Compute here the epipolar line for the point (x,y)
             * Display it using drawLine.
             *
             */
            int x = p.xpoints[i];
            int y = p.ypoints[i];
            
            Vec3 pnt1 = c1.find_3d_point(x, y, 2 );

            pnt2d = c2.project( pnt1 );
            double u1 = pnt2d.x;
            double v1 = pnt2d.y;

            System.out.println("pnt1: " + u1 + " : " + v1 );

//             Vec3 pnt2 = c1.find_3d_point(x, y, 4 );

//             pnt2d = c2.project( pnt2 );
//             double u2 = pnt2d.x;
//             double v2 = pnt2d.y;

//             System.out.println("pnt2: " + u2 + " : " + v2 );

            // draw the infinite line passing trough these
            // two points on the second image
            drawLine(ip2,u1,v1,ex, ey);
         }
      }


   public int setup(String arg, ImagePlus imp) {
      this.imp = imp;
      return NO_CHANGES | DOES_RGB;
   }


   public void run(ImageProcessor ip) {

      // Construct the to cameras used to create cam1.jpg and cam2.jpg
      int w = 640, h = 480;
      Camera c1 = new Camera (w/2, h/2, 1000);
      Camera c2 = new Camera (w/2, h/2, 1000);
      c2.setTarget(new Vec3(0,0,2000), new Vec3(100, -1500, -100));

      // Get selected points on current image
      Roi roi = imp.getRoi();

      if (roi == null || (roi.getType() != roi.POINT)) {
         IJ.showMessage("No ROI or bad ROI type!\n");
         return;
      }

      // Try to access the other image
      ImagePlus window2 = getOtherWindow();

      if (window2 == null) {
         IJ.showMessage("No other window!");
         return;
      }
      ImageProcessor ip2 = window2.getProcessor();

      Polygon p = roi.getPolygon();

      // do the interesting work in there
      epipolarLines(c1, c2, p, ip2);

      window2.updateAndDraw();
   }

   /** return a reference to the other open window or null.
    */
   ImagePlus getOtherWindow() {
      int[] wList = WindowManager.getIDList();
      for (int i=0; i<wList.length; i++) {
         ImagePlus imp2 = WindowManager.getImage(wList[i]);
         if (imp2 != imp) {
            return imp2;
         }
      }
      return null;
   }

   /** Draw a line crossing the entire image and passing through the two
    * provided points. These points do not have to lie inside the image.
    */
   void drawLine(ImageProcessor ip, double cu, double cv, double pu, double pv)
      {
         double du = cu - pu;
         double dv = cv - pv;
         ip.setValue((double)(0xFFFFFF));
         if (du*du > dv*dv) {
            double a = dv/du;
            double b = cv - a*cu;

            int w = ip.getWidth();
            ip.drawLine(0, (int)(b), w, (int)(a*w+b));
         } else {
            double a = du/dv;
            double b = cu - a*cv;

            int h = ip.getHeight();
            ip.drawLine((int)(b),0, (int)(a*h+b), h);
         }
      }

}
