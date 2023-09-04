package io.taig.skunk.ext

import cats.data.Chain
import cats.syntax.all.*
import skunk.util.Origin
import skunk.{Fragment, Void}

@deprecated
opaque type Select = Chain[(String, String)]

@deprecated
object Select:
  extension (self: Select)
    def ++(insert: Select): Select = self ++ insert
    def :+(column: (String, String)): Select = self :+ column
    def toChain: Chain[(String, String)] = self
    def sql(using origin: Origin): Fragment[Void] =
      fragment(toChain.map { case (table, column) => s"\"$table\".\"$column\"" }.mkString_(", "))
  val Empty: Select = Chain.empty
  def apply(columns: Chain[(String, String)]): Select = columns
  def one(table: String, column: String): Select = Chain.one((table, column))
