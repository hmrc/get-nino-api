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
import play.api.libs.json.{JsValue, Json}

class AddressModelSpec extends WordSpec with Matchers {

  val minimumAddressJson: JsValue = Json.obj(
    "line1" -> "1234 Test Avenue"
  )

  val maximumAddressJson: JsValue = Json.obj(
    "addressType" -> "RESIDENTIAL",
    "line1" -> "1234 Test Avenue",
    "line2" -> "Test Line 2",
    "line3" -> "Test Line 3",
    "line4" -> "Test Line 4",
    "line5" -> "Test Line 5",
    "postcode" -> "TE5 5LN",
    "countryCode" -> "GBR",
    "startDate" -> "2019-01-01",
    "endDate" -> "2019-12-31"
  )

  val minimumAddressModel =
    AddressModel(
      addressType = None,
      line1 = AddressLine("1234 Test Avenue"),
      line2 = None,
      line3 = None,
      line4 = None,
      line5 = None,
      postcode = None,
      countryCode = None,
      startDate = None,
      endDate = None
    )

  val maximumAddressModel =
    AddressModel(
      addressType = Some(Residential),
      line1 = AddressLine("1234 Test Avenue"),
      line2 = Some(AddressLine("Test Line 2")),
      line3 = Some(AddressLine("Test Line 3")),
      line4 = Some(AddressLine("Test Line 4")),
      line5 = Some(AddressLine("Test Line 5")),
      postcode = Some(Postcode("TE5 5LN")),
      countryCode = Some("GBR"),
      startDate = Some("2019-01-01"),
      endDate = Some("2019-12-31")
    )


  "Reading an AddressModel" when {

    "only mandatory items are present" should {

      "return a minimum AddressModel" in {

        minimumAddressJson.as[AddressModel] shouldBe minimumAddressModel
      }
    }

    "all optional fields are populated" should {

      "return a full AddressModel" in {

        maximumAddressJson.as[AddressModel] shouldBe maximumAddressModel
      }
    }
  }

  "Writing an AddressModel" when {

    "only mandatory items are present" should {

      "correctly parse to json" in {

        Json.toJson(minimumAddressModel) shouldBe minimumAddressJson

      }
    }

    "all optional fields are populated" should {

      "correctly parse to json" in {

        Json.toJson(maximumAddressModel) shouldBe maximumAddressJson

      }
    }
  }

  "AddressModel.checkPostcodeMandated" when {

    "a country code is supplied" when {

      "the country code is GBR" when {

        "a postcode is supplied" should {

          "return a postcode object" in {

            val result = AddressModel.checkPostcodeMandated(Some(Postcode("TF3 4NT")), Some("GBR"))

            result shouldBe Some(Postcode("TF3 4NT"))
          }
        }

        "a postcode is not supplied" should {

          "throw an exception" in {

            val exception = intercept[IllegalArgumentException] {
              AddressModel.checkPostcodeMandated(None, Some("GBR"))
            }

            exception.getMessage shouldBe "Postcode required if Country code is GBR"
          }
        }
      }

      "the country code is gbr" when {

        "a postcode is supplied" should {

          "return a postcode object" in {

            val result = AddressModel.checkPostcodeMandated(Some(Postcode("TF3 4NT")), Some("gbr"))

            result shouldBe Some(Postcode("TF3 4NT"))
          }
        }
      }

      "the country code is not GBR" when {

        "a postcode is supplied" should {

          "return a postcode object" in {

            val result = AddressModel.checkPostcodeMandated(Some(Postcode("TF3 4NT")), Some("USA"))

            result shouldBe Some(Postcode("TF3 4NT"))
          }
        }

        "a postcode is not supplied" should {

          "return a None" in {

            val result = AddressModel.checkPostcodeMandated(None, Some("USA"))

            result shouldBe None
          }
        }
      }
    }

    "a country code is not supplied" should {

      "return the Postcode object" in {

        val result = AddressModel.checkPostcodeMandated(Some(Postcode("TF3 4NT")), None)

        result shouldBe Some(Postcode("TF3 4NT"))
      }
    }
  }
}