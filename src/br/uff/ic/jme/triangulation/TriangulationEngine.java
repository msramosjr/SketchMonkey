package br.uff.ic.jme.triangulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TriangulationEngine
{
   
   private List<Point>        points;
   
   private List<Edge>         edges;
   
   private List<Edge>         resultEdges;
   
   private List<Region>       regions;
   
   private List<Strip>        strips;
   
   private static final float EPSILON = 0.000000000000000000000000000000000000000000001f;
   
   // private List<Triangle> triangles;
   
   public TriangulationEngine()
   {
      points = new ArrayList<Point>();
      edges = new ArrayList<Edge>();
      resultEdges = new ArrayList<Edge>();
      // triangles = new ArrayList<Triangle>();
      regions = new ArrayList<Region>();
      strips = new ArrayList<Strip>();
   }
   
   public void addPoint(Point p)
   {
      p.setGPoint(true);
      points.add(p);
   }
   
   public void addEdge(Edge e)
   {
      e.setGEdge(true);
      edges.add(e);
      points.add(e.getSource());
      points.add(e.getTarget());
   }
   
   public List<Edge> getGEdges()
   {
      return edges;
   }
   
   public List<Edge> getResultEdges()
   {
      return resultEdges;
   }
   
   public void triangulate()
   {
      // first sort points by x coordinate
      Collections.sort(points, Point.COMPARE_BY_X);
      
      // eliminate vertical aligned points by making x = x - epsilon
      for (int i = 0; i + 1 < points.size(); i++)
      {
         if (points.get(i).x == points.get(i + 1).x)
            points.get(i).x = points.get(i).x - EPSILON;
      }
      
      // then for each point create a strip which contains the initial region
      // containing only the immediately above and below edges.
      // use a sweep-line for this
      for (Point p : points)
      {
         Strip s = new Strip(p.x, p.x);
         Region r = new Region(s);
         r.findUpperAndLowerEdges(s, p);
         r.addPoint(p);
         r.calculateInitialCDT();
         s.regions.add(r);
         strips.add(s);
      }
      
      while (true)
      {
         if (mergeStrips())
            break;
      }
      
      if (strips.size() != 1)
         throw new RuntimeException("Unexpected error: must have exactly one strip in the end of the strip merging process. Found " + strips.size());
      
      regions = strips.get(0).regions;
      // System.out.println("Regions count: "+regions.size());
      for (Region region : regions)
      {
         resultEdges.addAll(region.edges);
      }
      
      // System.out.println("strip instances: " + Strip.numInstances);
      // System.out.println("region instances: " + Region.numInstances);
   }
   
   private boolean mergeStrips()
   {
      // iterate through strips and merge them two-by-two
      Strip last = null;
      boolean merge = false;
      // System.out.println("before merge strip count: "+strips.size());
      List<Strip> mergedStrips = new ArrayList<Strip>();
      for (Strip s : strips)
      {
         if (merge)
         {
            last.merge(s);
            mergedStrips.add(last);
         }
         else
            last = s;
         
         merge = !merge;
      }
      if (strips.size() % 2 != 0)
         mergedStrips.add(last);
      strips = mergedStrips;
      // System.out.println("after merge strip count: "+strips.size());
      return strips.size() == 1;
   }
}
