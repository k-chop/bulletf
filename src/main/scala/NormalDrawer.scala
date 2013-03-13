package com.github.whelmaze.bulletf

import org.newdawn.slick.Color
import org.newdawn.slick.opengl.{ Texture, TextureLoader }
import org.lwjgl.opengl.{ Display, DisplayMode, GL11 }
import org.lwjgl.util.glu.GLU
import com.github.whelmaze.bulletf.constants._

object Drawer {

  def draw(texture: Texture, pos: Position) {

    val halfx = texture.getImageWidth / 2.0
    val halfy = texture.getImageHeight / 2.0

    Game.view2d()

    GL11.glEnable(GL11.GL_TEXTURE_2D)
    texture.bind() // or GL11.glBind(texture.getTextureID())

    GL11.glBegin(GL11.GL_QUADS)
    GL11.glTexCoord2d(0,0)
    GL11.glVertex2d(pos.x - halfx, pos.y - halfy)
    GL11.glTexCoord2d(1,0)
    GL11.glVertex2d(pos.x + texture.getTextureWidth - halfx, pos.y - halfy)
    GL11.glTexCoord2d(1,1)
    GL11.glVertex2d(pos.x + texture.getTextureWidth - halfx, pos.y + texture.getTextureHeight - halfy)
    GL11.glTexCoord2d(0,1)
    GL11.glVertex2d(pos.x - halfx, pos.y + texture.getTextureHeight - halfy)
    GL11.glEnd()

    GL11.glDisable(GL11.GL_TEXTURE_2D)
  }

  def draw3d(texture: Texture, pos: Position, time: Int) {
    import GL11._
    import constants._

    Game.view3d()

    val halfx = texture.getImageWidth / 2.0
    val halfy = texture.getImageHeight / 2.0
    val fx = ((pos.x - centerX).toFloat / Game.width)
    val fy = ((centerY - pos.y).toFloat / Game.height)

    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    ////GLU.gluPerspective(30.0f, (Game.width / Game.height.toFloat), 1.0f, 100.0f)
    //glFrustum(-1, 1, -1, 1, 1f, 10f)
    glOrtho(-1, 1, -1, 1, -1, 10)

    val zz = -4f
    val xx = -10f

    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
    //GLU.gluLookAt(0, 0, 1, 0, 0.0f, 0, 0.0f, 1.0f, 0.0f)

//    glBegin(GL_TRIANGLES)
//      glColor4f(1.0f, 0.0f, 0.0f, 1.0f)
//      glVertex3f(50f , 3f, 0f)
//      glColor4f(0.0f, 1.0f, 0.0f, 1.0f)
//      glVertex3f(10f , 10f, 0f)
//      glColor4f(0.0f, 0.0f, 1.0f, 1.0f)
//      glVertex3f(90f , 40f, 0f)
//    glEnd()

    // glTranslatef(50f,50f,0)
    // glColor3f(1f, 1f, 1f)

    //println(z)
    //println(zz)

    val w = Game.width.toFloat
    val h = Game.height.toFloat

    val z = 0f
    glTranslated((pos.x-centerX)/centerX, -(pos.y-centerY)/centerY, -1)
    //println((time/60.0)-2)
    glRotatef(time.toFloat*2f, 0f, 0f, 1f)
    //glRotatef(time.toFloat*2f, 1f, 0f, 0f)
    glRotatef(time.toFloat*2f, 0f, 1f, 0f)

    val t = 10f

    GLUtil.drawBox(0.06f, GL_LINE_LOOP)

//    glBegin(GL_QUADS)
//    glVertex3f(-1/t,  1/t, z)
//    glVertex3f(-1/t, -1/t, z)
//    glVertex3f( 1/t, -1/t, z)
//    glVertex3f( 1/t, 1/t, z)
//    glEnd()
/*
    glPushMatrix()
    glLoadIdentity()
    glTranslated(0.4,0,0)
    //glScaled(2.5,2.5,0.0)

    glBegin(GL_TRIANGLES)
    glVertex3f(-1/t, 1/t, 3)
    glVertex3f(-1/t, -1/t, 3)
    glVertex3f( 1/t, -1/t, 3)
    glEnd()

    glRotatef(time.toFloat*2f, 1f, 0f, z)
    glPopMatrix()
*/
    //GLUtil.drawBox(400f, GL_QUADS)

    //glMatrixMode(GL_PROJECTION)
    glLoadIdentity()


  }
}
