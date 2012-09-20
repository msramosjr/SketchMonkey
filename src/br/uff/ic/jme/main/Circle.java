package br.uff.ic.jme.main;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;

/**
 * Circle.
 *
 * @author Martin Simons
 * @version $Id$
 */
public class Circle
        extends Mesh
{
    /**
     * The center.
     */
    private Vector3f center;
 
    /**
     * The radius.
     */
    private float radius;
 
    /**
     * The samples.
     */
    private int samples;
 
    /**
     * Constructs a new instance of this class.
     *
     * @param radius
     */
    public Circle(float radius)
    {
        this(Vector3f.ZERO, radius, 16);
    }
 
    /**
     * Constructs a new instance of this class.
     *
     * @param radius
     * @param samples
     */
    public Circle(float radius, int samples)
    {
        this(Vector3f.ZERO, radius, samples);
    }
 
    /**
     * Constructs a new instance of this class.
     *
     * @param center
     * @param radius
     * @param samples
     */
    public Circle(Vector3f center, float radius, int samples)
    {
        super();
        this.center = center;
        this.radius = radius;
        this.samples = samples;
 
        setMode(Mode.Lines);
        updateGeometry();
    }
     
    private void updateGeometry()
    {
        FloatBuffer positions = BufferUtils.createFloatBuffer(samples * 3);
        FloatBuffer normals = BufferUtils.createFloatBuffer(samples * 3);
        short[] indices = new short[samples * 2];
 
        float rate = FastMath.TWO_PI / (float) samples;
        float angle = 0;
        for (int i = 0; i < samples; i++)
        {
            float x = FastMath.cos(angle);
            float y = FastMath.sin(angle);
 
            positions.put(center.x + x * radius).put(center.y + y * radius).put(center.z);
            normals.put(new float[] { 0, 1, 0 });
            indices[i * 2] = (short) i;
            if (i < samples - 1)
                indices[i * 2 + 1] = (short) (i + 1);
            else
                indices[i * 2 + 1] = 0;
 
            angle += rate;
        }
 
        setBuffer(Type.Position, 3, positions);
        setBuffer(Type.Normal, 3, normals);
        setBuffer(Type.Index, 2, indices);
 
        setBuffer(Type.TexCoord, 2, new float[] { 0, 0, 1, 1 });
 
        updateBound();
    }
}
