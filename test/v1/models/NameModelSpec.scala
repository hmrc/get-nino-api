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

import play.api.libs.json.{JsObject, JsResultException, Json}
import support.UnitSpec

class NameModelSpec extends UnitSpec {

  val validJsonWrite: JsObject = Json.obj(
    "title" -> "MR",
    "forename" -> "Dovah",
    "secondForename" -> "Dragon",
    "surname" -> "Kin",
    "startDate" -> "2020-10-10",
    "endDate" -> "2020-10-10"
  )

  val validJsonRead: JsObject = Json.obj(
    "title" -> "MR",
    "forename" -> "Dovah",
    "secondForename" -> "Dragon",
    "surname" -> "Kin",
    "startDate" -> "10-10-2020",
    "endDate" -> "10-10-2020"
  )

  val validModel = NameModel(
    Some("MR"),
    Some("Dovah"),
    Some("Dragon"),
    "Kin",
    DateModel("10-10-2020"),
    Some(DateModel("10-10-2020"))
  )

  "NameModelSpec" should {
    "correctly parse to Json" in {
      Json.toJson(validModel) shouldBe validJsonWrite
    }
    "correctly parse from Json" in {
      validJsonRead.as[NameModel] shouldBe validModel
    }

    "throw an error when parsing from json" when {
      "the Title field fails validation" in {
        val dodgeyJson = Json.obj(
          "title" -> "FAKE",
          "forename" -> "Dovah",
          "secondForename" -> "Dragon",
          "surname" -> "Kin",
          "startDate" -> "10-10-2020"
        )

        val exceptionResult = intercept[JsResultException] {
          dodgeyJson.as[NameModel]
        }

        exceptionResult.getMessage should include("Title failed validation. Must be one of: 'NOT KNOWN, MR, MRS, MISS, MS, DR, REV'")
      }
      "the forename field fails validation" in {
        val dodgeyJsonCharacters = Json.obj(
          "forename" -> "{}:@~NOTANAME:@~<>?}",
          "secondForename" -> "Dragon",
          "surname" -> "Kin",
          "startDate" -> "10-10-2020"
        )
        val dodgeyJsonLength = Json.obj(
          "forename" -> "DOVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH",
          "secondForename" -> "Dragon",
          "surname" -> "Kin",
          "startDate" -> "10-10-2020"
        )

        val expectedMessage = "Unable to validate forename. Ensure it matches the regex."

        val exceptionResultInvalidCharacters = intercept[JsResultException] {
          dodgeyJsonCharacters.as[NameModel]
        }
        val exceptionResultInvalidLength = intercept[JsResultException] {
          dodgeyJsonLength.as[NameModel]
        }

        exceptionResultInvalidCharacters.getMessage should include(expectedMessage)
        exceptionResultInvalidLength.getMessage should include(expectedMessage)
      }
      "the secondForename field fails validation" in {
        val dodgeyJsonCharacters = Json.obj(
          "title" -> "MR",
          "secondForename" -> ":@~{}<>?Dragon:~@{}<>?",
          "surname" -> "Kin",
          "startDate" -> "10-10-2020"
        )
        val dodgeyJsonLength = Json.obj(
          "title" -> "MR",
          "secondForename" -> "Dragoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooon",
          "surname" -> "Kin",
          "startDate" -> "10-10-2020"
        )

        val expectedMessage = "Unable to validate second forename. Ensure it matches the regex."

        val exceptionResultInvalidCharacters = intercept[JsResultException] {
          dodgeyJsonCharacters.as[NameModel]
        }
        val exceptionResultInvalidLength = intercept[JsResultException] {
          dodgeyJsonLength.as[NameModel]
        }

        exceptionResultInvalidCharacters.getMessage should include(expectedMessage)
        exceptionResultInvalidLength.getMessage should include(expectedMessage)
      }
      "the surname field fails validation" in {
        val dodgeyJsonCharacters = Json.obj(
          "title" -> "MR",
          "forename" -> "Dovah",
          "surname" -> ":@~{}<>?Kin:@~{}<>?",
          "startDate" -> "10-10-2020"
        )
        val dodgeyJsonLength = Json.obj(
          "title" -> "MR",
          "forename" -> "Dovah",
          "surname" -> "Kiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiin",
          "startDate" -> "10-10-2020"
        )

        val expectedMessage = "Unable to validate surname. Ensure it matches the regex."

        val exceptionResultInvalidCharacters = intercept[JsResultException] {
          dodgeyJsonCharacters.as[NameModel]
        }
        val exceptionResultInvalidLength = intercept[JsResultException] {
          dodgeyJsonLength.as[NameModel]
        }

        exceptionResultInvalidCharacters.getMessage should include(expectedMessage)
        exceptionResultInvalidLength.getMessage should include(expectedMessage)
      }
    }
  }
}
