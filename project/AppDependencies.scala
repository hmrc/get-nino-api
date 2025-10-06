import sbt.*

object AppDependencies {

  private lazy val bootstrapPlayVersion = "10.2.0"

  private lazy val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-backend-play-30" % bootstrapPlayVersion,
    "uk.gov.hmrc"                  %% "play-hmrc-api-play-30"     % "8.0.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"      % "2.20.0"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapPlayVersion
  ).map(_ % Test)

  def apply(): Seq[ModuleID]           = compile ++ test
}
