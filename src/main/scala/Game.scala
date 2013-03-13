package com.github.whelmaze.bulletf

import java.io.{ FileInputStream, IOException }

import org.lwjgl.{LWJGLException, Sys}
import org.lwjgl.opengl.{ Display, DisplayMode, GL11 }
import org.lwjgl.util.glu.GLU
import org.lwjgl.input.Controllers

object Game {

  private[this] var on3d: Boolean = false
  private[Game] var _width: Int = 0
  private[Game] var _height: Int = 0
  def width = _width
  def height = _height

  def clearScreen() {
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT)
  }

  def view3d() {
    import GL11._

    if (!on3d) {
      glMatrixMode(GL_PROJECTION)
      glPopMatrix()
      glMatrixMode(GL_MODELVIEW)
      glPopMatrix()
      glLoadIdentity()
      on3d = true
    }
  }

  def view2d() {
    import GL11._

    if (on3d) {
      glMatrixMode(GL_PROJECTION)
      glPushMatrix()
      glLoadIdentity()
      glOrtho(0, width, height, 0, 1, -1)
      glMatrixMode(GL_MODELVIEW)
      glPushMatrix()
      glLoadIdentity()
      glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
      on3d = false
    }
  }

}

class Game(_width: Int, _height: Int) {
  var fps: Int = 60
  private[this] var lastFrame: Long = 0
  private[this] var fpscount: Int = 0
  private[this] var lastFPS: Long = 0

  var ax: Float = 0f
  var ay: Float = 0f
  var az: Float = 0f
  var x = 0f
  var y = 0f
  var rotation: Double = 0
  var a: Int = 0
  var s: Int = 0
  var sum: Double = 0
  var error: Long = 0

  Game._width = _width
  Game._height = _height

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
      
      updateFPS()

      Game.clearScreen()
      Game.view3d()

      //render(0f, 0f)

      Game.view2d()
      if (a%60==0) {
        val ta = System.nanoTime()
        SceneController.draw()
        val elasp = (System.nanoTime() - ta) / 1000.0 / 1000.0
        println(f"draw takes time: $elasp%e ms")
      } else {
        SceneController.draw()
      }
      Display.update()

      // s += 1
      // sum += delta//getTime - be
      // if (s == fps) { println((sum / s.toDouble) + " : " + sum); s= 0; sum=0}

      sync(be)
      //Display.sync(fps) // ←dame (on windows7 64bit)
      a += 1; a %= 360
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
    if (y > Game.height) y = 0
  }

  def render(fx: Float, fy: Float) {
    import GL11._

    // ay += 0.5f; ax += 0.025f; az += 0.3f
    // glRotatef(ay, 0.0f, 1.0f, 0.0f)
    // glRotatef(ax, 1.0f, 0.0f, 0.0f)
    // glRotatef(az, 0.0f, 0.0f, 1.0f)

    //glOrtho(0.0, width, height, 0, 1, -1)

    glMatrixMode(GL_PROJECTION)
    glPushMatrix()
    glLoadIdentity()
    GLU.gluPerspective(30.0f, (Game.width / Game.height.toDouble).toFloat, 1.0f, 100.0f)
    GLU.gluLookAt(fx, fy, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f)
    //GLU.gluLookAt(0,800,800, 0,0,0, 0,1,0)

    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()

    // glBegin(GL_TRIANGLES)
    //   glColor4f(1.0f, 0.0f, 0.0f, 1.0f)
    //   glVertex3f(50f , 3f, 0f)
    //   glColor4f(0.0f, 1.0f, 0.0f, 1.0f)
    //   glVertex3f(10f , 10f, 0f)
    //   glColor4f(0.0f, 0.0f, 1.0f, 1.0f)
    //   glVertex3f(90f , 40f, 0f)
    // glEnd()

    // glTranslatef(50f,50f,0)
    // glColor3f(1f, 1f, 1f)

    glTranslated(fx, fy, (a*0.01))
    glRotatef(a.toFloat*2f, 0f, 0f, 1f)

    val t = 0.01f
    glBegin(GL_QUADS)
      glVertex3f(-1f*t, 1f*t, 0f)
      glVertex3f( 1f*t, 1f*t, 0f)
      glVertex3f( 1f*t,-1f*t, 0f)
      glVertex3f(-1f*t,-1f*t, 0f)
    glEnd()

    //glColor3f(1f,0f,1f)
    //GLUtil.drawBox(400f, GL_QUADS)

    glMatrixMode(GL_PROJECTION)
    glPopMatrix()

  }

  private def initGL() {
    Display.setDisplayMode(new DisplayMode(Game.width,Game.height))
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

    GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)          
    
    GL11.glEnable(GL11.GL_BLEND)
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    
    GL11.glViewport(0,0,Game.width,Game.height)
//    GL11.glMatrixMode(GL11.GL_MODELVIEW)

//    GL11.glMatrixMode(GL11.GL_PROJECTION)
//   GL11.glLoadIdentity()
//    GL11.glOrtho(0, width, height, 0, 1, -1)
//    GL11.glMatrixMode(GL11.GL_MODELVIEW)
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
