package io.taig.sql.ext.skunk

import cats.Monad
import cats.effect.Resource
import cats.syntax.all.*
import io.taig.sql.ext.Upsert
import skunk.Fragment
import skunk.Session
import skunk.Void
import skunk.util.Origin

type Sx[F[_]] = Session[F]

type SxPool[F[_]] = Resource[F, Sx[F]]

@deprecated
def fragment(sql: String)(using origin: Origin): Fragment[Void] = Fragment(List(Left(sql)), Void.codec, origin)

def upsert[F[_]: Monad, A](tx: Tx[F])(
    create: (Sx[F], A) => F[Boolean],
    get: (Sx[F], A) => F[A],
    update: (Sx[F], A) => F[Unit],
    hasChanged: (A, A) => Boolean
)(a: A): F[Upsert[A]] = create(tx.session, a).flatMap:
  case true => Upsert.Created(a).pure
  case false =>
    get(tx.session, a).flatMap: current =>
      if hasChanged(current, a)
      then update(tx.session, a).as(Upsert.Updated(previous = current, current = a))
      else Upsert.Unchanged(current).pure
