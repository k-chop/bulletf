package com.github.whelmaze.bulletf

import org.lwjgl.input.Keyboard

import collection.mutable

class GameOverScene extends Scene {

  lazy val drawObject = mutable.ListBuffer.empty[Sprite]
  
  def name() = "GameOver"
  
  def update(delta: Int): Scene = {
    import Input.keys._

    if ( Input(SHOT) )
      new TitleScene
    else
      this
  }

  def run () = {}
  
  def init() = {

    println(name + " inited.")
  }

  def dispose() = {
    drawObject.clear
    
    println(name + " disposed.")
  }
  

}
