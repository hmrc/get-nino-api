/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.libs.json.{JsString, Json}

class AddressTypeSpec extends AnyWordSpec with Matchers {

  val residentialJson: JsString    = JsString(Residential.value)
  val correspondenceJson: JsString = JsString(Correspondence.value)
  val invalidJson: JsString        = JsString("Error")

  "AddressType.read" when {

    "provided with valid JSON" when {

      "a valid AddressType is provided" should {

        "return an residential object" in {
          residentialJson.as[AddressType] shouldBe Residential
        }

        "return a correspondence object" in {
          correspondenceJson.as[AddressType] shouldBe Correspondence
        }
      }

      "an invalid AddressType is provided" should {

        "throw a JsResultException" in {

          invalidJson.validate[AddressType].isError shouldBe true

        }
      }
    }

    "provided with invalid JSON" should {
      "throw a JsResultException" in {

        val json = Json.obj("bad" -> "json")

        json.validate[AddressType].isError shouldBe true

      }
    }
  }

  "AddressType.writes" when {

    "provided with a valid AddressType" should {

      "return a Residential object" in {
        Residential shouldBe residentialJson.as[AddressType]
      }

      "return a Correspondence object" in {
        Correspondence shouldBe correspondenceJson.as[AddressType]
      }
    }
  }

  "AddressType.regexCheck" when {

    "provided with a valid AddressType of Residential" should {

      "return a true" in {
        AddressType.validAddressTypeCheck(Residential.value) shouldBe true
      }
    }

    "provided with a valid AddressType of Correspondence" should {

      "return a true" in {
        AddressType.validAddressTypeCheck(Correspondence.value) shouldBe true
      }
    }

    "provided with an invalid address AddressType" should {

      "return false" in {

        AddressType.validAddressTypeCheck("Invalid value") shouldBe false
      }
    }
  }
}
