package br.uff.ic.jme.interpolation;

import java.util.HashMap;
import java.util.Map;


import br.uff.ic.jme.exception.CombineOperationException;
import br.uff.ic.jme.main.AppData;
import br.uff.ic.jme.main.SceneManager;
import br.uff.ic.jme.skeletonization.Skeleton;
import br.uff.ic.jme.skeletonization.SkeletonBranch;
import br.uff.ic.jme.skeletonization.SkeletonPoint;

import com.jme3.math.Vector3f;
import java.util.List;

/**
 * Convolution interpolator
 * 
 * @author Marcos Ramos
 */
public class ConvolutionInterpolator implements Interpolator
{
   private enum KernelType
   {
      PSEUDO_CAUCHY, INVERSE
   }
   
   @SuppressWarnings("unused")
   private KernelType kernelType;
   
   private Skeleton   skeleton;
   
   private List<Skeleton>   skeletons;
   
   private Map<Vector3f, Double> evaluatedPoints;
   private int                   evaluations;
   
   /** Threshold */
   private double     T        = 1.0;
   
   private double     s        = 0.85 * 50.0 / AppData.worldLimits.getMin(null).distance(AppData.worldLimits.getMax(null));; // 0.85 - example on thesis;
                                       
   int                positive = 0;
   int                negative = 0;
   
   public ConvolutionInterpolator(Skeleton skeleton)
   {
      this.skeleton = skeleton;
      //this.skeleton = getTestSkeleton();
      kernelType = KernelType.PSEUDO_CAUCHY;
      
      evaluatedPoints = new HashMap<Vector3f, Double>();
      
      evaluations = 0;
      
      // Vector3f A = new Vector3f(-5f, -2.0f, 0.0f);
      // Vector3f B = new Vector3f(5f, 3.0f, 0.0f);
      // cauchy_max(A, B);
   }
   
   public ConvolutionInterpolator(List<Skeleton> skeletons)
   {
      this.skeletons = skeletons;
      //this.skeleton = getTestSkeleton();
      kernelType = KernelType.PSEUDO_CAUCHY;
      
      evaluatedPoints = new HashMap<Vector3f, Double>();
      
      evaluations = 0;
      
      // Vector3f A = new Vector3f(-5f, -2.0f, 0.0f);
      // Vector3f B = new Vector3f(5f, 3.0f, 0.0f);
      // cauchy_max(A, B);
   }
   
   @SuppressWarnings("unused")
//   private Skeleton getTestSkeleton()
//   {
//      Skeleton skel = new Skeleton();
//      SkeletonBranch branch = new SkeletonBranch();
//      SkeletonPoint p1 = new SkeletonPoint(-5, -2.5, 0, 1, 1);
//      SkeletonPoint p2 = new SkeletonPoint(0, -2.5, 0, 1, 2);
//      SkeletonPoint p3 = new SkeletonPoint(0, 2.5, 0, 1, 2);
//      SkeletonPoint p4 = new SkeletonPoint(5, 2.5, 0, 1, 1);
//      branch.points.add(p1);
//      branch.points.add(p2);
//      branch.points.add(p3);
//      branch.points.add(p4);
//      skel.branches.add(branch);
//      
//      return skel;
//   }
   
   @Override
   public double interpolate(Vector3f point)
   {
       //multiple skeletons, apply implicit blending
      if(skeletons != null)
      {
          return implicitBlending(point);
      }
       
       
      if (evaluatedPoints.containsKey(point))
         return evaluatedPoints.get(point);
      
      evaluations++;
      
      double result = basicCSG(point, skeleton);
      
      evaluatedPoints.put(point, result);
      
      return result;
      // return point.subtract(Vector3f.ZERO).length() - 0.5;
      // return cauchy(point, A, B) + cauchy(point, C, D) - T;
   }
   
   private double implicitBlending(Vector3f point)
   {
      if (evaluatedPoints.containsKey(point))
         return evaluatedPoints.get(point);
      
      evaluations++;
      
      //implicit blending (1-t) * f(x) + t * g(x)
      
      double t = 0;
      double step = 0.1;
      double result = 0;
      
      double f1 = basicCSG(point, skeletons.get(0));
      double f2 = basicCSG(point, skeletons.get(1));
      for (int i = 0; i <= 10; i++)
      {          
          t = i * step;          
          double sum = (1.0 - t) * f1 + t * f2;
          result += sum;
          
//          if(f1 != f2)
//          {
//              System.out.println("t: " + t);
//              System.out.println("f1: " + f1);
//              System.out.println("f2: " + f2);
//              System.out.println("sum: " + sum);
//              System.out.println("result: " + result);
//          }
      }
            
      evaluatedPoints.put(point, result);
      
      return result;
   }
   
