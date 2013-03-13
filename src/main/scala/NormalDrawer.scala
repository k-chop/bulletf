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
    //GLUtil.drawBox(0.06f, GL_LINE_LOOP)
    GLUtil.drawVBO()

    glLoadIdentity()


  }
}
