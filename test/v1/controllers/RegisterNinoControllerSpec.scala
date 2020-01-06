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

package v1.controllers

import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._
import support.ControllerBaseSpec
import uk.gov.hmrc.http.HeaderCarrier
import utils.NinoApplicationTestData.{maxRegisterNinoRequestJson, maxRegisterNinoRequestModel}
import v1.models.errors.{InvalidBodyTypeError, JsonValidationError}
import v1.models.request.NinoApplication
import v1.models.response.DesResponseModel
import v1.services.DesService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class RegisterNinoControllerSpec extends ControllerBaseSpec {

  val mockService: DesService = mock[DesService]
  val controller: RegisterNinoController = new RegisterNinoController(stubControllerComponents(), mockService)

  "Calling the register action" when {

    "the request is valid" should {
      "return 200" in {
        (mockService.registerNino(_: NinoApplication)(_: HeaderCarrier, _: ExecutionContext))
          .expects(maxRegisterNinoRequestModel, *, *)
          .returning(Future.successful(Right(DesResponseModel("A response"))))

        val result = controller.register()(
          fakeRequest
            .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")
            .withMethod("POST")
            .withJsonBody(maxRegisterNinoRequestJson(false))
        )

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.obj("message"->"A response")
      }
    }

    "the request body is not json" should {
      "return 400" in {
        val result = controller.register()(
          fakeRequest
            .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")
            .withMethod("POST")
        )

        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(InvalidBodyTypeError)
      }
    }

    "the request body is valid json, but cannot be validated as a NinoApplication" should {
      "return a 400" in {
        val result = controller.register()(
          fakeRequest
            .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")
            .withMethod("POST")
            .withJsonBody(Json.obj(
              "aField" -> "aValue"
            ))
        )

        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(JsonValidationError)
      }
    }

  }
}
