package io.taig.skunk.ext

import skunk.implicits._
import skunk.{Fragment, Void}

object Fragments {
  def insert(columns: List[String]): Fragment[Void] = sql"#${columns.map(column => s""""$column"""").mkString(", ")}"

  def insert(columns: String*): Fragment[Void] = insert(columns.toList)

  def select(table: String, columns: List[String]): Fragment[Void] =
    sql"#${columns.map(column => s""""$table"."$column"""").mkString(", ")}"

  def select(table: String)(columns: String*): Fragment[Void] = select(table, columns.toList)

  def record(table: String, columns: List[String]): Fragment[Void] =
    select(table, List("identifier", "updated", "created") ++ columns)

  def record(table: String)(columns: String*): Fragment[Void] = record(table, columns.toList)

  def immutable(table: String, columns: List[String]): Fragment[Void] =
    select(table, List("identifier", "created") ++ columns)

  def immutable(table: String)(columns: String*): Fragment[Void] =
    immutable(table, columns.toList)

  def plain(table: String, columns: List[String]): Fragment[Void] =
    select(table, List("identifier") ++ columns)

  def plain(table: String)(columns: String*): Fragment[Void] = plain(table, columns.toList)
}
