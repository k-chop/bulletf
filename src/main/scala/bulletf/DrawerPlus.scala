package bulletf

import org.lwjgl.opengl._
import java.nio.{IntBuffer, ByteBuffer, FloatBuffer}
import java.io.{IOException, FileInputStream}
import de.matthiasmann.twl.utils.PNGDecoder
import de.matthiasmann.twl.utils.PNGDecoder.Format
import org.lwjgl.{BufferUtils, LWJGLException}
import org.lwjgl.util.glu.GLU
import org.lwjgl.util.vector.{Matrix4f, Vector3f}


class Moving(var x: Float, var y: Float, width: Int, height: Int, val angle: Float, speed: Float) {
  import scala.math._

  def store(buf: FloatBuffer) = {
    import DrawerPlus.{width => w, height => h}
    val x1 = (x / w)*2f-1f
    val y1 = (y / h)*2f-1f
    val y2 = ((y+height) / h)*2f-1f
    val x2 = ((x+width) / w)*2f-1f

    buf.put(x1).put(y1).put(1f).put(1f).put(1f).put(1f).put(0f).put(0f)
    buf.put(x1).put(y2).put(1f).put(1f).put(1f).put(1f).put(0f).put(1f)
    buf.put(x2).put(y2).put(1f).put(1f).put(1f).put(1f).put(1f).put(1f)
    buf.put(x2).put(y1).put(1f).put(1f).put(1f).put(1f).put(1f).put(0f)
  }

  def update() = {
    x += (cos(angle * Pi / 180.0f) * speed).toFloat
    y += (sin(angle * Pi / 180.0f) * speed).toFloat
  }
}

object DrawerPlus {
  private[this] var vao: VAO = _
  private[this] var vbo: VBO = _
  private[this] var ibo: VBO = _
  private[this] var shader: Shader = _
  private[this] val texIds: Array[Int] = Array(0, 0)

  val width = 640
  val height = 480

  //-- test --------------
  private[this] var a: ByteBuffer = _
  private[this] var temp: ByteBuffer = _
  private[this] var idxsbuf: ByteBuffer = _
  private[this] var iboTemp: ByteBuffer = _
  private[this] var angleDelta = 1f
  private[this] val rotateVec = new Vector3f()
  private[this] val mat44buf = BufferUtils.createFloatBuffer(16)
  private[this] val projbuf = {
    val t4 = GLUtil.ortho2dMatrix(width, height)
    val bu = BufferUtils.createFloatBuffer(16)
    t4.store(bu)
    bu.flip()
    bu
  }
  val QUAD_MAX_NUM = 20000
  private[this] val movings = Array.tabulate(QUAD_MAX_NUM){ i => new Moving(width/2f, height/2f, 10, 10, (math.random*360f).toFloat, (math.random*0.6f).toFloat) }
  var timeidx = 0
  val updateTimes = Array.ofDim[Long](60)
  val drawTimes = Array.ofDim[Long](60)
  //--------------------

  def setup() = {
    vao = VAO.gen()
    vao.bindWith { _ =>
      vbo = VBO.gen(GL15.GL_ARRAY_BUFFER)
      ibo = VBO.gen(GL15.GL_ELEMENT_ARRAY_BUFFER)
    }
  }

  // ------------------------------------

  def main(args: Array[String]) = {

    def time = System.nanoTime()

    setupOpenGL()
    setupShader()
    setupQuads()
    setupTextures()

    while (!Display.isCloseRequested) {
      val be = time
      step()
      updateTimes(timeidx) = time - be
      val be2 = time
      render()
      drawTimes(timeidx) = time - be2

      timeidx+=1
      timeidx%=60
      if (timeidx == 0) {
        println("update average: " + updateTimes.sum/60.0/1000/1000) //msec
        println("draw average: " + drawTimes.sum/60.0/1000/1000)  //msec
      }

      Display.sync(60)
      Display.update()
    }

    destroyAll()
  }

