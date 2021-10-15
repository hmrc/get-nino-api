import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "get-nino-api"

lazy val wartRemoverError = {

  val errorWarts = Seq(
    Wart.ArrayEquals,
    Wart.AnyVal,
    Wart.EitherProjectionPartial,
    Wart.Enumeration,
    Wart.ExplicitImplicitTypes,
    Wart.FinalVal,
    Wart.JavaConversions,
    Wart.JavaSerializable,
    Wart.LeakingSealed,
    Wart.MutableDataStructures,
    Wart.Null,
    Wart.OptionPartial,
    Wart.Recursion,
    Wart.Return,
    Wart.TraversableOps,
    Wart.TryPartial,
    Wart.Var,
    Wart.While,
    Wart.FinalCaseClass, Wart.JavaSerializable,
    Wart.StringPlusAny,
    Wart.AsInstanceOf,
    Wart.IsInstanceOf,
    Wart.Any
  )
  wartremoverErrors in(Compile, compile) ++= errorWarts
}

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    majorVersion := 0,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    dependencyOverrides ++= AppDependencies.overrides
  )
  .settings(PlayKeys.playDefaultPort := 9750)
  .settings(publishingSettings: _*)
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings: _*)
  .settings(scalaVersion := "2.12.13")
  .settings(wartRemoverError, wartremoverExcluded ++= routes.in(Compile).value)
  .settings(resourceDirectory in IntegrationTest := (baseDirectory apply { baseDir: File => baseDir / "it/resources" }).value)

scalacOptions ++= Seq(
  "-P:silencer:globalFilters=Unused import",
  "-feature"
)

