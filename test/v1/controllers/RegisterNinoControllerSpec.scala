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

import org.slf4j.MDC
import play.api.http.Status
import play.api.libs.json.{JsError, JsPath, Json, JsonValidationError => JavaJsonValidationError}
import play.api.mvc.{AnyContent, Request}
import play.api.test.Helpers._
import support.ControllerBaseSpec
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames}
import utils.NinoApplicationTestData.{maxRegisterNinoRequestJson, maxRegisterNinoRequestModel}
import v1.controllers.predicates.PrivilegedApplicationPredicate
import v1.models.errors.{BadRequestError, InvalidBodyTypeError, Error => NinoError, JsonValidationError => NinoJsonValidationError}
import v1.models.request.NinoApplication
import v1.models.response.DesResponseModel
import v1.services.DesService

import scala.concurrent.{ExecutionContext, Future}

class RegisterNinoControllerSpec extends ControllerBaseSpec {

  implicit val ec: ExecutionContext = stubControllerComponents().executionContext

  val mockAuth: AuthConnector = mock[AuthConnector]

  val mockService: DesService = mock[DesService]

  val mockPredicate = new PrivilegedApplicationPredicate(
    mockAuth,
    stubControllerComponents(),
    ec
  )

  val controller: RegisterNinoController = new RegisterNinoController(stubControllerComponents(), mockService, mockPredicate)

  private def mockAuthCall(request: Request[AnyContent]) = (mockAuth.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
    .expects(*, *, *, *)
    .returns(Future.successful()) anyNumberOfTimes() atLeastOnce()

  "Calling the register action" when {

    "the request is valid" should {
      "return 200" in {
        (mockService.registerNino(_: NinoApplication)(_: HeaderCarrier, _: ExecutionContext))
          .expects(maxRegisterNinoRequestModel, *, *)
          .returning(Future.successful(Right(DesResponseModel("A response"))))

        val request = fakeRequest
          .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", HeaderNames.xRequestId -> "1234567890", HeaderNames.xSessionId -> "0987654321")
          .withMethod("POST")
          .withJsonBody(maxRegisterNinoRequestJson(false))

        mockAuthCall(request)

        val result = controller.register()(request)

        println(Console.YELLOW + "MDC in test 1: " + MDC.getCopyOfContextMap + Console.RESET)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.obj("message" -> "A response")

        println(Console.YELLOW + "MDC in test 2: " + MDC.getCopyOfContextMap + Console.RESET)

        MDC.get(HeaderNames.xRequestId) shouldBe "1234567890"
        MDC.get(HeaderNames.xSessionId) shouldBe "0987654321"
      }
    }

    "the request body is not json" should {
      "return 400" in {
        val request = fakeRequest
          .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")
          .withMethod("POST")

        mockAuthCall(request)

        val result = controller.register()(request)

        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(InvalidBodyTypeError)
      }
    }

    "the request body is valid json, but cannot be validated as a NinoApplication" should {
      "return a 400" in {
        val request = fakeRequest
          .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")
          .withMethod("POST")
          .withJsonBody(Json.obj(
            "aField" -> "aValue"
          ))

        mockAuthCall(request)

        val result = controller.register()(request)

        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(new NinoJsonValidationError(JsError(
          Seq("address", "entryDate", "name", "gender", "officeNumber", "nino", "birthDate").map { field =>
            (JsPath \ field, Seq(JavaJsonValidationError("error.path.missing")))
          }
        )))(NinoError.validationWrites)
      }
    }

  }

  "Calling convertJsErrorsToReadableFormat" should {

    "return the error passed in" when {

      "it is not a JsError" in {
        val nonJsError: NinoError = BadRequestError

        controller.convertJsErrorsToReadableFormat(nonJsError) shouldBe Json.toJson(nonJsError)
      }
    }

    "return a readable list of JsErrors" when {

      val jsError1 = new JavaJsonValidationError(Seq("Some issue"))
      val jsError2 = new JavaJsonValidationError(Seq("Some other issue"))

      val jsPath1 = JsPath \ "aThing"
      val jsPath2 = JsPath \ "anotherThing"

      val seqOfErrors: Seq[JavaJsonValidationError] = Seq(
        jsError1, jsError2
      )

      val fullJsErrorList: Seq[(JsPath, Seq[JavaJsonValidationError])] = Seq(
        jsPath1 -> seqOfErrors,
        jsPath2 -> seqOfErrors
      )

      val jsErrors = JsError(fullJsErrorList)

      "the error passed in contains a JsError type" in {
        val jsErrorModel = new NinoJsonValidationError(jsErrors)

        controller.convertJsErrorsToReadableFormat(jsErrorModel) shouldBe Json.obj(
          "code" -> "JSON_VALIDATION_ERROR",
          "message" -> "The provided JSON was unable to be validated as the selected model.",
          "errors" -> Json.arr(
            Json.obj(
              "code" -> "BAD_REQUEST",
              "message" -> "Some issue",
              "path" -> "aThing"
            ),
            Json.obj(
              "code" -> "BAD_REQUEST",
              "message" -> "Some other issue",
              "path" -> "aThing"
            ),
            Json.obj(
              "code" -> "BAD_REQUEST",
              "message" -> "Some issue",
              "path" -> "anotherThing"
            ),
            Json.obj(
              "code" -> "BAD_REQUEST",
              "message" -> "Some other issue",
              "path" -> "anotherThing"
            )
          )
        )
      }
    }
  }
}
