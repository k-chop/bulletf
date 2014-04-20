package bulletf



trait Scene {

  def name(): String

  init()
  /**
    * delta is milliseconds.
   */
  def update(): Scene
  
  def run()
  
  def draw()
  
  def init()
  
  def dispose()
  
}
