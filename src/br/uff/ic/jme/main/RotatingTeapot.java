package br.uff.ic.jme.main;

import com.bulletphysics.collision.broadphase.Dbvt.Node;
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

public class RotatingTeapot extends SimpleApplication {
  public Quaternion rotQuat1 = new Quaternion();
  public Quaternion rotQuat2 = new Quaternion();
 
  public float angle = 0;
  public Vector3f axis = new Vector3f(0, 1, 0);
  public Box s;
  //public Sphere moon1;
  public Box moon2;
  public com.jme3.scene.Node pivotNode1 = SceneManager.get().getRootNode();;
  public Node pivotNode2;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    RotatingTeapot app = new RotatingTeapot();
    //app.setConfigShowMode(ConfigShowMode.AlwaysShow);
    app.start();
  }
    private Geometry player;
  
@Override
public void simpleUpdate(float tpf) {
    if (tpf < 1) {
      angle = angle + (tpf * 1);
      if (angle > 360) {
        angle = 0;
      }
    }
    rotQuat1.fromAngleAxis(angle, axis);
    pivotNode1.setLocalRotation(rotQuat1);
   
    rotQuat2.fromAngleAxis(angle * 2, axis);
    //pivotNode2.setLocalRotation(rotQuat2);
   
  }

    @Override
    public void simpleInitApp() {
        //display.setTitle("jME - Rotation About a Point");

    //Planet
    s = new Box(Vector3f.ZERO, 0.2f, 0.2f, 0.2f);// "Planet", 25, 25, 25);
    s.setBound(new BoundingBox());// .setModelBound(new BoundingSphere());
    s.updateBound();//.updateModelBound();
    //rootNode.attachChild(s);
   
    //Moons
    //moon1 = new Sphere("Moon 1",25, 25, 10);
    //moon1.setModelBound(new BoundingSphere());
    //moon1.updateModelBound();
    //moon1.setLocalTranslation(40, 0, 0);
    //pivotNode1 = new Node("PivotNode 1");
    
    player = new Geometry("Box", s);  // create cube geometry from the shape
        Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.Blue);   // set color of material to blue
        player.setMaterial(mat);                   // set the cube's material
    
    pivotNode1.attachChild(player);
    flyCam.setDragToRotate(true);
    /*moon2 = new Box("Moon 2",new Vector3f(0,0,0), new Vector3f(20,20,20));
    //moon2 = new Box("Moon 2",new Vector3f(-10,-10,-10), new Vector3f(10,10,10));
    moon2.setModelBound(new BoundingSphere());
    moon2.updateModelBound();
    //moon2.setLocalTranslation(60, 0, 0);
    pivotNode2 = new Node("PivotNode 2");
    pivotNode2.attachChild(moon2);*/
   
    rootNode.attachChild(pivotNode1);
    //rootNode.attachChild(pivotNode2);
    }
}