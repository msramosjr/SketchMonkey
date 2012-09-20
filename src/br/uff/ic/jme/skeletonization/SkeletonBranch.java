package br.uff.ic.jme.skeletonization;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

import org.poly2tri.triangulation.TriangulationPoint;

public class SkeletonBranch
{
   public List<SkeletonPoint> points;
   
   public double length;
   
   public SkeletonBranch()
   {
      points = new ArrayList<SkeletonPoint>();
   }
   
   public void smoothSegment(int i)
   {
//      TriangulationPoint a = points.get(i - 1).point;
//      TriangulationPoint c = points.get(i + 1).point;
       Vector3f a = points.get(i - 1).point;
      Vector3f c = points.get(i + 1).point;
      // TriangulationPoint left = points.get(i).left();
      // TriangulationPoint right = points.get(i).right();
      //
      // // find intersection between segments left-right and a-c
      // double leftRightYdelta = (left.getY() - right.getY());
      // double leftRightXdelta = (left.getX() - right.getX());
      // double a1 = leftRightXdelta == 0 ? 1 : leftRightYdelta /
      // leftRightXdelta;
      // double b1 = a1 == 0 ? 1 : left.getY() / (a1 * left.getX());
      //
      // double acYdelta = (a.getY() - c.getY());
      // double acXdelta = (a.getX() - c.getX());
      // double a2 = acXdelta == 0 ? 1 : acYdelta / acXdelta;
      // double b2 = a2 == 0 ? 1 : a.getY() / (a2 * a.getX());
      //
      // double x = (b2 - b1) / (a1 - a2);
      // double y = a1 * x + b1;
      
      float x = (a.getX() + c.getX()) / 2.0f;
      float y = (a.getY() + c.getY()) / 2.0f;
      float z = (a.getZ() + c.getZ()) / 2.0f;
      
      points.get(i).point.set(x, y, z);
   }
}