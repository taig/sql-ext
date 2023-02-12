package io.taig.skunk.ext

import cats.effect.{IO, Resource}
import skunk.Session

final class Transaction private (val session: Session[IO], val underlying: skunk.Transaction[IO])
    extends skunk.Transaction[IO]:
  export underlying.*

object Transaction:
  def from(session: Session[IO]): Resource[IO, Transaction] = session.transaction.map(new Transaction(session, _))
  def from(sessions: Resource[IO, Session[IO]]): Resource[IO, Transaction] = sessions.flatMap(from)
