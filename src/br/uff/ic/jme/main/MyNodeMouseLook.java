package br.uff.ic.jme.main;

//import com.jme3.input.Mouse;
//import com.jme3.input.MouseInput;
//import com.jme3.input.action.InputActionEvent;
//import com.jme3.input.action.KeyNodeRotateLeftAction;
//import com.jme3.input.action.KeyNodeRotateRightAction;
//import com.jme3.input.action.MouseInputAction;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * <code>NodeMouseLook</code> defines a mouse action that detects mouse movement
 * and converts it into node rotations and node tilts.
 * 
 * @author Mark Powell
 * @version $Id: NodeMouseLook.java 4570 2009-08-09 04:37:47Z skye.book $
 */
public class MyNodeMouseLook /*extends MouseInputAction*/
{
   
   // the actions that handle looking up, down, left and right.
   private KeyNodeRotateDownAction  lookDown;
   
   private KeyNodeRotateUpAction    lookUp;
   
//   private KeyNodeRotateLeftAction  rotateLeft;
//   
//   private KeyNodeRotateRightAction rotateRight;
//   
   // the axis to lock
   private Vector3f                 lockAxis;
   
   // the node to control
   private Spatial                  node;
     
   public static final float        DEFAULT_MAXROLLOUT     = 240;
   
   public static final float        DEFAULT_MINROLLOUT     = 20;
   
   public static final float        DEFAULT_MOUSEROLLMULT  = 50;
   
   protected float                  maxRollOut             = DEFAULT_MAXROLLOUT;
   
   protected float                  minRollOut             = DEFAULT_MINROLLOUT;
   
   protected float                  mouseRollMultiplier    = DEFAULT_MOUSEROLLMULT;
   
   protected float                  rollInSpeed            = DEFAULT_MOUSEROLLMULT;
   
   private boolean                  buttonPressRequired    = true;
   
   private int                      mouseButtonForRequired = 1;
   
   /**
    * Constructor creates a new <code>NodeMouseLook</code> object. It takes the
    * mouse, node and speed of the looking.
    * 
    * @param mouse
    *           the mouse to calculate view changes.
    * @param node
    *           the node to move.
    * @param speed
    *           the speed at which to alter the camera.
    */
//   public MyNodeMouseLook(Mouse mouse, Spatial node, float speed)
//   {
//      this.mouse = mouse;
//      this.speed = speed;
//      this.node = node;      
//      
//      lookDown = new KeyNodeRotateDownAction(this.node, speed);
//      lookUp = new KeyNodeRotateUpAction(this.node, speed);
//      rotateLeft = new KeyNodeRotateLeftAction(this.node, speed);
//      rotateRight = new KeyNodeRotateRightAction(this.node, speed);
//   }
   
   /**
    * <code>setLockAxis</code> sets the axis that should be locked down. This
    * prevents "rolling" about a particular axis. Typically, this is set to the
    * mouse's up vector.
    * 
    * @param lockAxis
    *           the axis that should be locked down to prevent rolling.
    */
   public void setHorizontalLockAxis(Vector3f lockAxis)
   {
      this.lockAxis = lockAxis;
//      rotateLeft.setLockAxis(lockAxis);
//      rotateRight.setLockAxis(lockAxis);
   }
   
   public void setVerticalLockAxis(Vector3f lockAxis)
   {
      this.lockAxis = lockAxis;
      lookUp.setLockAxis(lockAxis);
      lookDown.setLockAxis(lockAxis);
   }
   
   /**
    * Returns the axis that is currently locked.
    * 
    * @return The currently locked axis
    * @see #setHorizontalLockAxis(com.jme.math.Vector3f)
    */
   public Vector3f getLockAxis()
   {
      return lockAxis;
   }
   
   /**
    * <code>setSpeed</code> sets the speed of the mouse look.
    * 
    * @param speed
    *           the speed of the mouse look.
    */
//   @Override
//   public void setSpeed(float speed)
//   {
//      super.setSpeed(speed);
//      lookDown.setSpeed(speed);
//      lookUp.setSpeed(speed);
//      rotateRight.setSpeed(speed);
//      rotateLeft.setSpeed(speed);
//      
//   }
   
