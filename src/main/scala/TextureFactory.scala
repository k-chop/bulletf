package com.github.whelmaze.bulletf

import org.newdawn.slick.opengl.{ Texture, TextureLoader }
import java.io.FileInputStream

import collection.mutable


object TextureFactory {
  private val cache = mutable.WeakHashMap.empty[Symbol, Texture]

  def get(key: Symbol): Texture = {
    cache.get(key) match {
      case Some(texture) => texture
      case None =>
        val texture = TextureLoader.getTexture("PNG", new FileInputStream( Resource.buildPath(key)) )
        cache.update(key, texture)
        texture
    }
  }
  
}