  def setupOpenGL() = {
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

  def setupShader() = {
    shader = new Shader("shader/subd.vert", "shader/subd.frag")
  }

  val MAX_VERTICES = QUAD_MAX_NUM*4
  def setupQuads() = {
    val p: Array[Array[Float]] = Array(
      // x,y,r,g,b,a,s,t
      Array(-0.5f, 0.5f, 1f, 0f, 0f, 1f, 0f, 0f),
      Array(-0.5f,-0.5f, 0f, 1f, 0f, 1f, 0f, 1f),
      Array( 0.5f,-0.5f, 0f, 0f, 1f, 1f, 1f, 1f),
      Array( 0.5f, 0.5f, 1f, 1f, 1f, 1f, 1f, 0f)
    )

    temp = BufferUtils.createByteBuffer(8 * 4 *4)
    a = BufferUtils.createByteBuffer(8 * 4 *4 *MAX_VERTICES)
    val vf = a.asFloatBuffer()
    p.foreach(vf.put)
    vf.rewind() // not flip!!!

    iboTemp = BufferUtils.createByteBuffer(6*4)
    val idxs: Array[Int] = Array(0,1,2,2,3,0)
    idxsbuf = BufferUtils.createByteBuffer(MAX_VERTICES*4) // Int
    val e = idxsbuf.asIntBuffer()
    e.put(idxs)
    e.rewind() // not flip!!!

    vao = VAO.gen()
    vao.bindWith { _ =>
      vbo = VBO.gen(GL15.GL_ARRAY_BUFFER)
      vbo.bindWith { v =>
        v.set(vf, GL15.GL_STREAM_DRAW)
        v.attribute(0, 2, GL11.GL_FLOAT, normalized = false, 8*4, 0)
        v.attribute(1, 4, GL11.GL_FLOAT, normalized = false, 8*4, 2*4)
        v.attribute(2, 2, GL11.GL_FLOAT, normalized = false, 8*4, 6*4)
      }
    }
    ibo = VBO.gen(GL15.GL_ELEMENT_ARRAY_BUFFER)
    ibo.bindWith {
      _.set(idxsbuf, GL15.GL_STREAM_DRAW)
    }
  }

  def setupTextures() = {
    texIds(0) = TextureLoader.fromPNG("img/ash_uvgrid01.png")
    texIds(1) = TextureLoader.fromPNG("img/ash_uvgrid07.png")
  }

  def step() = {

    //p("first")
    val fbuf = temp.asFloatBuffer()
    movings.foreach(_.update())

    //p("vbo bind before")
    vbo.bind()
    var i = 0
    var bb = true
    while(i < movings.length) {
      fbuf.rewind()
      movings(i).store(fbuf)
      fbuf.flip()
      //println(i +"__: " + inp(fbuf))
      //p(s"sub bef (${temp.limit()}})")
      val q = 4L*8*4*i
      GL11.glGetError() // suteru
      GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, q, temp)
      if (bb && GL11.glGetError() == GL11.GL_INVALID_VALUE) {
        println("idx:"+i +" (invalid value found)")
        bb = false
      }
      //p("sub aft")
      i+=1
    }
    vbo.unbind()
    //p("vbo bind after, ibo bind before")

    ibo.bind()
    var j = 0
    while(j < movings.length) {
      val ofs = j*4
      iboTemp.rewind()
      val ib = iboTemp.asIntBuffer()
      ib.put(0+ofs).put(1+ofs).put(2+ofs).put(2+ofs).put(3+ofs).put(0+ofs)
      ib.flip()
      GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 6L*4*j, iboTemp)
      j+=1
    }
    ibo.unbind()
    //p("ibo bind after")

    val modelmat = new Matrix4f()
    //rotateVec.z += angleDelta
    //Matrix4f.rotate(rotateVec.getZ.toDouble.toRadians.toFloat, new Vector3f(0, 0, 1), modelmat, modelmat)

    shader.useWith { s =>
      modelmat.store(mat44buf)
      mat44buf.flip()
      s.setUniformMat4("modelMatrix", transpose = false, mat44buf)
      s.setUniformMat4("projMatrix", transpose = false, projbuf)
    }

    /*vbo.bindWith { b =>
      temp.clear()
      GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, 0L, temp)
      println("0: " + inp(temp.asFloatBuffer()))
      temp.clear()
      GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, 8*4*4, temp)
      println("1: " + inp(temp.asFloatBuffer()))
      temp.clear()
      GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, 8L*4*4*2, temp)
      println("2: " + inp(temp.asFloatBuffer()))
    }*/
  }
  def p(s: String) = println(s + ": " +GLU.gluErrorString(GL11.glGetError()))

  def inp(f: FloatBuffer) = {
    f.rewind()
    val a = collection.mutable.ArrayBuilder.make[Float]()
    val lim = f.limit()
    (0 until lim) foreach { i =>
      a += f.get(i)
    }
    a.result().deep.toString()
  }
  def render() = {
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)

    shader.useWith { _ =>
      GL13.glActiveTexture(GL13.GL_TEXTURE0)
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, texIds(0))

      vao.bindWith { _ =>
        vbo.enableAttributes()
        ibo.bindWith { _ =>
          GL11.glDrawElements(GL11.GL_TRIANGLES, 6*movings.length, GL11.GL_UNSIGNED_INT, 0)
        }
        vbo.disableAttributes()
      }
    }

  }

  def destroyAll() = {
    shader.delete()
    vao.bindWith { _ =>
      vbo.disableAttributes()
      vbo.delete()
    }
    vao.delete()
    ibo.delete()
  }
}

class Shader(vertex: String, fragment: String) {
  val vertId = load(vertex, GL20.GL_VERTEX_SHADER)
  val fragId = load(fragment, GL20.GL_FRAGMENT_SHADER)
  val pId = GL20.glCreateProgram()
  val uniforms = collection.mutable.HashMap.empty[String, Int]