   /**
    * Sets the option for requiring the user to click the mouse button specified
    * by <code>mouseButtonForRequired</code> in order to rotate the node.
    * 
    * @param buttonPressRequired
    *           the buttonPressRequired to set
    */
   public void setButtonPressRequired(boolean buttonPressRequired)
   {
      this.buttonPressRequired = buttonPressRequired;
   }
   
   /**
    * Sets which mouse button needs to be pressed in order to rotate the node
    * (that is, assuming <code>buttonPressRequired</code> is set to true).
    * 
    * @param mouseButtonForRequired
    *           the mouseButtonForRequired to set
    */
   public void setMouseButtonForRequired(int mouseButtonForRequired)
   {
      this.mouseButtonForRequired = mouseButtonForRequired;
   }
   
   /**
    * <code>performAction</code> checks for any movement of the mouse, and calls
    * the appropriate method to alter the node's orientation when applicable.
    * 
    * @see com.jme.input.action.MouseInputAction#performAction(InputActionEvent)
    */
//   @Override
//   public void performAction(InputActionEvent evt)
//   {
//      float time = 0.01f * speed;
//      
//      if (buttonPressRequired && MouseInput.get().isButtonDown(mouseButtonForRequired) || !buttonPressRequired)
//      {
//         SceneManager.get().handCursor();
//         
//         if (MouseInput.get().getXDelta() /* mouse.getLocalTranslation().x */> 0)
//         {
//            evt.setTime(time * MouseInput.get().getXDelta() /* mouse.
//                                                             * getLocalTranslation
//                                                             * ().x */);
//            rotateRight.performAction(evt);
//         }
//         else if (MouseInput.get().getXDelta() /* mouse.getLocalTranslation().x */< 0)
//         {
//            evt.setTime(time * MouseInput.get().getXDelta() /* mouse.
//                                                             * getLocalTranslation
//                                                             * ().x */);
//            rotateLeft.performAction(evt);
//         }
//         if (MouseInput.get().getYDelta() /* mouse.getLocalTranslation().y */> 0)
//         {
//            evt.setTime(time * MouseInput.get().getYDelta() /* mouse.
//                                                             * getLocalTranslation
//                                                             * ().y */);
//            lookUp.performAction(evt);
//         }
//         else if (MouseInput.get().getYDelta() /* mouse.getLocalTranslation().y */< 0)
//         {
//            evt.setTime(time * MouseInput.get().getYDelta() /* mouse.
//                                                             * getLocalTranslation
//                                                             * ().y */);
//            lookDown.performAction(evt);
//         }
//      }
//      else
//      {
//         SceneManager.get().crosshairCursor();
//      }
//      
//      // int wdelta = MouseInput.get().getWheelDelta();
//      // if (wdelta != 0)
//      // {
//      // float amount = time * -wdelta;
//      // rollIn(amount);
//      //
//      // camera.update(time);
//      // // camera.getCamera().onFrameChange();
//      // }
//   }
   
   // /**
   // * <code>rollIn</code> updates the radius value of the camera's spherical
   // * coordinates.
   // *
   // * @param amount
   // */
   // private void rollIn(float amount)
   // {
   // camera.getCamera().getLocation().z += (amount * rollInSpeed);
   // //camera.getIdealSphereCoords().x =
   // clampRollIn(camera.getIdealSphereCoords().x + (amount * rollInSpeed));
   // }
   //
   // /**
   // * clampRollIn
   // *
   // * @param r
   // * float
   // * @return float
   // */
   // private float clampRollIn(float r)
   // {
   // if (Float.isInfinite(r) || Float.isNaN(r))
   // return 100f;
   // if (r > maxRollOut)
   // r = maxRollOut;
   // else if (r < minRollOut)
   // r = minRollOut;
   // return r;
   // }
   
}
