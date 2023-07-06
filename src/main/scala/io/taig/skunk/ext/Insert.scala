package io.taig.skunk.ext

import cats.data.Chain
import cats.syntax.all.*
import skunk.util.Origin
import skunk.{Fragment, Void}

opaque type Insert = Chain[String]

object Insert:
  extension (self: Insert)
    def ++(insert: Insert): Insert = self ++ insert
    def :+(column: String): Insert = self :+ column
    def toChain: Chain[String] = self
    def sql(using origin: Origin): Fragment[Void] = fragment(toChain.map(column => s"\"$column\"").mkString_(", "))
  val Empty: Insert = Chain.empty
  def apply(columns: Chain[String]): Insert = columns
  def one(column: String): Insert = Chain.one(column)
