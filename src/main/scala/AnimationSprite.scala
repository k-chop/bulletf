package com.github.whelmaze.bulletf

class AnimationSprite(resourceId: Symbol) extends Sprite {

  val (texture, animInfo) = TextureFactory.getAnimate(resourceId)

  // 使わないけどnull入れるのも嫌なので適当に入れとく
  val rect: Rect = animInfo.next(0)

  def draw(custom_rect: Rect, pos: Position, angle: Double) {}

  def draw(pos: Position, angle: Double, scale: Double, alpha: Double, time: Int) {
    Drawer.draw(texture, animInfo.next(time), pos, angle, scale, alpha)
  }
}

/**
 * アニメーションするSpriteの定義
 * キャッシュして共有するので内部状態は持たない
 * @param rect 最初の1つのRect, これを元に他のコマのRectを求める
 * @param length アニメーションパターンの長さ
 * @param pattern どうアニメーションするかの定義、(frame, index)の配列, indexをframe時間表示する。
 * @param loop 最後まで再生したあとループするかどうか
 */
class SpriteAnimationInfo(rect: Rect, length: Int, pattern: Array[(Int, Int)], loop: Boolean) {

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
