package br.uff.ic.jme.skeletonization;

import java.util.List;

import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

class BoundaryJTriangle
{
   public int                      numNeighbors = 0;
   
   public DelaunayTriangle         triangle;
   
   public List<TriangulationPoint> auxPolygon1;
   
   public List<TriangulationPoint> auxPolygon2;
   
   @Override
   public boolean equals(Object otherTri)
   {
      if (otherTri == null || !(otherTri instanceof BoundaryJTriangle))
         return false;
      
      BoundaryJTriangle temp = (BoundaryJTriangle) otherTri;
      return triangle == temp.triangle;
   }
}
