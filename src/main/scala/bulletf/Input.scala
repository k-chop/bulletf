package bulletf



import org.lwjgl.input.{ Controllers, Controller, Keyboard }

object Input {
  import Keyboard._
  import keys._

  object keys {
    val SHOT = 1
    val LASER = 2
    val BOMB = 4
    val ACT = 8
  }
  
  var usePad = false
  val padmap = Map(SHOT -> 3, LASER -> 5, BOMB -> 2, ACT -> 1)
  val keymap = Map(SHOT -> KEY_Z, LASER -> KEY_X, BOMB -> KEY_C, ACT -> KEY_S)
  
  def apply(i: Int) = if (usePad) pad(i) else key(i)
  def x = if (usePad) pad.x else key.x
  def y = if (usePad) pad.y else key.y
  
  object key {
    final val keydown = { i:Int => Keyboard.isKeyDown(i) }
    def apply(i: Int) = keydown(keymap(i))
    def x: Float = if (keydown(KEY_RIGHT)) 1f else if (keydown(KEY_LEFT)) -1f else 0f
    def y: Float = if (keydown(KEY_UP)) -1f else if (keydown(KEY_DOWN)) 1f else 0f
  }

  object pad {

    private val controller = Controllers.getController(1)

    def poll() {
      controller.poll()
    }
    def apply(i: Int) = controller.isButtonPressed(padmap(i)) 
    def x: Float = controller.getPovX
    def y: Float = controller.getPovY
    
  }
  
}
