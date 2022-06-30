/*
 * Copyright 2022 HM Revenue & Customs
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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsObject, Json}

class OriginDataSpec extends AnyWordSpec with Matchers {

  val minOriginDataJson: JsObject = Json.obj()

  val maxOriginDataJson: Boolean =>JsObject = isWrite => {
    val addressLinePrefix = (lineNo: Int) => if (isWrite) s"addressLine$lineNo" else s"line$lineNo"
    Json.obj(
    "birthTown" -> "Birth town value",
    "birthProvince" -> "Birth province value",
    "birthCountryCode" -> "GBR",
    "birthSurname" -> "Birth surname value",
    "maternalForename" -> "Maternal forename value",
    "maternalSurname" -> "Maternal surname value",
    "paternalForename" -> "Paternal forename value",
    "paternalSurname" -> "Paternal surname value",
    "foreignSocialSecurity" -> "Foreign social security value",
    "lastEUAddress" -> Json.obj(
      addressLinePrefix(1) -> "1 line value",
      addressLinePrefix(2) -> "2 line value",
      addressLinePrefix(3) -> "3 line value",
      addressLinePrefix(4) -> "4 line value",
      addressLinePrefix(5) -> "5 line value"
    )
  )}

  val maxOriginDataModel = OriginData(
    birthTown = Some("Birth town value"),
    birthProvince = Some("Birth province value"),
    birthCountryCode = Some("GBR"),

    birthSurname = Some("Birth surname value"),
    maternalForename = Some("Maternal forename value"),
    maternalSurname = Some("Maternal surname value"),
    paternalForename = Some("Paternal forename value"),
    paternalSurname = Some("Paternal surname value"),
    foreignSocialSecurity = Some("Foreign social security value"),
    lastEUAddress = Some(LastEUAddress(
      addressLine1 = Some(AddressLine("1 line value")),
      addressLine2 = Some(AddressLine("2 line value")),
      addressLine3 = Some(AddressLine("3 line value")),
      addressLine4 = Some(AddressLine("4 line value")),
      addressLine5 = Some(AddressLine("5 line value"))
    ))

  )

  "OriginData.reads" when {

    "provided with the maximum number of data items" should {

      "return an OriginData model" in {

        maxOriginDataJson(false).as[OriginData] shouldBe maxOriginDataModel
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
        Json.toJson(maxOriginDataModel) shouldBe maxOriginDataJson(true)
      }
    }

    "provided with the minimum number of data items" should {

      "correctly parse to json" in {

        Json.toJson(OriginData()) shouldBe minOriginDataJson
      }
    }
  }

  "OriginData.nameElementValidation" when {

    "provided with the optional string" which {

      "matches the supplied regex" should {

        "return true" in {

          val result = OriginData.nameElementValidation(Some("Example value"), "Example value")

          result shouldBe true
        }
      }

      "the string is too long" should {

        "return false" in {

          val result = OriginData.nameElementValidation(Some("this example is far too long for the regex this in unfortunate"), "Example value")

          result shouldBe false
        }
      }

      "does not start with a valid character" should {

        "return false" in {

          val result = OriginData.nameElementValidation(Some("!nvalid example"), "Example value")

          result shouldBe false
        }
      }
    }

    "not provided with the optional string" should {

      "return true" in {

        val result = OriginData.nameElementValidation(None, "Example value")

        result shouldBe true
      }
    }
  }

  "OriginData.countryCodeValidation" when {

    "provided with the optional String" which {

      "passes the regex" should {

        "return true" in {

          val result = OriginData.countryCodeValidation(Some("ABC"))

          result shouldBe true
        }
      }

      "has too many characters to pass the regex" should {

        "return false" in {

          val result = OriginData.countryCodeValidation(Some("ABCD"))

          result shouldBe false
        }
      }

      "has too few characters to pass the regex" should {

        "return false" in {

          val result = OriginData.countryCodeValidation(Some("AB"))

          result shouldBe false
        }
      }
    }

    "is not provided with the optional String" should {

      "return true" in {

        val result = OriginData.countryCodeValidation(None)

        result shouldBe true

      }
    }
  }
}
