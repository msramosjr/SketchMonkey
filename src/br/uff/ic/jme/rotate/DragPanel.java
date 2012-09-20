package br.uff.ic.jme.rotate;

import java.awt.*;
import java.lang.*;
import java.text.NumberFormat;

public class DragPanel extends Panel
{
    Tform world , camera, incr;
    Point base, arwdir;
    double zoom = 2.4, arrowlen = .5;
    Image sph_img;

    String errmsg;

    Image back; Graphics bg; Dimension bsize = size();

    int mode = 0;
    boolean busy = false;
    Color oct_col = new Color(.4f,.0f,.2f),
	  arw_col = new Color(.7f,.5f,.0f),
	  tri_col = new Color(.0f,.4f,.6f),
	  sav_col = new Color(.1f,.1f,.2f);
    NumberFormat degfmt;

    Vect octa, arrow, tri, svarw;
    Point triverts[] = new Point[3]; int ntri;

    double lastx, lasty;

    public DragPanel(Image s)
    { sph_img = s; }


    public void init()
    {
	Vect polyh = Poly.oct; polyh.tosphere();
	octa=polyh.chop(20); octa.tosphere();
	degfmt = NumberFormat.getNumberInstance();
	degfmt.setMaximumFractionDigits(1);
	degfmt.setMinimumFractionDigits(1);
	reset();
    }

    public String degs( double radians ) {
	return degfmt.format( radians * 180.0/Math.PI );
    }

    public static String pad( String s, int wantlength ) {
	if(s.length() < wantlength) {
	    return "          ".substring(0, wantlength - s.length()) + s;
	} else {
	    return s;
	}
    }

    public void save_arrow()
    {
	svarw = arrow;
    }

    public void reset()
    {
	world = Tform.xrot(.2).compose(Tform.yrot(.4));
	base = new Point(0.,0.,1.); arwdir = new Point(arrowlen,0.,0.);
	movearrow(base);
	ntri = 0; svarw = null;
	repaint();
    }

    public void setmode(int m)
    { mode = (m>=0 && m<=2)? m : 0; }

    void addtri(Point p)
    {
	if (++ntri > 3) ntri=1;
	changetri(p);
    }

    void changetri(Point p)
    {
	if (ntri==0) return; // should never get here
	triverts[ntri-1] = p;
	Path t = new Path(ntri);
	for (int i = 0; i<ntri; i++)
	    t.loc[i]=triverts[i];
	t.loc[ntri]=t.loc[0];
	t = t.chop(2); t.tosphere();  // do two stages in case antipodal
	tri = new Vect(t.chop(10)); tri.tosphere();
    }

    double [] tri_angles()
    {
	if(ntri < 3) return null;
	int i;
	Point dual[] = new Point[3];
	for(i = 0; i < 3; i++) {
	    dual[i] = triverts[(i+1)%3].cross( triverts[(i+2)%3] );
	    dual[i] = dual[i].tosphere();
	}
	double angles[] = new double[3];
	for(i = 0; i < 3; i++) {
	    Point a = dual[(i+1)%3];
	    Point b = dual[(i+2)%3];
	    Point axb = a.cross(b);
	    double s = Math.sqrt( axb.dot(axb) );
	    double c = a.dot(b);
	    angles[i] = Math.PI - Math.atan2( s, c );
	}
	return angles;
    }

    public boolean mouseDown(Event e, int x, int y)
    {
        int b=0;
	if ((e.modifiers&Event.ALT_MASK)!=0) b=1;
	else if ((e.modifiers&Event.META_MASK)!=0) b=2;
        b = (b+mode)%3;

        if (b==1) { lastx = x; lasty = y; return true; }
	else if (b==2) addtri(invert(x,y));
	else /*if (b==0)*/ movebase(invert(x,y));
	repaint();
	return true;
    }

    public boolean mouseDrag(Event e, int x, int y)
    {
        System.out.println("OI");
        int b=0;
	if ((e.modifiers&Event.ALT_MASK)!=0) b=1;
	else if ((e.modifiers&Event.META_MASK)!=0) b=2;
        b = (b+mode)%3;

	if (b==1)
	{
	    double thx = (x-lastx) * 6.283 / size().width;
	    double thy = (y-lasty) * 6.283 / size().width;

	    world = Tform.xrot(thy).compose(Tform.yrot(thx)).compose(world);

	    lastx = x; lasty = y;
	}
	//else if (b==2)
	//    changetri(invert(x,y));
	//else /* if (b==0) */
	//    movebase(invert(x,y));

	repaint();
	return true;
    }

