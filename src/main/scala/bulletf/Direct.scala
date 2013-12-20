package bulletf



case class Direct (vx: Double, vy: Double) {
  
  def update(t: (Double, Double)) = t match {
    case (nx, ny) => Direct(nx, ny)
  }
  
}
