package io.taig.sql.ext

import munit.DisciplineSuite
import cats.kernel.laws.SemigroupLaws
import cats.laws.SemigroupalLaws
import cats.laws.discipline.SemigroupalTests
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import cats.syntax.all.*
import cats.laws.discipline.TraverseTests
import cats.laws.discipline.ApplicativeTests
import cats.kernel.laws.discipline.EqTests

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
