package br.uff.ic.jme.interpolation;

import br.uff.ic.jme.exception.CombineOperationException;

import com.jme3.math.Vector3f;

public interface Interpolator
{
   
   /**
    * Return the result of the interpolation at the given point.
    * 
    * @param point
    *           , the point to interpolate.
    * @return 0, if the point is exactly on the surface; > 0, if it is outside;
    *         < 0, if it is inside.
    */
   double interpolate(Vector3f point);
   
   void combine(Interpolator otherFunction) throws CombineOperationException;
   
   Vector3f[] getRestrictionPoints();
   
   double[] getValues();
   
   int getEvaluations();

   void finishInterpolation();
}
