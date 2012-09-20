package br.uff.ic.jme.main;

import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import darwin.jopenctm.errorhandling.InvalidDataException;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

import br.uff.ic.jme.exception.ModelGenerationException;
import br.uff.ic.jme.exception.SkeletonizationException;
import br.uff.ic.jme.exception.TriangulationException;
import br.uff.ic.jme.skeletonization.Skeleton;
import br.uff.ic.jme.skeletonization.SkeletonGenerator;
import br.uff.ic.jme.visualization.AdaptedMarchingCubesViewer;
import com.jme3.app.SimpleApplication;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Mesh;
import com.jme3.system.AppSettings;

import com.jme3.input.MouseInput;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

import br.uff.ic.jme.rotate.DragPanel;
import br.uff.ic.jme.skeletonization.SkeletonBranch;
import br.uff.ic.jme.skeletonization.SkeletonPoint;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.system.JmeCanvasContext;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
//import sun.awt.CustomCursor;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.post.Filter;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.CartoonEdgeFilter;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.scene.shape.Line;
import com.jme3.util.BufferUtils;
import darwin.jopenctm.compression.MG2Encoder;
import darwin.jopenctm.data.AttributeData;
import darwin.jopenctm.errorhandling.BadFormatException;
import darwin.jopenctm.io.CtmFileWriter;
import darwin.jopenctm.io.CtmFileReader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Callable;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
//import javax.swing.JProgressBar;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import org.poly2tri.triangulation.TriangulationPoint;
//import com.jme3.texture.Image;

public class SketchMonkeyApplication extends SimpleApplication implements AnalogListener, ActionListener, RawInputListener
{

    private static final float zAxis = AppData.zDrawing;
    private Node finishedVisualization;
    private Node visualizationNode;
    protected Node sunNode = new Node("Sun Node");
    private AdaptedMarchingCubesViewer viewer;
    private SkeletonGenerator skeletonGenerator;
    protected Geometry player;
    protected DragPanel mainpanel;
    public double constant;
    public Vector3f actualPosition;
    public DirectionalLight sun;
    private BitmapText cursor;    
    public static JButton colorButton = new JButton("Choose Color");
//    public static JFrame popupFrame = new JFrame("Genereting Model...");
//    public static JButton popupButton = new JButton("Wait Model be Genereted...");
    //public static JProgressBar progressBar = new JProgressBar();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static ButtonGroup renderingOptionsGroup = new ButtonGroup();
    public static ButtonGroup modelingOptionsGroup = new ButtonGroup();
    public static ButtonGroup viewOptionsGroup = new ButtonGroup();
    public static JButton colorPickerButton = new JButton();
    public static JButton generateModelButton = new JButton();
    public static JButton frontViewButton = new JButton();
    public static JButton backViewButton = new JButton();
    public static JButton topViewButton = new JButton();
    public static JButton downViewButton = new JButton();
    public static JButton leftViewButton = new JButton();
    public static JButton rightViewButton = new JButton();
//    public static JButton centerButton = new JButton();
//    public static JButton mirrorButton = new JButton();
    public static JCheckBoxMenuItem debugViewMenuItem = new JCheckBoxMenuItem();
    public static JCheckBoxMenuItem drawingViewMenuItem = new JCheckBoxMenuItem();
    public static JCheckBoxMenuItem meshViewMenuItem = new JCheckBoxMenuItem();
    public static JLabel radiusLabel = new JLabel();
    public static JLabel recursionLevelLabel = new JLabel();
    public static JMenu fileMenu = new JMenu();
    public static JMenu editMenu = new JMenu();
    public static JMenu viewMenu = new JMenu();
    public static JMenuBar menuBar = new JMenuBar();
    public static JMenuItem importMenuItem = new JMenuItem();
    public static JMenuItem exportMenuItem = new JMenuItem();
    public static JMenuItem exitMenuItem = new JMenuItem();
    public static JMenuItem undoMenuItem = new JMenuItem();
    public static JMenuItem redoMenuItem = new JMenuItem();
    public static JMenuItem clearMenuItem = new JMenuItem();    
    public static JPanel mainPanel = new JPanel();
    //public static JPanel progressBarPanel = new JPanel();
    public static JPanel renderingOptionsPanel = new JPanel();
    public static JPanel modelingOptionsPanel = new JPanel();
    public static JPanel viewOptionsPanel = new JPanel();
    public static JPanel toolbarPanel = new JPanel();
    public static JRadioButton defaultShadingRadioButton = new JRadioButton();
    public static JRadioButton toonShadingRadioButton = new JRadioButton();
    public static JRadioButton silhouetteDrawingRadioButton = new JRadioButton();
    public static JRadioButton skeletonDrawingRadioButton = new JRadioButton();
    public static JRadioButton translationModeRadioButton = new JRadioButton();
    public static JRadioButton rotationModeRadioButton = new JRadioButton();
    public static JPopupMenu.Separator jSeparator1 = new JPopupMenu.Separator();
    public static JToolBar.Separator jSeparator2 = new JToolBar.Separator();
    public static JToolBar.Separator jSeparator3 = new JToolBar.Separator();
    public static final SpinnerModel recursionSpinnerModel = new SpinnerNumberModel(
            5, //initial value
            3, //min
            10, //max
            1);//step
    public static final SpinnerModel csgMSpinnerModel = new SpinnerNumberModel(
            1, //initial value
            1, //min
            1000, //max
            2);//step
    public static final SpinnerModel csgNSpinnerModel = new SpinnerNumberModel(
            2, //initial value
            0, //min
            1000, //max
            2);//step
    public static final SpinnerModel radiusSpinnerModel = new SpinnerNumberModel(
            4.0, //initial value
            0.1, //min
            50.0, //max
            0.1);//step
    public static final SpinnerModel thresholdSpinnerModel = new SpinnerNumberModel(
            0.7, //initial value
            0.0, //min
            50.0, //max
            0.05);//step
    public static JSpinner recursionSpinner = new JSpinner(recursionSpinnerModel);    
    public static JToolBar toolBar = new JToolBar();
    public static JSpinner skeletonRadius = new JSpinner(radiusSpinnerModel);
    
    public static JLabel csgMLabel = new JLabel();
    public static JLabel csgNLabel = new JLabel();
    public static JLabel thresholdLabel = new JLabel();
    
    public static JSpinner csgM = new JSpinner(csgMSpinnerModel);
    public static JSpinner csgN = new JSpinner(csgNSpinnerModel);
    public static JSpinner threshold = new JSpinner(thresholdSpinnerModel);
    
    public static JCheckBox wireframeCheckbox = new JCheckBox();
    public static JCheckBox csgEnabled = new JCheckBox();
    // End of variables declaration//GEN-END:variables

    public static JmeCanvasContext ctx;
    public static SketchMonkeyApplication canvasApplication;
    //private int i;
    public static Toolkit toolkit = Toolkit.getDefaultToolkit();
    public static Image cursorImage;
//    public static CustomCursor c;
    private FilterPostProcessor fpp;
    
    private Node meshNode = new Node("meshNode");    
    
    private static JFrame mainFrame;
    //private static JFrame progressBarFrame;
    private static Cursor customHandCursor;
    private static Cursor customCrosshairCursor;
    
    private enum CursorState { DEFAULT, DRAGGING, WAITING, DISABLED }
    
    private static CursorState cursorState = CursorState.DEFAULT;
    
    private static Node selectedNode = null;

