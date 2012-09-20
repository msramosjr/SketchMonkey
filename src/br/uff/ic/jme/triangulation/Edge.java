package br.uff.ic.jme.triangulation;

import java.util.Comparator;

public class Edge
{
   
   private Point   source;
   
   private Point   target;
   
   private boolean gEdge;
   
   public Edge(Point src, Point trg)
   {
      gEdge = false;
      setSource(src);
      setTarget(trg);
   }
   
   public Point getSource()
   {
      return source;
   }
   
   public void setSource(Point p)
   {
      source = p;
      source.edges.add(this);
   }
   
   public Point getTarget()
   {
      return target;
   }
   
   public void setTarget(Point p)
   {
      target = p;
      target.edges.add(this);
   }
   
   public boolean isGEdge()
   {
      return gEdge;
   }
   
   public void setGEdge(boolean gEdge)
   {
      this.gEdge = gEdge;
   }
   
   public boolean intersectSweepLine(float sweepLine)
   {
      // true, if sweepline is between source and target(i.e. source <=
      // sweepline <= target or target <= sweepline <= source)
      return ((source.x < sweepLine && target.x > sweepLine) || (target.x < sweepLine && source.x > sweepLine));
   }
   
   public Float getSweepLineIntersection(float sweepLine)
   {
      if (!intersectSweepLine(sweepLine))
         return null;
      
      // solve the line equation based on the given two points
      float a = (target.y - source.y) / (target.x - source.x);
      float b = source.y - a * source.x;
      float y = a * sweepLine + b;
      return y;
   }
   
   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;
      if (!(o instanceof Edge))
         return false;
      Edge e = (Edge) o;
      return ((source.x == e.source.x) && (source.y == e.source.y) && (target.x == e.target.x) && (target.y == e.target.y));
   }
   
   public final static Comparator<Edge> COMPARE_BY_Y = new EdgeComparatorY();
   public final static Comparator<Edge> COMPARE_BY_X = new EdgeComparatorX();
   
   private static class EdgeComparatorY implements Comparator<Edge>
   {
      @Override
      public int compare(Edge e1, Edge e2)
      {
         if (e1.getSource().y == e2.getSource().y)
            return (int) (e1.getTarget().y - e2.getTarget().y);
         return (int) (e1.getSource().y - e2.getSource().y);
      }
   }
   
   private static class EdgeComparatorX implements Comparator<Edge>
   {
      @Override
      public int compare(Edge e1, Edge e2)
      {
         if (e1.getSource().x == e2.getSource().x)
            return (int) (e1.getTarget().x - e2.getTarget().x);
         return (int) (e1.getSource().x - e2.getSource().x);
      }
   }
}
