package com.github.whelmaze.bulletf

import org.newdawn.slick.Color
import org.newdawn.slick.opengl.{ Texture, TextureLoader }
import org.lwjgl.opengl.{ Display, DisplayMode, GL11 }

object Drawer {

  def draw(texture: Texture, pos: Position) = {

    val halfx = texture.getImageWidth / 2.0
    val halfy = texture.getImageHeight / 2.0
    
    texture.bind() // or GL11.glBind(texture.getTextureID())

    GL11.glBegin(GL11.GL_QUADS)
    GL11.glTexCoord2d(0,0)
    GL11.glVertex2d(pos.x - halfx, pos.y - halfy)
    GL11.glTexCoord2d(1,0)
    GL11.glVertex2d(pos.x + texture.getTextureWidth() - halfx, pos.y - halfy)
    GL11.glTexCoord2d(1,1)
    GL11.glVertex2d(pos.x + texture.getTextureWidth() - halfx, pos.y + texture.getTextureHeight() - halfy)
    GL11.glTexCoord2d(0,1)
    GL11.glVertex2d(pos.x - halfx, pos.y + texture.getTextureHeight() - halfy)
    GL11.glEnd()
    
  }

}
