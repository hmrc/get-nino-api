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

class PostcodeSpec extends WordSpec with Matchers {

  val postcodeModel: Postcode = Postcode("TF3 4NT")
  val postcodeJson: JsString = JsString("TF3 4NT")
  val invalidJson: JsString = JsString("!Invalid Json")

  "PostCode.reads" when {

    "provided with valid JSON" should {

      "return a valid postcode" in {
        postcodeJson.as[Postcode] shouldBe postcodeModel
      }

      "an invalid Postcode be provided" should {

        "throw an exception" in {

          val exception = intercept[IllegalArgumentException] {
            invalidJson.as[Postcode]
          }

          exception.getMessage shouldBe "Invalid Postcode"
        }
      }
    }

    "provided with invalid JSON" should {
      "throw a JsResultException" in {

        val json = Json.obj("bad" -> "!json")

        json.validate[Postcode].isError shouldBe true

      }
    }
  }

  "Postcode.writes" when {

    "provided with a valid postcode" should {

      "return a valid postcode object" in {
        Json.toJson(postcodeModel) shouldBe postcodeJson
      }
    }
  }

  "Postcode regex check" when {

    "provided with a postcode" which {

      "starts with a valid character" should {
        "return a postcode object" in {

          val postcodeString = "QW3 8RT"

          Postcode.regexCheck(postcodeString) shouldBe Postcode(postcodeString)
        }
      }

      "starts with an invalid character" should {
        "return an exception" in {

          val postcodeString = "!QW3 8RT"

          val exception = intercept[IllegalArgumentException] {
            Postcode.regexCheck(postcodeString)
          }

          exception.getMessage shouldBe "Invalid Postcode"

        }
      }

      "has two groupings of characters" should {
        "return a postcode object" in {

          val postcodeString = "QW3 8RT"

          Postcode.regexCheck(postcodeString) shouldBe Postcode(postcodeString)

        }
      }

      "has one grouping of characters" should {
        "return an exception" in {

          val postcodeString = "SA99"

          val exception = intercept[IllegalArgumentException] {
            Postcode.regexCheck(postcodeString)
          }

          exception.getMessage shouldBe "Invalid Postcode"
        }
      }

      "has an invalid character as the second character within the postcode" should {
        "return an exception" in {

          val postcodeString = "S!99 8TT"

          val exception = intercept[IllegalArgumentException] {
            Postcode.regexCheck(postcodeString)
          }

          exception.getMessage shouldBe "Invalid Postcode"

        }
      }
    }
  }
}
