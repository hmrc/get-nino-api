import uk.gov.hmrc.DefaultBuildSettings.*

val appName = "get-nino-api"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.12"

// To resolve a bug with version 2.x.x of the scoverage plugin - https://github.com/sbt/sbt/issues/6997
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(libraryDependencies ++= AppDependencies())
  .settings(PlayKeys.playDefaultPort := 9750)
  .settings(CodeCoverageSettings.settings)
  .settings(scalacOptions += "-Wconf:src=routes/.*:s")

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(itSettings)
  .settings(Test / javaOptions += "-Dlogger.resource=logback-test.xml")

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt it/Test/scalafmt")
addCommandAlias("scalastyleAll", "all scalastyle Test/scalastyle it/Test/scalastyle")