    private java.awt.event.ActionListener getChooseColorDialogActionListener()
    {
        return new java.awt.event.ActionListener()
        {

            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cursorState = CursorState.DISABLED;
                Color color = JColorChooser.showDialog(mainPanel, "Choose Color", Color.white);
//                System.out.println(color);
                SceneManager.get().Light(color);
                cursorState = CursorState.DEFAULT;
            }
        };
    }

    private java.awt.event.ActionListener getClearScreenActionListener()
    {
        return new java.awt.event.ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                clearScreen();
            }
        };
    }

    private java.awt.event.ActionListener getExitActionListener()
    {
        return new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                mainFrame.dispose();
                //progressBarFrame.dispose();
                stop();
            }
        };
    }

    private java.awt.event.ActionListener getExportActionListener()
    {
        return new java.awt.event.ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                if (meshNode != null && meshNode.getQuantity() > 0 && meshNode.getChild(0) instanceof Geometry)
                {
                    cursorState = CursorState.DISABLED;
                    JFileChooser fc = new JFileChooser();
                    fc.setAcceptAllFileFilterUsed(false);
                    FileFilter filter = new FileNameExtensionFilter("Open CTM format (*.ctm)", "ctm");
                    fc.setFileFilter(filter);
                    fc.setDialogTitle("Export model");
                    fc.setSelectedFile(new File("model.ctm"));
                    int returnVal = fc.showSaveDialog(mainPanel);

                    if (returnVal == JFileChooser.APPROVE_OPTION)
                    {

                        String pathName = fc.getCurrentDirectory().toString();
//                    sss = "ooooi";
//                    System.out.println(pathName);
                        Mesh implicitMesh = ((Geometry) meshNode.getChild(0)).getMesh();
                        FloatBuffer vBuf = implicitMesh.getFloatBuffer(Type.Position);
                        float[] vertexes = BufferUtils.getFloatArray(vBuf);
                        FloatBuffer nBuf = implicitMesh.getFloatBuffer(Type.Normal);
                        float[] normals = BufferUtils.getFloatArray(nBuf);
                        IndexBuffer indBuf = implicitMesh.getIndexBuffer();
                        Buffer iBuf = indBuf.getBuffer();
                        int[] indexes = BufferUtils.getIntArray((IntBuffer) iBuf);

                        System.out.println("before export");
                        darwin.jopenctm.data.Mesh exportMesh = new darwin.jopenctm.data.Mesh(vertexes, normals, indexes, new AttributeData[0], new AttributeData[0]);
                        try
                        {
                            String fileSuffix = ".ctm";
                            //String pathName = System.getProperty("user.home") + File.separator + "SketchMonkey";
                            //System.out.println(pathName);
                            String fileName = pathName + File.separator + fc.getName(fc.getSelectedFile());//"exportedMesh_";
                            String number;
                            File file = new File(pathName);
                            file.mkdirs();
                            for (int i = 1; i <= 100; i++)
                            {
//                            number = new DecimalFormat("000").format(i);
//                            file = new File(fileName + number + fileSuffix);
                                if (!fileName.trim().endsWith(".ctm"))
                                {
                                    fileName += fileSuffix;
                                }

                                file = new File(fileName.trim());

                                if (!file.exists())
                                {
                                    System.out.println("path: " + file.getPath());
                                    file.createNewFile();
                                    file.setReadable(true);
                                    file.setWritable(true);
                                    OutputStream os = new FileOutputStream(file);
                                    CtmFileWriter writer = new CtmFileWriter(os, new MG2Encoder());
                                    writer.encode(exportMesh, "OK");

                                    os.close();
                                    os = null;
                                    break;
                                }
                            }

                        } catch (IOException ex)
                        {
                            Logger.getLogger(SketchMonkeyApplication.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InvalidDataException ex)
                        {
                            Logger.getLogger(SketchMonkeyApplication.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.out.println("after export");
                        cursorState = CursorState.DEFAULT;
                    }
                }
            }
        };
    }

    private java.awt.event.ActionListener getGenerateModelActionListener()
    {
        return new java.awt.event.ActionListener()
        {

            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                if (SceneManager.get().getDrawingNode().getChildren().size() > 0)
                {
//                    System.out.println("has children");
                    cursorState = CursorState.WAITING;

//                    EventQueue.invokeLater(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            progressBarFrame.setVisible(true);                                                                       
//                        }
//                    });

                    enqueue(new Callable<Object>()
                    {
                        @Override
                        public Object call()
                        {                                   
                            try
                            {                            
                                double radius = (Double)skeletonRadius.getValue();                                
                                SceneManager.get().skeletonRadius = radius;

                                generateModel();
                                
                                drawResult();
                                mainPanel.grabFocus();                            
                            }
                            catch (ModelGenerationException ex)
                            {
                                Logger.getLogger(SketchMonkeyApplication.class.getName()).log(Level.SEVERE, null, ex);                                
                                ctx.getCanvas().setFocusable(true);
                                cursorState = CursorState.DEFAULT;
                                mainPanel.grabFocus();                            
                            }
                            return null;
                        }                                
                    });                                              
                }
            }           
        };
    }

    private java.awt.event.ActionListener getImportActionListener()
    {
        return new java.awt.event.ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                cursorState = CursorState.DISABLED;
                JFileChooser fc = new JFileChooser();
                fc.setAcceptAllFileFilterUsed(false);
                FileFilter filter = new FileNameExtensionFilter("Open CTM format (*.ctm)", "ctm");
                fc.setFileFilter(filter);
                fc.setDialogTitle("Import model");
                fc.setSelectedFile(new File("model.ctm"));
                int returnVal = fc.showOpenDialog(mainPanel);

                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    InputStream is;
                    File file = fc.getSelectedFile();
                    fc.setDialogTitle("Import model");
                    try
                    {
                        is = new FileInputStream(file);
                        CtmFileReader reader = new CtmFileReader(is);
                        Mesh importedMesh = new Mesh();

                        darwin.jopenctm.data.Mesh mesh = reader.decode();
                        importedMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(mesh.vertices));
                        importedMesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(mesh.normals));
                        importedMesh.setBuffer(Type.Index, 1, BufferUtils.createIntBuffer(mesh.indices));

                        Geometry g = new Geometry("implicitMesh", importedMesh);
                        Material mat = getMaterial();

                        g.setMaterial(mat);
                        meshNode.attachChild(g);
                        visualizationNode.detachAllChildren();
                        
                        visualizationNode.attachChild(meshNode);
                        SceneManager.get().lineCount = 0;
                        
                    } catch (IOException ex)
                    {
                        Logger.getLogger(SketchMonkeyApplication.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (BadFormatException ex)
                    {
                        Logger.getLogger(SketchMonkeyApplication.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvalidDataException ex)
                    {
                        Logger.getLogger(SketchMonkeyApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    cursorState = CursorState.DEFAULT;
                }
            }
        };
    }

    private Vector3f getMousePosition()
    {
        return this.getCamera().getWorldCoordinates(this.getInputManager().getCursorPosition(), zAxis);
    }

    public void initInput()
    {
        inputManager.addMapping("draw", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("drag", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        //inputManager.addMapping("generateModel", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("move", new MouseAxisTrigger(MouseInput.AXIS_X, true),
                new MouseAxisTrigger(MouseInput.AXIS_X, false), new MouseAxisTrigger(MouseInput.AXIS_Y, true),
                new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        //inputManager.addMapping("ZoomIn", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        //inputManager.addMapping("ZoomOut", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));

        inputManager.addListener(new Drawing2DHandler(this), new String[]
                {
                    "draw", "move"
                });
        inputManager.addListener(this, new String[]
                {
                    "drag"
                });
        //inputManager.setCursorVisible(false);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf)
    {
        if (name.equals("drag"))
        {
            cursorState = CursorState.DRAGGING;
//            ctx.getCanvas().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            //cursor.setText("<- ->");
            if (isPressed)
            {
                actualPosition = getMousePosition();
            } else
            {
                actualPosition = null;
                selectedNode = null;
                //cursor.setText("+");
                cursorState = CursorState.DEFAULT;
//                ctx.getCanvas().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf)
    {
        float zoomConst = 0.015f * 10f / AppData.worldLimits.getZExtent();
        if (name.equals("ZoomIn"))
        {
            cam.setViewPort(cam.getViewPortLeft() - zoomConst, cam.getViewPortRight() + zoomConst, cam.getViewPortBottom() - zoomConst, cam.getViewPortTop() + zoomConst);
            //viewPort.setBackgroundColor(ColorRGBA.White);
            //cam.clearViewportChanged();            
            System.out.println("ZOOM");

        } else if (name.equals("ZoomOut"))
        {
            cam.setViewPort(cam.getViewPortLeft() + zoomConst, cam.getViewPortRight() - zoomConst, cam.getViewPortBottom() + zoomConst, cam.getViewPortTop() - zoomConst);
            //viewPort.setBackgroundColor(ColorRGBA.White);
            //cam.clearViewportChanged();
        } else if (name.equals("drag"))
        {
            if (translationModeRadioButton.isSelected())
            {
                arcBall(tpf);
            } else if (rotationModeRadioButton.isSelected())
            {
                moveSketch();
            }
        }
    }

    void arcBall(float tpf)
    {

        if (actualPosition != null)
        {
            Node rotationNode = SceneManager.get().getRotationNode();
            Vector3f xAxis = rotationNode.worldToLocal(Vector3f.UNIT_Y, null);
            Quaternion quaternion = new Quaternion();
            rotationNode.rotate(quaternion.fromAngleAxis((float) -(actualPosition.x - getMousePosition().x) * tpf * (14) * 10f / AppData.worldLimits.getXExtent(), xAxis));

            Vector3f yAxis = rotationNode.worldToLocal(Vector3f.UNIT_X, null);
            Quaternion quaternion2 = new Quaternion();
            rotationNode.rotate(quaternion2.fromAngleAxis((float) (actualPosition.y - getMousePosition().y) * tpf * (14) * 10f / AppData.worldLimits.getYExtent(), yAxis));
        }

        actualPosition = getMousePosition();
    }

    void moveSketch()
    {
        System.out.println("move");
        if (actualPosition != null)
        {
            //clear color of all sketches
            for (Spatial s1 : SceneManager.get().getDrawingNode().getChildren())
            {
                if (s1 instanceof Node)
                {
                    Node n = (Node) s1;
                    for (Spatial s : n.getChildren())
                    {
                        if (s instanceof Geometry)
                        {
                            Geometry g = (Geometry) s;
                            if (g.getMesh() instanceof SketchLine)
                            {
                                if("silhuetteLine".equals(g.getName()))
                                    g.getMaterial().setColor("Color", ColorRGBA.Black);
                                else if("skeletonLine".equals(g.getName()))
                                    g.getMaterial().setColor("Color", ColorRGBA.Orange);
                            }
                        }
                    }
                }
            }

            // 1. Reset results list.
            CollisionResults results = new CollisionResults();
            if(selectedNode == null)
            {                
                // 2. Aim the ray from cam loc to cam direction.
                Ray ray = new Ray(new Vector3f(getMousePosition().x, getMousePosition().y, AppData.worldLimits.getZExtent() * 2), new Vector3f(0f, 0f, -1f));
                // 3. Collect intersections between Ray and Shootables in results list.

                //verify mesh collision
                SceneManager.get().getDrawingNode().collideWith(ray, results);
            }
            //selectedNode = null;
            if (selectedNode != null || results.size() > 0)
            {
                if(results != null && results.size() > 0)
                {
                    Geometry geomCollider = results.getClosestCollision().getGeometry();
                    selectedNode = geomCollider.getParent();                
                }

                System.out.println("fixed: " + selectedNode.getUserData("fixed"));
                if (selectedNode.getUserData("fixed") == null || selectedNode.getUserData("fixed").toString().equals("0"))
                {
                    selectedNode = selectedNode;
                    //paint selected sketch
                    for (Spatial s : selectedNode.getChildren())
                    {
                        if (s instanceof Geometry)
                        {
                            Geometry g = (Geometry) s;
                            if (g.getMesh() instanceof SketchLine)
                            {
                                g.getMaterial().setColor("Color", ColorRGBA.Red);
                            }
                        }
                    }

                    float deltaX = (float) -(actualPosition.x - getMousePosition().x);
                    float deltaY = (float) -(actualPosition.y - getMousePosition().y);
                    for (Spatial s : selectedNode.getChildren())
                    {
                        Vector3f in = new Vector3f(s.getWorldTranslation().x + deltaX, s.getWorldTranslation().y + deltaY, s.getWorldTranslation().z);
                        Vector3f store = new Vector3f();
                        selectedNode.worldToLocal(in, store);
                        s.setLocalTranslation(store);
                    }
                }
            }
        }

        actualPosition = getMousePosition();
    }

    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {   
                initCanvasApplication();
                initMainFrame();
                initCustomCursor();
                initMenuBar();
                initToolBar();
                initViewOptionsPanel();
                initRenderingOptionsPanel();
                initModelingOptionsPanel();                
                finalizeMainFrame();
                //initProgressBarFrame();
            }

            private void initCustomCursor() throws HeadlessException, IndexOutOfBoundsException
            {                
                customHandCursor = toolkit.createCustomCursor(toolkit.getImage(SketchMonkeyApplication.class.getResource("hand.png")), new Point(16,16), "ClosedHand");
                customCrosshairCursor = toolkit.createCustomCursor(toolkit.getImage(SketchMonkeyApplication.class.getResource("crosshair.png")), new Point(16,16), "ClosedHand");
            }

//            private void initProgressBarFrame() throws SecurityException, HeadlessException
//            {
////                progressBarFrame = new JFrame("GeneratingModel");
////                progressBarFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
////                progressBarPanel.setBorder(BorderFactory.createTitledBorder("Generating model..."));
////                GroupLayout jPanel7Layout = new GroupLayout(progressBarPanel);
////                progressBarPanel.setLayout(jPanel7Layout);
////                jPanel7Layout.setHorizontalGroup(
////                    jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
////                    .addGroup(jPanel7Layout.createSequentialGroup()
////                        .addContainerGap()
////                        .addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
////                        .addContainerGap())
////                );
////                jPanel7Layout.setVerticalGroup(
////                    jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
////                    .addGroup(jPanel7Layout.createSequentialGroup()
////                        .addContainerGap()
////                        .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
////                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
////                );
////                progressBarPanel.getAccessibleContext().setAccessibleName("GeneratingModel");
////                progressBarFrame.getContentPane().add(progressBarPanel, BorderLayout.CENTER);
////                progressBarFrame.setResizable(false);
////                progressBarFrame.setAlwaysOnTop(true);
////                progressBarFrame.setLocation((mainFrame.getWidth() / 2), (mainFrame.getHeight() / 2)); 
////                progressBar.setIndeterminate(true);
////                progressBarFrame.pack();
////                progressBarFrame.setVisible(false);
//            }

            private void initModelingOptionsPanel()
            {
                modelingOptionsPanel.setBorder(BorderFactory.createTitledBorder("Modeling"));

                modelingOptionsGroup.add(silhouetteDrawingRadioButton);
                silhouetteDrawingRadioButton.setText("Silhouette");
                silhouetteDrawingRadioButton.setSelected(true);

                modelingOptionsGroup.add(skeletonDrawingRadioButton);
                skeletonDrawingRadioButton.setText("Skeleton");

                radiusLabel.setText("Radius");                
                
                GroupLayout jPanel5Layout = new GroupLayout(modelingOptionsPanel);
                modelingOptionsPanel.setLayout(jPanel5Layout);
                jPanel5Layout.setHorizontalGroup(
                        jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(skeletonDrawingRadioButton)
                                .addComponent(silhouetteDrawingRadioButton)
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                    .addGap(8, 8, 8).addComponent(radiusLabel)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(skeletonRadius, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                                )
                            )
                            .addContainerGap(10, Short.MAX_VALUE)
                        ));
                jPanel5Layout.setVerticalGroup(
                        jPanel5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(silhouetteDrawingRadioButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(skeletonDrawingRadioButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(radiusLabel)
                                    .addComponent(skeletonRadius, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                )
                            .addContainerGap()
                            )
                        );
            }

            private void initRenderingOptionsPanel()
            {
                renderingOptionsPanel.setBorder(BorderFactory.createTitledBorder("Rendering"));
                
                renderingOptionsGroup.add(defaultShadingRadioButton);
                defaultShadingRadioButton.setText("Default Shading");
                defaultShadingRadioButton.setFocusable(false);
                defaultShadingRadioButton.setHorizontalTextPosition(SwingConstants.RIGHT);
                defaultShadingRadioButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                defaultShadingRadioButton.setSelected(true);
            
                renderingOptionsGroup.add(toonShadingRadioButton);
                toonShadingRadioButton.setText("Toon Shading");
                toonShadingRadioButton.setFocusable(false);
                toonShadingRadioButton.setHorizontalTextPosition(SwingConstants.RIGHT);
                toonShadingRadioButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                
                wireframeCheckbox.setText("Wireframe");

                recursionLevelLabel.setText("MC recursion level");
                
                thresholdLabel.setText("Prune threshold");
                
                csgEnabled.setText("CSG");
                csgEnabled.setSelected(true);                
                csgMLabel.setText("m");
                csgNLabel.setText("n");                              

                GroupLayout jPanel4Layout = new GroupLayout(renderingOptionsPanel);
                renderingOptionsPanel.setLayout(jPanel4Layout);
                jPanel4Layout.setHorizontalGroup(
                    jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)                                    
                            .addComponent(defaultShadingRadioButton)            
                            .addComponent(toonShadingRadioButton)
                            .addComponent(wireframeCheckbox)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(recursionLevelLabel)
                                .addGap(8, 8, 8)
                                .addComponent(recursionSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)                                
                            )
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(thresholdLabel)
                                .addGap(8, 8, 8)
                                .addComponent(threshold, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)                                
                            )
                            .addComponent(csgEnabled)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(csgMLabel)
                                .addGap(8, 8, 8)
                                .addComponent(csgM, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)                                
                            )
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(csgNLabel)
                                .addGap(8, 8, 8)
                                .addComponent(csgN, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)                                
                            )
                        )
                        .addContainerGap(10, Short.MAX_VALUE)
                    )
                );
                jPanel4Layout.setVerticalGroup(
                    jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(defaultShadingRadioButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(toonShadingRadioButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(wireframeCheckbox)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(recursionLevelLabel)
                            .addComponent(recursionSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        )
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(thresholdLabel)
                            .addComponent(threshold, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        )
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(csgEnabled)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(csgMLabel)
                            .addComponent(csgM, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        )
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(csgNLabel)
                            .addComponent(csgN, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        )
                    )
                );
            }

            private void initViewOptionsPanel()
            {                   
                viewOptionsPanel.setBorder(BorderFactory.createTitledBorder("Control"));

                viewOptionsGroup.add(translationModeRadioButton);
                translationModeRadioButton.setText("Rotation");
                translationModeRadioButton.setSelected(true);

                viewOptionsGroup.add(rotationModeRadioButton);
                rotationModeRadioButton.setText("Translation");

                GroupLayout jPanel6Layout = new GroupLayout(viewOptionsPanel);
                viewOptionsPanel.setLayout(jPanel6Layout);
                jPanel6Layout.setHorizontalGroup(
                    jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(translationModeRadioButton)
                            .addComponent(rotationModeRadioButton))
                        .addContainerGap(10, Short.MAX_VALUE))
                );
                jPanel6Layout.setVerticalGroup(
                    jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(translationModeRadioButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rotationModeRadioButton))
                );
            }

            private void initToolBar()
            {
                toolBar.setBorder(BorderFactory.createBevelBorder(0));
                toolBar.setRollover(true);

                generateModelButton.setText("Generate");
                generateModelButton.setFocusable(false);
                generateModelButton.setHorizontalTextPosition(SwingConstants.CENTER);
                generateModelButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                toolBar.add(generateModelButton);
                toolBar.add(jSeparator2);

                colorPickerButton.setText("Color");
                colorPickerButton.setFocusable(false);
                colorPickerButton.setHorizontalTextPosition(SwingConstants.CENTER);
                colorPickerButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                toolBar.add(colorPickerButton);
                toolBar.add(jSeparator3);

                frontViewButton.setText("Front");
                frontViewButton.setFocusable(false);
                frontViewButton.setHorizontalTextPosition(SwingConstants.CENTER);
                frontViewButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                toolBar.add(frontViewButton);
                
                backViewButton.setText("Back");
                backViewButton.setFocusable(false);
                backViewButton.setHorizontalTextPosition(SwingConstants.CENTER);
                backViewButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                toolBar.add(backViewButton);
                
                topViewButton.setText("Top");
                topViewButton.setFocusable(false);
                topViewButton.setHorizontalTextPosition(SwingConstants.CENTER);
                topViewButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                toolBar.add(topViewButton);
                
                downViewButton.setText("Down");
                downViewButton.setFocusable(false);
                downViewButton.setHorizontalTextPosition(SwingConstants.CENTER);
                downViewButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                toolBar.add(downViewButton);
                
                leftViewButton.setText("Left");
                leftViewButton.setFocusable(false);
                leftViewButton.setHorizontalTextPosition(SwingConstants.CENTER);
                leftViewButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                toolBar.add(leftViewButton);
                
                rightViewButton.setText("Right");
                rightViewButton.setFocusable(false);
                rightViewButton.setHorizontalTextPosition(SwingConstants.CENTER);
                rightViewButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                toolBar.add(rightViewButton);
                
//                centerButton.setText("Center");
//                centerButton.setFocusable(false);
//                centerButton.setHorizontalTextPosition(SwingConstants.CENTER);
//                centerButton.setVerticalTextPosition(SwingConstants.BOTTOM);
//                toolBar.add(centerButton);
//                
//                mirrorButton.setText("Mirror");
//                mirrorButton.setFocusable(false);
//                mirrorButton.setHorizontalTextPosition(SwingConstants.CENTER);
//                mirrorButton.setVerticalTextPosition(SwingConstants.BOTTOM);
//                toolBar.add(mirrorButton);
                                
                mainFrame.add(toolBar, BorderLayout.PAGE_START);
            }

            private void initMenuBar()
            {
                mainFrame.setJMenuBar(menuBar);
                
                //JMENU 1//
                fileMenu.setText("File");
                importMenuItem.setText("Import");
                fileMenu.add(importMenuItem);
                
                exportMenuItem.setText("Export");
                fileMenu.add(exportMenuItem);
                fileMenu.add(jSeparator1);       

                exitMenuItem.setText("Exit");        
                fileMenu.add(exitMenuItem);
                menuBar.add(fileMenu);

                //END JMENU 1//

                //JMENU 2//
                editMenu.setText("Edit");
                undoMenuItem.setText("Undo");        
                editMenu.add(undoMenuItem);
                
                redoMenuItem.setText("Redo");        
                editMenu.add(redoMenuItem);

                clearMenuItem.setText("Clear");        
                editMenu.add(clearMenuItem);
                menuBar.add(editMenu);

                //END JMENU 2//

                //JMENU 3//

                viewMenu.setText("View");
                debugViewMenuItem.setSelected(true);
                debugViewMenuItem.setText("Debug");        
                viewMenu.add(debugViewMenuItem);

                drawingViewMenuItem.setSelected(true);
                drawingViewMenuItem.setText("Drawing");        
                viewMenu.add(drawingViewMenuItem);

                meshViewMenuItem.setSelected(true);
                meshViewMenuItem.setText("Mesh");        
                viewMenu.add(meshViewMenuItem);

                menuBar.add(viewMenu);
            }

            private void finalizeMainFrame()
            {
                GroupLayout layout = new GroupLayout(mainPanel);
                mainPanel.setLayout(layout);
                layout.setAutoCreateGaps(true);
                layout.setAutoCreateContainerGaps(true);
                                
                layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(viewOptionsPanel)
                        .addComponent(modelingOptionsPanel)
                        .addComponent(renderingOptionsPanel)
                    )
                    .addComponent(ctx.getCanvas())
                );
                
                layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(viewOptionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(modelingOptionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(renderingOptionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        )
                        .addComponent(ctx.getCanvas())
                );
                              
                mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);
                // Aqui nao permitimos que o usuario altere o tamnho da janela
                mainFrame.setResizable(false);
                //mainFrame.add(canvasPanel);
                mainFrame.pack();
                mainFrame.setVisible(true);
                canvasApplication.startCanvas();                
            }

            private void initMainFrame() throws HeadlessException
            {
                //ctx.getCanvas().setCursor(c);

                mainFrame = new JFrame("SketchMonkey");
                mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }

            private void initCanvasApplication()
            {
                //final SketchMonkeyApplication game = new SketchMonkeyApplication();
                //game.setShowSettings(false);
                AppSettings settings = new AppSettings(true);
                settings.setAudioRenderer(null);
                settings.setFrameRate(30);
                //settings.setResolution(800, 600);
                settings.setTitle("SketchMonkey");
                settings.setVSync(true);
                //game.setSettings(settings);
                //game.start();

                canvasApplication = new SketchMonkeyApplication();
                canvasApplication.setSettings(settings);
                canvasApplication.setPauseOnLostFocus(false);
                canvasApplication.createCanvas(); // create canvas!
                ctx = (JmeCanvasContext) canvasApplication.getContext();
                ctx.setSystemListener(canvasApplication);
                Dimension dim = new Dimension(600, 600);
                ctx.getCanvas().setPreferredSize(dim);
//                ctx.getCanvas().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                cursorState = CursorState.DEFAULT;
            }
        });
    }

    public void initSettings()
    {
        viewPort.setBackgroundColor(ColorRGBA.White);

        flyCam.setEnabled(false);

        viewPort.detachScene(rootNode);
        rootNode = SceneManager.get().getRootNode();
        visualizationNode = SceneManager.get().getVisualizationNode();
        viewPort.attachScene(rootNode);

        cam.setParallelProjection(true);

        Vector3f worldCenter = AppData.worldLimits.getCenter();
        Vector3f extent = AppData.worldLimits.getExtent(null);
        float cubeDiagonal = AppData.worldLimits.getMin(null).distance(AppData.worldLimits.getMax(null));
        cam.setFrustum(worldCenter.z - cubeDiagonal / 2f, worldCenter.z + cubeDiagonal / 2f, worldCenter.x - extent.x, worldCenter.x + extent.x, worldCenter.y + extent.y, worldCenter.y - extent.y);
        cam.setLocation(new Vector3f(worldCenter.x, worldCenter.y, worldCenter.z));
        System.out.println(cam.getFrustumNear() + ", " + cam.getFrustumFar());
    }

    private void clearScreen()
    {
        //thread-safe method
        //http://jmonkeyengine.org/wiki/doku.php/jme3:advanced:multithreading
        enqueue(new Callable<Object>()
        {
            @Override
            public Object call() throws Exception
            {
                SceneManager.get().getVisualizationNode().detachAllChildren();
                visualizationNode.detachAllChildren();
                meshNode.detachAllChildren();
                SceneManager.get().getDrawingNode().detachAllChildren();
                SceneManager.get().getRotationNode().setLocalRotation(Quaternion.DIRECTION_Z);
                Spatial gridLineX = SceneManager.get().getScreenNode().getChild("gridLineX");
                Spatial gridLineY = SceneManager.get().getScreenNode().getChild("gridLineY");
                SceneManager.get().getScreenNode().detachAllChildren();
                SceneManager.get().getScreenNode().attachChild(gridLineX);
                SceneManager.get().getScreenNode().attachChild(gridLineY);
                skeletonGenerator = null;
                viewer = new AdaptedMarchingCubesViewer(AppData.worldLimits);
                SceneManager.get().undoStack.clear();
                return null;
            }
        });
    }

    @Override
    public void simpleInitApp()
    {
        initSettings();

        initInput();
        
        mainFrame.addWindowListener(new WindowListener(){
                    public void windowOpened(WindowEvent e) {                        
                    }
                    public void windowClosing(WindowEvent e) {                        
                    }
                    public void windowClosed(WindowEvent e) {
                        //progressBarFrame.dispose();
                        stop();
                    }
                    public void windowIconified(WindowEvent e) {                        
                    }

                    public void windowDeiconified(WindowEvent e) {                        
                    }

                    public void windowActivated(WindowEvent e) {                        
                    }

                    public void windowDeactivated(WindowEvent e) {                        
                    }                    
                });

        SceneManager.get().toonMode = toonShadingRadioButton.isSelected();
        SceneManager.get().wireMode = wireframeCheckbox.isSelected();
        
        SceneManager.get().csgEnabled = csgEnabled.isSelected();
        SceneManager.get().csgM = (Integer) csgM.getValue();
        SceneManager.get().csgN = (Integer) csgN.getValue();
        SceneManager.get().threshold = (Double) threshold.getValue();

        SceneManager.get().skeletonDrawing = skeletonDrawingRadioButton.isSelected();
        skeletonRadius.setEnabled(skeletonDrawingRadioButton.isSelected());        
        
        setupToonShading();

        SceneManager.get().assetManager = assetManager;

        SceneManager.get().setUpMaterials();

        SceneManager.get().setDebugView(debugViewMenuItem.isSelected());
        SceneManager.get().setDrawingView(drawingViewMenuItem.isSelected());
        SceneManager.get().setMeshView(meshViewMenuItem.isSelected());

        //Cursor
//        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
//        cursor = new BitmapText(guiFont, false);
//        cursor.setSize(30);
//        cursor.setText("+");
//        cursor.setColor(ColorRGBA.Black);
//        cursor.setLocalTranslation(0, 0, 0);
        //guiNode.attachChild(cursor);

        inputManager.addRawInputListener(this);
        //inputManager.setCursorVisible(false);
        initActionListeners();

        viewer = new AdaptedMarchingCubesViewer(AppData.worldLimits);
        
        // Setamos aqui a posicao do canvas, na mao.
        //ctx.getCanvas().setLocation(283, ctx.getCanvas().getLocation().y);
//        ctx.getCanvas().setLocation(230, 66);
        //ctx.getCanvas().setLocation(1, 1);

//        finishModel.addActionListener(new java.awt.event.ActionListener() {
//
//            public void actionPerformed(ActionEvent ae) {
//                try
//                    {
//                        finishModel();
//                        panel.grabFocus();                        
//                    }
//                    catch (ModelGenerationException ex) 
//                    {
//                        Logger.getLogger(SketchMonkeyApplication.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//            }
//        });
//        
//        blendModels.addActionListener(new java.awt.event.ActionListener() {
//
//            public void actionPerformed(ActionEvent ae) {
//                blendModels();
//            }
//        });
    }

    private void initActionListeners()
    {
        //ADD ACTION LISTENERS
        try
        {
            importMenuItem.addActionListener(getImportActionListener());
            exportMenuItem.addActionListener(getExportActionListener());
        } catch (UnsupportedClassVersionError e)
        {
        }        
        exitMenuItem.addActionListener(getExitActionListener());
        undoMenuItem.addActionListener(getUndoActionListener());
        redoMenuItem.addActionListener(getRedoActionListener());
        clearMenuItem.addActionListener(getClearScreenActionListener());
        debugViewMenuItem.addActionListener(getViewDebugActionListener());
        drawingViewMenuItem.addActionListener(getViewDrawingActionListener());
        meshViewMenuItem.addActionListener(getViewMeshActionListener());       
        generateModelButton.addActionListener(getGenerateModelActionListener());
        colorPickerButton.addActionListener(getChooseColorDialogActionListener());
        frontViewButton.addActionListener(getFrontViewActionListener());
        backViewButton.addActionListener(getBackViewActionListener());
        topViewButton.addActionListener(getTopViewActionListener());
        downViewButton.addActionListener(getDownViewActionListener());
        leftViewButton.addActionListener(getLeftViewActionListener());
        rightViewButton.addActionListener(getRightViewActionListener());
//        centerButton.addActionListener(getCenterActionListener());
//        mirrorButton.addActionListener(getMirrorActionListener());
        
        wireframeCheckbox.addActionListener(getWireframeActionListener());
        toonShadingRadioButton.addActionListener(getToonShadingActionListener());
        defaultShadingRadioButton.addActionListener(getToonShadingActionListener());
        
        csgEnabled.addActionListener(getCSGActionListener());
        csgM.addChangeListener(getCSGMChangeListener());
        csgN.addChangeListener(getCSGNChangeListener());
        threshold.addChangeListener(getThresholdChangeListener());
        
        silhouetteDrawingRadioButton.addActionListener(getSkeletonDrawingActionListener());
        skeletonDrawingRadioButton.addActionListener(getSkeletonDrawingActionListener());        
        recursionSpinner.addChangeListener(getRecursionLevelChangeListener());
//        END ADD ACTION LISTENERS
    }
    
    private java.awt.event.ActionListener getCSGActionListener()
    {
        return new java.awt.event.ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                SceneManager.get().csgEnabled = csgEnabled.isSelected();
            }
        };
    }

    private java.awt.event.ActionListener getViewMeshActionListener()
    {
        return new java.awt.event.ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                SceneManager.get().setMeshView(meshViewMenuItem.isSelected());
            }
        };
    }

    private java.awt.event.ActionListener getViewDrawingActionListener()
    {
        return new java.awt.event.ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                SceneManager.get().setDrawingView(drawingViewMenuItem.isSelected());
            }
        };
    }

    private java.awt.event.ActionListener getViewDebugActionListener()
    {
        return new java.awt.event.ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                SceneManager.get().setDebugView(debugViewMenuItem.isSelected());
            }
        };
    }

    private java.awt.event.ActionListener getRedoActionListener()
    {
        return new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
//thread-safe method
//http://jmonkeyengine.org/wiki/doku.php/jme3:advanced:multithreading
                enqueue(new Callable<Object>()
                {
                    @Override
                    public Object call() throws Exception
                    {
                        if (!SceneManager.get().undoStack.isEmpty())
                        {
                            SceneManager.get().lineCount++;
                            SceneManager.get().getDrawingNode().attachChild(SceneManager.get().undoStack.remove(SceneManager.get().undoStack.size() - 1));
                        }
                        return null;
                    }
                });
            }
        };
    }

    private java.awt.event.ActionListener getUndoActionListener()
    {
        return new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
//thread-safe method
//http://jmonkeyengine.org/wiki/doku.php/jme3:advanced:multithreading
                enqueue(new Callable<Object>()
                {
                    @Override
                    public Object call() throws Exception
                    {
                        if (SceneManager.get().getDrawingNode().getQuantity() > 0)
                        {
                            SceneManager.get().undoStack.add(SceneManager.get().getDrawingNode().detachChildAt(SceneManager.get().getDrawingNode().getQuantity() - 1));
                            visualizationNode.detachAllChildren();
                            if (SceneManager.get().lineCount <= 0)
                            {
                                meshNode.detachAllChildren();
                                skeletonGenerator = null;
                                viewer = new AdaptedMarchingCubesViewer(AppData.worldLimits);

                                //allow translation mode again on previously fixed nodes
                                // 1) get drawing node
                                Node node = SceneManager.get().getDrawingNode();
                                // 2) iterate over drawing planes        
                                for (Spatial plane : node.getChildren())
                                {
                                    if (!(plane instanceof Node))
                                    {
                                        continue;
                                    }
                                    Node planeNode = (Node) plane;
                                    planeNode.setUserData("fixed", 0);
                                }
                                //update items in undoStack
                                for (Spatial plane : SceneManager.get().undoStack)
                                {
                                    if (!(plane instanceof Node))
                                    {
                                        continue;
                                    }
                                    Node planeNode = (Node) plane;
                                    planeNode.setUserData("fixed", 0);
                                }
                            } else
                            {
                                SceneManager.get().lineCount--;
                                visualizationNode.attachChild(meshNode);
                            }
                        }
                        return null;
                    }
                });
            }
        };
    }
    
