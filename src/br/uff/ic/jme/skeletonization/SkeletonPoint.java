package br.uff.ic.jme.skeletonization;

import br.uff.ic.jme.triangulation.Circle;
import br.uff.ic.jme.triangulation.Point;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;
import org.poly2tri.triangulation.point.TPoint;

public class SkeletonPoint
{
//   public TriangulationPoint   point;
//   
//   public TriangulationPoint[] enclosingPoints;
//   
//   public DelaunayTriangle     backTriangle;
//   
//   public DelaunayTriangle     frontTriangle;
//   
//   public double               radius;
//   
//   public float                circumRadius;
//   
//   public Vector2f             circumCenter;
//   
//   public int                  divisionFactor;
//   
//   public final double         inflationFactor = 1;
   
   
   //new fields
   private List<SkeletonPoint> neighbors;
   
   public Vector3f point;
   
   public float radius;
   
   public float divisionFactor = 1;
   
   public Vector3f circumcenter;
   
   public float circumradius;
   
   public List<SkeletonBranch> startBranches = new ArrayList<SkeletonBranch>();
   public List<SkeletonBranch> endBranches = new ArrayList<SkeletonBranch>();
   
   public SkeletonPoint()
   {      
   }
   
   public SkeletonPoint(Vector3f pos, float radius, int divisionFactor, Vector3f circumcenter, float circumradius)
   {
       neighbors = new ArrayList<SkeletonPoint>();
       point = pos;
       this.radius = radius;
       this.divisionFactor = divisionFactor;
       this.circumcenter = circumcenter;
       this.circumradius = circumradius;
   }
   
   public void addNeighbor(SkeletonPoint neighbor)
   {
       neighbors.add(neighbor);       
   }
   
   @Override
   public String toString()
   {
       return point.toString();
   }
   
//   public SkeletonPoint(double x, double y, double z, double radius, int divisionFactor)
//   {
//      point = new TPoint(x, y, z);
//      this.radius = radius;
//      this.divisionFactor = divisionFactor;
//   }
//   
//   public static SkeletonPoint firstSkeletonPoint(DelaunayTriangle tri)
//   {
//      SkeletonPoint firstPoint = new SkeletonPoint();
//      firstPoint.frontTriangle = tri;
//      firstPoint.point = barycenter(tri);
//      //firstPoint.point = circumcenter(tri);
//      //firstPoint.circumRadius = circumradius(tri);
//      return firstPoint;
//   }
//   
//   public SkeletonPoint(DelaunayTriangle backTriangle, TriangulationPoint backPoint)
//   {
//      
//      System.out.println("back tri: " + backTriangle + " back point: " + backPoint);
//      this.backTriangle = backTriangle;
//      divisionFactor = 1;
//      
//      if (backPoint == null)
//      {         
//         point = barycenter(backTriangle);
//         //point = circumcenter(backTriangle);
//         //circumRadius = circumradius(backTriangle);
//         radius = distance(backTriangle.points[0], point) * inflationFactor;         
//         return;
//      }
//      
//      
//      
//      frontTriangle = backTriangle.neighborAcross(backPoint);
//      TriangulationPoint left = backTriangle.pointCW(backPoint);
//      TriangulationPoint right = backTriangle.pointCCW(backPoint);
//      TriangulationPoint front = frontTriangle.oppositePoint(backTriangle, backPoint);
//      
//      backTriangle.visitedNeighbors[backTriangle.index(backPoint)] = true;
//      frontTriangle.visitedNeighbors[frontTriangle.index(front)] = true;
//      enclosingPoints = new TriangulationPoint[] { left, front, right, backPoint };
//      
//      if (backTriangle.isStriangle())
//      {
//         divisionFactor = 2;
//         point = midPoint(left, right);
//         //point = circumcenter(backTriangle);
//         //circumRadius = circumradius(backTriangle);
//         radius = (distance(left, point) + distance(right, point)) / 2.0 * inflationFactor;
//      }
//      else
//      {
//         divisionFactor = 3;
//         point = barycenter(backTriangle);
//         //point = circumcenter(backTriangle);
//         //circumRadius = circumradius(backTriangle);
//         radius = distance(backTriangle.points[0], point) * inflationFactor;
//      }
//      
//      // System.out.println("back: " + backPoint + " front: " + front +
//      // " right: " + right + " left: " + left);
//   }
//   
//   public static float circumradius(DelaunayTriangle tri)
//   {
//      return new Circle(tri.points[0], tri.points[1], tri.points[2]).radius;      
//   }
//   
//   public static TriangulationPoint circumcenter(DelaunayTriangle tri)
//   {
//      Point p = new Circle(tri.points[0], tri.points[1], tri.points[2]).center;      
//      return new TPoint(p.x, p.y, tri.points[0].getZf());
//   }
//      
//   private static TriangulationPoint barycenter(DelaunayTriangle triangle)
//   {
//      TriangulationPoint a = triangle.points[0];
//      TriangulationPoint b = triangle.points[1];
//      TriangulationPoint c = triangle.points[2];
//      
//      return new TPoint((a.getX() + b.getX() + c.getX()) / 3.0, (a.getY() + b.getY() + c.getY()) / 3.0, (a.getZ() + b.getZ() + c.getZ()) / 3.0);
//   }
//   
//   public TriangulationPoint left()
//   {
//      return enclosingPoints == null ? null : enclosingPoints[0];
//   }
//   
//   public TriangulationPoint front()
//   {
//      return enclosingPoints == null ? null : enclosingPoints[1];
//   }
//   
//   public TriangulationPoint right()
//   {
//      return enclosingPoints == null ? null : enclosingPoints[2];
//   }
//   
//   public TriangulationPoint back()
//   {
//      return enclosingPoints == null ? null : enclosingPoints[3];
//   }
//   
//   private TriangulationPoint midPoint(TriangulationPoint p1, TriangulationPoint p2)
//   {
//      return new TPoint((p1.getX() + p2.getX()) / 2.0, (p1.getY() + p2.getY()) / 2.0, (p1.getZ() + p2.getZ()) / 2.0);
//   }
//   
//   private double distance(TriangulationPoint p1, TriangulationPoint p2)
//   {
//      return Math.sqrt((p2.getX() - p1.getX()) * (p2.getX() - p1.getX()) + (p2.getY() - p1.getY()) * (p2.getY() - p1.getY()) + (p2.getZ() - p1.getZ()) * (p2.getZ() - p1.getZ()));
//   }
//   
//   public void raise()
//   {
//      point.set(point.getX(), point.getY(), point.getZ() + radius);
//   }
}
