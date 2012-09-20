package br.uff.ic.jme.triangulation;

import java.util.ArrayList;
import java.util.List;

public class Strip
{
   
   public static int   numInstances = 0;
   
   public float        start;
   
   public float        end;
   
   public List<Region> regions;
   
   public Strip(float start, float end)
   {
      this.start = start;
      this.end = end;
      regions = new ArrayList<Region>();
      numInstances++;
   }
   
   public void merge(Strip s)
   {
      if (regions == null || regions.isEmpty() || s == null || s.regions == null || s.regions.isEmpty())
         throw new RuntimeException("Unexpected error: a strip must contain at least one region. Found:" + regions.size() + " and " + s.regions.size());
      
      Region r1 = regions.get(0);
      Region r2 = s.regions.get(0);
      
      // System.out.println("left regions: " + regions.size());
      // System.out.println("right regions: " + s.regions.size());
      
      Region newRegion;
      List<Region> newList = new ArrayList<Region>();
      boolean isLeft;
      int i = 0, j = 0;
      if (r1.upperEdge.getSource().y >= r2.upperEdge.getSource().y)
      {
         // System.out.println("newregion = r1");
         newRegion = r1;
         isLeft = true;
         i++;
      }
      else
      {
         // System.out.println("newregion = r2");
         newRegion = r2;
         isLeft = false;
         j++;
      }
      
      int auxIndex = 0;
      List<Region> auxList;
      if (isLeft)// left region fixed
      {
         // System.out.println("left fixed");
         auxIndex = j;
         auxList = s.regions;
      }
      else
      // right region fixed
      {
         // System.out.println("right fixed");
         auxIndex = i;
         auxList = regions;
      }
      boolean changed = false;
      while (i < regions.size() || j < s.regions.size())
      {
         if (changed)
         {
            if (isLeft)// left region fixed
            {
               // System.out.println("left fixed");
               auxIndex = j;
               auxList = s.regions;
            }
            else
            // right region fixed
            {
               // System.out.println("right fixed");
               auxIndex = i;
               auxList = regions;
            }
         }
         changed = false;
         
         while (auxIndex < auxList.size())
         {
            // System.out.println("auxIndex: "+auxIndex);
            Region r = auxList.get(auxIndex++);
            if (isLeft)
               j = auxIndex;
            else
               i = auxIndex;
            
            // System.out.println("i: "+i+" j: "+j+" aux: "+auxIndex );
            // case 1: region reaches the end and the next one doesn't have
            // intersection. add the new region to list.
            if (r == null || r.upperEdge.getSource().y > newRegion.lowerEdge.getSource().y)
            {
               // System.out.println("case 1");
               newList.add(newRegion);
               break;
            }
            
            // case 2: region reaches the end, but the next one has an
            // intersection, change fixed side.
            if (r.lowerEdge.getSource().y > newRegion.lowerEdge.getSource().y)
            {
               // System.out.println("case 2");
               isLeft = !isLeft;
               changed = true;
            }
            
            // System.out.println("merge regions");
            // cases 2 and 3: regions have an intersection. merge the regions.
            newRegion.merge(r);
            if (changed)
               break;
         }
         newList.add(newRegion);
         // if there are no more regions on a side, we need to change the side
         if (auxIndex < auxList.size())
         {
            // System.out.println("change side");
            isLeft = !isLeft;
            changed = true;
         }
      }
      
      // System.out.println("regions: " + regions.size());
      // System.out.println("newList regions: " + newList.size());
      regions.clear();
      regions = newList;
      
      start = Math.min(start, s.start);
      end = Math.max(end, s.end);
   }
   
   /**
    * Returns true if one of the edge's points is between start and end of the
    * strip.
    * 
    * @param e
    * @return
    */
   public boolean contains(Edge e)
   {
      return ((start <= e.getSource().x && end >= e.getSource().x) || (start <= e.getTarget().x && end >= e.getTarget().x));
   }
}
