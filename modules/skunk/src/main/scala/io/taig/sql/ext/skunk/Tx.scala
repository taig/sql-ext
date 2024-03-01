package io.taig.sql.ext.skunk

import cats.effect.Resource
import cats.effect.MonadCancelThrow
import skunk.{Session, Transaction}

final class Tx[F[_]] private (val session: Session[F], val underlying: Transaction[F]) extends Transaction[F]:
  export underlying.*

object Tx:
  def from[F[_]](session: Session[F]): Resource[F, Tx[F]] = session.transaction.map(new Tx(session, _))

  def from[F[_]](sessions: Resource[F, Session[F]]): Resource[F, Tx[F]] = sessions.flatMap(from)

  def use[F[_]: MonadCancelThrow, A](sessions: Resource[F, Session[F]])(f: Tx[F] => F[A]): F[A] =
    from(sessions).use(f)
