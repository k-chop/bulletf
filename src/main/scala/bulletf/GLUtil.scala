package bulletf



import org.lwjgl.opengl._
import java.nio.{ByteBuffer, FloatBuffer}
import org.lwjgl.BufferUtils
import org.lwjgl.util.vector.Matrix4f

// from https://bitbucket.org/chuck/lwjgl-sandbox/src/tip/src/main/java/sandbox/util/GLUT.java


object GLUtil {

  def ortho2dMatrix(width: Int, height: Int, dest: Matrix4f = new Matrix4f()): Matrix4f = {
    import dest._
    val m00a = 2.0f / width
    val m11a = -2.0f / height
    m00 = m00a;  m01 = 0f;    m02 = 0f;   m03 = 1f
    m10 = 0f;    m11 = m11a;  m12 = 0f;   m13 = 1f
    m20 = 0f;    m21 = 0f;    m22 = -1f;  m23 = 0f
    m30 = 0f;    m31 = 0f;    m32 = 0f;   m33 = 1f
    dest
  }

  def printGLSpecs() = {
    println("vbo: " + GLContext.getCapabilities.GL_ARB_vertex_buffer_object)
    println("drawElement: " + GLContext.getCapabilities.GL_ARB_draw_elements_base_vertex)
    println("uniform buffer object: " + GLContext.getCapabilities.GL_ARB_uniform_buffer_object)
    println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION))
    println("GLSL version: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION))
  }

}
