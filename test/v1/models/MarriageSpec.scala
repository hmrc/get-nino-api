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

  private lazy val maxMarriageJson: JsObject = Json.obj(
    "maritalStatus" -> 1,
    "startDate" -> "01-01-1990",
    "endDate" -> "01-01-2000",
    "partnerNino" -> "AA000000B",
    "birthDate" -> "01-01-1970",
    "forename" -> "Testforename",
    "secondForename" -> "Testsecondforename",
    "surname" -> "Testsurname"
  )

  private lazy val minMarriageJson: JsObject = Json.obj(
    "partnerNino" -> "AA000000B",
    "birthDate" -> "01-01-1970"
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

        maxMarriageJson.as[Marriage] shouldBe maxMarriageModel
      }
    }

    "provided with no optional values" should {

      "return a Marriage model with only mandatory items" in {

        minMarriageJson.as[Marriage] shouldBe minMarriageModel
      }
    }
  }

  "Marriage .writes" when {

    "provided with all optional values" should {

      "correctly parse to JSON" in {

      }
    }

    "provided with no optional values" should {

      "correctly parse to JSON" in {

      }
    }
  }
}
