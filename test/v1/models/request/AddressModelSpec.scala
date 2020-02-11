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
import play.api.libs.json.{JsValue, Json}

class AddressModelSpec extends WordSpec with Matchers {

  val minimumAddressJson: Boolean => JsValue = isReads => {

    val line1Path = if (isReads) "line1" else "addressLine1"

    Json.obj(
      line1Path -> "1234 Test Avenue",
      if (isReads) {
        "startDate" -> "01-01-2019"
      } else {
        "startDate" -> "2019-01-01"
      }
    )
  }


  def maximumAddressJson: Boolean => JsValue = isReads => {
    val addressLinePrefix = (lineNo: Int) => if (isReads) s"line$lineNo" else s"addressLine$lineNo"
    Json.obj(
      "addressType" -> "RESIDENTIAL",
      addressLinePrefix(1) -> "1234 Test Avenue",
      addressLinePrefix(2) -> "Test Line 2",
      addressLinePrefix(3) -> "Test Line 3",
      addressLinePrefix(4) -> "Test Line 4",
      addressLinePrefix(5) -> "Test Line 5",
      "postcode" -> "TE5 5LN",
      "countryCode" -> "GBR"
    ) ++ (if (isReads) {
      Json.obj(
        "startDate" -> "01-01-2019",
        "endDate" -> "31-12-2019"
      )
    }
    else {
      Json.obj(
        "startDate" -> "2019-01-01",
        "endDate" -> "2019-12-31"
      )
    })
  }


  val minimumAddressModel =
    AddressModel(
      addressType = None,
      addressLine1 = AddressLine("1234 Test Avenue"),
      addressLine2 = None,
      addressLine3 = None,
      addressLine4 = None,
      addressLine5 = None,
      postcode = None,
      countryCode = None,
      startDate = DateModel("01-01-2019"),
      endDate = None
    )

  val maximumAddressModel =
    AddressModel(
      addressType = Some(Residential),
      addressLine1 = AddressLine("1234 Test Avenue"),
      addressLine2 = Some(AddressLine("Test Line 2")),
      addressLine3 = Some(AddressLine("Test Line 3")),
      addressLine4 = Some(AddressLine("Test Line 4")),
      addressLine5 = Some(AddressLine("Test Line 5")),
      postcode = Some(Postcode("TE5 5LN")),
      countryCode = Some("GBR"),
      startDate = DateModel("01-01-2019"),
      endDate = Some(DateModel("31-12-2019"))
    )


  "Reading an AddressModel" when {

    "only mandatory items are present" should {

      "return a minimum AddressModel" in {

        minimumAddressJson(true).as[AddressModel] shouldBe minimumAddressModel
      }
    }

    "all optional fields are populated" should {

      "return a full AddressModel" in {

        maximumAddressJson(true).as[AddressModel] shouldBe maximumAddressModel
      }
    }

    "missing a postcode field" should {

      "return an error" in {

        val missingPostcodeAddressJson: JsValue = Json.obj(
          "addressType" -> "RESIDENTIAL",
          "line1" -> "1234 Test Avenue",
          "line2" -> "Test Line 2",
          "line3" -> "Test Line 3",
          "line4" -> "Test Line 4",
          "line5" -> "Test Line 5",
          "countryCode" -> "GBR",
          "startDate" -> "01-01-2019",
          "endDate" -> "31-12-2019"
        )

        missingPostcodeAddressJson.validate[AddressModel].isError shouldBe true
      }
    }
  }

  "Writing an AddressModel" when {

    "only mandatory items are present" should {

      "correctly parse to json" in {

        Json.toJson(minimumAddressModel) shouldBe minimumAddressJson(false)
      }
    }

    "all optional fields are populated" should {

      "correctly parse to json" in {

        Json.toJson(maximumAddressModel) shouldBe maximumAddressJson(false)
      }
    }
  }

  "AddressModel.checkPostcodeMandated" when {

    "a country code is supplied" when {

      "the country code is GBR" when {

        "a postcode is supplied" should {

          "return a postcode object" in {

            val result = AddressModel.checkPostcodeMandated(Some(Postcode("TF3 4NT")), Some("GBR"))

            result shouldBe true
          }
        }

        "a postcode is not supplied" should {

          "throw an exception" in {

            val result = AddressModel.checkPostcodeMandated(None, Some("GBR"))

            result shouldBe false
          }
        }
      }

      "the country code is gbr" when {

        "a postcode is supplied" should {

          "return a postcode object" in {

            val result = AddressModel.checkPostcodeMandated(Some(Postcode("TF3 4NT")), Some("gbr"))

            result shouldBe true
          }
        }
      }

      "the country code is not GBR" when {

        "a postcode is supplied" should {

          "return a postcode object" in {

            val result = AddressModel.checkPostcodeMandated(Some(Postcode("TF3 4NT")), Some("USA"))

            result shouldBe true
          }
        }

        "a postcode is not supplied" should {

          "return a None" in {

            val result = AddressModel.checkPostcodeMandated(None, Some("USA"))

            result shouldBe true
          }
        }
      }
    }

    "a country code is not supplied" should {

      "return the Postcode object" in {

        val result = AddressModel.checkPostcodeMandated(Some(Postcode("TF3 4NT")), None)

        result shouldBe true
      }
    }
  }
}