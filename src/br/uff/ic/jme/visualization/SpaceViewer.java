package br.uff.ic.jme.visualization;

import br.uff.ic.jme.interpolation.Interpolator;
import com.jme3.scene.Mesh;

public interface SpaceViewer
{
   
   Mesh generateMesh(Interpolator i);
}
