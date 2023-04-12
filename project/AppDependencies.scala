import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  private lazy val bootstrapPlayVersion = "7.15.0"

  private lazy val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-backend-play-28" % bootstrapPlayVersion,
    "uk.gov.hmrc"                  %% "play-hmrc-api"             % "7.2.0-play-28",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"      % "2.14.2"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "com.typesafe.play"     %% "play-test"              % current,
    "uk.gov.hmrc"           %% "bootstrap-test-play-28" % bootstrapPlayVersion,
    "org.scalatest"         %% "scalatest"              % "3.2.15",
    "org.scalamock"         %% "scalamock"              % "5.2.0",
    "com.github.tomakehurst" % "wiremock-jre8"          % "2.35.0",
    "com.vladsch.flexmark"   % "flexmark-all"           % "0.64.0"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID]           = compile ++ test

}
