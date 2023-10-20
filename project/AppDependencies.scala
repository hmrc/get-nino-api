import sbt.*

object AppDependencies {

  private lazy val bootstrapPlayVersion = "7.22.0"

  private lazy val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-backend-play-28" % bootstrapPlayVersion,
    "uk.gov.hmrc"                  %% "play-hmrc-api"             % "7.2.0-play-28",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"      % "2.15.3"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"         %% "bootstrap-test-play-28" % bootstrapPlayVersion,
    "org.scalatest"       %% "scalatest"              % "3.2.17",
    "org.scalamock"       %% "scalamock"              % "5.2.0",
    "org.wiremock"         % "wiremock-standalone"    % "3.2.0",
    "com.vladsch.flexmark" % "flexmark-all"           % "0.64.8"
  ).map(_ % Test)

  def apply(): Seq[ModuleID]           = compile ++ test
}