//    private java.awt.event.ActionListener getMirrorActionListener()
//    {
//        return new java.awt.event.ActionListener()
//        {
//            public void actionPerformed(java.awt.event.ActionEvent evt)
//            {
////thread-safe method
////http://jmonkeyengine.org/wiki/doku.php/jme3:advanced:multithreading
//                enqueue(new Callable<Object>()
//                {
//                    @Override
//                    public Object call() throws Exception
//                    {
//                        if(selectedNode != null)
//                        {
//                            List<Spatial> mirror = new ArrayList<Spatial>();
//                            for (Spatial s : selectedNode.getChildren())
//                            {
//                                Spatial copy = s.clone();
//                                Vector3f pos = s.getWorldTranslation();
//                                Vector3f out = new Vector3f();
//                                selectedNode.worldToLocal(pos, out);                                
//                                copy.setLocalTranslation(-out.x, out.y, out.z);
//                                mirror.add(copy);
//                            }
//                            for(Spatial s : mirror)
//                            {
//                                selectedNode.attachChild(s);
//                            }
//                            selectedNode = null;
//                        }
//                        return null;
//                    }
//                });
//            }
//        };
//    }

    private java.awt.event.ActionListener getFrontViewActionListener()
    {
        return new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
//thread-safe method
//http://jmonkeyengine.org/wiki/doku.php/jme3:advanced:multithreading
                enqueue(new Callable<Object>()
                {

                    @Override
                    public Object call() throws Exception
                    {
                        SceneManager.get().getRotationNode().lookAt(new Vector3f(0,0,1), new Vector3f(0,1,0));
                        return null;
                    }
                });
            }
        };
    }
    
    private java.awt.event.ActionListener getBackViewActionListener()
    {
        return new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
//thread-safe method
//http://jmonkeyengine.org/wiki/doku.php/jme3:advanced:multithreading
                enqueue(new Callable<Object>()
                {

                    @Override
                    public Object call() throws Exception
                    {
                        SceneManager.get().getRotationNode().lookAt(new Vector3f(0,0,-1), new Vector3f(0,1,0));
                        return null;
                    }
                });
            }
        };
    }
    
    private java.awt.event.ActionListener getTopViewActionListener()
    {
        return new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
//thread-safe method
//http://jmonkeyengine.org/wiki/doku.php/jme3:advanced:multithreading
                enqueue(new Callable<Object>()
                {

                    @Override
                    public Object call() throws Exception
                    {
                        SceneManager.get().getRotationNode().lookAt(new Vector3f(0,-1,0), new Vector3f(0,0,1));
                        return null;
                    }
                });
            }
        };
    }
    
    private java.awt.event.ActionListener getDownViewActionListener()
    {
        return new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
//thread-safe method
//http://jmonkeyengine.org/wiki/doku.php/jme3:advanced:multithreading
                enqueue(new Callable<Object>()
                {

                    @Override
                    public Object call() throws Exception
                    {
                        SceneManager.get().getRotationNode().lookAt(new Vector3f(0,1,0), new Vector3f(0,0,-1));
                        return null;
                    }
                });
            }
        };
    }
    
    private java.awt.event.ActionListener getLeftViewActionListener()
    {
        return new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
//thread-safe method
//http://jmonkeyengine.org/wiki/doku.php/jme3:advanced:multithreading
                enqueue(new Callable<Object>()
                {

                    @Override
                    public Object call() throws Exception
                    {
                        SceneManager.get().getRotationNode().lookAt(new Vector3f(1,0,0), new Vector3f(0,1,0));
                        return null;
                    }
                });
            }
        };
    }
    
    private java.awt.event.ActionListener getRightViewActionListener()
    {
        return new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
//thread-safe method
//http://jmonkeyengine.org/wiki/doku.php/jme3:advanced:multithreading
                enqueue(new Callable<Object>()
                {

                    @Override
                    public Object call() throws Exception
                    {
                        SceneManager.get().getRotationNode().lookAt(new Vector3f(-1,0,0), new Vector3f(0,1,0));
                        return null;
                    }
                });
            }
        };
    }

    private java.awt.event.ActionListener getSkeletonDrawingActionListener()
    {
        return new java.awt.event.ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                SceneManager.get().skeletonDrawing = skeletonDrawingRadioButton.isSelected();
                skeletonRadius.setEnabled(skeletonDrawingRadioButton.isSelected());
            }
        };
    }

    private ChangeListener getRecursionLevelChangeListener()
    {
        return new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent ce)
            {
                viewer.setRecursionLevel((Integer) recursionSpinner.getValue());
            }
        };
    }
    
    private ChangeListener getCSGMChangeListener()
    {
        return new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent ce)
            {
                SceneManager.get().csgM = (Integer) csgM.getValue();
            }
        };
    }
    
    private ChangeListener getCSGNChangeListener()
    {
        return new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent ce)
            {
                SceneManager.get().csgN = (Integer) csgN.getValue();
            }
        };
    }
    
    private ChangeListener getThresholdChangeListener()
    {
        return new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent ce)
            {
                enqueue(new Callable<Object>()
                {
                    @Override
                    public Object call()
                    {
                        SceneManager.get().threshold = (Double) threshold.getValue();

                        List<Spatial> planes = SceneManager.get().getDrawingNode().getChildren();
                        for (Spatial plane : planes)
                        {
                            if(!(plane instanceof Node))
                                continue;
                            
                            Node n = (Node) plane;
                            List<Spatial> removedChildren = new ArrayList<Spatial>();
                            List<Vector3f> pointsv = new ArrayList<Vector3f>();
                            for (Spatial s : n.getChildren())
                            {
                                if(!(s instanceof Geometry))
                                    continue;

                                Geometry g = (Geometry) s;
                                if("skeletonSegmentLine".equals(g.getName()))
                                {
                                    removedChildren.add(g);
                                }
                                else if("silhuetteLine".equals(g.getName()))
                                {                                    
                                    pointsv.addAll(Arrays.asList(BufferUtils.getVector3Array(g.getMesh().getFloatBuffer(Type.Position))));                                                                      
                                }
                            }
                            System.out.println("pointsv: " + pointsv.size());
                            for (Spatial c : removedChildren)
                            {
                                n.detachChild(c);
                            }
                            try
                            {   
                                /** generate a mesh to use in ray casting process **/
                                List<PolygonPoint> points = new ArrayList<PolygonPoint>();
                                for (int i = 0; i < pointsv.size(); i++)
                                {
                                    points.add(new PolygonPoint(pointsv.get(i).x, pointsv.get(i).y, pointsv.get(i).z));
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
                                            n.attachChild(skgeom);
                                        }
                                        lastPoint = point.point;
                                    }
                                }
                            }
                            catch(Throwable e)
                            {
                                System.out.println("triangulation error");                            
                            }
                        }
                        return null;
                    }
                });
            }
        };
    }

    private java.awt.event.ActionListener getToonShadingActionListener()
    {
        return new java.awt.event.ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {                
                SceneManager.get().toonMode = toonShadingRadioButton.isSelected();
                setupToonShading();
//                System.out.println("toon");
                if (ctx.getCanvas().getCursor().getType() != Cursor.WAIT_CURSOR)
                {
                    List<Spatial> children = meshNode.getChildren();
                    for (Spatial spatial : children)
                    {
                        if (spatial instanceof Geometry)
                        {
//                            System.out.println("geom");
                            ((Geometry) spatial).setMaterial(getMaterial());
                        }
                    }

//                    panel.grabFocus();
                }

            }
        };
    }

    private java.awt.event.ActionListener getWireframeActionListener()
    {
        return new java.awt.event.ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
//                System.out.println("wire");
                SceneManager.get().wireMode = wireframeCheckbox.isSelected();
                                
                if (ctx.getCanvas().getCursor().getType() != Cursor.WAIT_CURSOR)
                {
                    List<Spatial> children = meshNode.getChildren();
                    for (Spatial spatial : children)
                    {
                        if (spatial instanceof Geometry)
                        {
//                            System.out.println("geom");
                            ((Geometry) spatial).getMaterial().getAdditionalRenderState().setWireframe(wireframeCheckbox.isSelected());
                        }
                    }

//                    panel.grabFocus();
                }
            }
        };
    }

    @Override
    public void simpleUpdate(float tpf)
    {
        switch (cursorState)
        {
            case DISABLED:
                if (mainFrame.getCursor() != Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR))
                    mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                break;
            case DRAGGING:
            {
                if (ctx.getCanvas().getCursor() != customHandCursor)
                    ctx.getCanvas().setCursor(customHandCursor);
                break;
            }
            case WAITING:
            {
                if (ctx.getCanvas().getCursor() != Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR))
                    ctx.getCanvas().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                
                if (mainFrame.getCursor() != Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR))
                    mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                break;
            }
            case DEFAULT:
            default:
            {                
                ctx.getCanvas().setCursor(customCrosshairCursor);
                
                if (mainFrame.getCursor() != Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR))
                    mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                break;
            }
        }
        //ctx.getCanvas().setFocusable(true);
        //ctx.getCanvas().setCursor(Cursor.getPredefinedCursor(i));
        //System.out.println("RECURSION LEVEL: " + viewer.getRecursionLevel() + " SPINNER VALUE " + spinnerModel.getValue().toString());
    }

