package br.uff.ic.jme.rotate;

public class Tform
{
    double e[][] = {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};

    Tform()
    {
    }

    Tform(double a00, double a01, double a02, double a03,
          double a10, double a11, double a12, double a13,
          double a20, double a21, double a22, double a23,
          double a30, double a31, double a32, double a33)
    {
	e[0][0] = a00; e[0][1] = a01; e[0][2] = a02; e[0][3] = a03;
	e[1][0] = a10; e[1][1] = a11; e[1][2] = a12; e[1][3] = a13;
	e[2][0] = a20; e[2][1] = a21; e[2][2] = a22; e[2][3] = a23;
	e[3][0] = a30; e[3][1] = a31; e[3][2] = a32; e[3][3] = a33;
    }

    Tform transpose()
    {
	Tform t = new Tform();
	for(int i=0; i<4; i++)
	    for(int j=0; j<4; j++)
		t.e[i][j] = e[j][i];
	return t;
    }

    Tform compose(Tform a)
    {
	Tform c = new Tform();
	for (int i = 0; i<4; i++)
	{
	    c.e[i][i] = 0;
	    for (int j = 0; j<4; j++)
		for (int k = 0; k<4; k++)
		    c.e[i][k] += e[i][j]*a.e[j][k];
	}
	return c;
    }


    Point apply(Point p)
    {
	double pp[] = {p.x,p.y,p.z,1};
	double tp[] = {0,0,0,0};
	for (int i=0; i<4; i++)
	    for (int j=0; j<4; j++)
		tp[i] += e[i][j]*pp[j];
	return new Point(tp[0]/tp[3],tp[1]/tp[3],tp[2]/tp[3]);
	/* if tp[3]<0 should flag somehow that this point is behind camera */
    }

    Path apply(Path p)
    {
	Path tp = new Path(p.len);
	for (int i=0; i<=p.len; i++)
	    tp.loc[i] = apply(p.loc[i]);
	return tp;
    }

    Vect apply(Vect v)
    {
	if (v==null)
	    return null;
	else
	    return new Vect(apply(v.first),apply(v.rest));
    }

    public String toString()
    {
	String out="{ ";
	for (int i = 0; i<4; i++)
	    for (int j = 0; j<4; j++)
		out = out + e[j][i] + " ";
	out = out + "}\n";
	return out;
    }

    static Tform trans(double x, double y, double z)
    { double xx[] = {x,y,z}; return trans(xx); }

    static Tform trans(double x[])
    {
	Tform t = new Tform();
	for (int i=0; i<3; i++)
	    t.e[i][3] = x[i];
	return t;
    }

    static Tform scale(double s)
    { return scale(s,s,s); }

    static Tform scale(double x, double y, double z)
    { double xx[] = {x,y,z}; return scale(xx); }

    static Tform scale(double x[])
    {
	Tform t = new Tform();
	for (int i=0; i<3; i++)
	    t.e[i][i] = x[i];
	return t;
    }

    static Tform perspective(double x)
    {
	Tform t = new Tform();
	t.e[3][2] = -x;
	return t;
    }

    static Tform zrot(double th)
    {
	Tform t = new Tform();
	t.e[0][0] = t.e[1][1] = Math.cos(th);
	t.e[0][1] = -(t.e[1][0] = Math.sin(th));
	return t;
    }

    static Tform yrot(double th)
    {
	Tform t = new Tform();
	t.e[0][0] = t.e[2][2] = Math.cos(th);
	t.e[2][0] = -(t.e[0][2] = Math.sin(th));
	return t;
    }

    static Tform xrot(double th)
    {
	Tform t = new Tform();
	t.e[1][1] = t.e[2][2] = Math.cos(th);
	t.e[1][2] = -(t.e[2][1] = Math.sin(th));
	return t;
    }
}
