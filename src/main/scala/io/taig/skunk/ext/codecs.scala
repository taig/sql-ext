package io.taig.skunk.ext

import cats.syntax.all.*
import io.taig.enumeration.ext.{EnumerationValues, Mapping}
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

  def record[A](value: Codec[A]): Codec[Record[A]] = (identifier *: value).to

  def mapping[A](tpe: Type)(using mapping: Mapping[A, String]): Codec[A] = Codec.simple(
    mapping.inj,
    s => mapping.prj(s).toRight(s"${tpe.name}: no such element '$s', expected '${mapping.values.mkString(",")}'"),
    tpe
  )

  def enumeration[A](tpe: Type)(f: A => String)(using EnumerationValues.Aux[A, A]): Codec[A] =
    mapping(tpe)(using Mapping.enumeration(f))
