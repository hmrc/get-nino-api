/*
 * Copyright 2026 HM Revenue & Customs
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

import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.*

trait AppConfig {
  def desBaseUrl(): String

  def desEnvironment(): String

  def desToken(): String

  def desEndpoint(): String

  def logDesJson(): Boolean

  def logDwpJson(): Boolean

  def featureSwitch: Option[Configuration]
}

@Singleton
class AppConfigImpl @Inject() (implicit val configuration: ServicesConfig, config: Configuration) extends AppConfig {

  private val desServicePrefix = "microservice.services.des"

  def featureSwitch: Option[Configuration] = config.getOptional[Configuration]("feature-switch")

  override def desBaseUrl(): String     = configuration.baseUrl("des")
  override def desEnvironment(): String = configuration.getString(s"$desServicePrefix.env")
  override def desToken(): String       = configuration.getString(s"$desServicePrefix.token")
  override def desEndpoint(): String    = configuration.getString(s"$desServicePrefix.endpoint")
  override def logDesJson(): Boolean    = config.getOptional[Boolean]("feature-switch.logDesJson").getOrElse(false)
  override def logDwpJson(): Boolean    = config.getOptional[Boolean]("feature-switch.logDwpJson").getOrElse(false)
}
