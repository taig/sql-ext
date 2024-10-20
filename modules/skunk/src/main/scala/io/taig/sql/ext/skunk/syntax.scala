package io.taig.sql.ext.skunk

import cats.syntax.all.*
import skunk.Codec

object syntax:
  extension [A](self: Codec[A])
    def +:[B](codec: Codec[B]): Codec[Either[A, B]] =
      (self.opt *: codec.opt).eimap {
        case (Some(a), None) => a.asLeft.asRight
        case (None, Some(b)) => b.asRight.asRight
        case _               => "Invalid".asLeft
      } {
        case Left(a)  => (Some(a), None)
        case Right(b) => (None, Some(b))
      }

  extension [A <: Matchable](self: Codec[A])
    inline def |[B <: Matchable](codec: Codec[B]): Codec[A | B] =
      (self.opt *: codec.opt).eimap {
        case (Some(a), None) => a.asRight
        case (None, Some(b)) => b.asRight
        case _               => "Invalid".asLeft
      } {
        case a: A => (Some(a), None)
        case b: B => (None, Some(b))
      }
