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

package v1.testOnly.controllers

import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.AnyContentAsJson
import play.api.test.FakeRequest
import play.api.test.Helpers._
import support.ControllerBaseSpec

class FeatureSwitchControllerSpec extends ControllerBaseSpec {

  lazy val controller = new FeatureSwitchController(mockAppConfig, stubControllerComponents())

  "Calling the .update action" should {

    "return an updated set of feature switches" when {

      "a successful call is made" which {
        mockAppConfig.features.useDesStub(false)

        val requestBody: JsValue = Json.obj(
          "useDesStub" -> true
        )

        val request: FakeRequest[AnyContentAsJson] = FakeRequest()
          .withMethod("POST")
          .withJsonBody(requestBody)

        val result = controller.update(request)

        "returns a 200 status" in {
          status(result) shouldBe Status.OK
        }

        "the body contains the updated json" in {
          contentAsJson(result) shouldBe requestBody
        }
      }
    }
    "return a non-updated set of feature switches" when {

      "a body that is not valid json is passed in" which {
        mockAppConfig.features.useDesStub(false)

        val requestBody = "not valid json body"

        val request = FakeRequest()
          .withMethod("POST")
          .withBody(requestBody)

        val result = controller.update()(request)

        "returns a 200" in {
          status(result) shouldBe Status.OK
        }

        "returns an unedited set of switches" in {
          contentAsJson(result) shouldBe Json.obj(
            "useDesStub" -> false
          )
        }
      }

      "a valid json body is passed in, but is not the correct model" which {
        mockAppConfig.features.useDesStub(false)

        val requestBody = Json.obj(
          "someField" -> "someValue"
        )

        val request = FakeRequest()
          .withMethod("POST")
          .withJsonBody(requestBody)

        val result = controller.update.apply(request)

        "returns a 200" in {
          status(result) shouldBe Status.OK
        }

        "returns an unedited set of switches" in {
          contentAsJson(result) shouldBe Json.obj(
            "useDesStub" -> false
          )
        }
      }
    }
  }
}
