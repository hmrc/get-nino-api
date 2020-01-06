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

package config

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import v1.config.featureSwitch.Features
import config.ConfigKeys._

trait AppConfig {

  def desBaseUrl(): String

  def desEnvironment(): String

  def desToken(): String

  def desContext(): String

  def desStubUrl: String

  def desStubContext: String

  def featureSwitch: Option[Configuration]

  def features: Features
}

@Singleton
class AppConfigImpl @Inject()(implicit val configuration: ServicesConfig, config: Configuration)
  extends AppConfig {

  private val desServicePrefix = "microservice.services.des"

  def featureSwitch: Option[Configuration] = config.getOptional[Configuration]("feature-switch")

  override lazy val desBaseUrl: String = configuration.baseUrl("des")
  override lazy val desEnvironment: String = configuration.getString(s"$desServicePrefix.env")
  override lazy val desToken: String = configuration.getString(s"$desServicePrefix.token")
  override lazy val desContext: String = configuration.getString(s"$desServicePrefix.context")
  override lazy val desStubUrl: String = configuration.baseUrl("desStub")
  override lazy val desStubContext: String = configuration.getString(desStubContextKey)
  override lazy val features: Features = new Features
}
