package bulletf

import org.lwjgl.opengl._
import java.nio.{IntBuffer, ByteBuffer, FloatBuffer}
import java.io.{IOException, FileInputStream}
import de.matthiasmann.twl.utils.PNGDecoder
import de.matthiasmann.twl.utils.PNGDecoder.Format
import org.lwjgl.{BufferUtils, LWJGLException}
import org.lwjgl.util.glu.GLU
import org.lwjgl.util.vector.{Vector2f, Vector4f, Matrix4f, Vector3f}
import scala.collection.mutable

object DrawerPlus {

  private[this] var vao: VAO = _
  private[this] var vbo: VBO = _
  private[this] var ibo: VBO = _
  private[this] var shader: Shader = _

  lazy val width = Game.width
  lazy val height = Game.height

  //-- test --------------
  private[this] var verticesBuffer: ByteBuffer = _
  private[this] var verticesBufferTemp: ByteBuffer = _
  private[this] var indexBuffer: ByteBuffer = _
  private[this] var indexBufferTemp: ByteBuffer = _
  private[this] val mat44buf = BufferUtils.createFloatBuffer(16)
  private[this] val projbuf = {
    val t4 = GLUtil.ortho2dMatrix(width, height)
    val bu = BufferUtils.createFloatBuffer(16)
    t4.store(bu)
    bu.flip()
    bu
  }

  val QUAD_MAX_NUM = 20000
  val MAX_VERTICES = QUAD_MAX_NUM*4

  // time
  var timeidx = 0
  val updateTimes = Array.ofDim[Long](60)
  val drawTimes = Array.ofDim[Long](60)
  //--------------------

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

  def setupObjects() = {
    // x,y,r,g,b,a,s,t -> (x,y): position, (r,g,b,a): color, (s,t): texture coordinate
    val ms = VerticesLayout("xxcccctt", Map().withDefaultValue(GL11.GL_FLOAT))

    verticesBufferTemp = BufferUtils.createByteBuffer(ms.stride * 4) // vertices per quad
    verticesBuffer = BufferUtils.createByteBuffer(ms.stride * 4 * QUAD_MAX_NUM) // vertices are Float

    indexBufferTemp = BufferUtils.createByteBuffer(6*4)  // indices per quad
    indexBuffer = BufferUtils.createByteBuffer(MAX_VERTICES * 4) // indices are Int

    vao = VAO.gen()
    vao.bindWith { implicit vaoi => // for vbo operation which vao bind required.
      vbo = VBO.gen(GL15.GL_ARRAY_BUFFER)
      vbo.bindWith { v =>
        v.setData(verticesBuffer, GL15.GL_STREAM_DRAW)
        v.setAttributes(ms)
      }
    }
    ibo = VBO.gen(GL15.GL_ELEMENT_ARRAY_BUFFER)
    ibo.bindWith {
      _.setData(indexBuffer, GL15.GL_STREAM_DRAW)
    }
  }

  //--------------------
  var idx = 0
  var nowTextureId = 0
  var drawCalls = 0

  def begin() = {
    idx = 0
    drawCalls = 0
  }

  def end() = {
    if (idx > 0) flush()
  }

  // temp matrices (to avoid allocation)
  val rotSrc = new Matrix4f()
  val rotVec = new Vector3f(0.0f, 0.0f, 1.0f) // Z-axis
  val srcVec = new Vector4f(0.0f, 0.0f, 1.0f, 1.0f)
  val posVec = new Vector3f(0.0f, 0.0f, 1.0f)
  val scaleVec = new Vector3f()

