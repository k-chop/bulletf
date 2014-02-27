package bulletf


import org.newdawn.slick.Color
import org.newdawn.slick.opengl.{ Texture, TextureLoader }
import org.lwjgl.opengl.{ Display, DisplayMode, GL11 }
import org.lwjgl.util.glu.GLU
import constants._
import org.lwjgl.opengl.GL11._

object Drawer {
  private[this] var nowRenderTexId = -1

  def draw(texture: Texture, rect: Rect, pos: Position, rotate: Double, scale: Double, alpha: Double) {
    import MathUtil.sq

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

    if (rotate != 0) {
      GL11.glRotated(rotate, 0.0, 0.0, 1.0)
    }
    if (sq(scale-1.0) > 0.001) {
      GL11.glScaled(scale, scale, 1)
    }
    if (sq(alpha-1.0) > 0.001) {
      GL11.glColor4d(1.0, 1.0, 1.0, alpha)
    } else { // ここ重くね？
      GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
    }

    GL11.glBegin(GL11.GL_QUADS)
    GL11.glTexCoord2d(rect.x / texW, rect.y / texH)
    GL11.glVertex2d(-halfWidth, -halfHeight)
    GL11.glTexCoord2d((rect.x + rect.w) / texW, rect.y / texH)
    GL11.glVertex2d(halfWidth, -halfHeight)
    GL11.glTexCoord2d((rect.x + rect.w) / texW, (rect.y + rect.h) / texH)
    GL11.glVertex2d(halfWidth, rect.h - halfHeight)
    GL11.glTexCoord2d(rect.x / texW, (rect.y + rect.h) / texH)
    GL11.glVertex2d(-halfWidth, rect.h - halfHeight)
    GL11.glEnd()

    GL11.glDisable(GL11.GL_TEXTURE_2D)
    GL11.glLoadIdentity()
  }



  def draw(texture: Texture, rect: Rect, pos: Position, rotate: Double) {
    draw(texture, rect, pos, rotate, 1.0, 1.0)
  }

  def draw(texture: Texture, pos: Position) {
    draw(texture, Rect(0, 0, texture.getImageWidth, texture.getImageHeight), pos, 0, 1.0, 1.0)
  }

}
