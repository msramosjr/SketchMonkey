package br.uff.ic.jme.main;

import com.jme3.asset.AssetManager;
//import com.jme3.image.Texture;
//import com.jme3.input.AbsoluteMouse;
//import com.jme3.light.PointLight;
//import com.jme3.light.SimpleLightNode;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
//import com.jme3.renderer.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
//import com.jme3.scene.Spatial.LightCombineMode;
//import com.jme3.scene.shape.Box;
//import com.jme3.scene.state.BlendState;
//import com.jme3.scene.state.LightState;
//import com.jme3.scene.state.RenderState.StateType;
//import com.jme3.scene.state.TextureState;
//import com.jme3.system.DisplaySystem;
//import com.jme3.util.TextureManager;

public class SceneManager
{
   private Node                rootNode;
   private Node                screenNode;
   private Node                drawingNode;
   private Node                mouseNode;
   private Node                visualizationNode;
   private Node                rotationNode;
   private Node                fixedNode;
   
   public Material controlPointsMaterial;
   
   public Material skeletonDrawingMaterial;
   
   public Material silhouetteDrawingMaterial;
   
   public Material skeletonSegmentMaterial;
   
   public Material triangulationMaterial;
   
   public Material triangulationMaterial2;
   
   private Material meshMaterial;
   
   private Material toonMeshMaterial;
   
   public List<Spatial> undoStack = new ArrayList<Spatial>();
   
   public int lineCount = 0;
   
   public boolean skeletonDrawing = true;
   
   public double skeletonRadius = 4.0;
   
   private boolean debugView = true;
   
   private boolean drawingView = true;
   
   private boolean meshView = true;
   
   public boolean wireMode = false;
   
   public boolean toonMode = false;
   
   public double threshold = 0.7f;
   
   public boolean csgEnabled = true;
   
   public int csgM = 1;
   
   public int csgN = 2;
   
   public void setDebugView(boolean enabled)
   {
       debugView = enabled;
              
       if(enabled)
       {           
           triangulationMaterial.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
           
           triangulationMaterial2.getAdditionalRenderState().setFaceCullMode(FaceCullMode.FrontAndBack);
           
           controlPointsMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Off);           
           controlPointsMaterial.setColor("Color", ColorRGBA.Red);
           
           skeletonSegmentMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Off);
           skeletonSegmentMaterial.setColor("Color", ColorRGBA.Red);
       }
       else
       {           
           triangulationMaterial.getAdditionalRenderState().setFaceCullMode(FaceCullMode.FrontAndBack);
           
           triangulationMaterial2.getAdditionalRenderState().setFaceCullMode(FaceCullMode.FrontAndBack);
           
           controlPointsMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);           
           controlPointsMaterial.setColor("Color", new ColorRGBA(1,1,1,0));
           
           skeletonSegmentMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);           
           skeletonSegmentMaterial.setColor("Color", new ColorRGBA(1,1,1,0));
       }          
   }
   
   public boolean isDebugView()
   {
       return debugView;
   }
   
   public void setDrawingView(boolean enabled)
   {
       drawingView = enabled;
              
       if(enabled)
       {           
           skeletonDrawingMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Off);           
           skeletonDrawingMaterial.setColor("Color", ColorRGBA.Orange);
           
           silhouetteDrawingMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Off);
           silhouetteDrawingMaterial.setColor("Color", ColorRGBA.Black);
       }
       else
       {           
           skeletonDrawingMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);           
           skeletonDrawingMaterial.setColor("Color", new ColorRGBA(1,1,1,0));
           
           silhouetteDrawingMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
           silhouetteDrawingMaterial.setColor("Color", new ColorRGBA(1,1,1,0));           
       }      
   }
   
   public boolean isDrawingView()
   {
       return drawingView;
   }
   
   public void setMeshView(boolean enabled)
   {
       meshView = enabled;
       
       if(enabled)
       {           
           getMeshMaterial().getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
       }
       else
       {           
           getMeshMaterial().getAdditionalRenderState().setFaceCullMode(FaceCullMode.FrontAndBack);
       }   
   }
   
   public Material getMeshMaterial()
   {   
       if(toonMode)
       {
           toonMeshMaterial.getAdditionalRenderState().setWireframe(wireMode);
           return toonMeshMaterial;
       }
       else
       {
           meshMaterial.getAdditionalRenderState().setWireframe(wireMode);
           return meshMaterial;
       }
   }
   
   public boolean isMeshView()
   {
       return meshView;
   }
   
   public AssetManager assetManager;
   public DirectionalLight dl;
