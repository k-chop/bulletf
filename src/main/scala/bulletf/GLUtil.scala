package bulletf



import org.lwjgl.opengl.{GL20, GL15, GL30, GL11}
import java.nio.{ByteBuffer, FloatBuffer}
import org.lwjgl.BufferUtils

// from https://bitbucket.org/chuck/lwjgl-sandbox/src/tip/src/main/java/sandbox/util/GLUT.java


object GLUtil {

  var vaoId = 0
  var vboId = 0
  var vbocId = 0
  var vboiId = 0
  var indicesCount = 0

  def setup() {
    val vertices: Array[Float] = Array(
      -0.5f, 0.5f, 0f, 1f,
      -0.5f, -0.5f, 0f, 1f,
      0.5f, -0.5f, 0f, 1f,
      0.5f, 0.5f, 0f, 1f
    )
    val verticesBuffer: FloatBuffer  = BufferUtils.createFloatBuffer(vertices.length)
    verticesBuffer.put(vertices)
    verticesBuffer.flip()

    val colors: Array[Float] = Array(
      1f, 0f, 0f, 1f,
      0f, 1f, 0f, 1f,
      0f, 0f, 1f, 1f,
      1f, 1f, 1f, 1f
    )
    val colorsBuffer: FloatBuffer = BufferUtils.createFloatBuffer(colors.length)
    colorsBuffer.put(colors)
    colorsBuffer.flip()

    val indices: Array[Byte] = Array[Byte](
      0, 1, 2,
      2, 3, 0
    )
    indicesCount = indices.length
    val indicesBuffer: ByteBuffer = BufferUtils.createByteBuffer(indicesCount)
    indicesBuffer.put(indices)
    indicesBuffer.flip()

    vaoId = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vaoId)

    vboId = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW)
    GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 0, 0)
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)

    vbocId = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbocId)
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorsBuffer, GL15.GL_STATIC_DRAW)
    GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 0, 0)
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)

    GL30.glBindVertexArray(0)

    vboiId = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId)
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW)
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)

    println("GLUtils setuped.")
  }

  def drawVBO() {
    GL30.glBindVertexArray(vaoId)
    GL20.glEnableVertexAttribArray(0)
    GL20.glEnableVertexAttribArray(1)

    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId)

    GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0)

    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)
    GL20.glDisableVertexAttribArray(0)
    GL20.glDisableVertexAttribArray(1)
    GL30.glBindVertexArray(0)
  }

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
