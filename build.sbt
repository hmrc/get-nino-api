import uk.gov.hmrc.DefaultBuildSettings._

val appName = "get-nino-api"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    majorVersion := 0,
    libraryDependencies ++= AppDependencies(),
    // To resolve a bug with version 2.x.x of the scoverage plugin - https://github.com/sbt/sbt/issues/6997
    libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
  )
  .settings(PlayKeys.playDefaultPort := 9750)
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(CodeCoverageSettings.settings: _*)
  .settings(scalaVersion := "2.13.10")
  .settings(IntegrationTest / resourceDirectory := (baseDirectory apply { baseDir: File =>
    baseDir / "it/resources"
  }).value)
  .settings(scalacOptions += "-Wconf:src=routes/.*:s")

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt")
addCommandAlias("scalastyleAll", "all scalastyle Test/scalastyle")
