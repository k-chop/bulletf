package bulletf



import org.newdawn.slick.opengl.{ Texture, TextureLoader }
import java.io.{File, FileInputStream}

import collection.mutable


object TextureFactory {
  private val cache = mutable.WeakHashMap.empty[Symbol, (Texture, Rect)]
  private val aniCache = mutable.WeakHashMap.empty[Symbol, (Texture, SpriteAnimationInfo)]
  // Textureの生成はゲロ重いので指定したタイミング以外で破棄されないように
  private val texCache = mutable.HashMap.empty[String, Texture]

    //XMLのロードが超重いので先に済ませてしまう
  private val id2rect: Map[String, Rect] = {
    import java.io.File

    val xml = scala.xml.XML.load(new File(Resource.imgpath + "sprite.xml").toURI.toURL)
    val acc = mutable.HashMap.empty[String, Rect]

    (xml \\ "TextureAtlas" \\ "sprite") foreach { node =>
      for {
        s <- node
        x <- s.attribute("x")
        y <- s.attribute("y")
        h <- s.attribute("h")
        w <- s.attribute("w")
        n <- s.attribute("n")
      } { // sprite.xmlは自動生成でいじらないし整数以外が混入することなんてないよね！！！
        val id = n.text.toLowerCase
        val rect = Rect(x.text.toInt, y.text.toInt, w.text.toInt, h.text.toInt)
        acc += (id -> rect)
        println(s"make mapping: $id -> $rect")
      }
    }
    acc.toMap
  }

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

    val animInfo = id2rect.get(id.name.toLowerCase + ".png") map { r =>
      // 現状アニメーションする1つのコマは縦横が同じサイズのものと仮定
      val newRect = r.copy(w = r.h)
      // idに対応するアニメーション定義ファイルを読み込む
      // けど仕様決まってないのであとで
      // 今は2フレームごとに次に進む & ループすると決め打ち
      val len = r.w / r.h
      val timeTable = Array.tabulate(len){ i => (2,i) }
      new SpriteAnimationInfo(newRect, len, timeTable, loop = true)
    } getOrElse {
      // 見つからない場合は適当なの用意して渡す
      new SpriteAnimationInfo(Rect(0, 0, 1, 1), 1, Array((10, 0)), loop = false)
    }

    (uri, animInfo)
  }

  /**
   * スクリプトで用いる識別子から、実際のファイル/切り取り範囲などへのマッピングを取得する。
   * 指定したidに対応する定義が存在しなかったら1x1の空テクスチャを返す。
   * @param id 識別子
   * @return ファイルのURIと切り取り範囲のTuple
   */
  private[this] def fileMapped(id: Symbol): (String, Rect) = {

    val a = 'sprite
    val uri = Resource.buildPath(a)

    val rect = id2rect.get(id.name.toLowerCase + ".png").getOrElse(Rect(0,0,1,1))

    (uri, rect)
  }

  private[this] def exists(path: String) = new File(path).exists()

  // リソースの開放
  def free() {
    texCache.values.foreach(_.release())
    println("Freeing texture resources complete.")
  }

}
