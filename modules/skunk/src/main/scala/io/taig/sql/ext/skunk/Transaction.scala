package io.taig.sql.ext.skunk

import cats.effect.{IO, Resource}
import skunk.{Session, Transaction}

final class Transaction private (val session: Session[IO], val underlying: skunk.Transaction[IO])
    extends skunk.Transaction[IO]:
  export underlying.*

object Transaction:
  def from(session: Session[IO]): Resource[IO, Transaction] = session.transaction.map(new Transaction(session, _))
  def from(sessions: Resource[IO, Session[IO]]): Resource[IO, Transaction] = sessions.flatMap(from)
  def use[A](sessions: Resource[IO, Session[IO]])(f: Transaction => IO[A]): IO[A] = from(sessions).use(f)
