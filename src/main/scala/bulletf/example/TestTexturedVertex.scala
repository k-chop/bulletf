package bulletf.example

import scala.language.implicitConversions

object TestTexturedVertex

object TexturedVertex {
  // constants
  final val elementBytes = 4

  final val positionElementCount = 4
  final val colorElementCount = 4
  final val textureElementCount = 2

  final val positionBytesCount = positionElementCount * elementBytes
  final val colorByteCount = colorElementCount * elementBytes
  final val textureByteCount = textureElementCount * elementBytes

  final val positionByteOffset = 0
  final val colorByteOffset = positionByteOffset + positionBytesCount
  final val textureByteOffset = colorByteOffset + colorByteCount

  final val elementCount = positionElementCount + colorElementCount + textureElementCount
  final val stride = positionBytesCount + colorByteCount + textureByteCount

  // definition
  def apply(xyzw: Array[Float], rbga: Array[Float], st: Array[Float]): TexturedVertex = new TexturedVertex(xyzw, rbga, st)

  def xyzw(x:Float, y:Float, z:Float, w: Float = 1f): TexturedVertexBuilder =
    (new TexturedVertexBuilder).xyzw(x,y,z,w)
  def xyz(x:Float, y:Float, z:Float) = xyzw(x, y, z)
  def rgba(r:Float, g:Float, b:Float, a: Float = 1f): TexturedVertexBuilder =
    (new TexturedVertexBuilder).rgba(r,g,b,a)
  def rgb(r:Float, g:Float, b:Float) = rgba(r, g, b)
  def st(s: Float, t: Float) = (new TexturedVertexBuilder).st(s, t)

  implicit def autoBuild(vb: TexturedVertexBuilder): TexturedVertex = vb.x

  private[TexturedVertex] class TexturedVertexBuilder {
    val pos = Array(0f, 0f, 0f, 1f)
    val col = Array(1f, 1f, 1f, 1f)
    val t_st = Array(0f, 0f)

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

    def st(s: Float, t: Float) = {
      t_st(0) = s; t_st(1) = t
      this
    }

    def x: TexturedVertex = apply(pos, col, t_st)

    override def toString = s"TexturedVertexBuilder(x:${pos(0)}, y:${pos(1)}, z:${pos(2)}, w:${pos(3)}, r:${col(0)}, g:${col(1)}, b:${col(2)}, a:${col(3)}, s:${t_st(0)}, t:${t_st(1)})"
  }
}

class TexturedVertex(var _xyzw: Array[Float], var _rgba: Array[Float], var _st: Array[Float]) {

  private[this] def copy(f: Array[Float]) = {
    val a = Array.ofDim[Float](f.length)
    Array.copy(f, 0, a, 0, f.length)
    a
  }

  def xyzw = copy(_xyzw)
  def rgba = copy(_rgba)
  def st = copy(_st)

  def elements: Array[Float] = {
    val dest = Array.ofDim[Float](TexturedVertex.elementCount)
    var i = 0

    dest(i) = _xyzw(0); i+=1
    dest(i) = _xyzw(1); i+=1
    dest(i) = _xyzw(2); i+=1
    dest(i) = _xyzw(3); i+=1
    dest(i) = _rgba(0); i+=1
    dest(i) = _rgba(1); i+=1
    dest(i) = _rgba(2); i+=1
    dest(i) = _rgba(3); i+=1
    dest(i) = _st(0); i+=1
    dest(i) = _st(1); i+=1

    dest
  }

  override def toString = s"TexturedVertex(x:${_xyzw(0)}, y:${_xyzw(1)}, z:${_xyzw(2)}, w:${_xyzw(3)}, r:${_rgba(0)}, g:${_rgba(1)}, b:${_rgba(2)}, a:${_rgba(3)}, s:${_st(0)}, t:${_st(1)})"

}
