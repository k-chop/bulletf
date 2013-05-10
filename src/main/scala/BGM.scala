package com.github.whelmaze.bulletf

import collection.mutable

import org.newdawn.slick.Music


object BGM {

  private[this] val music_map = mutable.HashMap.empty[Symbol, Music]
  private[this] val uri_map = mutable.HashMap.empty[Symbol, String]

  private[this] var now_playing: Symbol = 'stop

  def init() {
    val transform = { s: String => s.replace(" ","") } andThen { s: String => s.split(":") }
    io.Source.fromFile("music/mapping.txt")("UTF-8").getLines().map {
      transform
    } foreach {
      case Array(id, filename) =>
        uri_map += (Symbol(id) -> (s"${Resource.musicpath}$filename"))
        println(s"BGM: uri map registered: $id -> $filename")
      case s =>
        println(s"BGM: invalid definition line\n -> ${s.deep}")
    }
  }

  // future使って非同期にしてもいいのでは
  def load(id: Symbol) {
    if (!music_map.isDefinedAt(id) && uri_map.isDefinedAt(id)) {
      val m = new Music(uri_map(id))
      music_map += (id -> m)
      println(s"BGM: $id loaded successfully.")
    } else {
      println(s"BGM: $id is not registered.")
    }
  }

  def release(id: Symbol) {
    if (!music_map.isDefinedAt(id)) {
      music_map(id).release()
      println(s"BGM: $id released successfully.")
      music_map -= id
    } else {
      println(s"BGM: $id is not registered.")
    }
  }

  def play(id: Symbol) {
    music_map.get(id) foreach { m: Music =>
      now_playing = id
      m.loop(1f, 1.0f)
    }
  }

  def stop() {
    music_map.get(now_playing) foreach { m: Music =>
      now_playing = 'stop
      m.stop()
    }
  }

  def free() {
    stop()
    music_map.foreach(_._2.release())
    println("Freeing BGM resources complete.")
  }

}
