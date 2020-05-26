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

import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsObject, Json}

class MarriageSpec extends WordSpec with Matchers {

  private lazy val maxMarriageJson: Boolean => JsObject = isReads =>
    if (isReads) {
      Json.obj(
        "maritalStatus" -> "DIVORCED",
        "startDate" -> "01-01-1990",
        "endDate" -> "01-01-2000",
        "partnerNino" -> "AA000000B",
        "birthDate" -> "01-01-1970",
        "forename" -> "Testforename",
        "surname" -> "Testsurname"
      )
    } else {
      Json.obj(
        "maritalStatus" -> "DIVORCED",
        "startDate" -> "1990-01-01",
        "endDate" -> "2000-01-01",
        "spouseNino" -> "AA000000B",
        "spouseDateOfBirth" -> "1970-01-01",
        "spouseFirstName" -> "Testforename",
        "spouseSurname" -> "Testsurname"
      )
    }

  private lazy val minMarriageJson: Boolean => JsObject = isReads =>
    if (isReads) {
      Json.obj(
        "partnerNino" -> "AA000000B",
        "birthDate" -> "01-01-1970"
      )
    } else {
      Json.obj(
        "spouseNino" -> "AA000000B",
        "spouseDateOfBirth" -> "1970-01-01"
      )
    }

  private lazy val maxMarriageModel: Marriage = Marriage(
    maritalStatus = Some(DIVORCED),
    startDate = Some(DateModel("01-01-1990")),
    endDate = Some(DateModel("01-01-2000")),
    spouseNino = "AA000000B",
    spouseDateOfBirth = DateModel("01-01-1970"),
    spouseFirstName = Some("Testforename"),
    spouseSurname = Some("Testsurname")
  )

  private lazy val minMarriageModel: Marriage = Marriage(
    spouseNino = "AA000000B",
    spouseDateOfBirth = DateModel("01-01-1970")
  )


  "Marriage .reads" when {

    "provided with all optional values" should {

      "return a fully populated Marriage model" in {

        maxMarriageJson(true).as[Marriage] shouldBe maxMarriageModel
      }
    }

    "provided with no optional values" should {

      "return a Marriage model with only mandatory items" in {

        minMarriageJson(true).as[Marriage] shouldBe minMarriageModel
      }
    }

    "provided with invalid json" should {

      "throw an JsResultException" in {

        val json = Json.obj("bad" -> "json")

        json.validate[Marriage].isError shouldBe true
      }
    }
  }

  "Marriage .writes" when {

    "provided with all optional values" should {

      "correctly parse to JSON" in {

        Json.toJson(maxMarriageModel) shouldBe maxMarriageJson(false)
      }
    }

    "provided with no optional values" should {

      "correctly parse to JSON" in {

        Json.toJson(minMarriageModel) shouldBe minMarriageJson(false)
      }
    }
  }

  "Marriage. .stringValidation" when {

    "provided with a valid string" should {

      "return true" in {

        val result = Marriage.stringValidation(item = Some("Name"), itemName = "example")

        result shouldBe true
      }
    }

    "provided with a string which does not match the regex" should {

      "return false" in {

        val result = Marriage.stringValidation(item = Some("!incorrectName!"), itemName = "example")

        result shouldBe false
      }
    }

    "provided with a string with a length which is too long for the regex" should {

      "return false" in {

        val result = Marriage.stringValidation(item = Some("thisnameisunfortunatelytoolongfortheregexthatitwillbematchingagainst"), itemName = "example")

        result shouldBe false

      }
    }

    "not provided with the optional item value" should {

      "return true" in {

        val result = Marriage.stringValidation(item = None, itemName = "failed example")

        result shouldBe true
      }
    }
  }
}
