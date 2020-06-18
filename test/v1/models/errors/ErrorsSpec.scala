/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.models.errors

import play.api.libs.json.{JsResultException, JsValue, Json}
import support.UnitSpec

class ErrorsSpec extends UnitSpec {
  "Serialising a single error into JSON" should {
    "generate the correct JSON" in {
      val expected = Json.parse(
        """
          |{
          |  "code": "SERVER_ERROR",
          |  "message": "Service unavailable."
          |}
        """.stripMargin
      )

      val error = ServiceUnavailableError

      val result = Json.toJson(error)

      result shouldBe expected
    }
  }

  "Serialising no errors into JSON" should {
    "generate the correct JSON" in {

      val errors = Errors(Seq())

      val result: JsValue = Json.toJson(errors)

      val expected = Json.parse(
        """
          |{
          |  "errors": []
          |}
        """.stripMargin)

      result shouldBe expected
    }
  }

  "Serialising multiple errors into JSON" should {
    "generate the correct JSON" in {
      val expected = Json.parse(
        """
          |{
          |  "errors": [
          |    {
          |      "code": "SERVER_ERROR",
          |      "message": "Service unavailable."
          |    },
          |    {
          |      "code": "MATCHING_RESOURCE_NOT_FOUND",
          |      "message": "Matching resource not found"
          |    }
          |  ]
          |}
        """.stripMargin
      )

      val errors = Errors(
        Seq(
          ServiceUnavailableError,
          NotFoundError
        )
      )

      val result = Json.toJson(errors)

      result shouldBe expected
    }
  }
}