   @Override
   public void finishInterpolation()
   {
//      System.out.println("positive: " + positive);
//      System.out.println("negative: " + negative);
   }
   
   private double basicCSG(Vector3f point, Skeleton currentSkeleton)
   {
      double sum = 0;
      boolean isCSG = SceneManager.get().csgEnabled;
      
      for (int i = 0; i < currentSkeleton.branches.size(); i++)
      {
         SkeletonBranch branch = currentSkeleton.branches.get(i);
         for (int j = 0; j < branch.points.size() - 1; j++)
         {
            SkeletonPoint SP1 = branch.points.get(j);
            SkeletonPoint SP2 = branch.points.get(j + 1);
//            TriangulationPoint P1 = SP1.point;
//            TriangulationPoint P2 = SP2.point;
//            Vector3f p = new Vector3f(P1.getXf(), P1.getYf(), P1.getZf());
//            Vector3f q = new Vector3f(P2.getXf(), P2.getYf(), P2.getZf());
            Vector3f p = SP1.point;
            Vector3f q = SP2.point;
            
            // calculate radius of influence
            float Rp = (float) SP1.radius;
            float Rq = (float) SP2.radius;
            
            // calculate surface points Sp and Sq
            Vector3f PQ = q.subtract(p);
            Vector3f normalVector = new Vector3f(-PQ.getY(), PQ.getX(), PQ.getZ()).normalize();
            Vector3f Sp = p.add(normalVector.mult(Rp));
            Vector3f Sq = q.add(normalVector.mult(Rq));
            Vector3f Smid = Sp.add(Sq).divide(2f);
            
            // System.out.println("L: " + PQ.length() + " Sp: " + Sp + " Sq: " +
            // Sq + " Smid: " + Smid);
            
            // calculate weights
            double w0 = T / flt(Sp, p, q);
            double w1 = T / ft(Sq, p, q);
            
            // division factor
            w0 /= (double) SP1.divisionFactor;
            w1 /= (double) SP2.divisionFactor;
            
            double k = T / (w0 * flt(Smid, p, q) + w1 * ft(Smid, p, q));
            
            // scale factor
            w0 *= k;
            w1 *= k;
            
            // System.out.println("j " + j);
            // System.out.println("w0 " + w0 + " w1 " + w1);
                        
            // CSG blending            
            if(isCSG)
            {
                double fx = cauchy(point, p, q, w0, w1);
                double R = cauchy(Smid, p, q, w0, w1);
                double blending = blending(-fx + T, R);
                sum += blending;
            }
            else
            {
            // common sum
                sum += cauchy(point, p, q, w0, w1);
            }
            
         }
      }
      
      // if(sum > 1)
      // blendingGTone++;
      // else if(sum < 1)
      // blendingLTone++;
      
      // sum /= n;
      
      // Vector3f A = new Vector3f(-5f, 0.0f, 0.0f);
      // Vector3f B = new Vector3f(5f, 0.0f, 0.0f);
      // Vector3f C = new Vector3f(-5f, 4.0f, 0.0f);
      // Vector3f D = new Vector3f(5f, 4.0f, 0.0f);
      //
      // // define threshold based on a pre determined surface point
      // float radius = 5.0f;
      // Vector3f AB = B.subtract(A);
      // Vector3f normalVectorAB = new Vector3f(-AB.getY(), AB.getX(),
      // AB.getZ()).normalize().mult(radius);
      // Vector3f midPointAB = (A.add(B)).divide(2f);
      // Vector3f surfacePointAB = midPointAB.add(normalVectorAB);
      //
      // T = 1.0;
      //
      // sum += blending(-cauchy(point, A, B) + T, cauchy(surfacePointAB, A,
      // B));
      // sum += blending(-cauchy(point, C, D) + T, cauchy(surfacePointAB, C,
      // D));
      
      // sum = cauchy(point, A, B) + cauchy(point, C, D);
      
//      if(!single) //i.e. is multiple
//      {
//          // common sum      
////          double result = -sum + T;
////          if (result > 0)
////             positive++;
////          else if (result < 0)
////             negative++;
////          return result;
//          return sum;
//      }
//      else
//      {
      if(!isCSG)
          return T - sum;
      else
          // CSG composition
          return 1 - sum;
//      }
   }
   
