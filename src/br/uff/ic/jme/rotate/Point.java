package br.uff.ic.jme.rotate;

import java.awt.Graphics;

public class Point
{
    double x,y,z;

    Point()
    { x = 0; y = 0; z = 0; }

    Point(double u, double v, double w)
    { x = u; y = v; z = w; }

    Point scalar_mult(double r)
    { return new Point(r*x,r*y,r*z); }

    Point neg()
    { return scalar_mult(-1); }

    Point sum(Point p)
    { return new Point(x+p.x,y+p.y,z+p.z); }

    Point cross(Point p)
    { return new Point(y*p.z-z*p.y, z*p.x-x*p.z, x*p.y-y*p.x); }

    double dot(Point p)
    { return  x*p.x + y*p.y + z*p.z; }

    Point tosphere()
    {
	double norm = Math.sqrt(dot(this));
	x = x/norm; y = y/norm; z = z/norm;
	return this;
    }

    Point interp(double t, double s, Point p)
    {
	return scalar_mult(1-t/s).sum(p.scalar_mult(t/s));
    }

    public String toString()
    { return " " + x + " " + y + " " + z + "\n"; }

    public void lineto(Point p, Graphics g, int width)
    {
	int start = width/2;
	for (int i=-start; i<width-start; i++)
	    for (int j=-start; j<width-start; j++)
		g.drawLine((int)x+i,(int)y+j, (int)p.x+i,(int)p.y+j);
	
    }
}
