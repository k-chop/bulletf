package com.github.whelmaze.bulletf

import org.newdawn.slick.opengl.{ Texture, TextureLoader }
import java.io.{File, FileInputStream}

import collection.mutable

/**
 * アニメーションするSpriteの定義
 * キャッシュさせるんだから可変な内部状態持たせたらイカン
 * @param rect 最初の1つのRect
 * @param length スプライトのパターンの長さ
 * @param pattern どうアニメーションするかの定義、(frame, index)の配列, indexをframe時間表示する。
 * @param loop 最後まで再生したあとループするかどうか
 */
class SpriteAnimationInfo(val rect: Rect, val length: Int, pattern: Array[(Int, Int)], loop: Boolean) {

  // 今の所、同サイズで横一列に並んだ画像しか対応してない
  private[this] val rects: Array[Rect] = Array.tabulate(length){ i =>
    rect.copy(x = rect.x + (rect.w * i))
  }

  private[this] val lastFrame: Int = pattern.foldLeft(0){ case (sum,t) => sum + t._1}
  private[this] lazy val lastRect: Rect = rects(length-1)

  def next(_time: Int): Rect = {
    val time = if (loop) _time % lastFrame else _time
    //println(s"normalize time ${_time} to $time")
    if (lastFrame <= time) { // lengthを越している(loopでない)
      lastRect
    } else {

      @scala.annotation.tailrec
      def findIdx(idx: Int, chkTime: Int): Int = {
        if (time < chkTime) idx
        else findIdx(idx+1, chkTime + pattern(idx)._1)
      }

      val at = findIdx(0, pattern(0)._1)

      val idxp = pattern(at)._2
      rects(idxp)
    }
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
      case _ => new SpriteAnimationInfo(Rect(2,68,17,17), 8, Array.tabulate(8){ i => (2,i) }, loop = true)
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
      case 'ENG02R => Rect(104,2,32,32)
      case 'invader01 => Rect(138,2,64,64)
      case 'ship => Rect(204,2,32,32)
      case 'shot => Rect(140,68,32,32)
      case 'number => Rect(2,102,160,18)
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
