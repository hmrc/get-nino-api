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

package v1.models.request

import play.api.libs.json.{JsResultException, JsString, Json}
import support.UnitSpec

class DateModelSpec extends UnitSpec {

  val jsonForRead: JsString = JsString(
    "06-01-1993"
  )

  val jsonForReadInvalidNpsConversion: JsString = JsString(
    "29-02-1993"
  )

  val jsonForReadInvalidDate: JsString = JsString(
    "1993-01-06"
  )

  val validJsonWrite: JsString = JsString(
    "1993-01-06"
  )

  val validModel: DateModel = DateModel("06-01-1993")

  "Date model" should {

    "correctly parse from Json" when {

      "all fields are present" in {
        jsonForRead.as[DateModel] shouldBe validModel
      }

      "regardless of the month" which {

        (1 to 12).map(number => s"20-${("0" + number.toString).takeRight(2)}-2000").foreach { stringDate =>
          s"for the following string: $stringDate" in {
            val expectedModel = DateModel(stringDate)

            JsString(stringDate).as[DateModel] shouldBe expectedModel
          }
        }

      }
    }

    "correctly parse to Json" when {

      "all fields are present" in {
        Json.toJson(validModel) shouldBe validJsonWrite
      }
    }

    "fail to parse from json and throw an error" when {

      "the date field is not a valid date" in {
        val thrownException = intercept[JsResultException] {
          jsonForReadInvalidDate.as[DateModel]
        }

        thrownException.getMessage should include("Date has failed validation. Needs to be in format of dd-MM-yyyy")
      }

      "the date field passes DWP validation, but fails NPS validation" in {
        val thrownException = intercept[JsResultException] {
          jsonForReadInvalidNpsConversion.as[DateModel]
        }

        thrownException.getMessage should include("Transformed date fails NPS validation.")
      }

    }
  }
}
