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

package v1.utils

import play.api.libs.json.Json
import play.api.mvc.AnyContentAsJson
import play.api.test.FakeRequest
import support.UnitSpec
import utils.NinoApplicationTestData.{maxRegisterNinoRequestJson, maxRegisterNinoRequestModel}
import v1.models.errors.{InvalidBodyTypeError, JsonValidationError}
import v1.models.request.NinoApplication

class JsonBodyUtilSpec extends UnitSpec {

  object testUtil extends JsonBodyUtil

  "parsedBodyJson" should {
    "return a successful model" when {
      "the request is valid" in {
        implicit val request: FakeRequest[AnyContentAsJson] = FakeRequest()
          .withMethod("POST")
          .withJsonBody(maxRegisterNinoRequestJson(false))

        testUtil.parsedJsonBody[NinoApplication] shouldBe Right(maxRegisterNinoRequestModel)
      }
    }
    "return an error" when {
      "the request body is not json" in {
        implicit val request: FakeRequest[String] = FakeRequest()
          .withMethod("POST")
          .withBody("this is just a string, and not json")

        testUtil.parsedJsonBody[NinoApplication] shouldBe Left(InvalidBodyTypeError)
      }
      "the json body cannot be validated" in {
        implicit val request: FakeRequest[AnyContentAsJson] = FakeRequest()
          .withMethod("POST")
          .withJsonBody(Json.obj(
            "thisIs" -> "validJson",
            "putNotThe" -> "correctJson"
          ))

        testUtil.parsedJsonBody[NinoApplication] shouldBe Left(JsonValidationError)
      }
    }
  }

}
