package br.uff.ic.jme.main;

import br.uff.ic.jme.skeletonization.Skeleton;
import br.uff.ic.jme.skeletonization.SkeletonBranch;
import br.uff.ic.jme.skeletonization.SkeletonGenerator;
import br.uff.ic.jme.skeletonization.SkeletonPoint;
import com.jme3.app.Application;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;

import java.util.LinkedHashSet;
import java.util.Set;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Line;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;
import java.util.List;
import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

public class Drawing2DHandler implements ActionListener, AnalogListener
{
    
    private void filterControlPoints(boolean closing)
    {
        int size = filteredControlPoints.size();
        if (size >= 3)
        {
            float mi_0 = 0.01f;
            float mi_1 = 0.05f;            
            float theta_0 = 0f;
            float theta_1 = 20f; //in degrees
            
            //3 last points
            Vector3f p;
            Vector3f q;
            Vector3f r;            
            if(closing)
            {
                p = filteredControlPoints.get(size - 2);
                q = filteredControlPoints.get(size - 1);
                r = filteredControlPoints.get(0);
            }
            else
            {
                p = filteredControlPoints.get(size - 3);
                q = filteredControlPoints.get(size - 2);
                r = filteredControlPoints.get(size - 1);
            }
            
//            System.out.println("p: " + p);
//            System.out.println("q: " + q);
//            System.out.println("r: " + r);
            
            //2 last segments
            Vector3f pq = q.subtract(p);
            Vector3f qr = r.subtract(q);
            
            //angle between 2 last segments (in degrees)
            float theta = pq.normalize().angleBetween(qr.normalize()) * FastMath.RAD_TO_DEG;
//            System.out.println("theta: " + theta);
            
            // using scale conversion (geometric similarity)
            // we get the value of mi (between mi_0 and mi_0) that references theta (between theta_0 and theta_1)
            float mi = mi_1; //when theta < theta_0
            if(theta >= theta_1)
                mi = mi_0;
            else if(theta > theta_0)
                mi = ((theta - theta_0) / (theta_1 - theta_0)) * (mi_0 - mi_1) + mi_1;
            
            //canvas width
            float W = AppData.worldLimits.getXExtent() * 2;
            
//            System.out.println("mi: " + mi);
//            System.out.println("W: " + W);
//            System.out.println("QR length: " + qr.length());
            
            if(qr.length() > mi_1 * W) //divide segment if it is bigger than mi_1 * W
            {
//                System.out.println("divide");
                filteredControlPoints.remove(size - 1);
                filteredControlPoints.remove(size - 2);
                int numSegments = (int)Math.floor((double)(qr.length() / (mi_1 * W)));
//                System.out.println("numSegments: " + numSegments);
                Vector3f subsegment = qr.normalize().mult(qr.length() / numSegments);
                for (int i = 0; i < numSegments; i++)
                {   
                    Vector3f newPoint1 = q.add(subsegment.mult(i));
                    Vector3f newPoint2 = q.add(subsegment.mult(i + 1));
                    filteredControlPoints.add(newPoint1);
                    filteredControlPoints.add(newPoint2);
                }
            }
            else if(qr.length() < mi * W) //combine segment if it is smaller than mi
            {
                if(qr.length() + pq.length() < mi * W) // if segments combined are smaller than mi
                {
//                    System.out.println("combine");
                    filteredControlPoints.remove(size - 1);
                    filteredControlPoints.remove(size - 2);
                    filteredControlPoints.add(r); // p -> q -> r becomes p -> r
                }                
            }
            
            //adjust line closing
            if(filteredControlPoints.size() > 3 && filteredControlPoints.get(filteredControlPoints.size() - 1).distance(filteredControlPoints.get(0)) <= mi_1 * W)
            {
                filteredControlPoints.remove(filteredControlPoints.size() - 1);
            }
        }

        //System.out.println("filteredControlPoints: " + filteredControlPoints.size());
        
        filteredLine = null;
        filteredLine = new SketchLine("filteredSketchLine", filteredControlPoints.toArray(new Vector3f[0]));
//        filteredLine.setMode(Mesh.Mode.LineStrip);
//        filteredLine.setLineWidth(2);
        //**********************************
    }

    private enum States
    {

        INITIAL, START_DRAWING, DRAWING, END_DRAWING
    }

