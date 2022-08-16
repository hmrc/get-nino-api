/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  private lazy val silencerVersion = "1.7.9"

  private lazy val silencerDependencies: Seq[ModuleID] = Seq(
    compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
    "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
  )

  private lazy val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-backend-play-28" % "6.4.0",
    "uk.gov.hmrc"                  %% "play-hmrc-api"             % "7.0.0-play-28",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"      % "2.13.3"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "com.typesafe.play"      %% "play-test"          % current,
    "org.scalatest"          %% "scalatest"          % "3.2.13",
    "org.scalamock"          %% "scalamock"          % "5.2.0",
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0",
    "com.github.tomakehurst"  % "wiremock-jre8"      % "2.33.2",
    "com.vladsch.flexmark"    % "flexmark-all"       % "0.62.2"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID]           = compile ++ silencerDependencies ++ test

}
