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

import java.time.{LocalDate, ZoneId}
import java.time.format.DateTimeFormatter

import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsValue, Json}

class AddressModelSpec extends WordSpec with Matchers {

  val minimumAddressJson: Boolean => JsValue = isReads => {

    val line1Path = if (isReads) "line1" else "addressLine1"

    Json.obj(
      line1Path -> "1234 Test Avenue",
      "countryCode" -> "USA",
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
      countryCode = "USA",
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
      countryCode = "GBR",
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

            val result = AddressModel.checkPostcodeMandated(Some(Postcode("TF3 4NT")), "GBR")

            result shouldBe true
          }
        }

        "a postcode is not supplied" should {

          "throw an exception" in {

            val result = AddressModel.checkPostcodeMandated(None, "GBR")

            result shouldBe false
          }
        }
      }

      "the country code is gbr" when {

        "a postcode is supplied" should {

          "return true" in {

            val result = AddressModel.checkPostcodeMandated(Some(Postcode("TF3 4NT")), "gbr")

            result shouldBe true
          }
        }
      }

      "the country code is not GBR" when {

        "a postcode is supplied" should {

          "return a true" in {

            val result = AddressModel.checkPostcodeMandated(Some(Postcode("TF3 4NT")), "USA")

            result shouldBe true
          }
        }

        "a postcode is not supplied" should {

          "return a true" in {

            val result = AddressModel.checkPostcodeMandated(None, "USA")

            result shouldBe true
          }
        }
      }
    }
  }

  ".validateDateAsPriorDate" should {

    val currentDate = DateModel(LocalDate.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))

    "return true" when {

      "the first date provided is before the second" in {
        val validDate = DateModel("01-01-2000")

        AddressModel.validateDateAsPriorDate(validDate, Some(currentDate)) shouldBe true
      }

      "the provided dates are equal and canBeEqual is true" in {
        val validDate = DateModel(
          LocalDate.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        )

        AddressModel.validateDateAsPriorDate(validDate, Some(validDate)) shouldBe true
      }

      "only an earlier date is provided" in {
        AddressModel.validateDateAsPriorDate(currentDate, None) shouldBe true
      }
    }

    "return false" when {

      "the first date provided is after the second" in {
        val invalidDate = DateModel(
          LocalDate.now(ZoneId.of("UTC")).plusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        )

        AddressModel.validateDateAsPriorDate(invalidDate, Some(currentDate)) shouldBe false
      }

      "the provided dates are equal and canBeEqual is false" in {
        val validDate = DateModel(
          LocalDate.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        )

        AddressModel.validateDateAsPriorDate(validDate, Some(validDate), canBeEqual = false) shouldBe false
      }
    }

  }
}
