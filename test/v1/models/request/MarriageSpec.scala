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

import play.api.libs.json.{JsObject, Json}
import support.UnitSpec

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZoneId}

class MarriageSpec extends UnitSpec {

  private lazy val maxMarriageJson: Boolean => JsObject = isReads =>
    if (isReads) {
      Json.obj(
        "maritalStatus" -> "DIVORCED",
        "startDate"     -> "01-01-1990",
        "endDate"       -> "01-01-2000",
        "partnerNino"   -> "AA000000B",
        "birthDate"     -> "01-01-1970",
        "forename"      -> "Testforename",
        "surname"       -> "Testsurname"
      )
    } else {
      Json.obj(
        "maritalStatus"     -> "DIVORCED",
        "startDate"         -> "1990-01-01",
        "endDate"           -> "2000-01-01",
        "spouseNino"        -> "AA000000B",
        "spouseDateOfBirth" -> "1970-01-01",
        "spouseFirstName"   -> "Testforename",
        "spouseSurname"     -> "Testsurname"
      )
    }

  private lazy val minMarriageJson: JsObject = JsObject.empty

  private lazy val maxMarriageModel: Marriage = Marriage(
    maritalStatus = Some(DIVORCED),
    startDate = Some(DateModel("01-01-1990")),
    endDate = Some(DateModel("01-01-2000")),
    spouseNino = Some("AA000000B"),
    spouseDateOfBirth = Some(DateModel("01-01-1970")),
    spouseFirstName = Some("Testforename"),
    spouseSurname = Some("Testsurname")
  )

  private lazy val minMarriageModel: Marriage = Marriage()

  "Marriage" when {
    ".reads" should {
      "return a Marriage model" when {
        "provided with all optional values" in {
          maxMarriageJson(true).as[Marriage] shouldBe maxMarriageModel
        }

        "provided with no optional values" in {
          minMarriageJson.as[Marriage] shouldBe minMarriageModel
        }
      }

      "throw a JsResultException" when {
        "provided with invalid json" in {
          val json: JsObject = Json.obj("partnerNino" -> 3)

          json.validate[Marriage].isError shouldBe true
        }
      }
    }

    ".writes" should {
      "correctly parse to JSON" when {
        "provided with all optional values" in {
          Json.toJson(maxMarriageModel) shouldBe maxMarriageJson(false)
        }

        "provided with no optional values" in {
          Json.toJson(minMarriageModel) shouldBe minMarriageJson
        }
      }
    }

    ".stringValidation" should {
      "return true" when {
        "provided with a valid string" in {
          val result: Boolean = Marriage.stringValidation(item = Some("Name"), itemName = "example")

          result shouldBe true
        }

        "not provided with the optional item value" in {
          val result: Boolean = Marriage.stringValidation(item = None, itemName = "failed example")

          result shouldBe true
        }
      }

      "return false" when {
        "provided with a string" which {
          "is too long" in {
            val result: Boolean = Marriage.stringValidation(
              item = Some("thisnameisunfortunatelytoolongfortheregexthatitwillbematchingagainst"),
              itemName = "example"
            )

            result shouldBe false
          }

          "has an invalid character" in {
            val result: Boolean = Marriage.stringValidation(item = Some("!incorrectName!"), itemName = "example")

            result shouldBe false
          }
        }
      }
    }

    ".validateDateAsPriorDate" should {
      val currentDate: DateModel =
        DateModel(LocalDate.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))

      "return true" when {
        "only one or neither date is provided" in {
          Marriage.validateDateAsPriorDate(Some(currentDate), None) shouldBe true
          Marriage.validateDateAsPriorDate(None, Some(currentDate)) shouldBe true
          Marriage.validateDateAsPriorDate(None, None)              shouldBe true
        }

        "the first date provided is before the second" in {
          val validDate: DateModel = DateModel("01-01-2000")

          Marriage.validateDateAsPriorDate(Some(validDate), Some(currentDate)) shouldBe true
        }

        "the provided dates are equal and canBeEqual is true" in {
          Marriage.validateDateAsPriorDate(Some(currentDate), Some(currentDate)) shouldBe true
        }
      }

      "return false" when {
        "the first date provided is after the second" in {
          val invalidDate: DateModel = DateModel(
            LocalDate.now(ZoneId.of("UTC")).plusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
          )

          Marriage.validateDateAsPriorDate(Some(invalidDate), Some(currentDate)) shouldBe false
        }

        "the provided dates are equal and canBeEqual is false" in {
          Marriage.validateDateAsPriorDate(
            Some(currentDate),
            Some(currentDate),
            canBeEqual = false
          ) shouldBe false
        }
      }
    }
  }

}
