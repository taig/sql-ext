package io.taig.skunk.ext

import cats.data.{NonEmptyList, NonEmptySeq}
import cats.syntax.all.*
import skunk.{Fragment, Void}
import skunk.util.Origin

import scala.annotation.targetName

opaque type Columns = NonEmptySeq[Column]

object Columns:
  extension (self: Columns)
    def toNeSeq: NonEmptySeq[Column] = self
    def ++(columns: Columns): Columns = toNeSeq.concatNeSeq(columns)
    def size: Int = toNeSeq.length
    def toSeq: Seq[Column] = toNeSeq.toSeq
    def toNel: NonEmptyList[Column] = toNeSeq.toNonEmptyList
    def toList: List[Column] = toNel.toList

    private def prefix(columns: String*): Columns =
      toNeSeq.prependSeq(columns.map(Column(toNeSeq.head.table, _)))

    def record: Columns = prefix("identifier", "updated", "created")
    def immutable: Columns = prefix("identifier", "created")
    def plain: Columns = prefix("identifier")
    def insert: Columns = toNeSeq.map(_.copy(table = none))

    def sql(using origin: Origin): Fragment[Void] =
      val columns = toSeq
        .map {
          case Column(Some(table), column) => s"\"$table\".\"$column\""
          case Column(None, column)        => s"\"$column\""
        }
        .mkString(", ")

      Fragment(List(Left(columns)), Void.codec, origin)

  def apply(table: Table, columns: NonEmptySeq[String | Column | Columns]): Columns = columns.flatMap:
    case name: String     => fromName(table.some, name).toNeSeq
    case column: Column   => fromColumn(column).toNeSeq
    case columns: Columns => columns.toNeSeq

  @targetName("of")
  def apply(table: Table)(column: String | Column | Columns, columns: (String | Column | Columns)*): Columns =
    apply(table, NonEmptySeq(column, columns))

  def fromColumn(column: Column): Columns = NonEmptySeq.one(column)
  def fromName(table: Option[Table], name: String): Columns = fromColumn(Column(table, name))