//    public void finishModel() throws ModelGenerationException
//    {
//        generateModel();
//        
//        SceneManager.get().getDrawingNode().detachAllChildren();
//        
//        if(finishedVisualization == null)
//        {
//            finishedVisualization = new Node("finishedViz");
//            SceneManager.get().getRotationNode().attachChild(finishedVisualization);
//        }
//        for (Spatial spatial : visualizationNode.getChildren())
//        {
//            finishedVisualization.attachChild(spatial);
//        }
//        visualizationNode.detachAllChildren();        
//        
//        if(skeletonGenerator != null)
//            skeletonGenerator.finishSkeleton();
//    }
//    public void blendModels()
//    {
//        drawImplicitBlending(skeletonGenerator.getFinishedSkeletons());
//        //inputManager.setCursorVisible(false);
//        SceneManager.get().updateScene();
//        //panel.setVisible(true);
//        
//        ctx.getCanvas().setFocusable(true);
//        ctx.getCanvas().setCursor(Cursor.getPredefinedCursor(1));
//        panel.setCursor(Cursor.getPredefinedCursor(0));
//    }
    public void generateModel() throws ModelGenerationException
    {
        if (skeletonGenerator != null)
        {
            skeletonGenerator.clearSkeleton();
            skeletonGenerator = null;
        }

        // 1) get drawing node
        Node node = SceneManager.get().getDrawingNode();
        // 2) iterate over drawing planes        
        for (Spatial plane : node.getChildren())
        {
            if (!(plane instanceof Node))
            {
                continue;
            }
            Node planeNode = (Node) plane;
            planeNode.setUserData("fixed", 1);
            // 3) iterate over drawing lines              
            for (Spatial line : planeNode.getChildren())
            {
                if (!(line instanceof Geometry))
                {
                    continue;
                }
                Mesh m = ((Geometry) line).getMesh();
                if (!(m instanceof SketchLine))
                {
                    continue;
                }
                SketchLine drawingLine = (SketchLine) m;

                //verify if it is skeleton or silhuette
                boolean skeletonMode = false;
                if ("skeletonLine".equals(line.getName()))
                {
                    skeletonMode = true;
                } else if ("silhuetteLine".equals(line.getName()))
                {
                    skeletonMode = false;
                }

                //apply translation
                List<Vector3f> vertexes = new ArrayList<Vector3f>();
                for (Vector3f v : drawingLine.vertexes)
                {
                    vertexes.add(v.add(line.getLocalTranslation()));
                }
                //vertexes.addAll(drawingLine.vertexes);

                if (planeNode.getUserData("rotated") == null)
                {
                    generateModel2(vertexes, planeNode.getLocalRotation(), skeletonMode);

                    if (skeletonMode)
                    {
                        planeNode.setUserData("rotated", 1);
                    }
                } else
                {
                    generateModel2(vertexes, null, skeletonMode);
                }
            }
        }
    }

    public void generateModel2(List<Vector3f> vertexList, Quaternion rotation, boolean skeletonMode) throws ModelGenerationException
    {
        Vector3f[] controlPoints = vertexList.toArray(new Vector3f[0]);

        if (skeletonMode)
        {
            // apply rotation
            if (rotation != null)
            {
                for (Vector3f point : controlPoints)
                {
                    point.set(rotation.mult(point));
                }
            }

            double radius = SceneManager.get().skeletonRadius;
            System.out.println("radius " + radius);

            SkeletonBranch branch = new SkeletonBranch();
            for (int i = 0; i < controlPoints.length; i++)
            {
                branch.points.add(new SkeletonPoint(controlPoints[i], (float)radius, 1, controlPoints[i], (float)radius));
            }

            Skeleton skeleton = new Skeleton();
            if (skeletonGenerator != null)
            {
                skeleton = skeletonGenerator.getSkeleton();
            }

            skeleton.branches.add(branch);
            skeletonGenerator = new SkeletonGenerator(skeleton);
        } else
        {
            try
            {
                //create a polygon point list
                List<PolygonPoint> points = new ArrayList<PolygonPoint>();
                for (int i = 0; i < controlPoints.length; i++)
                {
                    points.add(new PolygonPoint(controlPoints[i].x, controlPoints[i].y, controlPoints[i].z));
                }

                // calculate the 2d polygonal-constrained delaunay triangulation of the
                // sample
                List<DelaunayTriangle> triangles = triangulate(points);

                System.out.println("triangles: " + triangles.size());

                // calculate the new skeleton
                SkeletonGenerator skGenerator = new SkeletonGenerator(triangles);
                skGenerator.generateSkeleton2();
                Skeleton partialSkeleton = skGenerator.getSkeleton();

                Skeleton skeleton = new Skeleton();
                if (skeletonGenerator != null)
                {
                    skeleton = skeletonGenerator.getSkeleton();
                }

                for (SkeletonBranch branch : partialSkeleton.branches)
                {
                    if (rotation != null)
                    {
                        for (SkeletonPoint point : branch.points)
                        {
                            point.point.set(rotation.mult(point.point));
                        }
                    }
                    skeleton.branches.add(branch);
                }
                skeletonGenerator = new SkeletonGenerator(skeleton);
//                System.out.println("branches before: " + skeleton.branches.size());

            } catch (TriangulationException e)
            {
                throw new ModelGenerationException(e.getMessage());
            } catch (SkeletonizationException e)
            {
                throw new ModelGenerationException(e.getMessage());
            }
        }
    }

    private void drawResult()
    {
        if (skeletonGenerator == null)
        {
            return;
        }

        drawImplicitConvolutionMesh(skeletonGenerator.getSkeleton());
        //inputManager.setCursorVisible(false);
        //SceneManager.get().updateScene();
        //panel.setVisible(true);
    }

    private void drawImplicitConvolutionMesh(final Skeleton skeleton)
    {
        //thread-safe method
        //http://jmonkeyengine.org/wiki/doku.php/jme3:advanced:multithreading
        enqueue(new Callable<Object>()
        {
            @Override
            public Object call() throws Exception
            {
                drawImplicitConvolutionMesh2(skeleton);
                ctx.getCanvas().setFocusable(true);
            //        ctx.getCanvas().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            //        mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                cursorState = CursorState.DEFAULT;
                return null;
            }
        });
    }

    private void drawImplicitConvolutionMesh2(final Skeleton skeleton)
    {        
        Mesh implicitMesh = viewer.generateConvolutionMesh(skeleton);
        Geometry g = new Geometry("implicitMesh", implicitMesh);
        Material mat = getMaterial();

        g.setMaterial(mat);

        meshNode.detachAllChildren();
        meshNode.attachChild(g);
        visualizationNode.detachAllChildren();
        
        visualizationNode.attachChild(meshNode);
        SceneManager.get().lineCount = 0;        
        
//        EventQueue.invokeLater(new Runnable()
//        {
//            @Override
//            public void run()
//            {                
//                //progressBarFrame.setVisible(false);
//            }
//        });

//        System.out.println("branches: " + skeleton.branches.size());

        //skeleton visualization
//        Node skeletonMesh = viewer.generateSkeletonMesh(skeleton);        
//        visualizationNode.attachChild(skeletonMesh);
    }

    private Material getMaterial()
    {
        return SceneManager.get().getMeshMaterial();
    }

