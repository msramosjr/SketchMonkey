package br.uff.ic.jme.main;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Quaternion;

public class SketchPlane
{
   public List<SketchLine> vertexesList;
   
   public Quaternion rotation;
   
   public SketchPlane()
   {
      vertexesList = new ArrayList<SketchLine>();
      //rotation = new Quaternion(AppData.getInstance().getViz3DRootNode().getChild("main").getLocalRotation());      
   }
   
   public int size()
   {
      return vertexesList.size();
   }
   
   public SketchLine get(int index)
   {
      return vertexesList.get(index);
   }
}