//   private AbsoluteMouse       mouse;
   
   /* Singleton */
   private static SceneManager instance;
   
   public static SceneManager get()
   {
      if (instance == null)
         instance = new SceneManager();
      return instance;
   }
   
   /* Singleton - end */
   
   private SceneManager()
   {
      rootNode = new Node("rootNode");
      
      // fixed hierarchy
      fixedNode = new Node("fixedNode");
      screenNode = new Node("screenNode");
      fixedNode.attachChild(screenNode);
      mouseNode = new Node("mouseNode");
      fixedNode.attachChild(mouseNode);
      
      // rotation hierarchy
      rotationNode = new Node("rotationNode");
      drawingNode = new Node("drawingNode");
      rotationNode.attachChild(drawingNode);
      visualizationNode = new Node("visualizationNode");
      rotationNode.attachChild(visualizationNode);
      
      //lighting
      dl = new DirectionalLight();
      dl.setDirection(new Vector3f(0, 0, -1).normalizeLocal());
      dl.setColor(ColorRGBA.Blue);
      visualizationNode.addLight(dl);
      
      rootNode.attachChild(fixedNode);
      rootNode.attachChild(rotationNode);
      
      setUpLight();
      
      setUpMouse();
   }
   
   public void setUpMaterials()
   {
        controlPointsMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        controlPointsMaterial.setColor("Color", ColorRGBA.Red);
        controlPointsMaterial.getAdditionalRenderState().setWireframe(true);
        
        skeletonDrawingMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        skeletonDrawingMaterial.setColor("Color", ColorRGBA.Orange);
        
        silhouetteDrawingMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        silhouetteDrawingMaterial.setColor("Color", ColorRGBA.Black);
        
        skeletonSegmentMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        skeletonSegmentMaterial.setColor("Color", ColorRGBA.Red);

        triangulationMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        triangulationMaterial.setColor("Color", ColorRGBA.Blue);
        triangulationMaterial.getAdditionalRenderState().setWireframe(true); 
        
        triangulationMaterial2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");        
        
        toonMeshMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture t = assetManager.loadTexture("Textures/ColorRamp/toon.png");        
        toonMeshMaterial.setTexture("ColorRamp", t);
        toonMeshMaterial.setBoolean("UseMaterialColors", true);
        toonMeshMaterial.setColor("Specular", ColorRGBA.Black);
        toonMeshMaterial.setColor("Diffuse", ColorRGBA.White);
        toonMeshMaterial.setBoolean("VertexLighting", true);

        meshMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        meshMaterial.setFloat("Shininess", 12);
        meshMaterial.setBoolean("UseMaterialColors", true);
        meshMaterial.setColor("Ambient", ColorRGBA.Black);
        meshMaterial.setColor("Diffuse", new ColorRGBA(0.8f, 0.8f, 0.8f, 1f));
        meshMaterial.setColor("Specular", ColorRGBA.Black);
        
        if (wireMode)
            getMeshMaterial().getAdditionalRenderState().setWireframe(true);
        else
            getMeshMaterial().getAdditionalRenderState().setWireframe(false);
   }
   
   public void updateScene()
   {
//      rootNode.updateRenderState();
//      rootNode.updateWorldBound();
//      rootNode.updateGeometricState(0.0f, true);
//      rootNode.updateModelBound();
   }
      
   private void setUpLight()
   {   
//      PointLight pl=new PointLight();      
//      pl.setDiffuse(ColorRGBA.red.clone());      
//      pl.setEnabled(true);      
//      LightState lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
//      lightState.attach(pl);
//      
//      SimpleLightNode ln=new SimpleLightNode("A node for my pointLight",pl);
//      ln.setLocalTranslation(new Vector3f(0,10,0));
//      // I attach the light's node to my pivot
//      //rootNode.attachChild(ln);
//      
//      Box b=new Box("LightBox",new Vector3f(-.3f,-.3f,-.3f),new Vector3f(.3f,.3f,.3f));      
//      b.setModelBound(new BoundingBox());
//      b.updateModelBound();
//      ln.attachChild(b);
      
      //rootNode.setLightCombineMode(LightCombineMode.CombineFirst);
      //fixedNode.setLightCombineMode(LightCombineMode.CombineFirst);
      
      //rootNode.setRenderState(lightState);
      //rootNode.updateRenderState();
   }

   private void setUpMouse()
   {
      // hide mouse cursor
//      MouseInput.get().setCursorVisible(false);
//            
//      //create absolute mouse
//      mouse = new AbsoluteMouse("Drawing mouse", DisplaySystem.getDisplaySystem().getRenderer().getWidth(), DisplaySystem.getDisplaySystem().getRenderer().getHeight());
//      
//      //define texture state
//      crosshairCursor();
//      
//      //define blend state
//      BlendState bs = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
//      bs.setBlendEnabled(true);
//      bs.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
//      bs.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
//      bs.setTestEnabled(true);
//      bs.setTestFunction(BlendState.TestFunction.GreaterThanOrEqualTo);
//      mouse.setRenderState(bs);
      
      //attach to mouse node
//      mouseNode.attachChild(mouse);
   }
   
   public Node getRootNode()
   {
      return rootNode;
   }
   
   public Node getScreenNode()
   {
      return screenNode;
   }
   
   public Node getDrawingNode()
   {
      return drawingNode;
   }
   
   public Node getMouseNode()
   {
      return mouseNode;
   }
   
   public Node getVisualizationNode()
   {
      return visualizationNode;
   }
   
   public Node getRotationNode()
   {
      return rotationNode;
   }
   
   public Node getFixedNode()
   {
      return fixedNode;
   }
   
//   public AbsoluteMouse getMouse()
//   {
//      return mouse;
//   }

   public void handCursor()
   {
      changeCursor("br/uff/ic/jme/main/hand.png");
   }
   
   public void crosshairCursor()
   {
      changeCursor("br/uff/ic/jme/main/crosshair.png");
   }
   
   private void changeCursor(String url)
   {    
//      TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
//      ts.setEnabled(true);
//      ts.setTexture(TextureManager.loadTexture(Drawing2DHandler.class.getClassLoader().getResource(url), Texture.MinificationFilter.BilinearNearestMipMap, Texture.MagnificationFilter.Bilinear));
//      mouse.clearRenderState(StateType.Texture);
//      mouse.setRenderState(ts);
//      mouseNode.updateRenderState();
   }

    public void Light(int i) {
        
        switch (i)
        {
            case 0:
                dl.setColor(ColorRGBA.Blue);
                break;
            case 1:
                dl.setColor(ColorRGBA.Yellow);
                break;
            case 2:
                dl.setColor(ColorRGBA.Green);
                break;
            case 3:
                dl.setColor(ColorRGBA.Red);
                break;
                
        }
    }
    public void Light(Color color)
    {
        if(color != null)
        {
            ColorRGBA color2 = new ColorRGBA(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, color.getAlpha()/255f);
            dl.setColor(color2);
        }
    }
}
