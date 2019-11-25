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

import play.api.libs.json.{JsObject, JsResultException, Json}
import support.UnitSpec

class StartDateEndDateModelSpec extends UnitSpec {

  val jsonForRead: JsObject = Json.obj (
    "startDate" -> "06-01-1993",
    "endDate" -> "06-01-2020"
  )

  val jsonForReadInvalidStart: JsObject = Json.obj (
    "startDate" -> "1993-01-06",
    "endDate" -> "06-01-2020"
  )

  val jsonForReadInvalidEnd: JsObject = Json.obj (
    "startDate" -> "06-01-1993",
    "endDate" -> "062-01-2020"
  )

  val validJsonWrite: JsObject = Json.obj(
    "startDate" -> "1993-01-06",
    "endDate" -> "2020-01-06"
  )

  val validModel = StartDateEndDateModel("06-01-1993", "06-01-2020")

  "StartDateEndDateModel" should {
    "correctly parse from Json" in {
      jsonForRead.as[StartDateEndDateModel] shouldBe validModel
    }
    "correctly parse to Json" in {
      Json.toJson(validModel) shouldBe validJsonWrite
    }
    "fail to parse to json and throw an error" when {
      "the date in the model does not match the NPS regex" in {
        val model = StartDateEndDateModel("2222222-111-222", "2020-01-06")

        assertThrows[IllegalArgumentException] {
          Json.toJson(model)
        }
      }
    }
    "fail to parse from json and throw an error" when {
      "the startDate field is not a valid date" in {
        assertThrows[JsResultException] {
          jsonForReadInvalidStart.as[StartDateEndDateModel] shouldBe ""
        }
      }
      "the endDate field is not a valid date" in {
        assertThrows[JsResultException] {
          jsonForReadInvalidStart.as[StartDateEndDateModel] shouldBe ""
        }
      }
    }
  }
}
