package io.taig.skunk.ext

import cats.syntax.all.*
import org.typelevel.ci.CIString
import skunk.Codec
import skunk.codec.all.*
import skunk.data.{Arr, Type}

import java.time.{Instant, ZoneOffset}

object codecs:
  val citext: Codec[CIString] = Codec.simple(_.toString, CIString(_).asRight, Type("citext"))

  val _citext: Codec[Arr[CIString]] =
    Codec.array(_.toString, value => CIString(value).asRight, Type("_citext", List(Type("citext"))))

  /** A postgres `TIMESTAMPTZ` does not actually store the timezone information, but instead normalizes the time to UTC,
    * which (in some cases) is better represented by `Instant`
    */
  val instant: Codec[Instant] = timestamptz.imap(_.toInstant)(_.atOffset(ZoneOffset.UTC))

  val identifier: Codec[Record.Identifier] = int8.imap(Record.Identifier.apply)(_.toLong)

  val _identifier: Codec[Arr[Record.Identifier]] = _int8.imap(_.map(Record.Identifier.apply))(_.map(_.toLong))

  @deprecated("Use _identifier instead", since = "0.7.1")
  val identifiers: Codec[Arr[Record.Identifier]] = _identifier

  def record[A](value: Codec[A]): Codec[Record[A]] = (identifier *: value).to
