package io.taig.skunk.ext

import cats.syntax.all._
import org.typelevel.ci.CIString
import skunk.Codec
import skunk.codec.all._
import skunk.data.Type

import java.time.{Instant, ZoneOffset}

object codecs {
  val ciString: Codec[CIString] = Codec.simple(_.toString, CIString(_).asRight, Type("citext"))

  /** A postgres `TIMESTAMPTZ` does not actually store the timezone information, but instead normalizes the time to UTC,
    * which (in some cases) is better represented by `Instant`
    */
  val instant: Codec[Instant] = timestamptz.imap(_.toInstant)(_.atOffset(ZoneOffset.UTC))
}
