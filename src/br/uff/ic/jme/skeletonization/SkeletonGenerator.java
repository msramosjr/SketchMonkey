package br.uff.ic.jme.skeletonization;

import java.util.List;

import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

import br.uff.ic.jme.exception.SkeletonizationException;
import br.uff.ic.jme.main.Circle;
import br.uff.ic.jme.main.SceneManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;
import java.util.ArrayList;

public class SkeletonGenerator
{

    private List<DelaunayTriangle> triangles;
    private List<Skeleton> finishedSkeletons;
    private Skeleton skeleton;
    private int numCircumcenters = 0;
    private int jTriangles = 0;
    private int tTriangles = 0;
    private int iOverlapping;

    //private Map<DelaunayTriangle, List<SkeletonBranch>> intersections;
    public Skeleton getSkeleton()
    {
        if (skeleton == null)
        {
            skeleton = new Skeleton();
        }
        return skeleton;
    }
    
    public List<Skeleton> getFinishedSkeletons()
    {
        if (finishedSkeletons == null)
        {
            finishedSkeletons = new ArrayList<Skeleton>();
        }
        return finishedSkeletons;
    }
        
    public SkeletonGenerator(Skeleton skeleton)
    {
        this.skeleton = skeleton;
    }

    public SkeletonGenerator(List<DelaunayTriangle> triangles) throws SkeletonizationException
    {
        this.triangles = triangles;
        skeleton = new Skeleton();
        //intersections = new HashMap<DelaunayTriangle, List<SkeletonBranch>>();
    }
    
    public void finishSkeleton()
    {
        if(finishedSkeletons == null)
            finishedSkeletons = new ArrayList<Skeleton>();
        finishedSkeletons.add(skeleton);
        clearSkeleton();
    }

    public void clearSkeleton()
    {
        skeleton = new Skeleton();
    }

    public void setTriangles(List<DelaunayTriangle> triangles)
    {
        this.triangles = triangles;
    }
    
//   public void generateSkeleton()
//   {
//      for (DelaunayTriangle tri : triangles)
//      {
//         // clear visited status
//         tri.clearVisited();
//         
//         // clear invalid neighbor pointers
//         for (int i = 0; i < 3; i++)
//         {
//            if (!triangles.contains(tri.neighbors[i]))
//               tri.neighbors[i] = null;
//         }
//      }
//      
//      // System.out.println("begin skeleton generation");
//      DelaunayTriangle triangle = getTTriangle();      
//      // System.out.println("initial triangle T");
//      while (true)
//      {
//         SkeletonBranch branch = generateSkeletonBranch(triangle);
//         
////         if(intersections.containsKey(triangle))
////         {
////             intersections.get(triangle).add(branch);
////         }
////         else
////         {
////             intersections.put(triangle, new ArrayList<SkeletonBranch>());
////             intersections.get(triangle).add(branch);
////         }
//         
//         if (branch.points.size() > 1)
//            skeleton.branches.add(branch);
//         triangle = getTTriangle();
//         if (triangle == null)
//         {
//            triangle = getJTriangle();
//            if(triangle == null)
//                break;
//         }
//         // System.out.println("initial triangle " + (triangle.isTtriangle() ?
//         // "T" : "J"));
//      }
//      // System.out.println("end skeleton generation");
//      
//      // raiseSkeleton();
//      //pruneSkeleton2();
//      
//      smoothSkeleton();
//      // retriangulate();
//   }
    public void generateSkeleton2()
    {
        //cleaning up all triangles
        for (DelaunayTriangle tri : triangles)
        {
            // clear visited status
            tri.clearVisited();

            // clear invalid neighbor pointers
            for (int i = 0; i < 3; i++)
            {
                if (!triangles.contains(tri.neighbors[i]))
                {
                    tri.neighbors[i] = null;
                }
            }
        }

//        System.out.println("begin skeleton generation");
        DelaunayTriangle triangle = getInitialTriangle();
//        System.out.println("initial triangle J");
        while (true)
        {
//            System.out.println("begin branch");
            SkeletonBranch branch = generateSkeletonBranch2(triangle);
//            System.out.println("end branch");

            if (branch.points.size() > 1)
            {
                skeleton.branches.add(branch);
            }
            triangle = getInitialTriangle();
            if (triangle == null)
            {
                break;
            }
            //System.out.println("initial triangle J");
            // System.out.println("initial triangle " + (triangle.isTtriangle() ?
            // "T" : "J"));
        }
//        System.out.println("triangles: " + triangles.size());
//        System.out.println("J-triangles: " + jTriangles);
//        System.out.println("T-triangles: " + tTriangles);
//        System.out.println("circumcenters: " + numCircumcenters);
        numCircumcenters = 0;
        jTriangles = 0;
        tTriangles = 0;
//        System.out.println("end skeleton generation");
        
        finishPruning();
        
        //smoothSkeleton2();
    }

