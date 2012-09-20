package br.uff.ic.jme.rotate;

public class Poly
{

    static Vect cube = init_cube(), oct = init_oct(),
                dodec = init_dodec(), icos = init_icos(), tetr = init_tetr(); 

    static Vect init_cube()
    {
	Path p1 = new Path(new Point(-1,-1,-1), new Point(1,-1,-1),
			   new Point(1,-1,1), new Point(1,1,1));
	Path p2 = new Path(p1.loc[1], new Point(1,1,-1),
	    p1.loc[3], p1.loc[1].neg());
	Path p3 = new Path(p2.loc[1], new Point(-1,1,-1),
	    p2.loc[3], p2.loc[1].neg());
	Path p4 = new Path(p3.loc[1], new Point(-1,-1,-1),
	    p3.loc[3], p3.loc[1].neg());

	return new Vect(p1, new Vect(p2, new Vect(p3, new Vect(p4))));
    }

    static Vect init_oct()
    {
	Point   xp = new Point(1,0,0), xm = new Point(-1,0,0),
		yp = new Point(0,1,0), ym = new Point(0,-1,0),
		zp = new Point(0,0,1), zm = new Point(0,0,-1);
	Path p = new Path(12);
	p.loc[0] = xp; p.loc[1] = yp; p.loc[2] = zp; p.loc[3] = xp;
	p.loc[4] = ym; p.loc[5] = zp; p.loc[6] = xm; p.loc[7] = ym;
	p.loc[8] = zm; p.loc[9] = xm; p.loc[10]= yp; p.loc[11]= zm;
	p.loc[12]= xp;

	return new Vect(p);
    }

    static Vect init_dodec()
    {
	double tau = (Math.sqrt(5)+1)/2;
	Point ppp = new Point(1,1,1), ppm = new Point(1,1,-1),
	      pmp = new Point(1,-1,1), pmm = new Point(1,-1,-1);
	Point mpp = pmm.neg(), mpm = pmp.neg(),
	      mmp = ppm.neg(), mmm = ppp.neg();
	Point opp = new Point(0,tau-1,tau), ppo = new Point(tau-1,tau,0),
		pop = new Point(tau,0,tau-1);
	Point opm = new Point(0,tau-1,-tau), pmo = new Point(tau-1,-tau,0),
		mop = new Point(-tau,0,tau-1);
	Point omm = opp.neg(), mmo = ppo.neg(), mom = pop.neg();
	Point omp = opm.neg(), mpo = pmo.neg(), pom = mop.neg();

	Path p1 = new Path(pop,pmp,pmo,mmo);
	Path p2 = new Path(pmp,omp,mmp,mop);
	Path p3 = new Path(omp,opp,mpp,mpo);
	Path p4 = new Path(opp,ppp,ppo,ppm);
	Path p5 = new Path(ppp,pop,pom,pmm);

	Path m1 = new Path(mom,mpm,mpo,ppo);
	Path m2 = new Path(mpm,opm,ppm,pom);
	Path m3 = new Path(opm,omm,pmm,pmo);
	Path m4 = new Path(omm,mmm,mmo,mmp);
	Path m5 = new Path(mmm,mom,mop,mpp);

	return new Vect(p1, new Vect(p2, new Vect(p3, new Vect(p4, new Vect(p5,
	    new Vect(m1, new Vect(m2, new Vect(m3, new Vect(m4, new Vect(m5)))))
	    )))));
    }

    static Vect init_icos()
    {
	double tau = (Math.sqrt(5)+1)/2;
	Point pop = new Point(2+tau, 0,1+3*tau), opp = new Point(0,1+3*tau, 2+tau), ppo = new Point(1+3*tau, 2+tau,0);
	Point mop = new Point(-2-tau,0,1+3*tau), opm = new Point(0,1+3*tau,-2-tau), pmo = new Point(1+3*tau,-2-tau,0);
	Point omm = opp.neg(), mmo = ppo.neg(), mom = pop.neg();
	Point omp = opm.neg(), mpo = pmo.neg(), pom = mop.neg();

	Path p2 = new Path(24);
	p2.loc[0] = ppo; p2.loc[1] = pom; p2.loc[2] = pmo; p2.loc[3] = omm;
	p2.loc[4] = pom; p2.loc[5] = opm; p2.loc[6] = mom; p2.loc[7] = mpo;
	p2.loc[8] = opm; p2.loc[9] = ppo; p2.loc[10]= opp; p2.loc[11]= pop;
	p2.loc[12]= pmo; p2.loc[13]= omp; p2.loc[14]= mmo; p2.loc[15]= mom;
	p2.loc[16]= omm; p2.loc[17]= mmo; p2.loc[18]= mop; p2.loc[19]= opp;
	p2.loc[20]= mpo; p2.loc[21]= mop; p2.loc[22]= omp; p2.loc[23]= pop;
	p2.loc[24] = ppo;

	Path p3 = new Path(pop,mop); Path p4 = new Path(opp,opm);
	Path p5 = new Path(ppo,pmo); Path p6 = new Path(omm,omp);
	Path p7 = new Path(mmo,mpo); Path p8 = new Path(mom,pom);

	return new Vect(p2, new Vect(p3, new Vect(p4, new Vect(p5,
		 new Vect(p6, new Vect(p7, new Vect(p8)))))));
    }

    static Vect init_tetr()
    {
	Path p1 = new Path(new Point(-1,-1,-1),
	    new Point(-1,1,1), new Point(1,-1,1), new Point(1,1,-1));
	Path p2 = new Path(3);
	for (int i=0; i<4; i++)
	    p2.loc[i] = p1.loc[((i&1)<<1) | (1-((i&2)>>1))];

	return new Vect(p1, new Vect(p2));
    }
}
