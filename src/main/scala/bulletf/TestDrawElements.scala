package bulletf

import org.lwjgl.opengl._
import java.nio.{ByteBuffer, FloatBuffer}
import org.lwjgl.{LWJGLException, BufferUtils}

object TestDrawElements {
  private var vaoId = 0
  private var vboId = 0
  private var vboiId = 0
  private var indicesCount = 0

  private val width = 640
  private val height = 480

  def main(args: Array[String]) {
    setupOpenGL()
    setupQuad()

    while(!Display.isCloseRequested) {
      loopCycle()

      Display.sync(60)
      Display.update()
    }

    destroyOpenGL()
  }

  def setupOpenGL() {
    try {
      Display.setDisplayMode(new DisplayMode(width, height))
      Display.setTitle("test")
      Display.create()

      GL11.glViewport(0, 0, width, height)
    } catch {
      case e: LWJGLException =>
        e.printStackTrace()
        sys.exit(1)
    }

    GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f)
    GL11.glViewport(0, 0, width, height)
  }

  def setupQuad() {
    val vertices = Array[Float](
      -0.5f, 0.5f, 0f, // ID:0
      -0.5f, -0.5f, 0f,// ID:1
      0.5f, -0.5f, 0f, // ID:2
      0.5f, 0.5f, 0f   // ID:3
    )
    val verticesBuffer = BufferUtils.createFloatBuffer(vertices.length)
    verticesBuffer.put(vertices)
    verticesBuffer.flip()

    val indices = Array[Byte](
      0, 1, 2,
      2, 3, 0
    )

    indicesCount = indices.length
    val indicesBuffer = BufferUtils.createByteBuffer(indicesCount)
    indicesBuffer.put(indices)
    indicesBuffer.flip()

    vaoId = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vaoId)

    vboId = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW)

    GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0)

    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)

    GL30.glBindVertexArray(0)

    // ↓add (DrawArray -> DrawElements)
    vboiId = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId)
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW)
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)

  }

  def loopCycle() {
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
    GL30.glBindVertexArray(vaoId)
    GL20.glEnableVertexAttribArray(0)

    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId)

    GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0)

    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)
    GL20.glDisableVertexAttribArray(0)
    GL30.glBindVertexArray(0)
  }

  def destroyOpenGL() {
    // VAO属性リストからVBOインデックスを無効に（？？？）
    GL20.glDisableVertexAttribArray(0)

    // 頂点情報を格納したVBOを消去
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    GL15.glDeleteBuffers(vboId)

    // インデックス情報を格納したVBOを消去
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)
    GL15.glDeleteBuffers(vboiId)

    // VAO消去
    GL30.glBindVertexArray(0)
    GL30.glDeleteVertexArrays(vaoId)

    Display.destroy()

  }
}
