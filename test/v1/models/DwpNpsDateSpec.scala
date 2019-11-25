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

import play.api.libs.json.{JsObject, JsResultException, JsString, Json}
import support.UnitSpec

class DwpNpsDateSpec extends UnitSpec {

  val jsonForRead: JsString = JsString (
    "06-01-1993"
  )

  val jsonForReadInvalidStart: JsString = JsString (
    "1993-01-06"
  )

  val validJsonWrite: JsString = JsString(
    "1993-01-06"
  )

  val validModel = DwpNpsDate("06-01-1993")

  "DwpNpsDate model" should {
    "correctly parse from Json" when {
      "all fields are present" in {
        jsonForRead.as[DwpNpsDate] shouldBe validModel
      }
    }
    "correctly parse to Json" when {
      "all fields are present" in {
        Json.toJson(validModel) shouldBe validJsonWrite
      }
    }
    "fail to parse to json and throw an error" when {
      "the date in the model does not match the NPS regex" in {
        val errorModel = DwpNpsDate("2222222-111-222")

        val thrownException = intercept[IllegalArgumentException] {
          Json.toJson(errorModel)
        }

        thrownException.getMessage should include("The following date failed validation against NPS regex: 222-111-2222222")
      }
    }
    "fail to parse from json and throw an error" when {
      "the startDate field is not a valid date" in {
        val thrownException = intercept[JsResultException] {
          jsonForReadInvalidStart.as[DwpNpsDate]
        }

        thrownException.getMessage should include("Date has failed validation. Needs to be in format: dd-MM-yyyy")
      }
    }
  }
}
