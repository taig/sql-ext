package io.taig.skunk

import cats.data.Kleisli
import cats.effect.{IO, Resource}
import cats.syntax.all._
import fs2.Stream
import skunk.Session

import scala.reflect.ClassTag

package object ext {
  type Database[A] = Kleisli[IO, Session[IO], A]

  object Database {
    def apply[A](f: Session[IO] => IO[A]): Database[A] = Kleisli(f)
    def lift[A](fa: IO[A]): Database[A] = Kleisli.liftF(fa)
    def pure[A](a: A): Database[A] = Kleisli.pure(a)

    type Attempt[E, A] = Database[Either[E, A]]

    object Attempt {
      def apply[E <: Throwable: ClassTag, A](f: Session[IO] => IO[A]): Database.Attempt[E, A] =
        Database(f(_).attemptNarrow[E])
    }

    object Transactional {
      def apply[A](f: Transaction => IO[A]): Database[A] = Database(Transaction.from(_).use(f))

      object Attempt {
        def apply[E <: Throwable: ClassTag, A](f: Transaction => IO[A]): Database.Attempt[E, A] =
          Transactional(f(_).attemptNarrow[E])
      }
    }

    type Streaming[A] = Kleisli[Stream[IO, *], Session[IO], A]

    object Streaming {
      def apply[A](f: Session[IO] => Stream[IO, A]): Database.Streaming[A] = Kleisli(f)
      def lift[A](fa: Stream[IO, A]): Database.Streaming[A] = Kleisli.liftF(fa)
      def pure[A](a: A): Database.Streaming[A] = Kleisli.pure(a)

      type Attempt[E, A] = Kleisli[Resource[IO, *], Session[IO], Either[E, Stream[IO, A]]]

      object Transactional {
        object Attempt {
          def apply[E <: Throwable: ClassTag, A](
              f: Transaction => IO[Stream[IO, A]]
          ): Database.Streaming.Attempt[E, A] = Kleisli { session =>
            Transaction.from(session).evalMap(f(_).attemptNarrow[E])
          }
        }
      }
    }
  }
}
