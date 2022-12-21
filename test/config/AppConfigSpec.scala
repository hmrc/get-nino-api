/*
 * Copyright 2022 HM Revenue & Customs
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
import support.UnitSpec
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class AppConfigSpec extends UnitSpec {

  private trait Test {
    implicit lazy val mockServiceConfig: ServicesConfig = mock[ServicesConfig]
    implicit lazy val mockConfig: Configuration         = mock[Configuration]

    lazy val target: AppConfigImpl = new AppConfigImpl
  }

  "AppConfigImpl" when {
    ".desBaseUrl" should {
      "retrieve the DES URL" in new Test {
        (mockServiceConfig
          .baseUrl(_: String))
          .expects("des")
          .returns("http://des-host")

        target.desBaseUrl shouldBe "http://des-host"
      }
    }

    ".desEnvironment" should {
      "return the des env" when {
        "a value is added to the configuration" in new Test {
          (mockServiceConfig
            .getString(_: String))
            .stubs("microservice.services.des.env")
            .returns("TEST_ENV")

          target.desEnvironment shouldBe "TEST_ENV"
        }
      }

      "return a runtime exception" when {
        "no value is added to the configuration" in new Test {
          intercept[RuntimeException] {
            target.desEnvironment
          }
        }
      }
    }

    ".desToken" should {
      "return the DES token" when {
        "token is added to the configuration" in new Test {
          (mockServiceConfig
            .getString(_: String))
            .stubs("microservice.services.des.token")
            .returns("some-token")

          target.desToken shouldBe "some-token"
        }
      }

      "return a runtime exception" when {
        "no value is added to the configuration" in new Test {
          intercept[RuntimeException] {
            target.desToken
          }
        }
      }
    }
  }
}
