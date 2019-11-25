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
      postcode = Some("TE5 5LN"),
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
}
