package com.github.whelmaze.bulletf

import java.io.{ FileInputStream, IOException }

import org.lwjgl.LWJGLException
import org.lwjgl.opengl.{ Display, DisplayMode, GL11 }
import org.newdawn.slick.Color
import org.newdawn.slick.opengl.{ Texture, TextureLoader }

import collection.mutable

object SceneController {
  
  val scenes = new mutable.Stack[Scene]

  def init(initScene: Scene) = {
    scenes.push( initScene )
  }
  
  def update(delta: Int) = {
    val next = scenes.top.update(delta)
    if (next != scenes.top) {
      scenes.top.dispose()
      scenes.pop()
      scenes.push(next)
    }
  }

  def draw() = {
    scenes.top.draw()
  }
  
}
