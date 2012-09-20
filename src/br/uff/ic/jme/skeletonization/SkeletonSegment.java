package br.uff.ic.jme.skeletonization;

import org.poly2tri.triangulation.TriangulationPoint;

public class SkeletonSegment
{
   
   public TriangulationPoint a;
   public TriangulationPoint b;
   
   public double             distance;
   
   public SkeletonSegment(TriangulationPoint a, TriangulationPoint b)
   {
      this.a = a;
      this.b = b;
      // this.distance = distance;
   }
}
