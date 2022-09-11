package io.taig.skunk.ext

import skunk.{Fragment, Void}
import skunk.implicits._

object Fragments {
  def select(table: String, columns: List[String]): Fragment[Void] =
    sql"#${columns.map(column => s""""$table"."$column"""").mkString(", ")}"

  def record(table: String)(columns: String*): Fragment[Void] =
    select(table, List("identifier", "updated", "created") ++ columns)

  def immutable(table: String)(columns: String*): Fragment[Void] =
    select(table, List("identifier", "created") ++ columns)
}