    public SkeletonBranch generateSkeletonBranch2(DelaunayTriangle originTriangle)
    {
        // System.out.println("begin skeleton branch");
        SkeletonBranch branch = new SkeletonBranch();

        SkeletonPoint originPoint = getSkeletonPoint2(originTriangle, null);

        DelaunayTriangle lastTriangle = null;
        DelaunayTriangle triangle = originTriangle;
        SkeletonPoint point = originPoint;

        if(triangle.isJtriangle())
        {
            point.startBranches.add(branch);
        }
        
        boolean isFirstTriangle = true;
        while (true)
        {
//            System.out.println("triangle: " + triangle);
//            System.out.println("point: " + point);

            branch.points.add(point);

            lastTriangle = triangle;
            triangle = getNextTriangle(triangle, isFirstTriangle);
            
            if (triangle == null)
            {
                break;
            }

            //prune skeleton points
            pruneSkeletonPoints(branch, lastTriangle, triangle);

            point = getSkeletonPoint2(triangle, point);
            
            //if is last in branch and is J
            if(!isFirstTriangle && triangle.isJtriangle())
            {
                point.endBranches.add(branch);
            }

            isFirstTriangle = false;
        }

        // System.out.println("end skeleton branch");
        return branch;
    }

    private void pruneSkeletonPoints(SkeletonBranch branch, DelaunayTriangle lastTriangle, DelaunayTriangle currentTriangle)
    {
        // Tai et al. prune method
        /****** prune step *******/
        int size = branch.points.size();

        if (size < 3)
        {
//            if(size == 2)
//            {
//                if(branch.points.get(0).divisionFactor == 3 && branch.points.get(1).divisionFactor == 1)
//                {
//                    branch.points.remove(1);
//                    SkeletonPoint point = branch.points.get(0);
//                    for (SkeletonBranch b : point.startBranches)
//                    {
//                        b.points.get(0).divisionFactor--;                        
//                    }
//                    for (SkeletonBranch b : point.endBranches)
//                    {
//                        b.points.get(b.points.size() - 1).divisionFactor--;                        
//                    }                    
//                    System.out.println("removed by irrelevance");
//                }
//            }
            return;
        }

        SkeletonPoint p = branch.points.get(size - 3);
        SkeletonPoint q = branch.points.get(size - 2);
        SkeletonPoint r = branch.points.get(size - 1);

        //prune criteria
        if (p != null && q != null && r != null)
        {
            Vector3f p_point = p.point;
            Vector3f q_point = q.point;
            Vector3f r_point = r.point;
            
//            if(currentTriangle.isTtriangle())
//            {
//                branch.points.remove(r);
//                return;
//            }

//            if(currentTriangle.isStriangle())
//            {
                //1) overlapping criterion
//                float tau = 0.9f;
//                float d = q_point.distance(r_point);
//                float r2 = r.radius + q.radius;
//                if (d <= r2 * tau)
//                {
//                    System.out.println("pruned by overlapping criterion: " + iOverlapping++);
//                    //if(r.radius <= q.radius)
//                        branch.points.remove(r);
//                    //else
//                    //    branch.points.remove(q);
//                    return;
//                }
                
//                else if (q_point.distance(r_point) + tau * q.radius <= r.radius)
//                {
//                    System.out.println("pruned by overlapping criterion: " + iOverlapping++);
//                    branch.points.remove(q);
//                    return;
//                }
                
                float tau = 0.2f;
                if (q_point.distance(r_point) + tau * r.radius <= q.radius)
                {
//                    System.out.println("pruned by overlapping criterion: " + iOverlapping++);
                    branch.points.remove(r);
                    return;
                }
//                else if (q_point.distance(r_point) + tau * q.radius <= r.radius)
//                {
//                    System.out.println("pruned by overlapping criterion: " + iOverlapping++);
//                    branch.points.remove(q);
//                    return;
//                }

                //2) ordering criterion            
//                Vector3f A = null;
//                Vector3f B = null;
//                Vector3f C = null;
//                q_point = q.circumcenter;
//                r_point = r.circumcenter;
//
//                //direct iteration returns points in CCW orientation
//                for (int i = 0; i < currentTriangle.points.length; i++)
//                {
//                    TriangulationPoint TP = currentTriangle.points[i];
//                    if (lastTriangle.contains(TP))
//                    {
//                        TriangulationPoint TPCW = currentTriangle.pointCW(TP);
//                        TriangulationPoint TPCCW = currentTriangle.pointCCW(TP);
//                        if (lastTriangle.contains(TPCW))
//                        {
//                            A = new Vector3f(TP.getXf(), TP.getYf(), TP.getZf());
//                            B = new Vector3f(TPCW.getXf(), TPCW.getYf(), TPCW.getZf());
//                            C = new Vector3f(TPCCW.getXf(), TPCCW.getYf(), TPCCW.getZf());
//                        }
//                    }
//                }
//
//                Vector3f AB = B.subtract(A).normalize(); //common edge
//                Vector3f AC = C.subtract(A).normalize(); //other edge                        
//                Vector3f n = AB.cross(AC.cross(AB).normalize()).normalize();
//
//                //verify criterion
//                if (r_point.subtract(q_point).dot(n) <= 0)
//                {
//                    System.out.println("pruned by ordering criterion");
//                    branch.points.remove(r);
//                    return;
//                }
//            }
            
            //3) proportionality criterion
//            if (((q - p).(r - q)) / (||q - p||*||r - q||)) >= cos(PI/18)
//            and if -0.1 < [(R_q - R_p) / (R_r - R_p)] - [(||q - p||/||r - p||) < 0.1]
//            p_point = p.point;
//            q_point = q.point;
//            r_point = r.point;
            
//            if(!lastTriangle.isJtriangle())
//            {
//                Vector3f pq = q_point.subtract(p_point);
//                Vector3f qr = r_point.subtract(q_point);
//                Vector3f pr = r_point.subtract(p_point);
//
//                boolean crit1 = pq.dot(qr) / (pq.length() * qr.length()) >= FastMath.cos2(FastMath.PI / 18f);
//                float expr = ((q.radius - p.radius) / (r.radius - p.radius)) - (pq.length() / pr.length());
//                boolean crit2 = expr > -0.1f;
//                boolean crit3 = expr < 0.1f;
//                if (crit1 && crit2 && crit3)
//                {
//                    System.out.println("pruned by proportionality criterion");
//                    branch.points.remove(q);
//                    return;
//                }
//            }
        }
    }