    /**
     * 2D drawing tools
     */
    public enum Tools
    {

        LINE, FREEHAND
    };
    public static Tools currentTool = Tools.FREEHAND;
    private States state = States.INITIAL;
    private Node drawingNode;
    private Node screenNode;
    private SketchLine line;
    private SketchLine filteredLine;
    private Geometry g;
    private Geometry mouse3d;
    private List<Vector3f> filteredControlPoints = new ArrayList<Vector3f>();
    private Set<Vector3f> controlPoints = new LinkedHashSet<Vector3f>();
    private static float zAxis = AppData.zDrawing;
    private Vector3f startPoint = new Vector3f(0, 0, zAxis);
    private Application app;
    private int planeCount = 0;

    public Drawing2DHandler(Application app)
    {
        this.app = app;
        drawingNode = SceneManager.get().getDrawingNode();
        screenNode = SceneManager.get().getScreenNode();
        
        /** arcball sphere to help visualization */
//        Sphere s = new Sphere(30, 30, AppData.worldLimits.getZExtent());
//        Geometry g = new Geometry("arcball", s);
//        //s.setDefaultColor(ColorRGBA.green);
//        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setColor("Color", ColorRGBA.Green);
//        mat.getAdditionalRenderState().setWireframe(true);        
//        g.setMaterial(mat);
//
//        drawingNode.attachChild(g);
        /***********************************/
        /** quad to help screen plane visualization */
//        Quad quad = new Quad(AppData.worldLimits.getXExtent() * 2f, AppData.worldLimits.getYExtent() * 2f);
//        Geometry g2 = new Geometry("screenPlane", quad);
//        g2.setLocalTranslation(-quad.getWidth() / 2f, -quad.getHeight() / 2f, 0);
//        Material mat2 = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//        mat2.setColor("Color", ColorRGBA.Blue);
//        mat2.getAdditionalRenderState().setWireframe(true);
//        mat2.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
//        g2.setMaterial(mat2);
//
//        drawingNode.attachChild(g2);
        /***********************************/
        
        //3D mouse sphere        
//        Sphere s = new Sphere(30, 30, 1f);
//        mouse3d = new Geometry("mouse3d", s);
//        //s.setDefaultColor(ColorRGBA.green);
//        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setColor("Color", ColorRGBA.Red);
//        mat.getAdditionalRenderState().setWireframe(false);        
//        mouse3d.setMaterial(mat);
//
//        screenNode.attachChild(mouse3d);
        
        //ray line
//        Geometry raygeom = new Geometry("raylinegeom", new SketchLine("rayLine", new Vector3f[]{new Vector3f(10,10,AppData.worldLimits.getZExtent() * -2), new Vector3f(0,0,AppData.worldLimits.getZExtent() * 2)}));
//                Material mat2 = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//                mat2.setColor("Color", ColorRGBA.Red);
//                raygeom.setMaterial(mat2);
//                //line = new SketchLine("sketchLine", new Vector3f[]{new Vector3f(-2,0,0), new Vector3f(2,0,0)});
//
//                //line.updateRenderState();
//                screenNode.attachChild(raygeom);
        
        Vector3f center = AppData.worldLimits.getCenter();
        float extentX = AppData.worldLimits.getXExtent();
        float extentY = AppData.worldLimits.getYExtent();
        Vector3f startX = new Vector3f(center.x - extentX, center.y, center.z);
        Vector3f endX = new Vector3f(center.x + extentX, center.y, center.z);
        Vector3f startY = new Vector3f(center.x, center.y - extentY, center.z);
        Vector3f endY = new Vector3f(center.x, center.y + extentY, center.z);
                        
        Line xLine = new Line(startX, endX);
        Geometry gridX = new Geometry("gridLineX", xLine);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        gridX.setMaterial(mat);
        screenNode.attachChild(gridX);
        
        Line yLine = new Line(startY, endY);
        Geometry gridY = new Geometry("gridLineY", yLine);
        mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        gridY.setMaterial(mat);
        screenNode.attachChild(gridY);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf)
    {
        //System.out.println("isPressed: " + isPressed);
        //System.out.println("(x, y): " + getMousePosition());

        if("move".equals(name))
        {            
            return;
        }
        
        switch (currentTool)
        {
            case LINE:
//               lineTool();
                break;
            case FREEHAND:
                freehandTool(isPressed);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf)
    {
//        if("move".equals(name))
//        {      
//            Vector3f mousePos = getMousePosition();
//            // 1. Reset results list.
//            CollisionResults results = new CollisionResults();
//            // 2. Aim the ray from cam loc to cam direction.                    
//            Ray ray = new Ray(new Vector3f(mousePos.x, mousePos.y, AppData.worldLimits.getZExtent() * 2), new Vector3f(0f,0f,-1f));
//            // 3. Collect intersections between Ray and Shootables in results list.
//
//            System.out.println("ray origin: " + ray.origin + ", direction: " + ray.direction + ", limit: " + ray.limit);
//
//            SceneManager.get().getRotationNode().collideWith(ray, results);
//
//            //zAxis = AppData.zDrawing;
//            Vector3f hitPoint = new Vector3f(mousePos.x, mousePos.y, zAxis);
//            if(results.size() > 0)
//            {
//                hitPoint = results.getClosestCollision().getContactPoint();
//                System.out.println("Contact Point: " + hitPoint);
//                //zAxis = hitPoint.z;
//            }
//
//            mouse3d.setLocalTranslation(hitPoint);
//            return;
//        }
        //System.out.println("drawing...");
        switch (state)
        {
            case DRAWING:
                Vector3f endPoint = getMousePosition();

                if (endPoint.equals(startPoint))
                {
                    return;
                }

//                System.out.println(zAxis);
                
                controlPoints.add(new Vector3f(startPoint.x, startPoint.y, zAxis));
                controlPoints.add(new Vector3f(endPoint.x, endPoint.y, zAxis));

//                System.out.println(startPoint.x + ", " + startPoint.y + ", " + startPoint.z);
                
                if (g != null)
                {
                    screenNode.detachChild(g);
                }
                
                if(controlPoints.size() < 2)
                    return; 
                
                line = null;
                line = new SketchLine("sketchLine", controlPoints.toArray(new Vector3f[0]));
                line.setMode(Mesh.Mode.LineStrip);
                line.setLineWidth(2);

                //filter points to enhance capture quality
                //using Tai et al. method (or my interpretation of it)

                Vector3f startPoint3f = new Vector3f(startPoint.x, startPoint.y, zAxis);
                if(!filteredControlPoints.contains(startPoint3f))
                    filteredControlPoints.add(startPoint3f);
                filteredControlPoints.add(new Vector3f(endPoint.x, endPoint.y, zAxis));
                filterControlPoints(false);               
                                
                //**********************************

                g = new Geometry("line", line);                
                if(SceneManager.get().skeletonDrawing)
                {   
                    g.setMaterial(SceneManager.get().skeletonDrawingMaterial);
                }
                else
                {   
                    g.setMaterial(SceneManager.get().silhouetteDrawingMaterial);
                }                
                //line = new SketchLine("sketchLine", new Vector3f[]{new Vector3f(-2,0,0), new Vector3f(2,0,0)});

                //line.updateRenderState();
                screenNode.attachChild(g);
                startPoint = endPoint;
                break;
            default:
                break;
        }

    }

    private Vector3f getMousePosition()
    {
        return app.getCamera().getWorldCoordinates(app.getInputManager().getCursorPosition(), 0).mult(new Vector3f(1,1,0)).add(new Vector3f(0,0,zAxis));
    }
   
    public void freehandTool(boolean isPressed)
    {

        if (isPressed) //on mouse down
        {
            switch (state)
            {
                case INITIAL:
                    
                    startPoint = getMousePosition();
                    
                    // 1. Reset results list.
                    CollisionResults results = new CollisionResults();
                    // 2. Aim the ray from cam loc to cam direction.
                    Ray ray = new Ray(new Vector3f(startPoint.x, startPoint.y, AppData.worldLimits.getZExtent() * 2), new Vector3f(0f,0f,-1f));
                    // 3. Collect intersections between Ray and Shootables in results list.

//                    System.out.println("ray origin: " + ray.origin + ", direction: " + ray.direction + ", limit: " + ray.limit);

                    //verify mesh collision
                    SceneManager.get().getRotationNode().collideWith(ray, results);
                    zAxis = AppData.zDrawing;
                    Vector3f hitPoint = new Vector3f(startPoint.x, startPoint.y, zAxis);
                    if(results.size() > 0)
                    {
                        hitPoint = results.getClosestCollision().getContactPoint();
//                        System.out.println("Contact Point: " + hitPoint);
                        zAxis = hitPoint.z;
                    }
                    
                    //mouse3d.setLocalTranslation(hitPoint);
                    
                    controlPoints.add(new Vector3f(startPoint.x, startPoint.y, zAxis));
                    filteredControlPoints.add(new Vector3f(startPoint.x, startPoint.y, zAxis));
                    state = States.DRAWING;
                    break;
                default:
                    break;
            }
        } else //on mouse up
        {
            switch (state)
            {
                case DRAWING:

                    if (filteredLine == null)
                    {
                        return;
                    }
                    // finalize shape drawing
                    //set line mode to loop, in this way the shape is closed
                    //line.setMode(Mode.Loop);
                    //filterControlPoints(true);
                                        
                    Node controlPointsNode = new Node("controlPoints");
                    for (Vector3f vector3f : filteredControlPoints)
                    {
                        Circle controlPoint = new Circle(vector3f, 0.005f * AppData.worldLimits.getXExtent(), 5);
                        Geometry geom = new Geometry("controlPoint", controlPoint);
                        geom.setMaterial(SceneManager.get().controlPointsMaterial);
                        controlPointsNode.attachChild(geom);
                    }
                    
                    //create a new plane to encapsulate the last drawing line
                    Node newPlane = new Node("drawingPlane");
                    filteredLine.setMode(Mesh.Mode.LineStrip);
                    filteredLine.setLineWidth(2);
                    
                    if(SceneManager.get().skeletonDrawing)
                    {
                        filteredLine.setBound(new BoundingBox(filteredControlPoints.get(0), filteredControlPoints.get(filteredControlPoints.size() - 1)));
                        g = new Geometry("skeletonLine", filteredLine);
                        g.setMaterial(SceneManager.get().skeletonDrawingMaterial);
                    }
                    else
                    {
                        g = new Geometry("silhuetteLine", filteredLine);
                        g.setMaterial(SceneManager.get().silhouetteDrawingMaterial);
                    }
                    
                    newPlane.attachChild(g);
                    newPlane.attachChild(controlPointsNode);
                    
                    // create a quad to help plane visualization //
//                    Quad quad = new Quad(AppData.worldLimits.getXExtent() * 2f, AppData.worldLimits.getYExtent() * 2f);
                    
                    boolean error = false;
                    if(!SceneManager.get().skeletonDrawing)
                    {
                        try
                        {   
                            /** generate a mesh to use in ray casting process **/
                            List<PolygonPoint> points = new ArrayList<PolygonPoint>();
                            for (int i = 0; i < filteredControlPoints.size(); i++)
                            {
                                points.add(new PolygonPoint(filteredControlPoints.get(i).x, filteredControlPoints.get(i).y, filteredControlPoints.get(i).z));
                            }
                            Polygon poly = new Polygon(points);
                            Poly2Tri.triangulate(poly);
                            List<DelaunayTriangle> triangles = poly.getTriangles();
                            List<Vector3f> vertexes = new ArrayList<Vector3f>();
                            for (DelaunayTriangle tri : triangles)
                            {
                                for(int i = 0; i < 3; i++)
                                {
                                    TriangulationPoint tp = tri.points[i];
                                    vertexes.add(new Vector3f(tp.getXf(), tp.getYf(), tp.getZf()));
                                }
                            }
                            
                            //only for debug view
                            //extract skeleton
                            SkeletonGenerator generator = new SkeletonGenerator(triangles);
                            generator.generateSkeleton2();
                            Skeleton skeleton = generator.getSkeleton();
                            
                            for (SkeletonBranch branch : skeleton.branches)
                            {
                                Vector3f lastPoint = null;
                                for (SkeletonPoint point : branch.points)
                                {
                                    if(lastPoint != null)
                                    {
                                        Line skelLine = new Line(lastPoint, point.point);
                                        Geometry skgeom = new Geometry("skeletonSegmentLine", skelLine);
                                        //mat3.getAdditionalRenderState().setWireframe(true);
                                        //mat3.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
                                        skgeom.setMaterial(SceneManager.get().skeletonSegmentMaterial);
                                        newPlane.attachChild(skgeom);
                                    }
                                    lastPoint = point.point;
                                }
                            }

                            Mesh mesh = new Mesh();
                            mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertexes.toArray(new Vector3f[0])));
                            mesh.updateBound();
                            /***********************************************************/   
                            Geometry g2 = new Geometry("drawingPlane" + planeCount++, mesh);
                            //g2.setLocalTranslation(-AppData.worldLimits.getXExtent(), -AppData.worldLimits.getYExtent(), zAxis);                            
                            g2.setMaterial(SceneManager.get().triangulationMaterial);
                            newPlane.attachChild(g2);
                        }
                        catch(Throwable e)
                        {
                            System.out.println("triangulation error");
                            error = true;
                        }
                    }
                    else
                    {
                        List<Vector3f> skVertexes = new ArrayList<Vector3f>();
                        Vector3f lastPoint = null;
                        for (Vector3f vector3f : filteredControlPoints)
                        {
                            if(lastPoint != null)
                            {
                                Vector3f segment = vector3f.subtract(lastPoint);
                                Vector3f perpSeg1 = new Vector3f(-segment.y, segment.x, segment.z);
                                Vector3f perpSeg2 = new Vector3f(segment.y, -segment.x, segment.z);
                                skVertexes.add(lastPoint);
                                skVertexes.add(vector3f.add(perpSeg1.normalize()));
                                skVertexes.add(vector3f.add(perpSeg2.normalize()));
                            }
                                
                            lastPoint = vector3f;
                        }
                        
                        Mesh mesh = new Mesh();
                        mesh.setMode(Mesh.Mode.Triangles);
                        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(skVertexes.toArray(new Vector3f[0])));
                        mesh.updateBound();
                        /***********************************************************/   
                        Geometry g2 = new Geometry("drawingPlane" + planeCount++, mesh);
                        //g2.setLocalTranslation(-AppData.worldLimits.getXExtent(), -AppData.worldLimits.getYExtent(), zAxis);                            
                        g2.setMaterial(SceneManager.get().triangulationMaterial);
                        newPlane.attachChild(g2);
                    }
                    
                    // adjust new plane rotation
                    newPlane.setLocalRotation(SceneManager.get().getRotationNode().getLocalRotation().inverse());


                    screenNode.detachChildNamed("line");
                    
                    // clear line control points
                    controlPoints.clear();
                    filteredControlPoints.clear();
                    
                    if(!error)
                    {
                        // attach new plane to drawing node
                        drawingNode.attachChild(newPlane);
                        SceneManager.get().undoStack.clear();
                        SceneManager.get().lineCount++;
                        // ***********************************//
                    }

                    state = States.INITIAL;
                    break;
                default:
                    break;
            }
        }
    }
