package com.github.whelmaze.bulletf

import org.newdawn.slick.Color
import org.newdawn.slick.opengl.{ Texture, TextureLoader }
import org.lwjgl.opengl.{ Display, DisplayMode, GL11 }
import org.lwjgl.util.glu.GLU
import com.github.whelmaze.bulletf.constants._
import org.lwjgl.opengl.GL11._

object Rect {
  lazy val whole = Rect(0,0,1,1)
}
case class Rect(x: Int, y:Int, w: Int, h: Int)

object Drawer {
  private[this] var nowRenderTexId = -1

  def draw(texture: Texture, rect: Rect, pos: Position, rotate: Double) {

    val halfWidth = rect.w / 2.0
    val halfHeight = rect.h / 2.0

    val texW = texture.getImageWidth.toDouble
    val texH = texture.getImageHeight.toDouble

    Game.view2d()
    GL11.glEnable(GL11.GL_TEXTURE_2D)
    if (nowRenderTexId != texture.getTextureID) {
      //println("flip: "+texture.getTextureRef)
      texture.bind() // or GL11.glBind(texture.getTextureID())
      nowRenderTexId = texture.getTextureID
    }

    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()

    GL11.glTranslated(pos.x, pos.y, 0.0)
    if (rotate != 0)
      GL11.glRotated(rotate, 0.0, 0.0, 1.0)

    GL11.glBegin(GL11.GL_QUADS)
    GL11.glTexCoord2d(rect.x / texW, rect.y / texH)
    GL11.glVertex2d(-halfWidth, -halfHeight)
    GL11.glTexCoord2d((rect.x + rect.w) / texW, rect.y / texH)
    GL11.glVertex2d(rect.w - halfWidth, -halfHeight)
    GL11.glTexCoord2d((rect.x + rect.w) / texW, (rect.y + rect.h) / texH)
    GL11.glVertex2d(rect.w - halfWidth, rect.h - halfHeight)
    GL11.glTexCoord2d(rect.x / texW, (rect.y + rect.h) / texH)
    GL11.glVertex2d(-halfWidth, rect.h - halfHeight)
    GL11.glEnd()

    GL11.glDisable(GL11.GL_TEXTURE_2D)
    GL11.glLoadIdentity()
  }

  def draw(texture: Texture, pos: Position) {
    draw(texture, Rect(0, 0, texture.getImageWidth, texture.getImageHeight), pos, 0)
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
    glOrtho(-1, 1, -1, 1, -1, 10)

    val zz = -4f
    val xx = -10f

    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()

    val w = Game.width.toFloat
    val h = Game.height.toFloat

    val z = 0f
    glTranslated((pos.x-centerX)/centerX, -(pos.y-centerY)/centerY, -1)
    glRotatef(time.toFloat*2f, 0f, 0f, 1f)
    glRotatef(time.toFloat*2f, 1f, 0f, 0f)
    glRotatef(time.toFloat*2f, 0f, 1f, 0f)

    val t = 10f

    //glScaled(0.1, 0.1, 1)
    GLUtil.drawBox(0.06f, GL_LINE_LOOP)
    //GLUtil.drawVBO()

    glLoadIdentity()


  }
}
