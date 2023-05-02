package io.taig.skunk.ext

import cats.syntax.all._
import org.typelevel.ci.CIString
import skunk.Codec
import skunk.codec.all._
import skunk.data.{Arr, Type}

import java.time.{Instant, ZoneOffset}

object codecs:
  val citext: Codec[CIString] = Codec.simple(_.toString, CIString(_).asRight, Type("citext"))

  /** A postgres `TIMESTAMPTZ` does not actually store the timezone information, but instead normalizes the time to UTC,
    * which (in some cases) is better represented by `Instant`
    */
  val instant: Codec[Instant] = timestamptz.imap(_.toInstant)(_.atOffset(ZoneOffset.UTC))

  val identifier: Codec[Record.Identifier] = int8.imap(Record.Identifier.apply)(_.toLong)

  val identifiers: Codec[Arr[Record.Identifier]] = _int8.imap(_.map(Record.Identifier.apply))(_.map(_.toLong))

  val created: Codec[Record.Created] = instant.imap(Record.Created.apply)(_.toInstant)

  val updated: Codec[Record.Updated] = instant.imap(Record.Updated.apply)(_.toInstant)

  def record[A](value: Codec[A]): Codec[Record[A]] = (identifier *: updated.opt *: created *: value).as
  def immutable[A](value: Codec[A]): Codec[Record.Immutable[A]] = (identifier *: created *: value).as
  def plain[A](value: Codec[A]): Codec[Record.Plain[A]] = (identifier *: value).as
