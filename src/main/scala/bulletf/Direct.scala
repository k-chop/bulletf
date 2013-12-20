package bulletf



case class Dir (val vx: Double, val vy: Double) {
  
  def update(t: (Double, Double)) = t match {
    case (nx, ny) => Dir(nx, ny)
  }
  
}
