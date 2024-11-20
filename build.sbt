val Version = new {
  val CaseInsensitive = "1.4.2"
  val Cats = "2.12.0"
  val DisciplineMunit = "2.0.0"
  val EnumerationExt = "0.3.0"
  val Scala = "3.3.4"
  val Skunk = "1.0.0-M8"
}

def module(identifier: Option[String]): Project = {
  Project(identifier.getOrElse("root"), file(identifier.fold(".")("modules/" + _))).settings(
    Compile / scalacOptions ++= "-source:future" :: "-rewrite" :: "-new-syntax" :: "-Wunused:all" :: Nil,
    name := "sql-ext" + identifier.fold("")("-" + _)
  )
}

inThisBuild(
  Def.settings(
    developers := List(Developer("taig", "Niklas Klein", "mail@taig.io", url("https://taig.io/"))),
    dynverVTagPrefix := false,
    homepage := Some(url("https://github.com/taig/sql-ext/")),
    licenses := List("MIT" -> url("https://raw.githubusercontent.com/taig/sql-ext/main/LICENSE")),
    organization := "io.taig",
    organizationHomepage := Some(url("https://taig.io/")),
    scalaVersion := Version.Scala,
    versionScheme := Some("early-semver")
  )
)

lazy val root = module(identifier = None)
  .enablePlugins(BlowoutYamlPlugin)
  .settings(noPublishSettings)
  .settings(
    blowoutGenerators ++= {
      val workflows = file(".github") / "workflows"
      BlowoutYamlGenerator.lzy(workflows / "main.yml", GitHubActionsGenerator.main) ::
        BlowoutYamlGenerator.lzy(workflows / "pull-request.yml", GitHubActionsGenerator.pullRequest) ::
        BlowoutYamlGenerator.lzy(workflows / "tag.yml", GitHubActionsGenerator.tag) ::
        Nil
    }
  )
  .aggregate(core, skunk)

lazy val core = module(identifier = Some("core"))
  .settings(
    libraryDependencies ++=
      "org.typelevel" %% "cats-core" % Version.Cats ::
        "org.typelevel" %% "cats-laws" % Version.Cats % "test" ::
        "org.typelevel" %% "discipline-munit" % Version.DisciplineMunit % "test" ::
        Nil
  )

lazy val skunk = module(identifier = Some("skunk"))
  .settings(
    libraryDependencies ++=
      "io.taig" %% "enumeration-ext-core" % Version.EnumerationExt ::
        "org.tpolecat" %% "skunk-core" % Version.Skunk ::
        "org.typelevel" %% "case-insensitive" % Version.CaseInsensitive ::
        Nil
  )
  .dependsOn(core)