   private double blending(double x, double R)
   {
      int m = 1, n = 2;
      m = SceneManager.get().csgM;
      n = SceneManager.get().csgN;      
      if (x > R)
         return 0;
      else if (x < -R)
         return 2;
      else if (0 <= x && x <= R)
         return (1.0 / Math.pow(R, m * n)) * Math.pow(Math.pow(R, m) - Math.pow(x, m), n);
      else if (-R <= x && x <= 0)
         return 2 - (1.0 / Math.pow(R, m * n)) * Math.pow(Math.pow(R, m) + Math.pow(x, m), n);
      
      //XXX: in some cases x is NaN, i don't know what is happening, i will just return zero
      return 0;
      
      //throw new RuntimeException("Unexpected value in function blending(): " + x);
   }
   
   @SuppressWarnings("unused")
   private double cauchy(Vector3f point, Vector3f center)
   {
      Vector3f vec_r = point.subtract(center);
      double s = 0.85; // 0.85 - example on thesis; //kernel width
      
      double r_2 = vec_r.lengthSquared();
      return 1.0 / ((1 + s * s * r_2) * (1 + s * s * r_2));
   }
   
   private double cauchy(Vector3f point, Vector3f p, Vector3f q, double w0, double w1)
   {
      return w0 * flt(point, p, q) + w1 * ft(point, p, q);
   }
   
   public double f1(Vector3f point, Vector3f A, Vector3f B)
   {
      // ****** basic parameters ******
      Vector3f vec_b = B;
      Vector3f vec_a = A.subtract(vec_b);
      Vector3f vec_r = point;
      double L = vec_a.length();
      Vector3f vec_d = vec_r.subtract(vec_b);
      double d = vec_d.length();
      double h = vec_d.dot(vec_a.normalize());
      
      // terms
      double s2 = s * s;
      double h2 = h * h;
      double lmh = L - h;
      double d2 = d * d;
      double p2 = 1 + s2 * (d2 - h2);
      double p = Math.sqrt(p2);
      double ww = p2 + s2 * h2;
      double qq = p2 + s2 * lmh * lmh;
      double sdp = s / p;
      double tpp = 2.0 * p2;
      double sp = s * p;
      double term = Math.atan(sdp * h) + Math.atan(sdp * lmh);
      
      return (h / ww + lmh / qq) / tpp + term / (tpp * sp);
   }
   
   public double ft(Vector3f point, Vector3f A, Vector3f B)
   {
      // ****** basic parameters ******
      Vector3f vec_b = B;
      Vector3f vec_a = A.subtract(vec_b);
      Vector3f vec_r = point;
      double L = vec_a.length();
      Vector3f vec_d = vec_r.subtract(vec_b);
      double d = vec_d.length();
      double h = vec_d.dot(vec_a.normalize());
      
      // terms
      double s2 = s * s;
      double h2 = h * h;
      double lmh = L - h;
      double d2 = d * d;
      double p2 = 1 + s2 * (d2 - h2);
      double ww = p2 + s2 * h2;
      double qq = p2 + s2 * lmh * lmh;
      
      double ts2 = 2.0 * s2;
      return h * f1(point, A, B) + (1.0 / ww - 1.0 / qq) / ts2;
   }
   
   public double flt(Vector3f point, Vector3f A, Vector3f B)
   {
      Vector3f vec_b = B;
      Vector3f vec_a = A.subtract(vec_b);
      double L = vec_a.length();
      
      return L * f1(point, A, B) - ft(point, A, B);
   }
   
   @Override
   public void combine(Interpolator otherFunction) throws CombineOperationException
   {
      // TODO Auto-generated method stub      
   }
   
   @Override
   public Vector3f[] getRestrictionPoints()
   {
      // TODO Auto-generated method stub
      return null;
   }
   
   @Override
   public double[] getValues()
   {
      // TODO Auto-generated method stub
      return null;
   }
   
   @Override
   public int getEvaluations()
   {      
      return evaluations;
   }
   
}
