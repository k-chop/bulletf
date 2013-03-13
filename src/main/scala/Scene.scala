package com.github.whelmaze.bulletf

import scala.collection.mutable

import org.lwjgl.opengl.GL11

trait Scene {

  def name(): String
  val drawObject: mutable.ListBuffer[Sprite]
  
  init()
  /**
    * delta is milliseconds.
   */
  def update(delta: Int): Scene
  
  def run()
  
  def draw() = {
    drawObject foreach { _.draw }
  }
  
  def init()
  
  def dispose()
  
}