  def storeVertices(fbuf: FloatBuffer, texture: Texture, rect: Rect, pos: Position, rotate: Double, scale: Double, alpha: Double) = {
    fbuf.rewind()

    import Game.{width => w, height => h}

    val ww = rect.w / 2.0
    val hh = rect.h / 2.0

    val x1 = ((pos.x-ww) / w * 2.0 - 1.0).toFloat
    val y1 = -((pos.y-hh) / h * 2.0 - 1.0).toFloat
    val y2 = -((pos.y-hh + rect.h) / h * 2.0 - 1.0).toFloat
    val x2 = ((pos.x-ww + rect.w) / w * 2.0 - 1.0).toFloat

    val fAlpha = alpha.toFloat
    val s1 = (rect.x / texture.width.toDouble).toFloat
    val s2 = ((rect.x + rect.w) / texture.width.toDouble).toFloat
    val t1 = (rect.y / texture.height.toDouble).toFloat
    val t2 = ((rect.y + rect.h) / texture.height.toDouble).toFloat

    rotSrc.setIdentity()

    val ddx = (pos.x/w*2.0-1.0).toFloat
    val ddy = -(pos.y/h*2.0-1.0).toFloat
    posVec.set(ddx, ddy)
    rotSrc.translate(posVec)
    rotSrc.rotate(-rotate.toRadians.toFloat, rotVec)
    scaleVec.set(scale.toFloat, scale.toFloat, 1f)
    rotSrc.scale(scaleVec)
    posVec.set(-ddx, -ddy)
    rotSrc.translate(posVec)

    srcVec.set(x1, y1, 1f, 1f); Matrix4f.transform(rotSrc, srcVec, srcVec)
    fbuf.put(srcVec.getX).put(srcVec.getY).put(1f).put(1f).put(1f).put(fAlpha).put(s1).put(t1)

    srcVec.set(x1, y2, 1f, 1f); Matrix4f.transform(rotSrc, srcVec, srcVec)
    fbuf.put(srcVec.getX).put(srcVec.getY).put(1f).put(1f).put(1f).put(fAlpha).put(s1).put(t2)

    srcVec.set(x2, y2, 1f, 1f); Matrix4f.transform(rotSrc, srcVec, srcVec)
    fbuf.put(srcVec.getX).put(srcVec.getY).put(1f).put(1f).put(1f).put(fAlpha).put(s2).put(t2)

    srcVec.set(x2, y1, 1f, 1f); Matrix4f.transform(rotSrc, srcVec, srcVec)
    fbuf.put(srcVec.getX).put(srcVec.getY).put(1f).put(1f).put(1f).put(fAlpha).put(s2).put(t1)

    fbuf.flip()
  }

  def draw(texture: Texture, rect: Rect, pos: Position, rotate: Double, scale: Double, alpha: Double) = {

    if (nowTextureId == -1) {
      texture.bind()
      nowTextureId = texture.id
    } else if (texture.id != nowTextureId) {
      flush()
      texture.bind()
      nowTextureId = texture.id
    }

    val fbuf = verticesBufferTemp.asFloatBuffer()
    storeVertices(fbuf, texture, rect, pos, rotate, scale, alpha)

    vbo.bindWith { _ =>
      GL11.glGetError()
      GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 4L*8*4*idx, verticesBufferTemp)
      if (GL11.glGetError() != GL11.GL_NO_ERROR) {
        println("error: " + GLU.gluErrorString(GL11.glGetError()))
      }
    }

    ibo.bindWith { _ =>
      indexBufferTemp.rewind()
      val ibuf = indexBufferTemp.asIntBuffer()
      val ofs = 4 * idx // quad = 4 vertices
      ibuf.put(0+ofs).put(1+ofs).put(2+ofs).put(2+ofs).put(3+ofs).put(0+ofs)
      ibuf.flip()
      GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 6L * 4 * idx, indexBufferTemp) // 6 index per quad
    }

    idx += 1
  }

  def flush() = {
    shader.useWith { s =>
      val modelmat = new Matrix4f()

      modelmat.store(mat44buf)
      mat44buf.flip()
      s.setUniformMat4("modelMatrix", transpose = false, mat44buf)
      s.setUniformMat4("projMatrix", transpose = false, projbuf)


      vao.bindWith { implicit a =>
        vbo.enableAttributes(a)
        ibo.bindWith { _ =>
          GL11.glDrawElements(GL11.GL_TRIANGLES, 6*idx, GL11.GL_UNSIGNED_INT, 0)
        }
        vbo.disableAttributes(a)
      }
    }
    drawCalls += 1
    idx = 0
  }

  def destroyAll() = {
    shader.delete()
    vao.bindWith { implicit a =>
      vbo.disableAttributes(a)
      vbo.delete()
    }
    vao.delete()
    ibo.delete()
  }
}

