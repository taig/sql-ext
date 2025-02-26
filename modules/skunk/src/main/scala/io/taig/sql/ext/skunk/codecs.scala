package io.taig.sql.ext.skunk

import cats.Order
import cats.syntax.all.*
import io.taig.enumeration.ext.EnumerationValues
import io.taig.enumeration.ext.Mapping
import org.typelevel.ci.CIString
import skunk.Codec
import skunk.data.Arr
import skunk.data.Type

object codecs:
  val citext: Codec[CIString] = Codec.simple(_.toString, CIString(_).asRight, Type("citext"))

  val _citext: Codec[Arr[CIString]] =
    Codec.array(_.toString, value => CIString(value).asRight, Type("_citext", List(Type("citext"))))

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
