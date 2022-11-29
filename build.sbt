val Version = new {
  val CaseInsensitive = "1.3.0"
  val Scala213 = "2.13.10"
  val Scala3 = "3.2.1"
  val Skunk = "0.4.0-M1"
}

enablePlugins(BlowoutYamlPlugin)

inThisBuild(
  Def.settings(
    developers := List(Developer("taig", "Niklas Klein", "mail@taig.io", url("https://taig.io/"))),
    dynverVTagPrefix := false,
    homepage := Some(url("https://github.com/taig/skunk-ext/")),
    licenses := List("MIT" -> url("https://raw.githubusercontent.com/taig/skunk-ext/main/LICENSE")),
    organization := "io.taig",
    organizationHomepage := Some(url("https://taig.io/")),
    versionScheme := Some("early-semver")
  )
)

blowoutGenerators ++= {
  val workflows = file(".github") / "workflows"
  BlowoutYamlGenerator.lzy(workflows / "main.yml", GitHubActionsGenerator.main) ::
    BlowoutYamlGenerator.lzy(workflows / "branches.yml", GitHubActionsGenerator.branches) ::
    Nil
}

crossScalaVersions := List(Version.Scala213, Version.Scala3)

libraryDependencies ++=
  "org.tpolecat" %% "skunk-core" % Version.Skunk ::
    "org.typelevel" %% "case-insensitive" % Version.CaseInsensitive ::
    Nil

name := "skunk-ext"

scalaVersion := Version.Scala213
