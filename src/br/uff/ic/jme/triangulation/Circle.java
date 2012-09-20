package br.uff.ic.jme.triangulation;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class Circle
{
   
   public Vector3f center;
   
   public float radius;
   
   public Circle(Vector3f center, float radius)
   {
      this.center = center;
      this.radius = radius;
   }
   
   public Circle(Vector3f a, Vector3f b, Vector3f c)
   {
      float x1 = a.x, y1 = a.y, x2 = b.x, y2 = b.y, x3 = c.x, y3 = c.y;
      float s = 0.5f * ((x2 - x3) * (x1 - x3) - (y2 - y3) * (y3 - y1));
      float sUnder = (x1 - x2) * (y3 - y1) - (y2 - y1) * (x1 - x3);
      
      s /= sUnder;
      
      float xc = 0.5f * (x1 + x2) + s * (y2 - y1); // center x coordinate
      float yc = 0.5f * (y1 + y2) + s * (x1 - x2); // center y coordinate
      center = new Vector3f(xc, yc, a.z);
      
      float deltaX = a.x - center.x;
      float deltaY = a.y - center.y;
      radius = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
      // fix the approximation error
      radius = (float) Math.ceil(radius);
   }
   
//   public Circle(TriangulationPoint a, TriangulationPoint b, TriangulationPoint c)
//   {
//      this(new Point(a.getXf(), a.getYf()), new Point(b.getXf(), b.getYf()), new Point(c.getXf(), c.getYf()));
//   }
//   
//   public boolean contains(Point p)
//   {
//      float deltaX = p.x - center.x;
//      float deltaY = p.y - center.y;
//      return deltaX * deltaX + deltaY * deltaY > radius * radius ? false : true;
//   }
//   
//   public static void main(String[] args)
//   {
//      Point p1 = new Point(10, 20);
//      Point p2 = new Point(50, 30);
//      Point p3 = new Point(20, 40);
//      Circle c = new Circle(p1, p2, p3);
//      boolean b = c.contains(new Point(10, 20));
//      // System.out.println(b + " r: " + c.radius + " c: " + c.center );
//   }
   
}
