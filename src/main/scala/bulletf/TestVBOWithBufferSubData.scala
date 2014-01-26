package bulletf

import org.lwjgl.opengl._
import java.nio.{ByteBuffer, FloatBuffer}
import org.lwjgl.{LWJGLException, BufferUtils}
import org.lwjgl.util.glu.GLU
import java.io.{FileInputStream, IOException}
import de.matthiasmann.twl.utils.PNGDecoder
import de.matthiasmann.twl.utils.PNGDecoder.Format
import org.lwjgl.input.Keyboard

object TestVBOWithBufferSubData {
  // Quad 変数
  private var vaoId = 0
  private var vboId = 0
  private var vboiId = 0
  private var indicesCount = 0
  // シェーダ用の変数
  private var vsId = 0
  private var fsId = 0
  private var pId = 0
  // 他
  private val width = 640
  private val height = 640
  // テクスチャ
  private val texIds = Array(0, 0)
  private var textureSelector = 0
  // update VBO variables
  private var vertices: Array[TexturedVertex] = null
  private var vertexByteBuffer: ByteBuffer = null
  private var verticesByteBuffer: ByteBuffer = null

  def main(args: Array[String]) {
    setupOpenGL()
    setupQuad()
    setupShaders()
    setupTextures()

    while(!Display.isCloseRequested) {
      loopCycle()

      Display.sync(60)
      Display.update()
    }

    destroyOpenGL()
  }

  def setupTextures() {
    texIds(0) = loadPNGTexture("img/ash_uvgrid01.png", GL13.GL_TEXTURE0)
    texIds(1) = loadPNGTexture("img/ash_uvgrid07.png", GL13.GL_TEXTURE0)
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

    val v0 = TexturedVertex.xyz(-0.5f, 0.5f, 0f).rgb(1, 0, 0).st(0, 0).x
    val v1 = TexturedVertex.xyz(-0.5f, -0.5f, 0f).rgb(0, 1, 0).st(0, 1).x
    val v2 = TexturedVertex.xyz(0.5f, -0.5f, 0f).rgb(0, 0, 1).st(1, 1).x
    val v3 = TexturedVertex.xyz(0.5f, 0.5f, 0f).rgb(1, 1, 1).st(1, 0).x

    vertices = Array[TexturedVertex](v0, v1, v2, v3)

    vertexByteBuffer = BufferUtils.createByteBuffer(TexturedVertex.stride)

    verticesByteBuffer = BufferUtils.createByteBuffer(vertices.length * TexturedVertex.stride)
    val verticesFloatBuffer = verticesByteBuffer.asFloatBuffer()
    vertices foreach { v =>
      verticesFloatBuffer.put(v.elements)
    }
    verticesFloatBuffer.flip()

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

    // 新しいVBOをメモリに作ってbindする(頂点と色)
    vboId = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesFloatBuffer, GL15.GL_STREAM_DRAW)

    GL20.glVertexAttribPointer(0, TexturedVertex.positionElementCount, GL11.GL_FLOAT, false, TexturedVertex.stride, TexturedVertex.positionByteOffset)
    GL20.glVertexAttribPointer(1, TexturedVertex.colorElementCount, GL11.GL_FLOAT, false, TexturedVertex.stride, TexturedVertex.colorByteOffset)
    GL20.glVertexAttribPointer(2, TexturedVertex.textureElementCount, GL11.GL_FLOAT, false, TexturedVertex.stride, TexturedVertex.textureByteOffset)

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

    vsId = loadShader("./shader/vertex_textured.glsl", GL20.GL_VERTEX_SHADER)
    fsId = loadShader("./shader/fragment_textured.glsl", GL20.GL_FRAGMENT_SHADER)

    pId = GL20.glCreateProgram()
    GL20.glAttachShader(pId, vsId)
    GL20.glAttachShader(pId, fsId)

    GL20.glBindAttribLocation(pId, 0, "in_Position")
    GL20.glBindAttribLocation(pId, 1, "in_Color")
    GL20.glBindAttribLocation(pId, 2, "in_TextureCoord")

    GL20.glLinkProgram(pId)
    GL20.glValidateProgram(pId)

    errorCheckValue = GL11.glGetError()
    if (errorCheckValue != GL11.GL_NO_ERROR) {
      println(s"ERROR - Could not create the shaders: ${GLU.gluErrorString(errorCheckValue)}")
      sys.exit(1)
    }
  }


  def loopCycle() {

    logicCycle()
    renderCycle()
  }

  def logicCycle() {
    while(Keyboard.next()) {
      if (Keyboard.getEventKeyState) {
        Keyboard.getEventKey match {
          case Keyboard.KEY_1 => textureSelector = 0
          case Keyboard.KEY_2 => textureSelector = 1
          case _ =>
        }
      }
    }

    // 頂点をupdateするためにまずVBOをbind
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)

    for(i <- 0 until vertices.length) {
      val vertex = vertices(i)

      val offsetX = (math.cos(math.Pi * math.random) * 0.1).toFloat
      val offsetY = (math.sin(math.Pi * math.random) * 0.1).toFloat

      val xyz = vertex.xyzw
      vertex._xyzw(0) += offsetX
      vertex._xyzw(1) += offsetY

      val vertexFloatBuffer = vertexByteBuffer.asFloatBuffer()
      vertexFloatBuffer.rewind()
      vertexFloatBuffer.put(vertex.elements)
      vertexFloatBuffer.flip()

      GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, i * TexturedVertex.stride, vertexByteBuffer)

      vertex._xyzw(0) = xyz(0)
      vertex._xyzw(1) = xyz(1)
      vertex._xyzw(2) = xyz(2)
    }

    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)

  }

  def renderCycle() {
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)

    GL20.glUseProgram(pId)

    GL13.glActiveTexture(GL13.GL_TEXTURE0)
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texIds(textureSelector))

    GL30.glBindVertexArray(vaoId)
    GL20.glEnableVertexAttribArray(0)
    GL20.glEnableVertexAttribArray(1)
    GL20.glEnableVertexAttribArray(2)

    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId)

    GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0)

    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)
    GL20.glDisableVertexAttribArray(0)
    GL20.glDisableVertexAttribArray(1)
    GL20.glDisableVertexAttribArray(2)
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

    // VAOを選択
    GL30.glBindVertexArray(vaoId)

    // VAO属性リストからVBOインデックスを無効に（？？？）
    GL20.glDisableVertexAttribArray(0)
    GL20.glDisableVertexAttribArray(1)
    GL20.glDisableVertexAttribArray(2)

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

  def loadPNGTexture(filename: String, textureUnit: Int): Int = {
    var buf: ByteBuffer = null
    var tWidth = 0
    var tHeight = 0

    try {
      val in = new FileInputStream(filename)
      val decoder = new PNGDecoder(in)

      tWidth = decoder.getWidth
      tHeight = decoder.getHeight

      buf = ByteBuffer.allocateDirect(4 * decoder.getWidth * decoder.getHeight)
      decoder.decode(buf, decoder.getWidth * 4, Format.RGBA)

      buf.flip()
      in.close()

    } catch {
      case e: IOException =>
        e.printStackTrace()
        sys.exit(-1)
    }

    val texId = GL11.glGenTextures()
    GL13.glActiveTexture(textureUnit)
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId)

    GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)

    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, tWidth, tHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf)
    GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)

    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT)
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT)

    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR)

    texId
  }

}
