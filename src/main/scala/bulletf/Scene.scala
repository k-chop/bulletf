package bulletf



trait Scene {

  def name(): String

  init()

  def update(): Scene
  
  def run()
  
  def draw()
  
  def init()
  
  def dispose()
  
}
