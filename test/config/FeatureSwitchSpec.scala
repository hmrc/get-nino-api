/*
 * Copyright 2019 HM Revenue & Customs
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

import com.typesafe.config.ConfigFactory
import play.api.Configuration
import support.UnitSpec

class FeatureSwitchSpec extends UnitSpec {

  private def createFeatureSwitch(config: String) =
    FeatureSwitch(Some(Configuration(ConfigFactory.parseString(config))))

  "version enabled" when {
    "there is config set" should {

      val featureSwitch = createFeatureSwitch(
        """
          |version-1.enabled = true
          |version-2.enabled = false
        """.stripMargin)

      "return true for the enabled version" in {
        featureSwitch.isVersionEnabled("1.0") shouldBe true
      }

      "return false for the disabled version" in {
        featureSwitch.isVersionEnabled("2.0") shouldBe false
      }

      "return false for non-version strings" in {
        featureSwitch.isVersionEnabled("x.x") shouldBe false
        featureSwitch.isVersionEnabled("x.1") shouldBe false
        featureSwitch.isVersionEnabled("2.x") shouldBe false
      }

    }
  }

}
