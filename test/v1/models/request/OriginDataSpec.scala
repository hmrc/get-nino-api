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

class OriginDataSpec extends UnitSpec {

  private val (lineNo1, lineNo2, lineNo3, lineNo4, lineNo5): (Int, Int, Int, Int, Int) = (1, 2, 3, 4, 5)

  private val minOriginDataJson: JsObject = JsObject.empty

  private val maxOriginDataJson: Boolean => JsObject = isWrite => {
    val addressLinePrefix = (lineNo: Int) => if (isWrite) s"addressLine$lineNo" else s"line$lineNo"
    Json.obj(
      "birthTown"             -> "Birth town value",
      "birthProvince"         -> "Birth province value",
      "birthCountryCode"      -> "GBR",
      "birthSurname"          -> "Birth surname value",
      "maternalForename"      -> "Maternal forename value",
      "maternalSurname"       -> "Maternal surname value",
      "paternalForename"      -> "Paternal forename value",
      "paternalSurname"       -> "Paternal surname value",
      "foreignSocialSecurity" -> "Foreign social security value",
      "lastEUAddress"         -> Json.obj(
        addressLinePrefix(lineNo1) -> "1 line value",
        addressLinePrefix(lineNo2) -> "2 line value",
        addressLinePrefix(lineNo3) -> "3 line value",
        addressLinePrefix(lineNo4) -> "4 line value",
        addressLinePrefix(lineNo5) -> "5 line value"
      )
    )
  }

  private val minOriginDataModel: OriginData = OriginData()

  private val maxOriginDataModel: OriginData = OriginData(
    birthTown = Some("Birth town value"),
    birthProvince = Some("Birth province value"),
    birthCountryCode = Some("GBR"),
    birthSurname = Some("Birth surname value"),
    maternalForename = Some("Maternal forename value"),
    maternalSurname = Some("Maternal surname value"),
    paternalForename = Some("Paternal forename value"),
    paternalSurname = Some("Paternal surname value"),
    foreignSocialSecurity = Some("Foreign social security value"),
    lastEUAddress = Some(
      LastEUAddress(
        addressLine1 = Some(AddressLine("1 line value")),
        addressLine2 = Some(AddressLine("2 line value")),
        addressLine3 = Some(AddressLine("3 line value")),
        addressLine4 = Some(AddressLine("4 line value")),
        addressLine5 = Some(AddressLine("5 line value"))
      )
    )
  )

  "OriginData" when {
    ".reads" should {
      "return an OriginData model" when {
        "provided with the maximum number of data items" in {
          maxOriginDataJson(false).as[OriginData] shouldBe maxOriginDataModel
        }

        "provided with the minimum number of data items" in {
          minOriginDataJson.as[OriginData] shouldBe minOriginDataModel
        }
      }
    }

    ".writes" should {
      "correctly parse to json" when {
        "provided with the maximum number of data items" in {
          Json.toJson(maxOriginDataModel) shouldBe maxOriginDataJson(true)
        }

        "provided with the minimum number of data items" in {
          Json.toJson(OriginData()) shouldBe minOriginDataJson
        }
      }
    }

    ".nameElementValidation" should {
      "return true" when {
        "provided with the optional string" which {
          "matches the supplied regex" in {
            val result: Boolean = OriginData.nameElementValidation(Some("Example value"), "Example value")

            result shouldBe true
          }
        }

        "optional string is not provided" in {
          val result: Boolean = OriginData.nameElementValidation(None, "Example value")

          result shouldBe true
        }
      }

      "return false" when {
        "provided with the optional string" which {
          "is too long" in {
            val result: Boolean = OriginData.nameElementValidation(
              Some("this example is far too long for the regex this in unfortunate"),
              "Example value"
            )

            result shouldBe false
          }

          "has an invalid character" in {
            val result: Boolean = OriginData.nameElementValidation(Some("!nvalid example"), "Example value")

            result shouldBe false
          }
        }
      }
    }

    ".countryCodeValidation" should {
      "return true" when {
        "provided with the optional string" which {
          "matches the supplied regex" in {
            val result: Boolean = OriginData.countryCodeValidation(Some("CAN"))

            result shouldBe true
          }
        }

        "optional string is not provided" in {
          val result: Boolean = OriginData.countryCodeValidation(None)

          result shouldBe true
        }
      }

      "return false" when {
        "provided with the optional string" which {
          "is too long" in {
            val result: Boolean = OriginData.countryCodeValidation(Some("ABCD"))

            result shouldBe false
          }

          "is too short" in {
            val result: Boolean = OriginData.countryCodeValidation(Some("AB"))

            result shouldBe false
          }
        }
      }
    }

    ".foreignSocialSecurityValidation" should {
      "return true" when {
        "provided with the optional string" which {
          "matches the supplied regex" in {
            val result: Boolean = OriginData.foreignSocialSecurityValidation(Some("1234567890AB"))

            result shouldBe true
          }
        }

        "optional string is not provided" in {
          val result: Boolean = OriginData.foreignSocialSecurityValidation(None)

          result shouldBe true
        }
      }

      "return false" when {
        "provided with the optional string" which {
          "is too long" in {
            val result: Boolean =
              OriginData.foreignSocialSecurityValidation(Some("this example is far too long for the regex"))

            result shouldBe false
          }

          "has an invalid character" in {
            val result: Boolean = OriginData.foreignSocialSecurityValidation(Some("!ABC"))

            result shouldBe false
          }
        }
      }
    }

    ".birthTownProvinceValidation" should {
      "return true" when {
        "provided with the optional string" which {
          "matches the supplied regex" in {
            val result: Boolean = OriginData.birthTownProvinceValidation(Some("ATown"), "birth town")

            result shouldBe true
          }
        }

        "optional string is not provided" in {
          val result: Boolean = OriginData.birthTownProvinceValidation(None, "birth province")

          result shouldBe true
        }
      }

      "return false" when {
        "provided with the optional string" which {
          "is too long" in {
            val result: Boolean =
              OriginData.birthTownProvinceValidation(Some("this example is far too long for the regex"), "birth town")

            result shouldBe false
          }

          "has an invalid character" in {
            val result: Boolean = OriginData.birthTownProvinceValidation(Some("!province"), "birth province")

            result shouldBe false
          }
        }
      }
    }
  }
}