//    private void drawImplicitBlending(List<Skeleton> skeletons)
//    {
//        Mesh implicitMesh = viewer.generateImplicitBlendingMesh(skeletons);
//        Geometry g = new Geometry("implicitBlendingMesh", implicitMesh);
//        Material mat = getMaterial();
//        
//        g.setMaterial(mat);
//        
//        finishedVisualization.detachAllChildren();
//        visualizationNode.detachAllChildren();
//        visualizationNode.attachChild(g);
//                
//    }
//    private void calculateSkeleton(List<DelaunayTriangle> triangles) throws SkeletonizationException
//    {
//        if (skeletonGenerator == null)
//        {
//            skeletonGenerator = new SkeletonGenerator(triangles);
//        } else
//        {
//            skeletonGenerator.setTriangles(triangles);
//        }
//
//        skeletonGenerator.generateSkeleton2();
//    }
    private List<DelaunayTriangle> triangulate(List<PolygonPoint> points) throws TriangulationException
    {
        try
        {
            // polygonal delaunay triangulation
            Polygon poly = new Polygon(points);
            Poly2Tri.triangulate(poly);
            List<DelaunayTriangle> triangles = poly.getTriangles();
            return triangles;
        } catch (Throwable e)
        {
            throw new TriangulationException("Something very wrong has happened in the triangulation process...");
        }
    }

