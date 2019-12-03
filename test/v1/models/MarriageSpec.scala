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

import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsObject, Json}

class MarriageSpec extends WordSpec with Matchers {

  private lazy val maxMarriageJson: Boolean => JsObject = isReads => Json.obj(
    "maritalStatus" -> 1,
    "startDate" -> (if (isReads) "01-01-1990" else "1990-01-01"),
    "endDate" -> (if (isReads) "01-01-2000" else "2000-01-01"),
    "partnerNino" -> "AA000000B",
    "birthDate" -> (if (isReads) "01-01-1970" else "1970-01-01"),
    "forename" -> "Testforename",
    "secondForename" -> "Testsecondforename",
    "surname" -> "Testsurname"
  )

  private lazy val minMarriageJson: Boolean => JsObject = isReads => Json.obj(
    "partnerNino" -> "AA000000B",
    "birthDate" -> (if (isReads) "01-01-1970" else "1970-01-01")
  )

  private lazy val maxMarriageModel: Marriage = Marriage(
    maritalStatus = Some(1),
    startDate = Some(DateModel("01-01-1990")),
    endDate = Some(DateModel("01-01-2000")),
    partnerNino = "AA000000B",
    birthDate = DateModel("01-01-1970"),
    forename = Some("Testforename"),
    secondForename = Some("Testsecondforename"),
    surname = Some("Testsurname")
  )

  private lazy val minMarriageModel: Marriage = Marriage(
    partnerNino = "AA000000B",
    birthDate = DateModel("01-01-1970")
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

  "Marriage .maritalStatusValidation" when {

    "provided with an optional int" which {

      "is within the valid range" should {

        "return true" in {

          val result = Marriage.maritalStatusValidation(Some(6))

          result shouldBe true
        }
      }

      "is on the lower allowed limit" should {

        "return true" in {

          val result = Marriage.maritalStatusValidation(Some(0))

          result shouldBe true
        }
      }

      "is on the upper allowed limit" should {

        "return true" in {

          val result = Marriage.maritalStatusValidation(Some(12))

          result shouldBe true
        }
      }

      "is below the lower allowed limit" should {

        "return false" in {

          val result = Marriage.maritalStatusValidation(Some(-1))

          result shouldBe false
        }
      }

      "is above the upper allowed limit" should {

        "return false" in {

          val result = Marriage.maritalStatusValidation(Some(13))

          result shouldBe false
        }
      }
    }

    "not provided with the optional int" should {

      "return true" in {

        val result = Marriage.maritalStatusValidation(None)

        result shouldBe true
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
