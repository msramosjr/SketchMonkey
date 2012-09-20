package br.uff.ic.jme.visualization;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import br.uff.ic.jme.interpolation.ConvolutionInterpolator;
import br.uff.ic.jme.interpolation.Interpolator;
import br.uff.ic.jme.main.SceneManager;
import br.uff.ic.jme.skeletonization.Skeleton;
import br.uff.ic.jme.skeletonization.SkeletonBranch;
import br.uff.ic.jme.skeletonization.SkeletonPoint;
import br.uff.ic.jme.visualization.MarchingCubes.TRIANGLE;

import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Line;
import com.jme3.util.BufferUtils;

public class AdaptedMarchingCubesViewer implements SpaceViewer, Runnable {

    private BoundingBox boundingBox;
    private List<TRIANGLE> triangles;
    private MarchingCubes marchingCubes;
    private int maxLevel = 5;
    private Interpolator lastFunction;
//    private Thread spaceSampleThread;

    public AdaptedMarchingCubesViewer(BoundingBox box) {
        boundingBox = box;
        marchingCubes = new MarchingCubes();
        triangles = new ArrayList<TRIANGLE>();
    }

    @Override
    public Mesh generateMesh(Interpolator interp) {
        lastFunction = interp;
//      if (lastFunction == null)
//         lastFunction = interp;
//      else
//      {
//         try
//         {
//            lastFunction.combine(interp);
//         }
//         catch (CombineOperationException e)
//         {
//            System.out.println(e.getMessage() + " ignoring...");
//            interp = lastFunction;
//         }
//      }

        long t = System.currentTimeMillis();
        System.out.println("[debug][" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] begin sample space (recursion level: " + maxLevel + ")");
        
        triangles.clear();        
        // start recursive process
        sampleSpace(interp, boundingBox, maxLevel, 0);
        
//        spaceSampleThread = new Thread(this, "GenerateThread");
//        spaceSampleThread.start();
//
//        while (spaceSampleThread.getState() != Thread.State.TERMINATED) {
//            try {
//                Thread.sleep(5000);
//                System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] sampling...");
//            } catch (InterruptedException e) {
//                System.out.println("interrupted...");
//            }
//        }

        interp.finishInterpolation();

        System.out.println("[debug] sample space: " + (System.currentTimeMillis() - t) + " ms.");
        // System.out.println("restriction points: " +
        // interp.getRestrictionPoints().length);
        System.out.println("evaluations: " + interp.getEvaluations());
        System.out.println("triangles: " + triangles.size());
        t = System.currentTimeMillis();
        // System.out.println("samples: " + samples);
        // samples = 0;

        Vector3f[] vertexes = new Vector3f[triangles.size() * 3];
        Vector3f[] normals = new Vector3f[vertexes.length];
        int[] indexes = new int[vertexes.length];

        for (int i = 0, j = 0; i < triangles.size() && j < vertexes.length; i++, j += 3) {
            if (triangles.get(i) == null) {
                continue;
            }
            vertexes[j] = triangles.get(i).p[0];
            vertexes[j + 1] = triangles.get(i).p[1];
            vertexes[j + 2] = triangles.get(i).p[2];

            indexes[j] = j;
            indexes[j + 1] = j + 1;
            indexes[j + 2] = j + 2;

            normals[j] = normals[j + 1] = normals[j + 2] = new Triangle(vertexes[j], vertexes[j + 1], vertexes[j + 2]).getNormal().normalize();
        }
        // rescale normal components
        Map<Vector3f, List<Integer>> map = new HashMap<Vector3f, List<Integer>>();
        for (int i = 0; i < vertexes.length; i++) {
            Vector3f vertex = vertexes[i];
            List<Integer> indexList = map.get(vertex);
            if (indexList == null) {
                indexList = new ArrayList<Integer>();
            }
            indexList.add(i);
            map.put(vertex, indexList);
        }
        for (List<Integer> list : map.values()) {
            Vector3f normal = Vector3f.ZERO;
            for (int iVertex : list) {
                Vector3f n = normals[iVertex];
                if (n != null) //normal.addLocal(normals[iVertex]);
                {
                    normal = FastMath.interpolateLinear(0.5f, normal, normals[iVertex]);
                }
            }
            normal.normalizeLocal();
            for (int iVertex : list) {
                normals[iVertex] = normal;
            }
        }

        Mesh mesh = new Mesh();
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertexes));
        mesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
        mesh.setBuffer(Type.Index, 1, BufferUtils.createIntBuffer(indexes));
        mesh.updateBound();

        System.out.println("generate mesh: " + (System.currentTimeMillis() - t) + " ms.");
        return mesh;
    }

