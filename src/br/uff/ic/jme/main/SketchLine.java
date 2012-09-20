package br.uff.ic.jme.main;

import java.util.Arrays;
import java.util.List;

import com.jme3.math.Vector3f;
import com.jme3.scene.shape.Curve;

public class SketchLine extends Curve
{
   /**
    * 
    */
   private static final long serialVersionUID = -279121842766743855L;
   
   public List<Vector3f>          vertexes;
   
   public SketchLine(String string, Vector3f[] controlPoints)
   {            
      super(controlPoints, 1);
      vertexes = Arrays.asList(controlPoints);
   }   
}
