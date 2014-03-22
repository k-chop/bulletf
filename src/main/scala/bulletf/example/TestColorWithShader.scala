package bulletf.example

import org.lwjgl.opengl._
import java.nio.FloatBuffer
import org.lwjgl.{LWJGLException, BufferUtils}
import org.lwjgl.util.glu.GLU

object TestColorWithShader {
  // Quad 変数
  private var vaoId = 0
  private var vboId = 0
  private var vbocId = 0
  private var vboiId = 0
  private var indicesCount = 0
  // シェーダ用の変数
  private var vsId = 0
  private var fsId = 0
  private var pId = 0
  // 他
  private val width = 640
  private val height = 480

  /*def main(args: Array[String]) {
    setupOpenGL()
    setupQuad()
    setupShaders()

    while(!Display.isCloseRequested) {
      loopCycle()

      Display.sync(60)
      Display.update()
    }

    destroyOpenGL()
  }*/

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
      -0.5f, 0.5f, 0f, 1f, // ID:0
      -0.5f, -0.5f, 0f, 1f,// ID:1
      0.5f, -0.5f, 0f, 1f, // ID:2
      0.5f, 0.5f, 0f, 1f   // ID:3
    )
    val verticesBuffer = BufferUtils.createFloatBuffer(vertices.length)
    verticesBuffer.put(vertices)
    verticesBuffer.flip()

    val colors = Array[Float](
      1f, 0f, 0f, 1f,
      0f, 1f, 0f, 1f,
      0f, 0f, 1f, 1f,
      1f, 1f, 1f, 1f
    )
    val colorsBuffer = BufferUtils.createFloatBuffer(colors.length)
    colorsBuffer.put(colors)
    colorsBuffer.flip()

    val indices = Array[Byte](
      0, 1, 2,
      2, 3, 0
    )

    indicesCount = indices.length
    val indicesBuffer = BufferUtils.createByteBuffer(indicesCount)
    indicesBuffer.put(indices)
    indicesBuffer.flip()

    // 新しいVAOをメモリに作ってbindする
    vaoId = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vaoId)

    // 新しいVBOをメモリに作ってbindする(頂点)
    vboId = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW)
    GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 0, 0)
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)

    // 新しいVBOをメモリに作ってbindする(色)
    vbocId = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbocId)
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorsBuffer, GL15.GL_STATIC_DRAW)
    GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 0, 0)
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)

    // VAOをunbind
    GL30.glBindVertexArray(0)

    // 新しいVBOを作ってbindする(頂点インデックス)
    vboiId = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId)
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW)
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)

  }

  def setupShaders() {
    var errorCheckValue = GL11.glGetError()

    vsId = loadShader("./shader/vertex.glsl", GL20.GL_VERTEX_SHADER)
    fsId = loadShader("./shader/fragment.glsl", GL20.GL_FRAGMENT_SHADER)

    pId = GL20.glCreateProgram()
    GL20.glAttachShader(pId, vsId)
    GL20.glAttachShader(pId, fsId)

    GL20.glBindAttribLocation(pId, 0, "in_Position")
    GL20.glBindAttribLocation(pId, 1, "in_Color")

    GL20.glLinkProgram(pId)
    GL20.glValidateProgram(pId)

    errorCheckValue = GL11.glGetError()
    if (errorCheckValue != GL11.GL_NO_ERROR) {
      println(s"ERROR - Could not create the shaders: ${GLU.gluErrorString(errorCheckValue)}")
      sys.exit(1)
    }
  }


  def loopCycle() {
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)

    GL20.glUseProgram(pId)

    GL30.glBindVertexArray(vaoId)
    GL20.glEnableVertexAttribArray(0)
    GL20.glEnableVertexAttribArray(1)

    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId)

    GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0)

    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)
    GL20.glDisableVertexAttribArray(0)
    GL20.glDisableVertexAttribArray(1)
    GL30.glBindVertexArray(0)
    GL20.glUseProgram(0)
  }

  def destroyOpenGL() {
    // シェーダの始末
    GL20.glUseProgram(0)
    GL20.glDetachShader(pId, vsId)
    GL20.glDetachShader(pId, fsId)

    GL20.glDeleteShader(vsId)
    GL20.glDeleteShader(fsId)
    GL20.glDeleteProgram(pId)

    GL30.glBindVertexArray(vaoId)

    // VAO属性リストからVBOインデックスを無効に（？？？）
    GL20.glDisableVertexAttribArray(0)
    GL20.glDisableVertexAttribArray(1)

    // 頂点情報を格納したVBOを消去
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    GL15.glDeleteBuffers(vboId)

    // 色情報を格納したVBOを消去
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)
    GL15.glDeleteBuffers(vbocId)

    // インデックス情報を格納したVBOを消去
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)
    GL15.glDeleteBuffers(vboiId)

    // VAO消去
    GL30.glBindVertexArray(0)
    GL30.glDeleteVertexArrays(vaoId)

    Display.destroy()
  }

  def loadShader(filename: String, shaderType: Int): Int = {
    var shaderId = 0
    val shaderSource = io.Source.fromFile(filename).mkString

    //println(shaderSource)

    shaderId = GL20.glCreateShader(shaderType)
    //println(s"shaderId: $shaderId")
    GL20.glShaderSource(shaderId, shaderSource)
    GL20.glCompileShader(shaderId)

    val succeeded: Int = GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS)
    if (succeeded == 0) {
      val len = GL20.glGetShaderi(shaderId, GL20.GL_INFO_LOG_LENGTH)
      println(GL20.glGetShaderInfoLog(shaderId, len))
    } else {
      println(s"$filename compile succeeded.")
    }

    shaderId
  }

}