case class VerticesLayout(mapping: String, mapper: Map[Char, Int]) {

  private[this] def iterator(): Iterator[(Int, Int)] = {
    require(mapping != "", "Attribute mapping string must be nonEmpty.")

    val sizeMap: Map[Char, Int] = mapping groupBy identity map { case (c, s) => c -> s.length }
    val compacted = mapping.foldLeft(""+mapping.head){ case (acc,c) => if (c==acc.last) acc else acc + c }

    new Iterator[(Int, Int)] {
      private[this] val str = compacted
      private[this] var idx = 0
      def hasNext = idx < str.length
      def next() = {
        val c = str(idx)
        idx += 1
        (sizeMap(c), mapper(c))
      }
    }
  }

  private[this] val l = iterator().toList

  val stride = l.foldLeft(0){ case (c, (size, typ)) => c + size * GLUtil.byteSize(typ) }

  def makeRegisterList(): Array[(Int, Int, Int, Int, Long)] = {

    var offset = 0L
    var idx = 0

    val builder = mutable.ArrayBuilder.make[(Int, Int, Int, Int, Long)]()
    l foreach { case (size, data_type) =>
      builder += ((idx, size, data_type, stride, offset))

      idx += 1
      offset += size * GLUtil.byteSize(data_type)
    }
    builder.result()
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
    val source = io.Source.fromFile(filename).mkString

    val id = GL20.glCreateShader(shaderType)
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

  private[this] val usedAttrib = mutable.BitSet.empty

  private[this] var bound = false

  def isBound = bound

  def bind() = {
    GL15.glBindBuffer(typ, id)
    bound = true
  }

  def unbind() = {
    GL15.glBindBuffer(typ, 0)
    bound = false
  }

  @inline final def bindWith[T](f: VBO => T) = {
    bind(); f(this); unbind()
  }

  def setData(data: FloatBuffer, drawType: Int) = GL15.glBufferData(typ, data, drawType)
  def setData(data: ByteBuffer, drawType: Int) = GL15.glBufferData(typ, data, drawType)
  def setData(data: IntBuffer, drawType: Int) = GL15.glBufferData(typ, data, drawType)

  def attribute(listIdx: Int, size: Int, dataType: Int, normalized: Boolean, stride: Int, offset: Long)(implicit vao: VAO) = {
    GL20.glVertexAttribPointer(listIdx, size, dataType, normalized, stride, offset)
  }

  def setAttributes(ms: VerticesLayout)(implicit vao: VAO) = {
    require(vao.isBound, "setAttributes: cannot set to attribute list with no VAO bound.")

    ms.makeRegisterList() foreach { case (idx, size, data_type, stride, offset) =>
      println(s"set attribute: idx($idx), size($size), type(${GLUtil.name(data_type)}), stride($stride), offset($offset)")
      usedAttrib += idx
      GL20.glVertexAttribPointer(idx, size, data_type, false, stride, offset)
    }

  }

  def disableAttributes(implicit vao: VAO) = {
    require(vao.isBound, "disableAttributes: This operation is need to VAO bound.")

    usedAttrib.foreach( GL20.glDisableVertexAttribArray )
  }

  def enableAttributes(implicit vao: VAO) = {
    require(vao.isBound, "enableAttributes: This operation is need to VAO bound.")

    usedAttrib.foreach( GL20.glEnableVertexAttribArray )
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

  private[this] var bound: Boolean = false

  def isBound = bound

  def bind() = {
    GL30.glBindVertexArray(id)
    bound = true
  }

  def unbind() = {
    GL30.glBindVertexArray(0)
    bound = false
  }

  @inline final def bindWith[T](f: VAO => T) = {
    this.bind()
    f(this)
    this.unbind()
  }

  def delete() = {
    this.unbind()
    GL30.glDeleteVertexArrays(id)
  }

}

