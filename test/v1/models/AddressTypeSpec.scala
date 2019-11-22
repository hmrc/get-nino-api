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

class AddressTypeSpec extends WordSpec with Matchers {

  val residentialJson = JsString(Residential.value)
  val correspondenceJson = JsString(Correspondence.value)
  val invalidJson = JsString("Error")

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

        "throw an exception" in {

          val exception = intercept[IllegalArgumentException] {
            invalidJson.as[AddressType]
          }

          exception.getMessage shouldBe "Invalid AddressType"
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
        Json.toJson(Residential) shouldBe residentialJson
      }

      "return a Correspondence object" in {
        Json.toJson(Correspondence) shouldBe correspondenceJson
      }
    }
  }

  "AddressType.apply" when {

    "provided with a valid AddressType" should {

      "return a Residential object" in {
        AddressType(Residential.value) shouldBe Residential
      }

      "return a Correspondence object" in {
        AddressType(Correspondence.value) shouldBe Correspondence
      }
    }
  }

  "AddressType.unapply" when {

    "provided with a valid AddressType" should {

      "return Residential AddressType value" in {
        AddressType.unapply(Residential) shouldBe Residential.value
      }

      "return Correspondence AddressType value" in {
        AddressType.unapply(Correspondence) shouldBe Correspondence.value
      }
    }
  }
}
