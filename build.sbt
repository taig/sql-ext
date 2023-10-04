val Version = new {
  val CaseInsensitive = "1.4.0"
  val Scala = "3.3.1"
  val Skunk = "0.6.0"
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
    scalaVersion := Version.Scala,
    versionScheme := Some("early-semver")
  )
)

Compile / scalacOptions ++=
  "-source:future" ::
    "-rewrite" ::
    "-new-syntax" ::
    "-Wunused:all" ::
    Nil

blowoutGenerators ++= {
  val workflows = file(".github") / "workflows"
  BlowoutYamlGenerator.lzy(workflows / "main.yml", GitHubActionsGenerator.main) ::
    BlowoutYamlGenerator.lzy(workflows / "branches.yml", GitHubActionsGenerator.branches) ::
    Nil
}

libraryDependencies ++=
  "org.tpolecat" %% "skunk-core" % Version.Skunk ::
    "org.typelevel" %% "case-insensitive" % Version.CaseInsensitive ::
    Nil

name := "skunk-ext"
