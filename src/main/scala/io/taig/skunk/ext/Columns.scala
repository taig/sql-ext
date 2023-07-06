package io.taig.skunk.ext

import cats.data.{NonEmptyList, NonEmptySeq}
import cats.syntax.all.*

import scala.annotation.targetName

final case class Columns(toNeSeq: NonEmptySeq[Column]) extends EncoderFragment:
  def ++(columns: Columns): Columns = Columns(this.toNeSeq.concatNeSeq(columns.toNeSeq))

  def size: Int = toNeSeq.length

  def toSeq: Seq[Column] = toNeSeq.toSeq
  def toNonEmptyList: NonEmptyList[Column] = toNeSeq.toNonEmptyList
  def toList: List[Column] = toNonEmptyList.toList

  private def prefix(columns: String*): Columns =
    Columns(toNeSeq.prependSeq(columns.map(Column(toNeSeq.head.table, _))))

  def record: Columns = prefix("identifier", "updated", "created")
  def immutable: Columns = prefix("identifier", "created")
  def plain: Columns = prefix("identifier")

  def insert: Columns = Columns(toNeSeq.map(_.copy(table = none)))

  override def render: String = toSeq
    .map {
      case Column(Some(table), column) => s"\"$table\".\"$column\""
      case Column(None, column)        => s"\"$column\""
    }
    .mkString(", ")

object Columns:
  def apply(table: String, columns: NonEmptySeq[String | Column | Columns]): Columns = Columns:
    columns.flatMap:
      case name: String     => fromName(table.some, name).toNeSeq
      case column: Column   => fromColumn(column).toNeSeq
      case columns: Columns => columns.toNeSeq

  @targetName("of")
  def apply(table: String)(column: String | Column | Columns, columns: (String | Column | Columns)*): Columns =
    apply(table, NonEmptySeq(column, columns))

  def fromColumn(column: Column): Columns = Columns(NonEmptySeq.one(column))

  def fromName(table: Option[String], name: String): Columns = fromColumn(Column(table, name))
