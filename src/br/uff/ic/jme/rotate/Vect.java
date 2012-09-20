package br.uff.ic.jme.rotate;

import java.awt.Graphics;

public class Vect
{
    Vect rest;
    Path first;

    Vect(Path p)
    { first = p; rest = null; }

    Vect(Path p, Vect v)
    { first = p; rest = v; }

    Vect chop(int n)
    {
	if (rest != null)
	    return new Vect(first.chop(n), rest.chop(n));
	else
	    return new Vect(first.chop(n));
    }

    int npath()
    { return (rest!=null)? 1 + rest.npath() : 1; }

    int tlen()
    { return (rest!=null)? first.len + rest.tlen() : first.len; }

    void tosphere()
    { first.tosphere(); if (rest!=null) rest.tosphere(); }

    public String toString()
    {
	String out = "{ VECT " + npath() + " " + (tlen()+npath()) + " 0\n";
	for (Vect vv=this; vv!=null; vv=vv.rest)
	    out = out + (vv.first.len+1) + " ";
	out = out + "\n";
	for (Vect vv=this; vv!=null; vv=vv.rest)
	    out = out + "0 ";
	out = out + "\n";
	for (Vect vv=this; vv!=null; vv=vv.rest)
	    out = out + vv.first.toString();
	out = out + "}\n";
	return out;
    }

    public void paint(Graphics g)
    { first.paint(g); if (rest!=null) rest.paint(g); }

    public void paint_front(Graphics g, double h)
    { first.paint_front(g,h); if (rest!=null) rest.paint_front(g,h); }

    public void paint_back(Graphics g, double h)
    { first.paint_back(g,h); if (rest!=null) rest.paint_back(g,h); }

}
