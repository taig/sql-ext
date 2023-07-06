package io.taig.skunk.ext

import cats.data.State
import skunk.data.Type
import skunk.{Encoder, Void}

abstract class EncoderFragment extends Encoder[Void]:
  final override def types: List[Type] = List.empty
  final override def sql: State[Int, String] = State.pure(toString)
  final override def encode(a: Void): List[Option[String]] = List.empty
  final override def toString: String = render
  def render: String
