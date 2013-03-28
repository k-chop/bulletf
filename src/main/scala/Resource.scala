package com.github.whelmaze.bulletf

object Resource {
  
  final val path = "./"
  final val imgpath = path + "img/"
  final val basicExtension = ".png"
  final val scriptPath = path + "script/"
  
  final def buildPath(s: Symbol) = imgpath + s.name + basicExtension
  
  final val shipGraphic = 'ship
  final val titleGraphic = 'karititle

  final val nullImg = buildPath('nullpo)
}
