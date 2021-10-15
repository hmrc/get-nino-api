/*
 * Copyright 2021 HM Revenue & Customs
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

package v1.models.request

import play.api.libs.json.JsString
import support.UnitSpec

class MaritalStatusSpec extends UnitSpec {

  val validMaritalStatusString = "MARRIED"
  val invalidMaritalStatusString = "NOT_A_VALID_MARRIAGE"

  "MaritalStatus" should {

    "parse from a correct JsString" in {
      JsString(validMaritalStatusString).as[MaritalStatus] shouldBe MARRIED
    }

    "parse to a JsString" in {
      MARRIED shouldBe JsString(validMaritalStatusString).as[MaritalStatus]
    }

    "return a JsError" when {
      "an incorrect JsString is parsed" in {
        val result = JsString(invalidMaritalStatusString).validate[MaritalStatus].asEither

        result.isLeft shouldBe true
        result.left.get.head._2.head shouldBe MaritalStatus.maritalStatusError
      }
    }
  }

  ".validateMaritalStatus" should {

    "return true" when {
      MaritalStatus.allStatuses.values.map(_.value).toSeq.foreach { maritalValue =>
        s"$maritalValue is passed in" in {
          MaritalStatus.validateMaritalStatus(maritalValue) shouldBe true
        }
      }
    }

    "return false" when {
      "a value that is not a valid status is passed in" in {
        MaritalStatus.validateMaritalStatus(invalidMaritalStatusString) shouldBe false
      }
    }
  }

}
