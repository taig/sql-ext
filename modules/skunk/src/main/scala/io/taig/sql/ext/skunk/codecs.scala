package io.taig.sql.ext.skunk

import cats.Order
import cats.syntax.all.*
import io.taig.enumeration.ext.EnumerationValues
import io.taig.enumeration.ext.Mapping
import io.taig.sql.ext.Record
import org.typelevel.ci.CIString
import skunk.Codec
import skunk.Decoder
import skunk.Encoder
import skunk.codec.all.*
import skunk.data.Arr
import skunk.data.Type

import java.time.Instant
import java.time.ZoneOffset

object codecs:
  val citext: Codec[CIString] = Codec.simple(_.toString, CIString(_).asRight, Type("citext"))

  val _citext: Codec[Arr[CIString]] =
    Codec.array(_.toString, value => CIString(value).asRight, Type("_citext", List(Type("citext"))))

  /** A postgres `TIMESTAMPTZ` does not actually store the timezone information, but instead normalizes the time to UTC,
    * which (in some cases) is better represented by `Instant`
    */
  val instant: Codec[Instant] = timestamptz.imap(_.toInstant)(_.atOffset(ZoneOffset.UTC))

  @deprecated("Record will be removed", "sql-ext 0.15.0")
  val identifier: Codec[Record.Identifier] = int8.imap(Record.Identifier.apply)(_.toLong)

  @deprecated("Record will be removed", "sql-ext 0.15.0")
  val _identifier: Codec[Arr[Record.Identifier]] = _int8.imap(_.map(Record.Identifier.apply))(_.map(_.toLong))

  @deprecated("Record will be removed", "sql-ext 0.15.0")
  object record:
    @deprecated("Record will be removed", "sql-ext 0.15.0")
    def apply[A](value: Decoder[A]): Decoder[Record[A]] = (identifier *: value).to
    @deprecated("Record will be removed", "sql-ext 0.15.0")
    def apply[A](value: Encoder[A]): Encoder[Record[A]] = (identifier *: value).to
    @deprecated("Record will be removed", "sql-ext 0.15.0")
    def apply[A](value: Codec[A]): Codec[Record[A]] = (identifier *: value).to

  def mapping[A](tpe: Type)(using mapping: Mapping[A, String]): Codec[A] = Codec.simple(
    mapping.inj,
    s =>
      mapping.prj(s).toRight(s"${tpe.name}: no such element '$s', expected '${mapping.values.toList.mkString(",")}'"),
    tpe
  )

  def mapping[A, B](codec: Codec[A])(using mapping: Mapping[B, A]): Codec[B] =
    codec.eimap(a => mapping.prj(a).toRight(s"Unknown value '$a', expected '${mapping.values.toList.mkString(",")}'"))(
      mapping.inj
    )

  def enumeration[A](tpe: Type)(f: A => String)(using EnumerationValues.Aux[A, A]): Codec[A] =
    mapping(tpe)(using Mapping.enumeration(f))

  def enumeration[A: Order, B](codec: Codec[A])(f: B => A)(using EnumerationValues.Aux[B, B]): Codec[B] =
    mapping(codec)(using Mapping.enumeration(f))

  def arr[A](tpe: Type)(using mapping: Mapping[A, String]): Codec[Arr[A]] =
    Codec.array(mapping.inj, value => mapping.prj(value).toRight(s"Invalid: $value"), tpe)

  def arr[A](tpe: Type)(f: A => String)(using EnumerationValues.Aux[A, A]): Codec[Arr[A]] =
    arr(tpe)(using Mapping.enumeration(f))
