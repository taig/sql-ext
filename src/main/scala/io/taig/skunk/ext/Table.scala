package io.taig.skunk.ext

import cats.syntax.all.*
import skunk.util.Origin
import skunk.{Fragment, Void}

opaque type Table = String

object Table:
  extension (self: Table)
    def apply(column: String): Column = Column(self.some, column)
    def sql(using origin: Origin): Fragment[Void] = Fragment(List(Left(s"\"$self\"")), Void.codec, origin)
    def toString: String = self
  def apply(name: String): Table = name
