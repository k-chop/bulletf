package bulletf



object Angle {
  def apply(d: Double) = new Angle(d)
  lazy val zero = Angle(0)
}

class Angle(private[this] var _dir: Double) {
  def dir: Double = _dir
  
  def update(newDir: Double) {
    _dir = newDir
  }

  def normalize(): Angle = {
    _dir = if(_dir > 360) _dir + 360 % 360 else _dir
    this
  }

  def +=(left: Angle) {
    _dir += left.dir
  }

  def +=(left: Double) {
    _dir += left
  }
}
