/* Copyright (c) 2003-2009 jMonkeyEngine All rights reserved. Redistribution and
 * use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: * Redistributions of source
 * code must retain the above copyright notice, this list of conditions and the
 * following disclaimer. * Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the distribution. *
 * Neither the name of 'jMonkeyEngine' nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package br.uff.ic.jme.main;


public class SketchMonkey
{
   
   public static void main(String[] args)
   {
      /**
       * @see http://www.jmonkeyengine.com/wiki/doku.php/
       *      standardgame_gamestates_and_multithreading_a_new_way_of_thinking
       *      #standardgame_awesomeness
       */
//      final MyStandardGame game = new MyStandardGame("SketchMonkey");
//      game.setConfigShowMode(ConfigShowMode.NeverShow);
//      game.getSettings().setFramerate(60);
//      game.getSettings().setFrequency(60);
//      game.getSettings().setFullscreen(false);
//      game.getSettings().setMusic(false);
//      game.getSettings().setSFX(false);
//      game.getSettings().setVerticalSync(true);
//      game.getSettings().setWidth(800);
//      game.getSettings().setHeight(800);
//      game.setBackgroundColor(ColorRGBA.white);
//      game.start();
      
      // game.getDisplay().moveWindowTo(200, 100);
      // MouseInput.get().setCursorVisible(false);
      
      // Game state to handle 3D visualization
//      Visualization3DGameState vizGameState = new Visualization3DGameState();
//      vizGameState.setName(Visualization3DGameState.class.getName());
      
//      SceneManager.get();
      // Game state to handle 2D drawing
      //Drawing2DGameState drawGameState = new Drawing2DGameState(game);
      //drawGameState.setName(Drawing2DGameState.class.getName());
      
      // attach our game states to the game state manager
//      GameStateManager.getInstance().attachChild(vizGameState);
      //GameStateManager.getInstance().attachChild(drawGameState);
      
      // Start with 2D drawing active
//      vizGameState.setActive(false);
      //drawGameState.setActive(true);
   }
}
