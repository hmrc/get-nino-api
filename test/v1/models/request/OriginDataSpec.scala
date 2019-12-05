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

import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsObject, Json}

class OriginDataSpec extends WordSpec with Matchers {

  val minOriginDataJson: JsObject = Json.obj()

  val maxOriginDataJson: JsObject = Json.obj(
    "birthTown" -> "Birth town value",
    "birthProvince" -> "Birth province value",
    "birthCountryCode" -> 129,
    "nationality" -> 111,
    "birthSurname" -> "Birth surname value",
    "maternalForename" -> "Maternal forename value",
    "maternalSurname" -> "Maternal surname value",
    "paternalForename" -> "Paternal forename value",
    "paternalSurname" -> "Paternal surname value",
    "foreignSocialSecurity" -> "Foreign social security value",
    "lastEUAddress" -> Json.obj(
      "line1" -> "1 line value",
      "line2" -> "2 line value",
      "line3" -> "3 line value",
      "line4" -> "4 line value",
      "line5" -> "5 line value"
    )
  )

  val maxOriginDataModel = OriginData(
    birthTown = Some("Birth town value"),
    birthProvince = Some("Birth province value"),
    birthCountryCode = Some(129),
    nationality = Some(111),
    birthSurname = Some("Birth surname value"),
    maternalForename = Some("Maternal forename value"),
    maternalSurname = Some("Maternal surname value"),
    paternalForename = Some("Paternal forename value"),
    paternalSurname = Some("Paternal surname value"),
    foreignSocialSecurity = Some("Foreign social security value"),
    lastEUAddress = Some(LastEUAddress(
      line1 = Some(AddressLine("1 line value")),
      line2 = Some(AddressLine("2 line value")),
      line3 = Some(AddressLine("3 line value")),
      line4 = Some(AddressLine("4 line value")),
      line5 = Some(AddressLine("5 line value"))
    ))

  )

  "OriginData.reads" when {

    "provided with the maximum number of data items" should {

      "return an OriginData model" in {

        maxOriginDataJson.as[OriginData] shouldBe maxOriginDataModel
      }
    }

    "provided with the minimum number of data items" should {

      "return an OriginData model" in {

        minOriginDataJson.as[OriginData] shouldBe OriginData()

      }
    }
  }

  "OriginData.writes" when {

    "provided with the maximum number of data items" should {

      "correctly parse to json" in {

        Json.toJson(maxOriginDataModel) shouldBe maxOriginDataJson
      }
    }

    "provided with the minimum number of data items" should {

      "correctly parse to json" in {

        Json.toJson(OriginData()) shouldBe minOriginDataJson
      }
    }
  }

  "OriginData.stringValidation" when {

    "provided with the optional string" which {

      "matches the supplied regex" should {

        "return true" in {

          val result = OriginData.stringValidation(Some("Example value"), "Example value")

          result shouldBe true
        }
      }

      "the string is too long" should {

        "return false" in {

          val result = OriginData.stringValidation(Some("this example is far too long for the regex this in unfortunate"), "Example value")

          result shouldBe false
        }
      }

      "does not start with a valid character" should {

        "return false" in {

          val result = OriginData.stringValidation(Some("!nvalid example"), "Example value")

          result shouldBe false
        }
      }
    }

    "not provided with the optional string" should {

      "return true" in {

        val result = OriginData.stringValidation(None, "Example value")

        result shouldBe true
      }
    }
  }

  "OriginData.countryCodeValidation" when {

    "provided with the optional Int" which {

      "is within the supplied range" should {

        "return true" in {

          val result = OriginData.countryCodeValidation(Some(111))

          result shouldBe true
        }
      }

      "is equal to the lower supplied valid range (0)" should {

        "return true" in {

          val result = OriginData.countryCodeValidation(Some(0))

          result shouldBe true
        }
      }

      "is equal to the higher supplied valid range (286)" should {

        "return true" in {

          val result = OriginData.countryCodeValidation(Some(286))

          result shouldBe true
        }
      }

      "is below the supplied valid range" should {

        "return false" in {

          val result = OriginData.countryCodeValidation(Some(-1))

          result shouldBe false
        }
      }

      "is above the supplied valid range" should {

        "return false" in {

          val result = OriginData.countryCodeValidation(Some(287))

          result shouldBe false

        }
      }
    }

    "is not provided with the optional Int" should {

      "return true" in {

        val result = OriginData.countryCodeValidation(None)

        result shouldBe true

      }
    }
  }
}
