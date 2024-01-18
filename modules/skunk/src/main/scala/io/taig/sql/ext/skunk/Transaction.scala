package io.taig.sql.ext.skunk

import cats.effect.Resource
import cats.effect.MonadCancelThrow
import skunk.{Session, Transaction}

final class Transaction[F[_]] private (val session: Session[F], val underlying: skunk.Transaction[F])
    extends skunk.Transaction[F]:
  export underlying.*

object Transaction:
  def from[F[_]](session: Session[F]): Resource[F, Transaction[F]] =
    session.transaction.map(new Transaction(session, _))

  def from[F[_]](sessions: Resource[F, Session[F]]): Resource[F, Transaction[F]] = sessions.flatMap(from)

  def use[F[_]: MonadCancelThrow, A](sessions: Resource[F, Session[F]])(f: Transaction[F] => F[A]): F[A] =
    from(sessions).use(f)
