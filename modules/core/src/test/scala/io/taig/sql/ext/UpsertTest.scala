package io.taig.sql.ext

import cats.laws.discipline.ApplicativeTests
import cats.laws.discipline.SemigroupalTests
import cats.laws.discipline.TraverseTests
import munit.DisciplineSuite
import org.scalacheck.Arbitrary
import org.scalacheck.Gen

final class UpsertTest extends DisciplineSuite:
  def gen[A](using a: Arbitrary[A]): Gen[Upsert[A]] =
    val unchanged = a.arbitrary.map(Upsert.Unchanged.apply)
    val created = a.arbitrary.map(Upsert.Created.apply)
    val updated = for
      previous <- a.arbitrary
      current <- a.arbitrary
    yield Upsert.Updated(previous, current)
    Gen.oneOf(unchanged, created, updated)

  given [A: Arbitrary]: Arbitrary[Upsert[A]] = Arbitrary(gen[A])

  checkAll("Applicative", ApplicativeTests[Upsert].applicative[Int, Int, Int])
  checkAll("Semigroupal", SemigroupalTests[Upsert].semigroupal[Int, Int, Int])
  checkAll("Traverse", TraverseTests[Upsert].traverse[Int, Int, Int, Int, Option, Option])
