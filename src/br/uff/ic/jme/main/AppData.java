package br.uff.ic.jme.main;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;

/**
 * Class to centralize app data
 * 
 * @author marcos
 */
public class AppData
{
   
   // private boolean hasNewVertexes = false;
   
   // private Sketch sketch;
   
   // private SketchLine currentLine;
   
   // private SketchLine currentSegment;
   
   // private Node viz3DRootNode;
   
   // private Node draw2DRootNode;
   
   //public final static String      instructions = "T: show/hide triangulation\nS: show/hide skeleton";
   
    //world size is the double of bounding box's extents size
   public final static BoundingBox worldLimits  = new BoundingBox(Vector3f.ZERO, 50f, 50f, 50f);
   
   public static final float             zDrawing     = 0f;
   
   // public void updateLineState()
   // {
   // Vector3f[] controlPoints = new Vector3f[currentLine.size()];
   // for (int i = 0; i < currentLine.size(); i++)
   // {
   // Vector3f vec = currentLine.get(i);
   // controlPoints[i] = vec;
   // }
   // currentSegment = new SketchLine("drawingLine", controlPoints);
   // }
   
   // public void generateLines()
   // {
   // ((Node) draw2DRootNode.getChild("drawingNode")).detachAllChildren();
   // for (SketchPlane plane : sketch.sketch)
   // {
   // for (SketchLine list : plane.vertexesList)
   // {
   // Vector3f[] controlPoints = new Vector3f[list.size() * 2 - 2];
   // int j = 0;
   // for (int i = 0; i < list.size() - 1; i++)
   // {
   // Vector3f vec1 = list.get(i);
   // Vector3f vec2 = list.get(i + 1);
   // controlPoints[j++] =
   // viz3DRootNode.getChild("main").getLocalRotation().mult(vec1);
   // controlPoints[j++] =
   // viz3DRootNode.getChild("main").getLocalRotation().mult(vec2);
   // }
   // currentSegment = new Line("drawingLine", controlPoints, null, null, null);
   // currentSegment.setDefaultColor(ColorRGBA.black);
   // currentSegment.setLineWidth(2);
   // currentSegment.updateRenderState();
   // ((Node)
   // draw2DRootNode.getChild("drawingNode")).attachChild(currentSegment);
   // }
   // }
   // draw2DRootNode.getChild("drawingNode").updateRenderState();
   // }
   
   // public SketchLine getLine()
   // {
   // return currentSegment;
   // }
   
   /**
    * Return if the vertex list has changed since last query. If the vertex list
    * has changed, set changed flag to false.
    * 
    * @return true, if the vertex has changed; otherwise, return false.
    */
   // public boolean hasNewVertexes()
   // {
   // if (hasNewVertexes)
   // {
   // hasNewVertexes = false;
   // return true;
   // }
   // return false;
   // }
   
   // public Sketch getVertexes()
   // {
   // return sketch;
   // }
   
   // public void setVertexes(List<Vector3f> vertexes)
   // {
   // this.currentLine.set(vertexes);
   // hasNewVertexes = true;
   // }
   
   // public void addVertex(Vector3f vertex)
   // {
   // currentLine.add(vertex);
   // hasNewVertexes = true;
   // }
   
   // public void finalizeShape()
   // {
   // //sketch.sketch.get(sketch.sketch.size() -
   // 1).vertexesList.add(currentLine);
   // currentLine = new SketchLine();
   // }
   
   // public void initializePlane()
   // {
   // sketch.sketch.add(new SketchPlane());
   // }
   
   // public void updateRotation()
   // {
   // // for (List<Vector3f> list : vertexesList)
   // // {
   // // for (Vector3f vertex : list)
   // // {
   // //
   // vertex.set(viz3DRootNode.getChild("main").getLocalRotation().mult(vertex));
   // // // Quaternion roll180 = new Quaternion();
   // // // roll180.fromAngleAxis( FastMath.PI / 4f , new Vector3f(0,1,0) );
   // // // vertex.set(roll180.mult(vertex));
   // // }
   // // }
   // }
   
   /* Singleton */
//   private static AppData          instance;
   
   private AppData()
   {
      // currentLine = new SketchLine();
      // sketch = new Sketch();
   }
   
//   public static AppData getInstance()
//   {
//      if (instance == null)
//         instance = new AppData();
//      return instance;
//   }
   
   /*
	 * 
	 */
   
   // public void setViz3DRootNode(Node viz3DRootNode)
   // {
   // this.viz3DRootNode = viz3DRootNode;
   // }
   //
   // public Node getViz3DRootNode()
   // {
   // return viz3DRootNode;
   // }
   //
   // public void setDraw2DRootNode(Node draw2DRootNode)
   // {
   // this.draw2DRootNode = draw2DRootNode;
   // }
   //
   // public Node getDraw2DRootNode()
   // {
   // return draw2DRootNode;
   // }
   
}
