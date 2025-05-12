/*
 * Copyright 2025 HM Revenue & Customs
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

import play.api._
import support.UnitSpec
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class AppConfigSpec extends UnitSpec {

  def createAppConfig(config: (String, Any)*): AppConfigImpl = {
    val configuration = Configuration(config: _*)
    new AppConfigImpl()(new ServicesConfig(configuration), configuration)
  }

  "AppConfigImpl" when {
    ".desBaseUrl" should {
      "return the DES URL" when {
        "a value is added to the configuration" in {
          val appConfig = createAppConfig(
            "microservice.services.des.host" -> "des-host",
            "microservice.services.des.port" -> "1234"
          )

          appConfig.desBaseUrl shouldBe "http://des-host:1234"
        }

        "return a runtime exception" when {
          "no value is added to the configuration" in {
            val appConfig = createAppConfig()

            intercept[RuntimeException] {
              appConfig.desBaseUrl
            }
          }
        }
      }
    }

    ".desEnvironment" should {
      "return the DES env" when {
        "a value is added to the configuration" in {
          val appConfig = createAppConfig("microservice.services.des.env" -> "TEST_ENV")
          appConfig.desEnvironment shouldBe "TEST_ENV"
        }
      }

      "return a runtime exception" when {
        "no value is added to the configuration" in {
          val appConfig = createAppConfig()

          intercept[RuntimeException] {
            appConfig.desEnvironment
          }
        }
      }
    }

    ".desToken" should {
      "return the DES token" when {
        "token is added to the configuration" in {
          val appConfig = createAppConfig("microservice.services.des.token" -> "some-token")
          appConfig.desToken shouldBe "some-token"
        }
      }

      "return a runtime exception" when {
        "no value is added to the configuration" in {
          val appConfig = createAppConfig()

          intercept[RuntimeException] {
            appConfig.desToken
          }
        }
      }
    }

    ".desEndpoint" should {
      "return the DES endpoint" when {
        "a value is added to the configuration" in {
          val appConfig = createAppConfig("microservice.services.des.endpoint" -> "/register")
          appConfig.desEndpoint shouldBe "/register"
        }
      }

      "return a runtime exception" when {
        "no value is added to the configuration" in {
          val appConfig = createAppConfig()

          intercept[RuntimeException] {
            appConfig.desEndpoint
          }
        }
      }
    }

    ".logDesJson" should {
      "return true" when {
        "the value true is added to the configuration" in {
          val appConfig = createAppConfig("feature-switch.logDesJson" -> true)
          appConfig.logDesJson shouldBe true
        }
      }

      "default to false" when {
        "no value is added to the configuration" in {
          val appConfig = createAppConfig()
          appConfig.logDesJson shouldBe false
        }
      }
    }

    ".logDwpJson" should {
      "return true" when {
        "the value true is added to the configuration" in {
          val appConfig = createAppConfig("feature-switch.logDwpJson" -> true)
          appConfig.logDwpJson shouldBe true
        }
      }

      "default to false" when {
        "no value is added to the configuration" in {
          val appConfig = createAppConfig()
          appConfig.logDwpJson shouldBe false
        }
      }
    }
  }
}
