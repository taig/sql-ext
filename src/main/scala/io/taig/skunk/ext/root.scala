package io.taig.skunk.ext

import skunk.{Fragment, Void}
import skunk.util.Origin

def fragment(sql: String)(using origin: Origin): Fragment[Void] = Fragment(List(Left(sql)), Void.codec, origin)
