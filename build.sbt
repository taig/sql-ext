val Version = new {
  val CaseInsensitive = "1.3.0"
  val Scala213 = "2.13.8"
  val Scala3 = "3.2.0"
  val Skunk = "0.3.1"
}

crossScalaVersions := List(Version.Scala213, Version.Scala3)

libraryDependencies ++=
  "org.tpolecat" %% "skunk-core" % Version.Skunk ::
    "org.typelevel" %% "case-insensitive" % Version.CaseInsensitive ::
    Nil

name := "skunk-ext"

scalaVersion := Version.Scala213
