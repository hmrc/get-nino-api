import sbt.*

object AppDependencies {

  private lazy val bootstrapPlayVersion = "9.5.0"

  private lazy val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-backend-play-30" % bootstrapPlayVersion,
    "uk.gov.hmrc"                  %% "play-hmrc-api-play-30"     % "8.0.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"      % "2.18.1"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"         %% "bootstrap-test-play-30" % bootstrapPlayVersion,
    "org.scalatest"       %% "scalatest"              % "3.2.19",
    "org.scalamock"       %% "scalamock"              % "6.0.0",
    "org.wiremock"         % "wiremock-standalone"    % "3.9.2",
    "com.vladsch.flexmark" % "flexmark-all"           % "0.64.8"
  ).map(_ % Test)

  def apply(): Seq[ModuleID]           = compile ++ test
}
