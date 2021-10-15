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

  val silencerVersion = "1.7.6"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % "5.14.0",
    "uk.gov.hmrc" %% "play-hmrc-api"             % "6.4.0-play-28",
    compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
    "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
  )

  val test: Seq[ModuleID] = Seq(
    "com.typesafe.play"       %% "play-test"          % current   % "test",
    "org.scalatest"           %% "scalatest"          % "3.2.9"   % "test",
    "org.scalamock"           %% "scalamock"          % "5.1.0"   % "test",
    "org.scalatestplus.play"  %% "scalatestplus-play" % "5.1.0"   % "test, it",
    "org.pegdown"             %  "pegdown"            % "1.6.0"   % "test, it",
    "com.github.tomakehurst"  %  "wiremock"           % "2.26.1"  % "test, it",
    "com.vladsch.flexmark"    %  "flexmark-all"       % "0.36.8"  % "test, it"
  )

  // Fixes a transitive dependency clash between wiremock and scalatestplus-play
  val overrides: Seq[ModuleID] = {

    val jettyFromWiremockVersion = "9.2.24.v20180105"
    Seq(
      "org.eclipse.jetty" % "jetty-client"        % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-continuation"  % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-http"          % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-io"            % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-security"      % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-server"        % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-servlet"       % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-servlets"      % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-util"          % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-webapp"        % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-xml"           % jettyFromWiremockVersion,
      "org.eclipse.jetty.websocket" % "websocket-api"     % jettyFromWiremockVersion,
      "org.eclipse.jetty.websocket" % "websocket-client"  % jettyFromWiremockVersion,
      "org.eclipse.jetty.websocket" % "websocket-common"  % jettyFromWiremockVersion
    )
  }
}
