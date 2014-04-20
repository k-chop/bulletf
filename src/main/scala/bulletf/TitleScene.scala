package bulletf



import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11

import collection.mutable

class TitleScene extends Scene {

  val titleImg = Sprite.get(Resource.titleGraphic)

  def name() = "Title"
  
  def update(): Scene = {
    import Input.keys._

    if ( Input(SHOT) )
      new TestScene //STGScene
    else
      this
  }

  def run () {}
  
  def init() {

    println(s"OpenGL version: ${GL11.glGetString(GL11.GL_VERSION)}")

    println(name + " inited.")
  }

  def draw() {
    titleImg.draw(Position.center)
  }

  def dispose() {
    Game.clearScreen()
    println(name + " disposed.")
  }
  

}
