package io.taig.sql.ext

import cats.Functor
import cats.Semigroupal
import cats.syntax.all.*
import cats.Applicative
import cats.Traverse
import cats.Eval
import cats.Eq
import cats.data.Ior

enum Upsert[A]:
  case Created(value: A)
  case Unchanged(value: A)
  case Updated(previous: A, current: A)

  final def map[B](f: A => B): Upsert[B] = this match
    case Created(value)             => Created(f(value))
    case Unchanged(value)           => Unchanged(f(value))
    case Updated(previous, current) => Updated(f(previous), f(current))

  final def result: A = this match
    case Created(value)      => value
    case Unchanged(value)    => value
    case Updated(_, current) => current

object Upsert:
  given Applicative[Upsert] with Traverse[Upsert] with
    override def pure[A](x: A): Upsert[A] = Unchanged(x)

    override def map[A, B](fa: Upsert[A])(f: A => B): Upsert[B] = fa.map(f)

    override def ap[A, B](ff: Upsert[A => B])(fa: Upsert[A]): Upsert[B] = (fa, ff) match
      case (Upsert.Created(a), Upsert.Created(f))           => Upsert.Created(f(a))
      case (Upsert.Created(a), Upsert.Unchanged(f))         => Upsert.Created(f(a))
      case (Upsert.Created(a), Upsert.Updated(f1, f2))      => Upsert.Updated(f1(a), f2(a))
      case (Upsert.Unchanged(a), Upsert.Created(f))         => Upsert.Created(f(a))
      case (Upsert.Unchanged(a), Upsert.Unchanged(f))       => Upsert.Unchanged(f(a))
      case (Upsert.Unchanged(a), Upsert.Updated(f1, f2))    => Upsert.Updated(f1(a), f2(a))
      case (Upsert.Updated(a1, a2), Upsert.Created(f))      => Upsert.Updated(f(a1), f(a2))
      case (Upsert.Updated(a1, a2), Upsert.Unchanged(f))    => Upsert.Updated(f(a1), f(a2))
      case (Upsert.Updated(a1, a2), Upsert.Updated(f1, f2)) => Upsert.Updated(f1(a1), f2(a2))

    override def foldLeft[A, B](fa: Upsert[A], b: B)(f: (B, A) => B): B = fa match
      case Created(a)                 => f(b, a)
      case Unchanged(a)               => f(b, a)
      case Updated(previous, current) => f(f(b, previous), current)

    override def foldRight[A, B](fa: Upsert[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] =
      fa match
        case Created(a)                 => f(a, lb)
        case Unchanged(a)               => f(a, lb)
        case Updated(previous, current) => f(previous, Eval.defer(f(current, lb)))

    override def traverse[G[_]: Applicative, A, B](fa: Upsert[A])(f: A => G[B]): G[Upsert[B]] = fa match
      case Created(a)                 => f(a).map(Created.apply)
      case Unchanged(a)               => f(a).map(Unchanged.apply)
      case Updated(previous, current) => (f(previous), f(current)).mapN(Updated.apply)

  given Semigroupal[Upsert] with
    override def product[A, B](fa: Upsert[A], fb: Upsert[B]): Upsert[(A, B)] = (fa, fb) match
      case (Upsert.Created(a), Upsert.Created(b))           => Upsert.Created((a, b))
      case (Upsert.Created(a), Upsert.Unchanged(b))         => Upsert.Created((a, b))
      case (Upsert.Created(a), Upsert.Updated(b1, b2))      => Upsert.Updated((a, b1), (a, b2))
      case (Upsert.Unchanged(a), Upsert.Created(b))         => Upsert.Created((a, b))
      case (Upsert.Unchanged(a), Upsert.Unchanged(b))       => Upsert.Unchanged((a, b))
      case (Upsert.Unchanged(a), Upsert.Updated(b1, b2))    => Upsert.Updated((a, b1), (a, b2))
      case (Upsert.Updated(a1, a2), Upsert.Created(b))      => Upsert.Updated((a1, b), (a2, b))
      case (Upsert.Updated(a1, a2), Upsert.Unchanged(b))    => Upsert.Updated((a1, b), (a2, b))
      case (Upsert.Updated(a1, a2), Upsert.Updated(b1, b2)) => Upsert.Updated((a1, b1), (a2, b2))

  given [A: Eq]: Eq[Upsert[A]] = Eq.instance:
    case (Created(a), Created(b))                         => a === b
    case (Unchanged(a), Unchanged(b))                     => a === b
    case (Upsert.Updated(a1, a2), Upsert.Updated(b1, b2)) => a1 === b1 && a2 === b2
    case _                                                => false
