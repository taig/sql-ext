package io.taig.skunk.ext

import cats.effect.{IO, Resource}
import skunk.Session

final class Transaction private (val session: Session[IO], val raw: skunk.Transaction[IO])

object Transaction {
  def from(session: Session[IO]): Resource[IO, Transaction] = session.transaction.map(new Transaction(session, _))
}
