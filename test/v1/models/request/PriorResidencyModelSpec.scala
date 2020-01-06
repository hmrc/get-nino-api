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

package v1.models.request

import play.api.libs.json.{JsObject, Json}
import support.UnitSpec

class PriorResidencyModelSpec extends UnitSpec {
  val minJson: JsObject = Json.obj()
  val maxJson: Boolean => JsObject = isWrite => Json.obj(
    "priorStartDate" -> (if(isWrite) "2020-10-10" else "10-10-2020"),
    "priorEndDate" -> (if(isWrite) "2020-10-10" else "10-10-2020")
  )

  val modelMin = PriorResidencyModel()
  val modelMax = PriorResidencyModel(Some(DateModel("10-10-2020")), Some(DateModel("10-10-2020")))

  "PriorResidencyModel" should {
    "correctly parse from json" when {
      "no fields are filled in" in {
        minJson.as[PriorResidencyModel] shouldBe modelMin
      }
      "all fields are filled in" in {
        maxJson(false).as[PriorResidencyModel] shouldBe modelMax
      }
    }
    "correctly write to json" when {
      "no fields are filled in" in {
        Json.toJson(modelMin) shouldBe minJson
      }
      "all fields are filled in" in {
        Json.toJson(modelMax) shouldBe maxJson(true)
      }
    }
  }
}
