package com.github.whelmaze.bulletf

import org.lwjgl.input.Keyboard

import collection.mutable

class TitleScene extends Scene {

  lazy val drawObject = mutable.ListBuffer.empty[Sprite]
  
  def name() = "Title"
  
  def update(delta: Int): Scene = {
    import Input.keys._

    if ( Input(SHOT) )
      new TestScene //STGScene
    else
      this
  }

  def run () = {}
  
  def init() = {
    drawObject += ( new Sprite(Resource.titleGraphic, Position.center()) )

    println(name + " inited.")
  }

  def dispose() = {
    drawObject.clear
    Game.clearScreen
    println(name + " disposed.")
  }
  

}
