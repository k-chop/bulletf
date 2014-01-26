package bulletf

import scala.language.implicitConversions

object TestVertex

object Vertex {
  final val elementCount = 8
  final val elementBytes = 4
  final val sizeInBytes = elementBytes * elementCount

  def apply(xyzw: Array[Float], rbga: Array[Float]): Vertex = new Vertex(xyzw, rbga)

  def xyzw(x:Float, y:Float, z:Float, w: Float = 1f): VertexBuilder =
    (new VertexBuilder).xyzw(x,y,z,w)
  def xyz(x:Float, y:Float, z:Float) = xyzw(x, y, z)
  def rgba(r:Float, g:Float, b:Float, a: Float = 1f): VertexBuilder =
    (new VertexBuilder).rgba(r,g,b,a)
  def rgb(r:Float, g:Float, b:Float) = rgba(r, g, b)

  implicit def autoBuild(vb: VertexBuilder): Vertex = vb.x

  private[Vertex] class VertexBuilder {
    val pos = Array(0f, 0f, 0f, 1f)
    val col = Array(1f, 1f, 1f, 1f)

    def xyzw(x:Float, y:Float, z:Float, w: Float = 1f) = {
      pos(0) = x; pos(1) = y; pos(2) = z; pos(3) = w
      this
    }

    def xyz(x:Float, y:Float, z:Float) = xyzw(x, y, z)

    def rgba(r:Float, g:Float, b:Float, a: Float = 1f) = {
      col(0) = r; col(1) = g; col(2) = b; col(3) = a
      this
    }

    def rgb(r:Float, g:Float, b:Float) = rgba(r, g, b)

    def x: Vertex = apply(pos, col)

    override def toString = s"VertexBuilder(x:${pos(0)}, y:${pos(1)}, z:${pos(2)}, w:${pos(3)}, r:${col(0)}, g:${col(1)}, b:${col(2)}, a:${col(3)})"
  }
}

class Vertex(var _xyzw: Array[Float], var _rgba: Array[Float]) {

  private[this] def copy(f: Array[Float]) = {
    val a = Array.ofDim[Float](f.length)
    Array.copy(f, 0, a, 0, f.length)
    a
  }

  def xyzw = copy(_xyzw)
  def rgba = copy(_rgba)

  override def toString = s"Vertex(x:${_xyzw(0)}, y:${_xyzw(1)}, z:${_xyzw(2)}, w:${_xyzw(3)}, r:${_rgba(0)}, g:${_rgba(1)}, b:${_rgba(2)}, a:${_rgba(3)})"

}
