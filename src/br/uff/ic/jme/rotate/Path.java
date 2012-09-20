package br.uff.ic.jme.rotate;

import java.awt.Graphics;

public class Path
{
    int len;
    Point loc[];

    Path(int n)
    { len = n; loc = new Point[len+1]; }

    Path(Point t, Point h)
    { len = 1; loc = new Point[2]; loc[0] = t; loc[1] = h; }

    Path(Point a, Point b, Point c)
    { len = 2; loc = new Point[3]; loc[0] = a; loc[1] = b; loc[2] = c; }

    Path(Point a, Point b, Point c, Point d)
    { len=3; loc=new Point[4]; loc[0]=a; loc[1]=b; loc[2]=c; loc[3]=d; }

    Path chop(int n)
    {
	Path p = new Path(n*len);
	for (int i=0; i<len; i++)
	    for (int j=0; j<n; j++)
		p.loc[n*i+j] = loc[i].interp(j,n,loc[i+1]);
	p.loc[n*len] = loc[len];
	return p;
    }

    void
    tosphere()
    {
	for (int i=0; i<=len; i++)
	    loc[i] = loc[i].tosphere();
    }

    public String toString()
    {
	String out = "";
	for (int i=0; i<=len; i++)
	    out = out + loc[i].toString();
	return out;
    }

    public void paint(Graphics g)
    {
	for (int i=0; i<len; i++)
	    loc[i].lineto(loc[i+1],g,2);
    }

    public void paint_front(Graphics g, double h)
    {
	for (int i=0; i<len; i++)
	    if (loc[i].z>=h)
		loc[i].lineto(loc[i+1],g,2);
    }

    public void paint_back(Graphics g, double h)
    {
	for (int i=0; i<len; i++)
	    if (loc[i].z<=h)
		loc[i].lineto(loc[i+1],g,2);
    }
}
