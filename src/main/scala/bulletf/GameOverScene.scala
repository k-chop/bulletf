package bulletf


import org.lwjgl.input.Keyboard

import collection.mutable

class GameOverScene extends Scene {

  def name() = "GameOver"
  
  def update(): Scene = {
    import Input.keys._

    if ( Input(SHOT) )
      new TitleScene
    else
      this
  }

  def run () {}
  
  def init() {
    println(name + " inited.")
  }

  def draw() {}

  def dispose() {
    println(name + " disposed.")
  }
  

}
