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

class PostcodeSpec extends AnyWordSpec with Matchers {

  val postcodeModel: Postcode = Postcode("TF3 4NT")
  val postcodeJson: JsString  = JsString("TF3 4NT")
  val invalidJson: JsString   = JsString("!Invalid Json")

  "PostCode.reads" when {

    "provided with valid JSON" should {

      "return a valid postcode" in {
        postcodeJson.as[Postcode] shouldBe postcodeModel
      }

      "an invalid Postcode be provided" should {

        "throw an JsResultException" in {

          invalidJson.validate[Postcode].isError shouldBe true

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
        "return true" in {

          val postcodeString: Postcode = Postcode("QW3 8RT")

          Postcode.regexCheckValidation(postcodeString.postCode) shouldBe true
        }
      }

      "starts with an invalid character" should {
        "return false" in {

          val postcodeString = "!QW3 8RT"

          Postcode.regexCheckValidation(postcodeString) shouldBe false

        }
      }

      "has two groupings of characters" should {
        "return true" in {

          val postcodeString = "QW3 8RT"

          Postcode.regexCheckValidation(postcodeString) shouldBe true

        }
      }

      "has one grouping of characters" should {
        "return false" in {

          val postcodeString = "SA99OP"

          Postcode.regexCheckValidation(postcodeString) shouldBe false

        }
      }

      "has an invalid character as the second character within the postcode" should {
        "return false" in {

          val postcodeString = "S!99 8TT"

          Postcode.regexCheckValidation(postcodeString) shouldBe false

        }
      }
    }
  }
}
