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

        "throw an exception" in {

          val exception = intercept[IllegalArgumentException] {
            invalidJson.as[AddressLine]
          }

          exception.getMessage shouldBe "Invalid AddressLine"
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

  "AddressLine.regexCheck" when {

    "provided with an AddressLine" which {

      val addressLineStartingWithLetter: AddressLine = AddressLine("Test Avenue")
      val maxLimitAddressLine = AddressLine("This address line is 35 characterss")
      val minLimitAddressLine = AddressLine("Add")
      val tooLongAddressLine = "This address is unfortunately over 35 characters"

      "is within the character length limit" should {
        "return an AddressLine object" in {
          AddressLine.regexCheck(addressLineStartingWithLetter.addressLine) shouldBe addressLineStartingWithLetter
        }
      }

      "is on the character length limit" should {
        "return an AddressLine object" in {
          AddressLine.regexCheck(maxLimitAddressLine.addressLine) shouldBe maxLimitAddressLine
        }
      }

      "is on the minimum character length limit" should {
        "return an AddressLine object" in {
          AddressLine.regexCheck(minLimitAddressLine.addressLine) shouldBe minLimitAddressLine
        }
      }

      "starts with a number" should {
        "return an AddressLine object" in {
          AddressLine.regexCheck(addressLineStartingWithNumber.addressLine) shouldBe addressLineStartingWithNumber
        }
      }

      "is too long" should {
        "return an exception" in {

          val exception = intercept[IllegalArgumentException] {
            AddressLine.regexCheck(tooLongAddressLine)
          }

          exception.getMessage shouldBe "Invalid AddressLine"
        }
      }

      "starts with an invalid character" should {
        "return an exception" in {

          val exception = intercept[IllegalArgumentException] {
            AddressLine.regexCheck(invalidJson.value)
          }

          exception.getMessage shouldBe "Invalid AddressLine"

        }
      }
    }
  }
}


