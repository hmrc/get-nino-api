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

import play.api.{ConfigLoader, Configuration}
import support.UnitSpec

class ApiDefinitionConfigSpec extends UnitSpec {

  private trait Test {
    lazy val mockConfig: Configuration = mock[Configuration]

    lazy val target: ApiDefinitionConfigImpl = {
      new ApiDefinitionConfigImpl(mockConfig)
    }
  }

  "calling status" when {

    "a value is added to the configuration" should {
      "retrieve the status specified" in new Test {

        val expected = "BETA"

        (mockConfig.get[String](_: String)(_: ConfigLoader[String]))
          .expects("api.status", *)
          .returns(expected)

        target.status shouldBe expected
      }
    }

    "no value is added to the configuration" should {
      "return a runtime exception" in new Test {
        intercept[RuntimeException] {
          target.status
        }
      }
    }
  }

  "calling accessType" when {

    "a value is added to the configuration" should {
      "retrieve the API access setting specified" in new Test {

        val expected = "PUBLIC"

        (mockConfig.getOptional[String](_: String)(_: ConfigLoader[String]))
          .expects("api.access.type", *)
          .returns(Some(expected))

        target.accessType shouldBe expected
      }
    }

    "no value is added to the configuration" should {
      "retrieve the default API access setting (PRIVATE)" in new Test {

        val expected = "PRIVATE"

        (mockConfig.getOptional[String](_: String)(_: ConfigLoader[String]))
          .expects("api.access.type", *)
          .returns(None)

        target.accessType shouldBe expected
      }
    }
  }

  "calling whitelistedApplicationIds" when {

    "values are added to the configuration" should {
      "retrieve the whitelisted application IDs specified" in new Test {

        private val expected = Seq("a", "b")

        (mockConfig.get[Seq[String]](_: String)(_: ConfigLoader[Seq[String]]))
          .expects("api.access.whitelistedApplicationIds", *)
          .returns(expected)

        target.whiteListedApplicationIds shouldBe expected
      }
    }

    "no value is added to the configuration" should {
      "return a runtime exception" in new Test {
        intercept[RuntimeException] {
          target.whiteListedApplicationIds
        }
      }
    }
  }
}