    private DelaunayTriangle getNextTriangle(DelaunayTriangle tri, boolean isFirst)
    {
        if (tri == null || tri.neighbors == null)
        {
            return null;
        }

        if (!isFirst && !tri.isStriangle())
        {
            return null;
        }

        for (int i = 0; i < tri.neighbors.length; i++)
        {
            if (tri.neighbors[i] != null && tri.visitedNeighbors[i] == false)
            {
                tri.visitedNeighbors[i] = true;
                DelaunayTriangle neighbor = tri.neighbors[i];
                int index = neighbor.index(neighbor.oppositePoint(tri, tri.points[i]));
                neighbor.visitedNeighbors[index] = true;
                return tri.neighbors[i];
            }
        }

        return null;
    }

    private SkeletonPoint getSkeletonPoint2(DelaunayTriangle tri, SkeletonPoint point)
    {

        if (tri == null)
        {
            return null;
        }

        TriangulationPoint A = tri.points[0];
        TriangulationPoint B = tri.points[1];
        TriangulationPoint C = tri.points[2];
        Vector3f P1 = new Vector3f(A.getXf(), A.getYf(), A.getZf());
        Vector3f P2 = new Vector3f(B.getXf(), B.getYf(), B.getZf());
        Vector3f P3 = new Vector3f(C.getXf(), C.getYf(), C.getZf());

        Vector3f circumcenter = circumcenter(P1, P2, P3);
        Vector3f barycenter = barycenter(P1, P2, P3);
        Vector3f incenter = incenter(P1, P2, P3);
        Vector3f obtuseCenter = obtusecenter(tri);

        numCircumcenters++;

        float circumradius = P1.distance(circumcenter);
        
//        Vector3f P1P2 = P2.subtract(P1).normalize();
//        Vector3f P1P3 = P3.subtract(P1).normalize();
//        Vector3f triNormal = P1P2.cross(P1P3).normalize();

        //debug circumcircle
        Material mat = new Material(SceneManager.get().assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        mat.getAdditionalRenderState().setWireframe(true);

        Material mat2 = new Material(SceneManager.get().assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Blue);
        mat2.getAdditionalRenderState().setWireframe(true);

        Circle circle = new Circle(circumcenter, circumradius, 30);
        Geometry geomCircle = new Geometry("circumcircle_" + tri.hashCode(), circle);
        //geomCircle.lookAt(P1.cross(P2).normalize(), Vector3f.UNIT_Y);
        geomCircle.setMaterial(mat2);

        Circle circumcenterPoint = new Circle(circumcenter, 0.05f, 5);
        Geometry geomCircumcenter = new Geometry("circumcenter_" + tri.hashCode(), circumcenterPoint);
        geomCircumcenter.setMaterial(mat);
//        Vector3f translation = geomCenter.getLocalTranslation();
//        geomCenter.setLocalTranslation(Vector3f.ZERO);
//        new Quaternion().lookAt(triNormal, Vector3f.UNIT_Y);
//        geomCenter.lookAt(circumcenter.add(triNormal), P1P2);
//        geomCenter.setLocalTranslation(translation);
        

        Circle barycenterPoint = new Circle(barycenter, 0.05f, 5);
        Geometry geomBarycenter = new Geometry("barycenter_" + tri.hashCode(), barycenterPoint);
        //geomBarycenter.lookAt(P1.cross(P2).normalize(), Vector3f.UNIT_Y);
        geomBarycenter.setMaterial(mat2);

        Line line1 = new Line(P1, P2);
        Line line2 = new Line(P2, P3);
        Line line3 = new Line(P3, P1);

        Geometry geomLine1 = new Geometry("line_1" + tri.hashCode(), line1);
        Geometry geomLine2 = new Geometry("line_2" + tri.hashCode(), line2);
        Geometry geomLine3 = new Geometry("line_3" + tri.hashCode(), line3);
        geomLine1.setMaterial(mat);
        geomLine2.setMaterial(mat);
        geomLine3.setMaterial(mat);

        Circle incenterPoint = new Circle(incenter, 0.05f, 5);
        Geometry geomIncenter = new Geometry("obtusecenter_" + tri.hashCode(), incenterPoint);
        geomIncenter.setMaterial(mat2);

        if(obtuseCenter != null)
        {
            Circle obtuseCenterPoint = new Circle(obtuseCenter, 0.05f, 5);
            Geometry geomObtuseCenter = new Geometry("obtusecenter_" + tri.hashCode(), obtuseCenterPoint);
            geomObtuseCenter.setMaterial(mat2);
            //SceneManager.get().getDrawingNode().attachChild(geomObtuseCenter);
        }
        
        //SceneManager.get().getDrawingNode().attachChild(geomIncenter);
        //SceneManager.get().getDrawingNode().attachChild(geomCircumcenter);
        //SceneManager.get().getDrawingNode().attachChild(geomRadius);
        //SceneManager.get().getDrawingNode().attachChild(geomBarycenter);
        //SceneManager.get().getDrawingNode().attachChild(geomCircle);
        //SceneManager.get().getDrawingNode().attachChild(geomLine1);
        //SceneManager.get().getDrawingNode().attachChild(geomLine2);
        //SceneManager.get().getDrawingNode().attachChild(geomLine3);
        //-------------------

        Vector3f skPoint = null;        
        int divisionFactor = 0;
        if (tri.isTtriangle())
        {
            skPoint = incenter;
            divisionFactor = 1;
        } else if (tri.isStriangle())
        {
            skPoint = obtuseCenter;            
            divisionFactor = 2;
        } else if (tri.isJtriangle())
        {
            skPoint = obtuseCenter;
            divisionFactor = 3;
        }
        
        if(skPoint == null)
        {
            skPoint = circumcenter;
        }
        
        float radius = Math.min(Math.min(skPoint.distance(P1), skPoint.distance(P2)), skPoint.distance(P3));
        
        SkeletonPoint sp = new SkeletonPoint(skPoint, radius, divisionFactor, circumcenter, circumradius);
        
        Circle skeletonPoint = new Circle(skPoint, 0.05f, 5);
        Geometry geomSkPoint = new Geometry("obtusecenter_" + tri.hashCode(), skeletonPoint);
        geomSkPoint.setMaterial(mat2);
        //SceneManager.get().getDrawingNode().attachChild(geomSkPoint);
        
        Circle skeletonPoint2 = new Circle(skPoint, radius, 30);
        Geometry geomSkPoint2 = new Geometry("obtusecircle_" + tri.hashCode(), skeletonPoint2);
        geomSkPoint2.setMaterial(mat2);
        //SceneManager.get().getDrawingNode().attachChild(geomSkPoint2);

        if (point != null)
        {
            point.addNeighbor(sp);
            sp.addNeighbor(point);
        }
        return sp;

    }
    
    private void finishPruning()
    {
        if(skeleton != null)
        {
            for (SkeletonBranch branch : skeleton.branches)
            {   
                if(branch.points.size() >= 2)
                {
                    float radius = branch.points.get(0).radius;
                    float length = 0f;
                    Vector3f lastPoint = null;
                    float firstSegment = branch.points.get(1).point.distance(branch.points.get(0).point);
                    for (SkeletonPoint point : branch.points)
                    {   
                        if(lastPoint != null)
                        {
                            length += lastPoint.distance(point.point);
                        }
                        
                        lastPoint = point.point;
                        
//                        Material mat2 = new Material(SceneManager.get().assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//                        mat2.setColor("Color", ColorRGBA.Red);
//                        mat2.getAdditionalRenderState().setWireframe(true);
//
//                        Circle skeletonPoint = new Circle(point.point, 0.05f, 5);
//                        Geometry geomSkPoint = new Geometry("obtusecenter_" + point.hashCode(), skeletonPoint);
//                        geomSkPoint.setMaterial(mat2);
//                        SceneManager.get().getDrawingNode().attachChild(geomSkPoint);
//
//                        Circle skeletonPoint2 = new Circle(point.point, point.radius, 30);
//                        Geometry geomSkPoint2 = new Geometry("obtusecircle_" + point.hashCode(), skeletonPoint2);
//                        geomSkPoint2.setMaterial(mat2);
//                        SceneManager.get().getDrawingNode().attachChild(geomSkPoint2);
                    }
                    length -= firstSegment;
                    
                    double phi = SceneManager.get().threshold;
                    if(phi * length < radius && branch.points.get(branch.points.size() - 1).divisionFactor == 1)
                    {
                        branch.points.clear();
//                        System.out.println("insignificant branch");
                    }
                        
                }
            }
        }
    }

    private Vector3f incenter(Vector3f P1, Vector3f P2, Vector3f P3)
    {

        Vector3f P1P2 = P2.subtract(P1);
        Vector3f P1P3 = P3.subtract(P1);
        Vector3f P2P3 = P3.subtract(P2);
        float a = P2P3.length();
        float b = P1P3.length();
        float c = P1P2.length();
        float sum = a + b + c;
        return new Vector3f((a * P1.x + b * P2.x + c * P3.x) / sum, (a * P1.y + b * P2.y + c * P3.y) / sum, (a * P1.z + b * P2.z + c * P3.z) / sum);

    }

    private Vector3f obtusecenter(DelaunayTriangle tri)
    {
        TriangulationPoint A = tri.points[0];
        TriangulationPoint B = tri.points[1];
        TriangulationPoint C = tri.points[2];
        Vector3f P1 = new Vector3f(A.getXf(), A.getYf(), A.getZf());
        Vector3f P2 = new Vector3f(B.getXf(), B.getYf(), B.getZf());
        Vector3f P3 = new Vector3f(C.getXf(), C.getYf(), C.getZf());
        Vector3f P1P2 = P2.subtract(P1);
        Vector3f P1P3 = P3.subtract(P1);
        Vector3f P2P3 = P3.subtract(P2);

//        if (P1P2.angleBetween(P1P3) > FastMath.HALF_PI
//                || P1P2.mult(-1f).angleBetween(P2P3) > FastMath.HALF_PI
//                || P1P3.mult(-1f).angleBetween(P2P3.mult(-1f)) > FastMath.HALF_PI)
//        {
            Vector3f O = null;
            Vector3f M = null;

            if (tri.isStriangle())
            {
                if (tri.getConstrainedEdgeAcross(A))
                {
                    O = P1;
                    M = P2.add(P3).divide(2f);
                } else if (tri.getConstrainedEdgeAcross(B))
                {
                    O = P2;
                    M = P1.add(P3).divide(2f);
                } else if (tri.getConstrainedEdgeAcross(C))
                {
                    O = P3;
                    M = P1.add(P2).divide(2f);
                }
            } else
            {
                float a = P2P3.length();
                float b = P1P3.length();
                float c = P1P2.length();

                if (a <= b && a <= c) // min a
                {
                    O = P1;
                    M = P2.add(P3).divide(2f);
                } else if (b <= a && b <= c) // min b
                {
                    O = P2;
                    M = P1.add(P3).divide(2f);
                } else // min c
                {
                    O = P3;
                    M = P1.add(P2).divide(2f);
                }
            }
            return O.add(M).divide(2f);
//        }
//        return null;
    }

    private Vector3f barycenter(Vector3f a, Vector3f b, Vector3f c)
    {
        return new Vector3f((a.x + b.x + c.x) / 3.0f, (a.y + b.y + c.y) / 3.0f, (a.z + b.z + c.z) / 3.0f);
    }

    /*****************************************************************************/
    /*                                                                           */
    /*  tricircumcenter3d()   Find the circumcenter of a triangle in 3D.         */
    /*                                                                           */
    /*  The result is returned both in terms of xyz coordinates and xi-eta       */
    /*  coordinates, relative to the triangle's point `a' (that is, `a' is       */
    /*  the origin of both coordinate systems).  Hence, the xyz coordinates      */
    /*  returned are NOT absolute; one must add the coordinates of `a' to        */
    /*  find the absolute coordinates of the circumcircle.  However, this means  */
    /*  that the result is frequently more accurate than would be possible if    */
    /*  absolute coordinates were returned, due to limited floating-point        */
    /*  precision.  In general, the circumradius can be computed much more       */
    /*  accurately.                                                              */
    /*                                                                           */
    /*  The xi-eta coordinate system is defined in terms of the triangle.        */
    /*  Point `a' is the origin of the coordinate system.  The edge `ab' extends */
    /*  one unit along the xi axis.  The edge `ac' extends one unit along the    */
    /*  eta axis.  These coordinate values are useful for linear interpolation.  */
    /*                                                                           */
    /*  If `xi' is NULL on input, the xi-eta coordinates will not be computed.   */
    /*                                                                           */
    /*****************************************************************************/
//    Triangle in R^3:
//
//    |                                                           |
//    | |c-a|^2 [(b-a)x(c-a)]x(b-a) + |b-a|^2 (c-a)x[(b-a)x(c-a)] |
//    |                                                           |
//r = -------------------------------------------------------------,
//                         2 | (b-a)x(c-a) |^2
//
//        |c-a|^2 [(b-a)x(c-a)]x(b-a) + |b-a|^2 (c-a)x[(b-a)x(c-a)]
//m = a + ---------------------------------------------------------.
//                           2 | (b-a)x(c-a) |^2
    private Vector3f circumcenter(Vector3f A, Vector3f B, Vector3f C)
    {
        float CA_2 = C.distanceSquared(A);
        float BA_2 = B.distanceSquared(A);

        Vector3f BA = B.subtract(A);
        Vector3f CA = C.subtract(A);

        Vector3f BAxCAxBA = (BA.cross(CA)).cross(BA);
        Vector3f CAxBAxCA = CA.cross(BA.cross(CA));

        Vector3f expr_num = (BAxCAxBA.mult(CA_2)).add(CAxBAxCA.mult(BA_2));
        float expr_den = 2f * (BA.cross(CA)).lengthSquared();

        Vector3f center = A.add(expr_num.divide(expr_den));

        return center;
    }

    public void smoothSkeleton2()
    {
        for (SkeletonBranch branch : skeleton.branches)
        {
            for (int i = 1; i < branch.points.size() - 1; i++)
            {
//                if (branch.points.get(i).divisionFactor != 2) {
//                    continue;
//                }

                branch.smoothSegment(i);
            }
        }
    }

//    public void smoothSkeleton() {
//        for (SkeletonBranch branch : skeleton.branches) {
//            for (int i = 1; i < branch.points.size() - 1; i++) {
//                if (!branch.points.get(i).backTriangle.isStriangle()) {
//                    continue;
//                }
//
//                branch.smoothSegment(i);
//            }
//        }
//    }
//   public void pruneSkeleton2()
//   {
//       for (int i = 0; i < intersections.size(); i++) {
//           Collection<List<SkeletonBranch>> branchGroups = intersections.values();
//           for (List<SkeletonBranch> branchGroup : branchGroups) {
//               
//               //calculate maxLength
//               double maxLength = 0;
//               for (int j = 0; j < branchGroup.size(); j++) {
//                   SkeletonBranch branch = branchGroup.get(j);
//                   if(maxLength < branch.length)
//                       maxLength = branch.length;
//               }
//               
//               //prune small branches
//               List<SkeletonBranch> newBranches = new ArrayList<SkeletonBranch>();
//               for (int j = 0; j < branchGroup.size(); j++) {
//                   SkeletonBranch branch = branchGroup.get(j);
//                   if(branch.length >  maxLength)
//                       newBranches.add(branch);                       
//               }
//               branchGroup.clear();
//               branchGroup.addAll(newBranches);
//           }
//       }
//   }
//   public void pruneSkeleton()
//   {
//      for (int i = 0; i < skeleton.branches.size(); i++)
//      {
//         SkeletonBranch branch = skeleton.branches.get(i);
//         SkeletonBranch newBranch = new SkeletonBranch();
//         
//         //reverse iterate because branches always ends with a T-triangle
//         //and we need to start with a T-triangle
//         for (int j = branch.points.size() - 1; j >= 0 ; j--)
//         {
//            SkeletonPoint point = branch.points.get(j);
//            
//            //finish prune process
//            if(point.backTriangle.isJtriangle())
//            {
//                for(int k = j; k >= 0; k--)
//                {
//                    point = branch.points.get(k);
//                    newBranch.points.add(new SkeletonPoint(point.backTriangle, point.back()));
//                }
//                break;
//            }
//            
//            TriangulationPoint left = point.left();            
//            TriangulationPoint right = point.right();
//            TriangulationPoint back = point.back();
//            
//            if(left == null || right == null || back == null)
//                continue;
//            
//            Vector2f vec_left = new Vector2f(left.getXf(), left.getYf());
//            Vector2f vec_right = new Vector2f(right.getXf(), right.getYf());
//            Vector2f vec_back = new Vector2f(back.getXf(), back.getYf());
//            Vector2f center = new Vector2f((left.getXf() + right.getXf()) / 2f, (left.getYf() + right.getYf()) / 2f);
//            float radius = vec_left.subtract(center).length();
//            
//            //verify if 3 triangle points are contained in the circle
//            if(vec_left.subtract(center).length() <= radius &&
//               vec_right.subtract(center).length() <= radius &&
//               vec_back.subtract(center).length() <= radius)
//            {
//                //pruned
//            }
//            else
//            {
//                newBranch.points.add(new SkeletonPoint(point.backTriangle, back));
//            }
//         }
//         
//         //do the cleaning
//         skeleton.branches.get(i).points.clear();
//         skeleton.branches.get(i).points = null;
//         
//         if(newBranch != null && newBranch.points != null && newBranch.points.size() > 0)
//            skeleton.branches.set(i, newBranch);
//         else
//            skeleton.branches.remove(i);
//      }
//   }
//   public SkeletonBranch generateSkeletonBranch(DelaunayTriangle originTriangle)
//   {
//      // System.out.println("begin skeleton branch");
//      SkeletonBranch branch = new SkeletonBranch();
//      
//      SkeletonPoint originPoint = null;
//      
//      // System.out.println("origin triangle: " + originTriangle);
//      
//      // branch.points.add(SkeletonPoint.firstSkeletonPoint(originTriangle));
//      
//      DelaunayTriangle triangle = originTriangle;
//      SkeletonPoint point = originPoint;
//      
////      SkeletonPoint p = null;
////      SkeletonPoint q = null;
////      SkeletonPoint r = null;
//      
////      SkeletonPoint lastPoint = null;
////      double length = 0;
//      while (true)
//      { 
//         // System.out.println("add point");
////         lastPoint = point;
//         point = getNextSkeletonPoint(triangle, point);
//         
//         if (point == null)
//         {
//            // System.out.println("skeleton point null");
//            break;
//         }
//         
////         if(lastPoint != null)
////         {
////             Vector2f a = new Vector2f(lastPoint.point.getXf(), lastPoint.point.getYf());
////             Vector2f b = new Vector2f(point.point.getXf(), point.point.getYf());
////             length += b.subtract(a).length();             
////         }
//         
//         // Tai et al. prune method
////         /****** prune step *******/
////         p = q;
////         q = r;
////         r = point;
////         
////         boolean prune = false;
////         
////         //prune criteria
////         if(p != null && q != null && r != null && r.backTriangle.isStriangle())
////         {             
////             Vector2f p_point = new Vector2f(p.point.getXf(), p.point.getYf());
////             Vector2f q_point = new Vector2f(q.point.getXf(), q.point.getYf());
////             Vector2f r_point = new Vector2f(r.point.getXf(), r.point.getYf());
////             
////             //1) overlapping criterion
////             float tau = 0.6f;
////             if(q_point.subtract(r_point).length() + tau * r.circumRadius <= q.circumRadius)
////                 prune = true;
////             
////             //2) ordering criterion
////             Vector2f q_left = new Vector2f(q.left().getXf(), q.left().getYf());
////             Vector2f q_right = new Vector2f(r.right().getXf(), r.right().getYf());
////             Vector2f q_front = new Vector2f(r.front().getXf(), r.front().getYf());
////             Vector2f commonEdge = q_right.subtract(q_left);
////             
////             //a. get common edge perpendicular vector
////             Vector2f perp1 = new Vector2f(-commonEdge.y, commonEdge.x);
////             Vector2f perp2 = new Vector2f(commonEdge.y, -commonEdge.x);
////             
////             //b. verify the distance to choose the correct perpendicular vector
////             float dist1 = (q_right.add(perp1)).subtract(q_front).length();
////             float dist2 = (q_right.add(perp2)).subtract(q_front).length();
////             Vector2f n = dist1 < dist2 ? perp1 : perp2;
////             
////             //c. verify criterion
////             if(r_point.subtract(q_point).dot(n) <= 0)
////                 prune = true;
////             
////             //3) proportionality criterion
////             //if (((q - p).(r - q)) / (||q - p||*||r - q||)) >= cos(PI/18)
////             if((
////                     (q_point.subtract(p_point)).dot(r_point.subtract(q_point))
////                     /
////                q_point.subtract(p_point).length() * r_point.subtract(q_point).length())
////                     >= FastMath.cos2(FastMath.PI / 18)                 
////                     )
////                 prune = true;
////             
////         }
//         
//         /****** end prune step *******/
//         
//         // System.out.println("point: " + point.point);
//         //if(!prune)
//            branch.points.add(point);
//         
//         triangle = point.frontTriangle;
//         
//         if (triangle == null)
//         {
//            // System.out.println("triangle null");
//            break;
//         }
//      }
////      branch.length = length;
//      // System.out.println("end skeleton branch");
//      return branch;
//   }
//    private SkeletonPoint getNextSkeletonPoint2(SkeletonPoint point, DelaunayTriangle tri, boolean first) {
//        if (tri == null) {
//            return null;
//        }
//
//        if(!first && !tri.isStriangle())
//            return null;
//        
//        for (int i = 0; i < tri.neighbors.length; i++) {
//            if (tri.neighbors != null && tri.neighbors[i] != null && tri.visitedNeighbors[i] == false) {
//                DelaunayTriangle nextTri = tri.neighbors[i];
//
//                SkeletonPoint sp = getSkeletonPoint2(nextTri);
//                point.addNeighbor(sp);
//                sp.addNeighbor(point);
//                return sp;
//            }
//        }
//
//        return null;
//    }
//    private SkeletonPoint getNextSkeletonPoint(DelaunayTriangle tri, SkeletonPoint point) {
//        if (tri == null) {
//            return null;
//        }
//
//        TriangulationPoint backPoint = null;
//        for (int i = 0; i < tri.neighbors.length; i++) {
//            if (tri.neighbors[i] != null && !tri.neighbors[i].isVisited()) {
//                if (point == null || tri.neighbors[i] != point.backTriangle) {
//                    backPoint = tri.points[i];
//                    break;
//                }
//            }
//        }
//        // if(backPoint == null)
//        // System.out.println("backPoint null");
//        return new SkeletonPoint(tri, backPoint);
//    }
    private DelaunayTriangle getInitialTriangle()
    {
        //look for a J-triangle
        for (DelaunayTriangle tri : triangles)
        {
            if (tri.isJtriangle() && !tri.isVisited())
            {
                jTriangles++;
                return tri;
            }
        }

        //in the case when a J triangle doesn't exist, look for a T-triangle
        for (DelaunayTriangle tri : triangles)
        {
            if (tri.isTtriangle() && !tri.isVisited())
            {
                tTriangles++;
                return tri;
            }
        }

        //if none is found, return null
        return null;
    }
}
