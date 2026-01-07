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

import java.time.{LocalDate, ZoneId}
import java.time.format.DateTimeFormatter

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class AddressModelSpec extends UnitSpec {

  private val (lineNo1, lineNo2, lineNo3, lineNo4, lineNo5): (Int, Int, Int, Int, Int) = (1, 2, 3, 4, 5)

  private val minimumAddressJson: Boolean => JsValue = isReads => {

    val line1Path = if (isReads) "line1" else "addressLine1"

    Json.obj(
      line1Path     -> "1234 Test Avenue",
      "countryCode" -> "USA"
    )
  }

  private def maximumAddressJson: Boolean => JsValue = isReads => {
    val addressLinePrefix = (lineNo: Int) => if (isReads) s"line$lineNo" else s"addressLine$lineNo"
    val postCode          = if (isReads) s"postcode" else s"postCode"
    Json.obj(
      "addressType"              -> "RESIDENTIAL",
      addressLinePrefix(lineNo1) -> "1234 Test Avenue",
      addressLinePrefix(lineNo2) -> "Test Line 2",
      addressLinePrefix(lineNo3) -> "Test Line 3",
      addressLinePrefix(lineNo4) -> "Test Line 4",
      addressLinePrefix(lineNo5) -> "Test Line 5",
      postCode                   -> "TE5 5LN",
      "countryCode"              -> "GBR"
    ) ++ (if (isReads) {
            Json.obj(
              "startDate" -> "01-01-2019",
              "endDate"   -> "31-12-2019"
            )
          } else {
            Json.obj(
              "startDate" -> "2019-01-01",
              "endDate"   -> "2019-12-31"
            )
          })
  }

  private val minimumAddressModel: AddressModel =
    AddressModel(
      addressType = None,
      addressLine1 = AddressLine("1234 Test Avenue"),
      addressLine2 = None,
      addressLine3 = None,
      addressLine4 = None,
      addressLine5 = None,
      postCode = None,
      countryCode = "USA",
      startDate = None,
      endDate = None
    )

  private val maximumAddressModel: AddressModel =
    AddressModel(
      addressType = Some(Residential),
      addressLine1 = AddressLine("1234 Test Avenue"),
      addressLine2 = Some(AddressLine("Test Line 2")),
      addressLine3 = Some(AddressLine("Test Line 3")),
      addressLine4 = Some(AddressLine("Test Line 4")),
      addressLine5 = Some(AddressLine("Test Line 5")),
      postCode = Some(Postcode("TE5 5LN")),
      countryCode = "GBR",
      startDate = Some(DateModel("01-01-2019")),
      endDate = Some(DateModel("31-12-2019"))
    )

  "AddressModel" when {
    ".reads" should {
      "return an AddressModel" when {
        "only mandatory items are present" in {
          minimumAddressJson(true).as[AddressModel] shouldBe minimumAddressModel
        }

        "all optional fields are populated" in {
          maximumAddressJson(true).as[AddressModel] shouldBe maximumAddressModel
        }
      }

      "return an error" when {
        "missing a postcode field" in {
          val missingPostcodeAddressJson: JsValue = Json.obj(
            "addressType" -> "RESIDENTIAL",
            "line1"       -> "1234 Test Avenue",
            "line2"       -> "Test Line 2",
            "line3"       -> "Test Line 3",
            "line4"       -> "Test Line 4",
            "line5"       -> "Test Line 5",
            "countryCode" -> "GBR",
            "startDate"   -> "01-01-2019",
            "endDate"     -> "31-12-2019"
          )

          missingPostcodeAddressJson.validate[AddressModel].isError shouldBe true
        }
      }
    }

    ".writes" should {
      "correctly parse to json" when {
        "only mandatory items are present" in {
          Json.toJson(minimumAddressModel) shouldBe minimumAddressJson(false)
        }

        "all optional fields are populated" in {
          Json.toJson(maximumAddressModel) shouldBe maximumAddressJson(false)
        }
      }
    }

    ".checkPostcodeMandated" should {
      "return true" when {
        "a postcode is supplied" which {
          def test(countryCode: String): Unit =
            s"the country code supplied is $countryCode" in {
              val result: Boolean = AddressModel.checkPostcodeMandated(Some(Postcode("TF3 4NT")), countryCode)

              result shouldBe true
            }

          val inputArgs = Seq("GBR", "gbr", "ggy", "imn", "USA")

          inputArgs.foreach(test)
        }

        "the country code supplied is USA and a postcode is not supplied" in {
          val result: Boolean = AddressModel.checkPostcodeMandated(None, "USA")

          result shouldBe true
        }
      }

      "return false" when {
        "the country code supplied is GBR and a postcode is not supplied" in {
          val result: Boolean = AddressModel.checkPostcodeMandated(None, "GBR")

          result shouldBe false
        }
      }
    }

    ".validateDateAsPriorDate" should {
      val currentDate: DateModel =
        DateModel(LocalDate.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))

      "return true" when {
        "only one or neither date is provided" in {
          AddressModel.validateDateAsPriorDate(Some(currentDate), None) shouldBe true
          AddressModel.validateDateAsPriorDate(None, Some(currentDate)) shouldBe true
          AddressModel.validateDateAsPriorDate(None, None)              shouldBe true
        }

        "the first date provided is before the second" in {
          val validDate: DateModel = DateModel("01-01-2000")

          AddressModel.validateDateAsPriorDate(Some(validDate), Some(currentDate)) shouldBe true
        }

        "the provided dates are equal and canBeEqual is true" in {
          AddressModel.validateDateAsPriorDate(Some(currentDate), Some(currentDate)) shouldBe true
        }
      }

      "return false" when {
        "the first date provided is after the second" in {
          val invalidDate: DateModel = DateModel(
            LocalDate.now(ZoneId.of("UTC")).plusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
          )

          AddressModel.validateDateAsPriorDate(Some(invalidDate), Some(currentDate)) shouldBe false
        }

        "the provided dates are equal and canBeEqual is false" in {
          AddressModel.validateDateAsPriorDate(
            Some(currentDate),
            Some(currentDate),
            canBeEqual = false
          ) shouldBe false
        }
      }
    }
  }
}
