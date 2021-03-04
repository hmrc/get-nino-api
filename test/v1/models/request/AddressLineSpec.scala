/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.libs.json.{JsString, Json}

class AddressLineSpec extends WordSpec with Matchers {

  val addressLineJson = JsString("1234 Test Avenue")
  val addressLineStartingWithNumber: AddressLine = AddressLine("1234 Test Avenue")
  val invalidJson = JsString("~~~~~ Error invalid json ~~~~~~")

  "AddressLine.read" when {

    "provided with valid JSON" should {

      "return a valid AddressLine" in {
        addressLineJson.as[AddressLine] shouldBe addressLineStartingWithNumber
      }

      "an invalid AddressLine is provided" should {

        "throw an JsResultException" in {

          invalidJson.validate[AddressLine].isError shouldBe true
        }
      }
    }

    "provided with invalid JSON" should {
      "throw a JsResultException" in {

        val json = Json.obj("bad" -> "json")

        json.validate[AddressLine].isError shouldBe true

      }
    }
  }

  "AddressLine.writes" when {

    "provided with a valid AddressLine" should {

      "return an AddressLine object" in {
        Json.toJson(addressLineStartingWithNumber) shouldBe addressLineJson
      }
    }
  }

  "AddressLine.addressLineValidation" when {

    "provided with an AddressLine" which {

      "is within the character length limit" should {
        "return true" in {

          val addressLineStartingWithLetter: AddressLine = AddressLine("Test Avenue")

          AddressLine.addressLineValidation(addressLineStartingWithLetter.addressLine) shouldBe true
        }
      }

      "is on the character length limit" should {
        "return true" in {

          val maxLimitAddressLine = AddressLine("This address line is 35 characterss")

          AddressLine.addressLineValidation(maxLimitAddressLine.addressLine) shouldBe true
        }
      }

      "is on the minimum character length limit" should {
        "return true" in {

          val minLimitAddressLine = AddressLine("Add")

          AddressLine.addressLineValidation(minLimitAddressLine.addressLine) shouldBe true
        }
      }

      "starts with a number" should {
        "return true" in {
          AddressLine.addressLineValidation(addressLineStartingWithNumber.addressLine) shouldBe true
        }
      }

      "is too long" should {
        "return false" in {

          val tooLongAddressLine = "This address is unfortunately over 35 characters"

          AddressLine.addressLineValidation(tooLongAddressLine) shouldBe false
        }
      }

      "starts with an invalid character" should {
        "return false" in {

          AddressLine.addressLineValidation(invalidJson.value) shouldBe false

        }
      }
    }
  }
}


