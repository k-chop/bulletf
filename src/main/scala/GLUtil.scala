package com.github.whelmaze.bulletf

import org.lwjgl.opengl.GL11

// from https://bitbucket.org/chuck/lwjgl-sandbox/src/tip/src/main/java/sandbox/util/GLUT.java


object GLUtil {

  val boxVertices: Array[Array[Float]] = Array(
    Array(-0.5f,-0.5f,-0.5f),
    Array(-0.5f,-0.5f,0.5f),
    Array(-0.5f,0.5f,0.5f),
    Array(-0.5f,0.5f,-0.5f),
    Array(0.5f,-0.5f,-0.5f),
    Array(0.5f,-0.5f,0.5f),
    Array(0.5f,0.5f,0.5f),
    Array(0.5f,0.5f,-0.5f)
  )

  val boxNormals: Array[Array[Float]] = Array(
    Array(-1f,0f,0f),
    Array(0f,1f,0f),
    Array(1f,0f,0f),
    Array(0f,-1f,0f),
    Array(0f,0f,1f),
    Array(0f,0f,-1f)
  )

  val boxFaces: Array[Array[Int]] = Array(
    Array(0,1,2,3),
    Array(3,2,6,7),
    Array(7,6,5,4),
    Array(4,5,1,0),
    Array(5,6,2,1),
    Array(7,4,0,3)
  )

  def drawBox(size: Float, typ: Int) {
    import GL11._

    val v = boxVertices
    val n = boxNormals
    val f = boxFaces
    var i = 5

    while(i >= 0) {
      glBegin(typ)
      glNormal3f(n(i)(0), n(i)(1), n(i)(2))
      var vt = v(f(i)(0))
      glColor3d(1.0, 0, 0)
      glVertex3f(vt(0)*size, vt(1)*size, vt(2)*size)
      vt = v(f(i)(1))
      glColor3d(0, 1, 0)
      glVertex3f(vt(0)*size, vt(1)*size, vt(2)*size)
      vt = v(f(i)(2))
      glColor3d(0, 0, 1)
      glVertex3f(vt(0)*size, vt(1)*size, vt(2)*size)
      vt = v(f(i)(3))
      glColor3d(1, 1, 1)
      glVertex3f(vt(0)*size, vt(1)*size, vt(2)*size)
      glEnd()

      i -= 1
    }

  }

}
