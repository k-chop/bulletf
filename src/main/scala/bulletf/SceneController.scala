package bulletf


import collection.mutable

object SceneController {
  
  val scenes = new mutable.Stack[Scene]

  def init(initScene: Scene) = {
    scenes.push( initScene )
  }
  
  def update() = {
    val next = scenes.top.update()
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
