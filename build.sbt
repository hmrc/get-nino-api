import uk.gov.hmrc.DefaultBuildSettings.*

val appName = "get-nino-api"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.7.4"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(libraryDependencies ++= AppDependencies())
  .settings(PlayKeys.playDefaultPort := 9750)
  .settings(CodeCoverageSettings.settings)
  .settings(scalacOptions += "-feature")

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(itSettings())
  .settings(Test / javaOptions += "-Dlogger.resource=logback-test.xml")

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt it/Test/scalafmt")
