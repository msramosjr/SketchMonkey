package br.uff.ic.jme.triangulation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Point
{
   
   public float      x;
   
   public float      y;
   
   public List<Edge> edges;
   
   private boolean   gPoint;
   
   public boolean isGPoint()
   {
      return gPoint;
   }
   
   public void setGPoint(boolean gPoint)
   {
      this.gPoint = gPoint;
   }
   
   public Point(float x, float y)
   {
      this.x = x;
      this.y = y;
      edges = new ArrayList<Edge>();
   }
   
   public final static Comparator<Point> COMPARE_BY_X = new PointComparatorX();
   public final static Comparator<Point> COMPARE_BY_Y = new PointComparatorY();
   
   private static class PointComparatorX implements Comparator<Point>
   {
      @Override
      public int compare(Point p1, Point p2)
      {
         return (int) (p1.x - p2.x);
      }
   }
   
   private static class PointComparatorY implements Comparator<Point>
   {
      @Override
      public int compare(Point p1, Point p2)
      {
         return (int) (p1.y - p2.y);
      }
   }
   
   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;
      if (!(o instanceof Point))
         return false;
      Point p = (Point) o;
      return (x == p.x) && (y == p.y);
   }
   
   @Override
   public String toString()
   {
      return "(x:" + x + ",y:" + y + ")";
   }
   
}
