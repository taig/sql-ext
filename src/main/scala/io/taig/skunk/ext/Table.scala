package io.taig.skunk.ext

import cats.data.Chain
import cats.syntax.all.*
import skunk.util.Origin
import skunk.{Fragment, Void}

import scala.annotation.targetName

@deprecated
final case class Table(name: String, columns: Chain[String | Table]):
  /* Render the table name as a `Fragment` */
  def sql(using origin: Origin): Fragment[Void] = fragment(s"\"$name\"")

  def prepend(columns: Chain[String | Table]): Table = copy(columns = columns ++ this.columns)

  @targetName("concat")
  def ++(columns: Chain[String | Table]): Table = copy(columns = this.columns ++ columns)

  def record: Table = prepend(Chain("identifier", "updated", "created"))
  def immutable: Table = prepend(Chain("identifier", "created"))
  def plain: Table = prepend(Chain("identifier"))

  def select: Select = columns.foldLeft(Select.Empty):
    case (select, column: String) => select :+ (name, column)
    case (select, table: Table)   => select ++ table.select

  def insert: Insert = columns.foldLeft(Insert.Empty):
    case (insert, column: String) => insert :+ column
    case (insert, table: Table)   => insert ++ table.insert

@deprecated
object Table:
  def apply(name: String)(columns: (String | Table)*): Table = Table(name, Chain.fromSeq(columns))
