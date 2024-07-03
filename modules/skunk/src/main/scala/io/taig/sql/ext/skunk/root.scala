package io.taig.sql.ext.skunk

import skunk.{Fragment, Void}
import skunk.util.Origin
import skunk.Session

type Sx[F[_]] = Session[F]

def fragment(sql: String)(using origin: Origin): Fragment[Void] = Fragment(List(Left(sql)), Void.codec, origin)
