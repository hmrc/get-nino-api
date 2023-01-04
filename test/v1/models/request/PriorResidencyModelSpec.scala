/*
 * Copyright 2023 HM Revenue & Customs
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

import java.time.{LocalDate, ZoneId}
import java.time.format.DateTimeFormatter

import play.api.libs.json.{JsObject, Json}
import support.UnitSpec

class PriorResidencyModelSpec extends UnitSpec {

  private val minJson: JsObject = JsObject.empty

  private val maxJson: Boolean => JsObject = isWrite => {
    if (isWrite) {
      Json.obj(
        "startDate" -> "2000-10-10",
        "endDate"   -> "2000-10-11"
      )
    } else {
      Json.obj(
        "priorStartDate" -> "10-10-2000",
        "priorEndDate"   -> "11-10-2000"
      )
    }
  }

  private val modelMin: PriorResidencyModel = PriorResidencyModel()
  private val modelMax: PriorResidencyModel =
    PriorResidencyModel(Some(DateModel("10-10-2000")), Some(DateModel("11-10-2000")))

  "PriorResidencyModel" when {
    ".reads" should {
      "correctly parse from json" when {
        "no fields are filled in" in {
          minJson.as[PriorResidencyModel] shouldBe modelMin
        }

        "all fields are filled in" in {
          maxJson(false).as[PriorResidencyModel] shouldBe modelMax
        }
      }
    }

    ".writes" should {
      "correctly write to json" when {
        "no fields are filled in" in {
          Json.toJson(modelMin) shouldBe minJson
        }

        "all fields are filled in" in {
          Json.toJson(modelMax) shouldBe maxJson(true)
        }
      }
    }

    ".validateDateAsPriorDate" should {
      val currentDate: DateModel =
        DateModel(LocalDate.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))

      "return true" when {
        "only one or neither date is provided" in {
          PriorResidencyModel.validateDateAsPriorDate(Some(currentDate), None) shouldBe true
          PriorResidencyModel.validateDateAsPriorDate(None, Some(currentDate)) shouldBe true
          PriorResidencyModel.validateDateAsPriorDate(None, None)              shouldBe true
        }

        "the first date provided is before the second" in {
          val validDate: DateModel = DateModel("01-01-2000")

          PriorResidencyModel.validateDateAsPriorDate(Some(validDate), Some(currentDate)) shouldBe true
        }

        "the provided dates are equal and canBeEqual is true" in {
          PriorResidencyModel.validateDateAsPriorDate(Some(currentDate), Some(currentDate)) shouldBe true
        }
      }

      "return false" when {
        "the first date provided is after the second" in {
          val invalidDate: DateModel = DateModel(
            LocalDate.now(ZoneId.of("UTC")).plusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
          )

          PriorResidencyModel.validateDateAsPriorDate(Some(invalidDate), Some(currentDate)) shouldBe false
        }

        "the provided dates are equal and canBeEqual is false" in {
          PriorResidencyModel.validateDateAsPriorDate(
            Some(currentDate),
            Some(currentDate),
            canBeEqual = false
          ) shouldBe false
        }
      }
    }
  }
}
