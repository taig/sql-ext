package io.taig.sql.ext

import cats.Applicative
import cats.Eval
import cats.Order
import cats.Traverse
import cats.syntax.all.*

@deprecated("Record will be removed", "sql-ext 0.15.0")
final case class Record[+A](identifier: Record.Identifier, value: A):
  def map[B](f: A => B): Record[B] = copy(value = f(value))
  def traverse[G[_]: Applicative, B](f: A => G[B]): G[Record[B]] =
    f(value).map(value => copy(value = value))

@deprecated("Record will be removed", "sql-ext 0.15.0")
object Record:
  @deprecated("Record will be removed", "sql-ext 0.15.0")
  opaque type Identifier = Long

  @deprecated("Record will be removed", "sql-ext 0.15.0")
  object Identifier:
    extension (identifier: Record.Identifier)
      @deprecated("Record will be removed", "sql-ext 0.15.0")
      def toLong: Long = identifier
    @deprecated("Record will be removed", "sql-ext 0.15.0")
    def apply(value: Long): Record.Identifier = value
    @deprecated("Record will be removed", "sql-ext 0.15.0")
    given (using order: Order[Long]): Order[Record.Identifier] = order

  @deprecated("Record will be removed", "sql-ext 0.15.0")
  given Traverse[Record] with
    override def map[A, B](fa: Record[A])(f: A => B): Record[B] = fa.map(f)
    override def traverse[G[_]: Applicative, A, B](fa: Record[A])(f: A => G[B]): G[Record[B]] = fa.traverse(f)
    override def foldLeft[A, B](fa: Record[A], b: B)(f: (B, A) => B): B = f(b, fa.value)
    override def foldRight[A, B](fa: Record[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = f(fa.value, lb)

  @deprecated("Record will be removed", "sql-ext 0.15.0")
  given [A: Order]: Order[Record[A]] = Order.by(Tuple.fromProductTyped)
