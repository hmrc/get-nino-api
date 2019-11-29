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

package v1.models

import play.api.libs.json.{JsString, Json}
import support.UnitSpec

class GenderSpec extends UnitSpec {
  val invalidGender = "NOTAGENDER"

  "Gender" should {
    "correctly parse from a JSON String" when {
      "passed in MALE" in {
        JsString("MALE").as[Gender] shouldBe Male
      }
      "passed in FEMALE" in {
        JsString("FEMALE").as[Gender] shouldBe Female
      }
      "passed in NOT-KNOWN" in {
        JsString("NOT-KNOWN").as[Gender] shouldBe GenderNotKnown
      }
    }
    "correctly parse to json" when {
      "passed in MALE" in {
        Json.toJson(Male) shouldBe JsString("MALE")
      }
      "passed in FEMALE" in {
        Json.toJson(Female) shouldBe JsString("FEMALE")
      }
      "passed in NOT-KNOWN" in {
        Json.toJson(GenderNotKnown) shouldBe JsString("NOT-KNOWN")
      }
    }
    "fail to parse from Json" when {
      "the value is not one of the available values" in {
        val expectedException = intercept[IllegalArgumentException] {
          JsString(invalidGender).as[Gender]
        }

        expectedException.getMessage should include (s"Provided gender does not match available options: MALE, FEMALE, NOT-KNOWN")
      }
    }
  }
}
