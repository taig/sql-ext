package io.taig.skunk.ext

import cats.syntax.all._
import org.typelevel.ci.CIString
import skunk.Codec
import skunk.codec.all._
import skunk.data.{Arr, Type}

import java.time.{Instant, ZoneOffset}

object codecs {
  val ciString: Codec[CIString] = Codec.simple(_.toString, CIString(_).asRight, Type("citext"))

  /** A postgres `TIMESTAMPTZ` does not actually store the timezone information, but instead normalizes the time to UTC,
    * which (in some cases) is better represented by `Instant`
    */
  val instant: Codec[Instant] = timestamptz.imap(_.toInstant)(_.atOffset(ZoneOffset.UTC))

  val identifier: Codec[Record.Identifier] = int8.imap(Record.Identifier.apply)(_.value)

  val identifiers: Codec[Arr[Record.Identifier]] = _int8.imap(_.map(Record.Identifier.apply))(_.map(_.value))

  val created: Codec[Record.Created] = instant.imap(Record.Created.apply)(_.value)

  val updated: Codec[Record.Updated] = instant.imap(Record.Updated.apply)(_.value)

  def record[A](value: Codec[A]): Codec[Record[A]] = (identifier ~ updated.opt ~ created ~ value).gimap
  def immutable[A](value: Codec[A]): Codec[Record.Immutable[A]] = (identifier ~ created ~ value).gimap

  val password: Codec[Password.Hashed] = text.imap(Password.Hashed.apply)(_.value)
}