//    private List<PolygonPoint> resampleCurve(Vector3f[] controlPoints, float precision)
//    {
//
//        //no resampling code (simple copy)
//        List<PolygonPoint> points = new ArrayList<PolygonPoint>();
//        for (int i = 0; i < controlPoints.length; i++)
//        {
//            points.add(new PolygonPoint(controlPoints[i].x, controlPoints[i].y, controlPoints[i].z));
//        }
//        return points;
//        // resample curve points using a Catmull-Rom curve
////        Spline spline = new Spline(Spline.SplineType.CatmullRom, controlPoints, 0.5f, true);        
////        float segmentSize = 1f / precision;
////
////        // create resampled points list based on catmull-rom curve
////        List<PolygonPoint> points = new ArrayList<PolygonPoint>();
////        for (int i = 0; i < controlPoints.length - 1; i++) {
////            for (int j = 0; ; j++) {
////                float dist = controlPoints[j].subtract(controlPoints[i]).length();
////                if(dist >= segmentSize)
////                {
////                    Vector3f newPoint = spline.interpolate(dist/segmentSize, i, null);
////                    points.add(new PolygonPoint(newPoint.x, newPoint.y, newPoint.z));
////                    break;
////                }
////            }
////        }
////        return points;
//    }
    public void setupToonShading()
    {

        if (toonShadingRadioButton.isSelected())
        {
            if (fpp == null)
            {
                fpp = new FilterPostProcessor(assetManager);
                viewPort.addProcessor(fpp);
            }

            boolean foundFilter = false;
            Iterator<Filter> it = fpp.getFilterIterator();
            while (it.hasNext())
            {
                Filter filter = it.next();
                System.out.println("get filter");
                if (filter instanceof CartoonEdgeFilter)
                {
                    filter.setEnabled(true);
                    foundFilter = true;
                    break;
                }
            }

            if (!foundFilter)
            {
                fpp.addFilter(new CartoonEdgeFilter());
            }


//            if (true)//renderer.getCaps().contains(Caps.GLSL100))
//            {
//                // Toon Shading - Bom filtro
//                
//                
//                //Gama Correction - Bom filtro
////                fpp=new FilterPostProcessor(assetManager);
////                fpp.addFilter(new GammaCorrectionFilter());
////                viewPort.addProcessor(fpp);
//            }
        } else
        {
            //System.out.println("toon unchecked");
            if (fpp != null)
            {
                //System.out.println("fpp != null");
                Iterator<Filter> it = fpp.getFilterIterator();
                while (it.hasNext())
                {
                    Filter filter = it.next();
                    //System.out.println("get filter");
                    if (filter instanceof CartoonEdgeFilter)
                    {
                        //System.out.println("remove cartoon");
                        filter.setEnabled(false);
                    }
                }
            }

//            if (true)
//            {
//                viewPort.removeProcessor(fpp);
//                fpp=new FilterPostProcessor(assetManager);
//                fpp.removeAllFilters();
//                viewPort.addProcessor(fpp);
//            }
        }
    }

    @Override
    public void beginInput()
    {
    }

    @Override
    public void endInput()
    {
    }

    @Override
    public void onJoyAxisEvent(JoyAxisEvent evt)
    {
    }

    @Override
    public void onJoyButtonEvent(JoyButtonEvent evt)
    {
    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt)
    {
        //cursor.setLocalTranslation(evt.getX() - 10, evt.getY() + 20, 0);
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt)
    {
    }

    @Override
    public void onKeyEvent(KeyInputEvent evt)
    {
    }

    @Override
    public void onTouchEvent(TouchEvent evt)
    {
    }
}
