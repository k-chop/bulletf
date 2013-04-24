package com.github.whelmaze.bulletf

sealed trait State

object Live extends State

object Lost extends State

case class ShotBy[T <: BulletLike with HasCollision](target: T) extends State

