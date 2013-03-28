package com.github.whelmaze.bulletf

import org.newdawn.slick.opengl.{ Texture, TextureLoader }
import java.io.{File, FileInputStream}

import collection.mutable


object TextureFactory {
  private val cache = mutable.WeakHashMap.empty[Symbol, Texture]

  def get(key: Symbol): Texture = {
    cache.get(key) match {
      case Some(texture) => texture
      case None =>
        val uriStr = fileMapped(key)
        val texture = TextureLoader.getTexture("PNG", new FileInputStream( uriStr ))
        cache.update(key, texture)
        texture
    }
  }

  /**
   * スクリプトで用いる識別子から、実際のファイル/切り取り範囲などへのマッピング。
   * 現状扱えるのが単一ファイルのみなのでそのままファイル名のSymbolを返しているが、
   * 大きなテクスチャから切り抜く形に実装を変更する際に返り値の型も変わる。たぶん。
   * ファイルが存在しなかったら空テクスチャを返す。
   * @param id テクスチャの識別子
   * @return ファイルのURI
   */
  private[this] def fileMapped(id: Symbol) = {
    val uri = Resource.buildPath(id)
    if (exists(uri)) uri else Resource.nullImg
  }

  // ファイルの存在確認。なんかもっとスマートな方法はないものか...
  private[this] def exists(path: String) = (new File(path)).exists()

}
