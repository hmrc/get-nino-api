/*
 * Copyright 2023 HM Revenue & Customs
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

import play.api.{Configuration, Logging}

final case class FeatureSwitch(value: Option[Configuration]) extends Logging {

  private val versionRegex = """(\d)\.\d""".r

  def isVersionEnabled(version: String): Boolean = {
    val versionNumber: Option[String] =
      version match {
        case versionRegex(ver) => Some(ver)
        case _                 =>
          logger.warn("[FeatureSwitch][isVersionEnabled] - version found does not match regex")
          None
      }

    val enabled = for {
      versionNum <- versionNumber
      config     <- value
      enabled    <- config.getOptional[Boolean](s"version-$versionNum.enabled")
    } yield enabled

    enabled.getOrElse(false)
  }

}
