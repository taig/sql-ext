package io.taig.skunk.ext

import skunk.util.Origin
import skunk.{Fragment, Void}

opaque type Table = String

object Table:
  extension (self: Table)
    def sql(using origin: Origin): Fragment[Void] = Fragment(List(Left(s"\"$self\"")), Void.codec, origin)
    def toString: String = self
  def apply(name: String): Table = name
