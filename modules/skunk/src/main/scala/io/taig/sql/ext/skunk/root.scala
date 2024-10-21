package io.taig.sql.ext.skunk

import cats.effect.kernel.Resource
import skunk.Fragment
import skunk.Session
import skunk.Void
import skunk.util.Origin

type Sx[F[_]] = Session[F]

type SxPool[F[_]] = Resource[F, Sx[F]]

@deprecated
def fragment(sql: String)(using origin: Origin): Fragment[Void] = Fragment(List(Left(sql)), Void.codec, origin)
