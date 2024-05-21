package io.taig.sql.ext.skunk

import cats.effect.Resource
import cats.effect.MonadCancelThrow
import skunk.{Session, Transaction}
import skunk.data.TransactionIsolationLevel
import skunk.data.TransactionAccessMode

final class Tx[F[_]] private (
    val session: Session[F],
    val underlying: Transaction[F],
    val isolationLevel: Option[TransactionIsolationLevel],
    val accessMode: Option[TransactionAccessMode]
) extends Transaction[F]:
  export underlying.*

object Tx:
  def from[F[_]](session: Session[F]): Resource[F, Tx[F]] = session.transaction.map(new Tx(session, _, None, None))

  def from[F[_]](
      session: Session[F],
      isolationLevel: TransactionIsolationLevel,
      accessMode: TransactionAccessMode
  ): Resource[F, Tx[F]] =
    session.transaction(isolationLevel, accessMode).map(new Tx(session, _, Some(isolationLevel), Some(accessMode)))

  def from[F[_]](sessions: Resource[F, Session[F]]): Resource[F, Tx[F]] = sessions.flatMap(from)

  def from[F[_]](
      sessions: Resource[F, Session[F]],
      isolationLevel: TransactionIsolationLevel,
      accessMode: TransactionAccessMode
  ): Resource[F, Tx[F]] = sessions.flatMap(from(_, isolationLevel, accessMode))

  def use[F[_]: MonadCancelThrow, A](sessions: Resource[F, Session[F]])(f: Tx[F] => F[A]): F[A] =
    from(sessions).use(f)

  def use[F[_]: MonadCancelThrow, A](
      sessions: Resource[F, Session[F]],
      isolationLevel: TransactionIsolationLevel,
      accessMode: TransactionAccessMode
  )(f: Tx[F] => F[A]): F[A] = from(sessions, isolationLevel, accessMode).use(f)
