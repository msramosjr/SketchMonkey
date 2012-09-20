package br.uff.ic.jme.triangulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Region
{
   
   public static int   numInstances = 0;
   
   public Edge         upperEdge;
   
   public Edge         lowerEdge;
   
   private List<Point> points;
   
   public List<Edge>   edges;
   
   public List<Point>  infinitePoints;
   
   public List<Edge>   infiniteEdges;
   
   public Strip        strip;
   
   public Region(Strip s)
   {
      strip = s;
      edges = new ArrayList<Edge>();
      points = new ArrayList<Point>();
      infiniteEdges = new ArrayList<Edge>(4);
      infinitePoints = new ArrayList<Point>(4);
      infinitePoints.add(new Point(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY));
      infinitePoints.add(new Point(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY));
      infinitePoints.add(new Point(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY));
      infinitePoints.add(new Point(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
      numInstances++;
   }
   
   public void addPoint(Point p)
   {
      points.add(p);
      edges.addAll(p.edges);
   }
   
   public void addEdge(Edge e)
   {
      edges.add(e);
      points.add(e.getSource());
      points.add(e.getTarget());
   }
   
   /**
    * Calculate Constrained Delaunay Triangulation for the initial region
    */
   public void calculateInitialCDT()
   {
      for (Point infP : infinitePoints)
      {
         for (Point p : points)
         {
            infiniteEdges.add(new Edge(p, infP));
         }
      }
   }
   
   /**
    * Calculate Constrained Delaunay Triangulation
    */
   public void calculateCDT()
   {
      // System.out.println("calculate CDT");
      List<Edge> newEdges = new ArrayList<Edge>();
      List<Edge> currentEdges = new ArrayList<Edge>();
      currentEdges.addAll(edges);
      currentEdges.addAll(infiniteEdges);
      Collections.sort(currentEdges, Collections.reverseOrder(Edge.COMPARE_BY_Y));
      // System.out.println("current edges: " + currentEdges.size());
      
      for (Edge e : currentEdges)
      {
         // consider e the edge AB
         // System.out.println("edge AB");
         // assuming point X is in A strip
         List<Edge> sourceEdges = e.getSource().edges;
         Collections.sort(sourceEdges, Collections.reverseOrder(Edge.COMPARE_BY_X));
         Edge candidateLeft = null;
         // System.out.println("source edges: " + sourceEdges.size());
         for (Edge edge : sourceEdges)
         {
            if (edge.isGEdge())
            {
               // System.out.println("G-Edge");
               candidateLeft = edge;
               break;
            }
            
            if (candidateLeft != null)
            {
               // System.out.println("verify if candidate left is valid");
               Point newPoint = candidateLeft.getTarget().equals(e.getSource()) ? candidateLeft.getTarget() : candidateLeft.getSource();
//               Circle c = new Circle(e.getSource(), e.getTarget(), newPoint);
//               if (!c.contains(edge.getSource()))
//                  break;
            }
            candidateLeft = edge;
         }
         
         // assuming point X is in B strip
         List<Edge> targetEdges = e.getTarget().edges;
         Collections.sort(targetEdges, Edge.COMPARE_BY_X);
         Edge candidateRight = null;
         // System.out.println("target edges: " + targetEdges.size());
         for (Edge edge : targetEdges)
         {
            if (edge.isGEdge())
            {
               // System.out.println("G-Edge");
               candidateRight = edge;
               break;
            }
            
            if (candidateRight != null)
            {
               // System.out.println("verify if candidate right is valid");
               Point newPoint = candidateRight.getTarget().equals(e.getSource()) ? candidateRight.getTarget() : candidateRight.getSource();
//               Circle c = new Circle(e.getSource(), e.getTarget(), newPoint);
//               if (!c.contains(edge.getSource()))
//                  break;
               edges.remove(edge);
            }
            candidateRight = edge;
         }
         Edge newEdge = null;
         if (Edge.COMPARE_BY_Y.compare(candidateLeft, candidateRight) > 0)
         {
            newEdge = new Edge(e.getTarget(), candidateLeft.getSource());
         }
         else
         {
            newEdge = new Edge(e.getTarget(), candidateRight.getSource());
         }
         
         newEdges.add(newEdge);
      }
      edges.addAll(newEdges);
   }
   
   private void mergeInfiniteVerticesAndEdges(Region r)
   {
      for (int i = 0; i < infinitePoints.size();)
      {
         Point p = infinitePoints.get(i);
         if (p.x == strip.end)
         {
            // remove infinite point between two strips
            infinitePoints.remove(i);
            // remove infinite edges between two strips
            infiniteEdges.removeAll(p.edges);
         }
         else
            i++;
      }
      for (int i = 0; i < r.infinitePoints.size(); i++)
      {
         Point p = r.infinitePoints.get(i);
         if (p.x != strip.start)
         {
            // merge infinite points that are not between two strips
            infinitePoints.add(p);
            // merge infinite edges that are not between two strips
            infiniteEdges.addAll(p.edges);
         }
      }
   }
   
   /**
    * Merge two regions.
    * 
    * @param r
    *           - the region to be merged with.
    */
   
   public void merge(Region r)
   {
      edges.addAll(r.edges);
      points.addAll(r.points);
      // remove the infinite vertices between the two strips
      mergeInfiniteVerticesAndEdges(r);
      
      // calculate CDT for the merged region
      calculateCDT();
   }
   
   public void findUpperAndLowerEdges(Strip s, Point p)
   {
      upperEdge = new Edge(new Point(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY), new Point(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY));
      lowerEdge = new Edge(new Point(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY), new Point(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
      ;
      for (Edge e : edges)
      {
         if (!e.isGEdge())
            continue;
         
         Float y = e.getSweepLineIntersection(p.x);
         if (y == null)
            continue;
         
         // if the strip contains the edge, this edge cannot be a delimiter to
         // this region.
         if (s.contains(e))
            continue;
         
         if (y > upperEdge.getSweepLineIntersection(p.x))
            upperEdge = e;
         if (y < lowerEdge.getSweepLineIntersection(p.x))
            lowerEdge = e;
      }
   }
}