//   public Mesh generateRBFMesh(Vector3f[] restriction, double[] val)
//   {
//      // for (int i = 0; i < restriction.length; i++)
//      // {
//      // // System.out.println(restriction[i]);
//      // }
//      // System.out.println("calculating RBF linear system...");
//      Interpolator newFunction = new RBFInterpolator(restriction, val);
//      
//      // System.out.println("generating mesh...");
//      
//      return generateMesh(newFunction);
//   }
    private void sampleSpace(Interpolator interp, BoundingBox box, int maxLevel, int level) {
        // System.out.println("level" + level);

        if (level == maxLevel) {
            // System.out.println("LEAF CUBE");

            if (marchingCubes.polygoniseCell(interp, box) > 0) {
                TRIANGLE[] tris = marchingCubes.getTriangles();
                for (int j = 0; j < tris.length; j++) {
                    if (!triangles.contains(tris[j])) {
                        triangles.add(tris[j]);
                    }
                }
            }
        } else // if (level < 4 || intersects(interp, box))
        {
            // System.out.println("divide cube");
            float xExtentHalf = box.getXExtent() / 2;
            float yExtentHalf = box.getYExtent() / 2;
            float zExtentHalf = box.getZExtent() / 2;

            for (int i = 0; i < 8; i++) {
                int a = i == 1 || i == 2 || i == 5 || i == 6 ? 1 : -1;
                int b = i == 4 || i == 5 || i == 6 || i == 7 ? 1 : -1;
                int c = i == 2 || i == 3 || i == 6 || i == 7 ? 1 : -1;
                Vector3f extentVectorHalf = new Vector3f(a * xExtentHalf, b * yExtentHalf, c * zExtentHalf);
                BoundingBox subBox = new BoundingBox(box.getCenter().add(extentVectorHalf), xExtentHalf, yExtentHalf, zExtentHalf);
                sampleSpace(interp, subBox, maxLevel, level + 1);
            }
        }
    }

    @SuppressWarnings("unused")
    private boolean intersects(Interpolator interp, BoundingBox box) {
        double r = new Vector3f(box.getXExtent(), box.getYExtent(), box.getZExtent()).length();
        return interp.interpolate(box.getCenter()) <= r;
        // return new BoundingSphere(50,
        // Vector3f.ZERO).intersectsBoundingBox(box);
    }

    @Override
    public void run() {
        sampleSpace(lastFunction, boundingBox, maxLevel, 0);
    }

    public Mesh generateConvolutionMesh(Skeleton skeleton) {
        Interpolator newFunction = new ConvolutionInterpolator(skeleton);
        // System.out.println("generating mesh...");
        return generateMesh(newFunction);
    }
    
    public Mesh generateImplicitBlendingMesh(List<Skeleton> skeletons) {
        Interpolator newFunction = new ConvolutionInterpolator(skeletons);
        // System.out.println("generating mesh...");
        return generateMesh(newFunction);
    }

    public Node generateSkeletonMesh(Skeleton skeleton) {
        Node node = new Node("skeleton");
        for (int i = 0; i < skeleton.branches.size(); i++) {
            SkeletonBranch branch = skeleton.branches.get(i);
            for (int j = 0; j < branch.points.size() - 1; j++) {
                SkeletonPoint SP1 = branch.points.get(j);
                SkeletonPoint SP2 = branch.points.get(j + 1);
//                TriangulationPoint P1 = SP1.point;
//                TriangulationPoint P2 = SP2.point;
//                Vector3f p = new Vector3f(P1.getXf(), P1.getYf(), P1.getZf());
//                Vector3f q = new Vector3f(P2.getXf(), P2.getYf(), P2.getZf());
                Vector3f p = SP1.point;
                Vector3f q = SP2.point;

                //skeleton visualization
                Material mat = new Material(SceneManager.get().assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setColor("Color", ColorRGBA.Green);   
                mat.getAdditionalRenderState().setWireframe(true);
        
                //Cylinder cyl = new Cylinder(10, 10, 0.1f, p.subtract(q).length(), true);
                Line segment = new Line(p, q);
                Geometry geom = new Geometry("skel_segment_"+i+"_"+j, segment);
                //geom.lookAt(q.subtract(p).normalize(), Vector3f.UNIT_Y);
                //geom.setLocalTranslation(p.add(q).divide(2f));
                geom.setMaterial(mat);
                node.attachChild(geom);
            }
        }
        return node;
    }
    
    public void setRecursionLevel(int i)
    {
        maxLevel = i;
    }
    
    public int getRecursionLevel()
    {
        return maxLevel;
    }
}
