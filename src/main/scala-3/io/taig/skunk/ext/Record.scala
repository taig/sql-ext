package io.taig.skunk.ext

import cats.{Applicative, Eval, Order, Traverse}
import cats.syntax.all.*

import java.time.Instant

final case class Record[A](
  identifier: Record.Identifier,
  updated: Option[Record.Updated],
  created: Record.Created,
  value: A
):
  def map[B](f: A => B): Record[B] = copy(value = f(value))
  def traverse[G[_]: Applicative, B](f: A => G[B]): G[Record[B]] =
    f(value).map(value => copy(value = value))

object Record:
  opaque type Identifier = Long
  object Identifier:
    extension (identifier: Record.Identifier) def toLong: Long = identifier
    def apply(value: Long): Record.Identifier = value
    given (using order: Order[Long]): Order[Record.Identifier] = order

  opaque type Updated = Instant
  object Updated:
    extension (updated: Record.Updated) def toInstant: Instant = updated
    def apply (value: Instant): Record.Updated = value
    given (using order: Order[Instant]): Order[Record.Updated] = order

  opaque type Created = Instant
  object Created:
    extension (created: Record.Created) def toInstant: Instant = created
    def apply (value: Instant): Record.Created = value
    given (using order: Order[Instant]): Order[Record.Created] = order

  final case class Immutable[A](identifier: Record.Identifier, created: Record.Created, value: A):
    def map[B](f: A => B): Record.Immutable[B] = copy(value = f(value))
    def traverse[G[_] : Applicative, B](f: A => G[B]): G[Record.Immutable[B]] =
      f(value).map(value => copy(value = value))

  object Immutable:
    given Traverse[Record.Immutable] with
      override def map[A, B](fa: Record.Immutable[A])(f: A => B): Record.Immutable[B] = fa.map(f)
      override def traverse[G[_] : Applicative, A, B](fa: Record.Immutable[A])(f: A => G[B]): G[Record.Immutable[B]] =
        fa.traverse(f)
      override def foldLeft[A, B](fa: Record.Immutable[A], b: B)(f: (B, A) => B): B = f(b, fa.value)
      override def foldRight[A, B](fa: Record.Immutable[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] =
        f(fa.value, lb)

    def created[A](value: A): ((Identifier, Created)) => Record.Immutable[A] =
      case (identifier, created) => Immutable(identifier, created, value)

  final case class Plain[A](identifier: Identifier, value: A):
    def map[B](f: A => B): Record.Plain[B] = copy(value = f(value))
    def traverse[G[_] : Applicative, B](f: A => G[B]): G[Record.Plain[B]] =
      f(value).map(value => copy(value = value))

  object Plain:
    given Traverse[Record.Plain] with
      override def map[A, B](fa: Record.Plain[A])(f: A => B): Record.Plain[B] = fa.map(f)
      override def traverse[G[_] : Applicative, A, B](fa: Record.Plain[A])(f: A => G[B]): G[Record.Plain[B]] =
        fa.traverse(f)
      override def foldLeft[A, B](fa: Record.Plain[A], b: B)(f: (B, A) => B): B = f(b, fa.value)
      override def foldRight[A, B](fa: Record.Plain[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] =
        f(fa.value, lb)

  def created[A] (value: A): ((Identifier, Created) ) => Record[A] =
    case (identifier, created) => Record (identifier, updated = None, created, value)

  def updated[A] (record: Record[A] ): Updated => Record[A] = updated => record.copy (updated = updated.some)

  given Traverse[Record] with
    override def map[A, B] (fa: Record[A] ) (f: A => B): Record[B] = fa.map (f)
    override def traverse[G[_]: Applicative, A, B] (fa: Record[A] ) (f: A => G[B] ): G[Record[B]] = fa.traverse (f)
    override def foldLeft[A, B] (fa: Record[A], b: B) (f: (B, A) => B): B = f (b, fa.value)
    override def foldRight[A, B] (fa: Record[A], lb: Eval[B] ) (f: (A, Eval[B] ) => Eval[B] ): Eval[B] = f (fa.value, lb)