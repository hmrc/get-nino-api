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

import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.{ConfigLoader, Configuration}
import support.UnitSpec

class ApiDefinitionConfigSpec extends UnitSpec {

  private trait Test {
    lazy val mockConfig: Configuration = mock[Configuration]

    lazy val target: ApiDefinitionConfigImpl = new ApiDefinitionConfigImpl(mockConfig)
  }

  "ApiDefinitionConfigImpl" when {
    ".status" should {
      "retrieve the status specified" when {
        "a value is added to the configuration" in new Test {
          when(mockConfig.get[String](ArgumentMatchers.eq("api.status"))(any[ConfigLoader[String]]))
            .thenReturn("BETA")

          target.status() shouldBe "BETA"
        }
      }

      "return a runtime exception" when {
        "no value is added to the configuration" in new Test {
          when(mockConfig.get[String](ArgumentMatchers.eq("api.status"))(any[ConfigLoader[String]]))
            .thenThrow(new RuntimeException("error"))

          intercept[RuntimeException] {
            target.status()
          }
        }
      }
    }

    ".accessType" should {
      "retrieve the API access setting specified" when {
        "a value is added to the configuration" in new Test {
          when(
            mockConfig
              .getOptional[String](ArgumentMatchers.eq("api.access.type"))(any[ConfigLoader[String]]())
          )
            .thenReturn(Some("PUBLIC"))

          target.accessType() shouldBe "PUBLIC"
        }
      }

      "retrieve the default API access setting (PRIVATE)" when {
        "no value is added to the configuration" in new Test {
          when(
            mockConfig
              .getOptional[String](ArgumentMatchers.eq("api.access.type"))(any[ConfigLoader[String]]())
          )
            .thenReturn(None)

          target.accessType() shouldBe "PRIVATE"
        }
      }
    }

  }

}