//      private void lineTool()
//      {
//         // on mouse down event
//         if (MouseInput.get().isButtonDown(mouseButtonForRequired))
//         {
//            switch (state)
//            {
//               case INITIAL:
//                  startPoint = getMousePosition();
//                  state = States.START_DRAWING;
//                  break;
//               case DRAWING:
//                  drawingNode.detachChild(tempLine);                  
//                  line = new SketchLine("line", new Vector3f[] { startPoint, getMousePosition() });
//                  line.setDefaultColor(ColorRGBA.black.clone());
//                  line.setLineWidth(2);                  
//                  drawingNode.attachChild(line);                  
//                  state = States.END_DRAWING;
//                  break;
//               default:
//                  break;
//            }
//         }
//         // on mouse up event
//         else if (!MouseInput.get().isButtonDown(mouseButtonForRequired))
//         {
//            switch (state)
//            {
//               case START_DRAWING:
//                  state = States.DRAWING;
//                  break;
//               case DRAWING:
//                  drawingNode.detachChild(tempLine);
//                  tempLine = new SketchLine("templine", new Vector3f[] { startPoint, getMousePosition() });
//                  tempLine.setDefaultColor(ColorRGBA.red.clone());
//                  tempLine.setLineWidth(2);                  
//                  drawingNode.attachChild(tempLine);                  
//                  break;
//               case END_DRAWING:
//                  state = States.INITIAL;
//                  break;
//               default:
//                  break;
//            }
//         }
//      }
}