  prepare()

  def on() = GL20.glUseProgram(pId)
  def off() = GL20.glUseProgram(0)

  def useWith[T](f: Shader => T) = {
     on(); f(this); off()
  }

  def prepare() = {
    GL20.glAttachShader(pId, vertId)
    GL20.glAttachShader(pId, fragId)

    GL20.glBindAttribLocation(pId, 0, "in_Position")
    GL20.glBindAttribLocation(pId, 1, "in_Color")
    GL20.glBindAttribLocation(pId, 2, "in_TextureCoord")

    GL20.glLinkProgram(pId)
    GL20.glValidateProgram(pId)

    uniforms += "modelMatrix" -> GL20.glGetUniformLocation(pId, "modelMatrix")
    uniforms += "projMatrix" -> GL20.glGetUniformLocation(pId, "projMatrix")

    val eChk = GL11.glGetError()
    if (eChk != GL11.GL_NO_ERROR) {
      println(s"ERROR - Could not create the shaders: ${GLU.gluErrorString(eChk)}")
      sys.exit(1)
    }
  }

  def setUniformMat4(uniformName: String, transpose: Boolean = false, buffer: FloatBuffer) = {
    if (!uniforms.isDefinedAt(uniformName)) println("uniform name '$uniformName' is not defined.")
    uniforms.get(uniformName) foreach { i =>
      GL20.glUniformMatrix4(i, transpose, buffer)
    }
  }

  def load(filename: String, shaderType: Int): Int = {
    var id = 0
    val source = io.Source.fromFile(filename).mkString

    id = GL20.glCreateShader(shaderType)
    GL20.glShaderSource(id, source)
    GL20.glCompileShader(id)

    val succeeded = GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS)
    if (succeeded == GL11.GL_FALSE) {
      println(s"error occured in compile $filename.")
      val len = GL20.glGetShaderi(id, GL20.GL_INFO_LOG_LENGTH)
      println(GL20.glGetShaderInfoLog(id, len))
    } else {
      val t = if (shaderType == GL20.GL_FRAGMENT_SHADER) "fragment" else if (shaderType == GL20.GL_VERTEX_SHADER) "vertex" else "???"
      println(s"$filename compile succeeded (as $t shader).")
    }

    id
  }

  def delete() = {
    this.off()
    GL20.glDetachShader(pId, vertId)
    GL20.glDetachShader(pId, fragId)
    GL20.glDeleteShader(vertId)
    GL20.glDeleteShader(fragId)
    GL20.glDeleteProgram(pId)
  }
}

object VBO {

  def gen(typ: Int): VBO = {
    val vboId = GL15.glGenBuffers()
    new VBO(vboId, typ)
  }
}

class VBO(id: Int, typ: Int) {

  def bind() = GL15.glBindBuffer(typ, id)

  def unbind() = GL15.glBindBuffer(typ, 0)

  def bindWith[T](f: VBO => T) = {
    bind(); f(this); unbind()
  }

  def set(data: FloatBuffer, drawType: Int) = GL15.glBufferData(typ, data, drawType)
  def set(data: ByteBuffer, drawType: Int) = GL15.glBufferData(typ, data, drawType)
  def set(data: IntBuffer, drawType: Int) = GL15.glBufferData(typ, data, drawType)

  def attribute(listIdx: Int, size: Int, dataType: Int, normalized: Boolean, stride: Int, offset: Long) = GL20.glVertexAttribPointer(listIdx, size, dataType, normalized, stride, offset)

  def disableAttributes() = {
    // TODO: atode naosu!!!!!!!!!!! kore VAO no hou ni ugokasu beki zyane?
    GL20.glDisableVertexAttribArray(0)
    GL20.glDisableVertexAttribArray(1)
    GL20.glDisableVertexAttribArray(2)
  }

  def enableAttributes() = {
    // TODO: atode naosu!!!!!!!!!!! kore VAO no hou ni ugokasu beki zyane?
    GL20.glEnableVertexAttribArray(0)
    GL20.glEnableVertexAttribArray(1)
    GL20.glEnableVertexAttribArray(2)
  }

  def delete() = {
    unbind()
    GL15.glDeleteBuffers(id)
  }
}

object VAO {

  def gen(): VAO = {
    val vaoId = GL30.glGenVertexArrays()
    new VAO(vaoId)
  }
}

class VAO(id: Int) {

  def bind() = GL30.glBindVertexArray(id)

  def unbind() = GL30.glBindVertexArray(0)

  @inline def bindWith[T](f: VAO => T) = {
    this.bind()
    f(this)
    this.unbind()
  }

  def delete() = {
    this.unbind()
    GL30.glDeleteVertexArrays(id)
  }

}

object TextureLoader {

  def fromPNG(filename: String, textureUnit: Int = GL13.GL_TEXTURE0): Int = {
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