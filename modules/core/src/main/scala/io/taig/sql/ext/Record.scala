package io.taig.sql.ext

import cats.{Applicative, Eval, Order, Traverse}
import cats.syntax.all.*

final case class Record[+A](identifier: Record.Identifier, value: A):
  def map[B](f: A => B): Record[B] = copy(value = f(value))
  def traverse[G[_]: Applicative, B](f: A => G[B]): G[Record[B]] =
    f(value).map(value => copy(value = value))

object Record:
  opaque type Identifier = Long
  object Identifier:
    extension (identifier: Record.Identifier) def toLong: Long = identifier
    def apply(value: Long): Record.Identifier = value
    given (using order: Order[Long]): Order[Record.Identifier] = order

  given Traverse[Record] with
    override def map[A, B](fa: Record[A])(f: A => B): Record[B] = fa.map(f)
    override def traverse[G[_]: Applicative, A, B](fa: Record[A])(f: A => G[B]): G[Record[B]] = fa.traverse(f)
    override def foldLeft[A, B](fa: Record[A], b: B)(f: (B, A) => B): B = f(b, fa.value)
    override def foldRight[A, B](fa: Record[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = f(fa.value, lb)
