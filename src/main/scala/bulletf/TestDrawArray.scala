package bulletf

import org.lwjgl.opengl._
import java.nio.FloatBuffer
import org.lwjgl.{LWJGLException, BufferUtils}

object TestDrawArray {
  private var vaoId: Int = 0
  private var vboId: Int = 0
  private var vertexCount: Int = 0
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
    val vertices: Array[Float] = Array(
      -0.5f, 0.5f, 0f,
      -0.5f, -0.5f, 0f,
      0.5f, -0.5f, 0f,
      0.5f, -0.5f, 0f,
      0.5f, 0.5f, 0f,
      -0.5f, 0.5f, 0f
    )
    val verticesBuffer = BufferUtils.createFloatBuffer(vertices.length)
    verticesBuffer.put(vertices)
    verticesBuffer.flip()

    vertexCount = 6

    vaoId = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vaoId)

    vboId = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW)

    GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0)

    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)

    GL30.glBindVertexArray(0)
  }

  def loopCycle() {
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
    GL30.glBindVertexArray(vaoId)
    GL20.glEnableVertexAttribArray(0)

    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount)

    GL20.glDisableVertexAttribArray(0)
    GL30.glBindVertexArray(0)
  }

  def destroyOpenGL() {

    GL20.glDisableVertexAttribArray(0)

    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    GL15.glDeleteBuffers(vboId)

    GL30.glBindVertexArray(0)
    GL30.glDeleteVertexArrays(vaoId)

    Display.destroy()

  }
}
