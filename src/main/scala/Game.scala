package com.github.whelmaze.bulletf

import java.io.{ FileInputStream, IOException }

import org.lwjgl.{LWJGLException, Sys}
import org.lwjgl.opengl.{ Display, DisplayMode, GL11 }
import org.lwjgl.input.Controllers

object Game {

  def clearScreen {
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT)
  }
  
}

class Game(val width: Int, val height: Int) {
  var fps: Int = 60
  private[this] var lastFrame: Long = 0
  private[this] var fpscount: Int = 0
  private[this] var lastFPS: Long = 0
  var x: Float = centerX
  var y: Float = 0
  var rotation: Double = 0
  var a: Int = 0
  var s: Int = 0
  var sum: Double = 0
  var error: Long = 0
  def start() {
    initGL()
    SceneController.init(new TitleScene)

    getDelta()
    lastFPS = getTime()
    
    while (true) {
      val be = getTime
      val delta = getDelta()
      //up(delta)
      SceneController.update(delta/1000)

      //render()
      
      updateFPS()
      SceneController.draw()
      
      Display.update()

      // s += 1
      // sum += delta//getTime - be
      // if (s == fps) { println((sum / s.toDouble) + " : " + sum); s= 0; sum=0}

      sync(be)
      //Display.sync(fps) // ←dame (on windows7 64bit)
      
      if (Display.isCloseRequested) {
        Display.destroy()
        System.exit(0)
      }
    }
  }

  final val WAIT: Int = 16650 // [us]
  // return overtime???
  def sync(before: Long) =  {
    var now = getTime()
    while(now - before < WAIT) {
      Thread.sleep(0)
      now = getTime()
    }
    //println("wait: " + (now - before))
  }
  
  def up(delta: Int) {
    a += 1
    rotation += 0.15 * delta / 1000.0;
    y += 0.15f * delta / 1000.0f
    if (y > height) y = 0
  }

  def render() {
    Game.clearScreen
    
    GL11.glColor3f(0.5f, 0.5f, 1.0f)
    GL11.glPushMatrix()
          GL11.glPushMatrix();
      GL11.glTranslatef(x, y, 0);
      GL11.glRotatef(rotation.toFloat, 0f, 0f, 1f);
      GL11.glTranslatef(-x, -y, 0);
      
      GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x - 50, y - 50);
        GL11.glVertex2f(x + 50, y - 50);
        GL11.glVertex2f(x + 50, y + 50);
        GL11.glVertex2f(x - 50, y + 50);
      GL11.glEnd();
    GL11.glPopMatrix();

  }

  private def initGL() {
    Display.setDisplayMode(new DisplayMode(width,height))
    //Display.setFullscreen(true);
    Display.setVSyncEnabled(true)
    Display.create()

    Controllers.create()
    val padcount = Controllers.getControllerCount()
    println("padcount : " + padcount)
    (0 until padcount).foreach{ i =>
      val co = Controllers.getController(i)
      println("[%d] \"%s\" (%d)" format (i, co.getName, co.getButtonCount))
    }
    
    GL11.glEnable(GL11.GL_TEXTURE_2D)               
    
    GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)          
    
    GL11.glEnable(GL11.GL_BLEND)
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    
    GL11.glViewport(0,0,width,height)
    GL11.glMatrixMode(GL11.GL_MODELVIEW)

    GL11.glMatrixMode(GL11.GL_PROJECTION)
    GL11.glLoadIdentity()
    GL11.glOrtho(0, width, height, 0, 1, -1)
    GL11.glMatrixMode(GL11.GL_MODELVIEW)
  }

  def getTime(): Long = System.nanoTime / 1000//(Sys.getTime() * 1000 / Sys.getTimerResolution())

  def getDelta(): Int = {
    val time: Long = getTime()
    val delta: Int = (time - lastFrame).toInt
    lastFrame = time
    delta
  }

  def updateFPS() {
    if (getTime() - lastFPS > 1000000) {
      Display.setTitle("FPS: " + fpscount);
      fpscount = 0;
      lastFPS += 1000000;
    }
    fpscount += 1;
  }
}