    public void update(Graphics g)
    {
	if (!busy)
	    { busy = true; paint(g); }
        
        System.out.println("UPDATE");
    }

    public void paint(Graphics g)
    {
	if (back == null ||
	    bsize.width!=size().width || bsize.height!=size().height)
	{
	    back = createImage(size().width, size().height);
	    bsize = size();
	    bg = back.getGraphics();
	    setcamera();
	}
	if (back != null)
	{
	    dopaint(bg);
	    g.drawImage(back,0,0,this);
	}
	else
	    dopaint(g);
	done();
    }

    Point invert(double x, double y)
    {
	double z,r;
	x = zoom*(x/size().width-0.5);
	y = zoom*(-(y-size().height/2)/size().width);
	if ((r = x*x+y*y) > 1)
	    { r = Math.sqrt(r); x /= r; y /= r; r = 1; }
	z = Math.sqrt(1-r);
	return world.transpose().apply(new Point(x,y,z));
    }

    void movebase(Point p)
    {
	p = p.scalar_mult(1/Math.sqrt(p.dot(p)));
	if (base.dot(p) < .1)
	{
	    movebase(base.sum(p));
	    movebase(p);
	}
	else
	{
	    movearrow(p);
	    base = p;
	}
    }

    void movearrow(Point p)
    {
	Point side;
	double lambda = - arwdir.dot(p) / (1 + base.dot(p));
	arwdir = arwdir.sum((base.sum(p)).scalar_mult(lambda));
/*	arwdir = arwdir.scalar_mult(arrowlen/Math.sqrt(arwdir.dot(arwdir)));*/
	side = p.cross(arwdir).scalar_mult(.2);
	Point tip = p.sum(arwdir);
	Path p1 = new Path(p.sum(arwdir.scalar_mult(.7)).sum(side),
		    tip, p.sum(arwdir.scalar_mult(.7)).sum(side.neg()));
	Path p2 = new Path(p,tip);
	Path p3 = new Path(p.sum(side),p.sum(side.neg()));
	arrow = new Vect(p1, new Vect(p2, new Vect(p3)));
    }

    public void setcamera()
    {
	double s = size().width/zoom;
        camera =  Tform.trans(size().width/2,size().height/2,0).
	  compose(Tform.scale(s,s,1)).
	  compose(Tform.scale(1,-1,1));
	  //compose(Tform.perspective(prsp));
    }

    public void dopaint(Graphics g)
    {
	Tform t1 = camera.compose(world);
	g.setColor(Color.white);
	g.fillRect(0,0,size().width,size().height);

	g.setColor(oct_col); t1.apply(octa).paint_back(g,0);
	g.setColor(tri_col); if (ntri > 0) t1.apply(tri).paint_back(g,0);
	g.setColor(sav_col); if (svarw!=null) t1.apply(svarw).paint_back(g,0);
	g.setColor(arw_col); t1.apply(arrow).paint_back(g,0);
	int sphsize = (int)(size().width/zoom);
	g.drawImage(sph_img,size().width/2-sphsize,size().height/2-sphsize,
				2*sphsize,2*sphsize,this);
	g.setColor(oct_col); t1.apply(octa).paint_front(g,0);
	g.setColor(tri_col); if (ntri > 0) t1.apply(tri).paint_front(g,0);
	g.setColor(sav_col); if (svarw!=null) t1.apply(svarw).paint_front(g,0);
	g.setColor(arw_col); t1.apply(arrow).paint_front(g,0);

	double ang[] = tri_angles();
	if(ang != null) {
	    g.setColor(tri_col);
	    g.drawString( pad(degs(ang[0]),6) + " + "
			+ pad(degs(ang[1]),6) + " + "
			+ pad(degs(ang[2]),6) + " = 180 + "
			+ degs( ang[0]+ang[1]+ang[2] - Math.PI ),
			5, size().height - 5 );
	}
    }

    private synchronized void done()
    {
	busy = false;
	notifyAll();
    }
}
