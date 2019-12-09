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

package v1.models.request

import play.api.libs.json.{JsResultException, JsString, Json}
import support.UnitSpec

class DateModelSpec extends UnitSpec {

  val jsonForRead: JsString = JsString (
    "06-01-1993"
  )

  val jsonForReadInvalidStart: JsString = JsString (
    "1993-01-06"
  )

  val validJsonWrite: JsString = JsString(
    "1993-01-06"
  )

  val validModel = DateModel("06-01-1993")

  "Date model" should {
    "correctly parse from Json" when {
      "all fields are present" in {
        jsonForRead.as[DateModel] shouldBe validModel
      }
    }
    "correctly parse to Json" when {
      "all fields are present" in {
        Json.toJson(validModel) shouldBe validJsonWrite
      }
    }
    "fail to parse to json and throw an error" when {
      "the date in the model does not match the NPS regex" in {
        val errorModel = DateModel("2222222-111-222")

        val thrownException = intercept[IllegalArgumentException] {
          Json.toJson(errorModel)
        }

        thrownException.getMessage should include("Date failed validation")
      }
    }
    "fail to parse from json and throw an error" when {
      "the startDate field is not a valid date" in {
        val thrownException = intercept[JsResultException] {
          jsonForReadInvalidStart.as[DateModel]
        }

        thrownException.getMessage should include("Date has failed validation. Needs to be in format: dd-MM-yyyy")
      }
    }
  }
}
