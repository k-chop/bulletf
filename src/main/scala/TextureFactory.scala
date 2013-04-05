package com.github.whelmaze.bulletf

import org.newdawn.slick.opengl.{ Texture, TextureLoader }
import java.io.{File, FileInputStream}

import collection.mutable


class SpriteAnimationInfo(pattern: Array[(Int, Rect)], loop: Boolean) {

  private[this] var pt: Int = 0

  def rect = pattern(0)._2

  def nextFrame: Int = pattern(pt)._1

  def nextStep(): Rect = {
    val res = pattern(pt)._2
    pt += 1
    if (pt >= pattern.length) {
      if (loop) pt = 0 else pt -= 1
    }
    res
  }
}

object TextureFactory {
  private val cache = mutable.WeakHashMap.empty[Symbol, (Texture, Rect)]
  private val aniCache = mutable.WeakHashMap.empty[Symbol, (Texture, SpriteAnimationInfo)]
  // Textureの生成はゲロ重いのでWeakだとすぐに破棄されてアカン
  private val texCache = mutable.HashMap.empty[String, Texture]

  def genNewTexture(uriStr: String, key: Symbol): Texture = {
    println("create texture: " + uriStr + ", key: " + key)
    val texture = TextureLoader.getTexture("PNG", new FileInputStream( uriStr ))
    texCache.update(uriStr, texture)
    texture
  }

  def get(key: Symbol): (Texture, Rect) = {
    cache.get(key) match {
      case Some(textureInfo) => textureInfo
      case None =>
        val (uriStr, rect) = fileMapped(key)
        val resTex = texCache.get(uriStr) match {
          case Some(tex) => tex
          case None => genNewTexture(uriStr, key)
        }
        val res = (resTex, rect)
        cache.update(key, res)
        res
    }
  }

  def getAnimate(key: Symbol): (Texture, SpriteAnimationInfo) = {
    aniCache.get(key) match {
      case Some(textureInfo) => textureInfo
      case None =>
        val (uriStr, animInfo) = fileMappedAnimate(key)
        val resTex = texCache.get(uriStr) match {
          case Some(tex) => tex
          case None => genNewTexture(uriStr, key)
        }
        val res =(resTex, animInfo)
        aniCache.update(key, res)
        res
    }
  }

  private[this] def fileMappedAnimate(id: Symbol): (String, SpriteAnimationInfo) = {
    val a = 'sprite
    val uri = Resource.buildPath(a)
    val animInfo = id match {
      case _ => new SpriteAnimationInfo(Array((0, Rect(2,2,32,32)), (5, Rect(36,2,32,32)), (9, Rect(2,2,32,32))), loop = true)
    }
    (uri, animInfo)
  }

  /**
   * スクリプトで用いる識別子から、実際のファイル/切り取り範囲などへのマッピング。
   * 現状扱えるのが単一ファイルのみなのでそのままファイル名のSymbolを返しているが、
   * 大きなテクスチャから切り抜く形に実装を変更する際に返り値の型も変わる。たぶん。
   * ファイルが存在しなかったら空テクスチャを返す。
   * @param id テクスチャの識別子
   * @return ファイルのURI
   */
  private[this] def fileMapped(id: Symbol): (String, Rect) = {
    val a = 'sprite
    val uri = Resource.buildPath(a)
    //val res = if (exists(uri)) uri else Resource.nullImg
    val rect = id match { // 超ベタ書き(仮)
      case 'ENG01B => Rect(2,2,32,32)
      case 'ENG01D => Rect(36,2,32,32)
      case 'ENG02B => Rect(70,2,32,32)
      case 'ENG02R => Rect(2,36,32,32)
      case 'invader01 => Rect(36,36,64,64)
      case 'ship => Rect(2,102,32,32)
      case 'DEFAULT => Rect(2,2,32,32)
      case _ => Rect(0,0,1,1)
    }
    (uri, rect)
  }

  // ファイルの存在確認。なんかもっとスマートな方法はないものか...
  private[this] def exists(path: String) = (new File(path)).exists()

  // リソースの開放
  def free() {
    texCache.values.foreach(_.release())
    println("Freeing texture resources complete.")
  }

}
