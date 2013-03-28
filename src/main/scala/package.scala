package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.script._

import scala.language.implicitConversions
import org.lwjgl.input.Keyboard

object `package` {

  object implicits {

    implicit def angle2double(a: Angle): Double = a.dir
    implicit def double2Container(d: Double): DVar = DVar(d)

  }

  //implicit def container2Double(c: Container): Double = c.get()

  object constants {
    
    final val screenWidth = 480
    final val screenHeight = 640
    final val centerX = screenWidth / 2
    final val centerY = screenHeight / 2
  }
  val ri = { i: Int => util.Random.nextInt(i) }
  val rd = { i: Int => util.Random.nextDouble()*i }

}
