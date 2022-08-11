/*
 * Copyright 2022 HM Revenue & Customs
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
import play.api.test.Helpers._
import support.ControllerBaseSpec
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.auth.core.{AuthConnector, InvalidBearerToken}
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames}
import utils.NinoApplicationTestData.{maxRegisterNinoRequestJson, maxRegisterNinoRequestModel}
import v1.controllers.predicates.{CorrelationIdPredicate, OriginatorIdPredicate, PrivilegedApplicationPredicate}
import v1.models.errors.{InvalidBodyTypeError, ErrorResponse => NinoError, JsonValidationError => NinoJsonValidationError}
import v1.models.request.NinoApplication
import v1.services.DesService

import scala.concurrent.{ExecutionContext, Future}

class RegisterNinoControllerSpec extends ControllerBaseSpec {

  implicit val ec: ExecutionContext = stubControllerComponents().executionContext

  val mockAuth: AuthConnector = mock[AuthConnector]

  val mockService: DesService = mock[DesService]

  val mockPrivilegedPredicate: PrivilegedApplicationPredicate = new PrivilegedApplicationPredicate(
    mockAuth,
    stubControllerComponents(),
    ec
  )

  val mockOriginatorPredicate: OriginatorIdPredicate = new OriginatorIdPredicate(
    ec,
    stubControllerComponents()
  )

  val mockCorrelationPredicate: CorrelationIdPredicate = new CorrelationIdPredicate(
    ec,
    stubControllerComponents()
  )

  val controller: RegisterNinoController = new RegisterNinoController(
    stubControllerComponents(),
    mockService,
    mockPrivilegedPredicate,
    mockCorrelationPredicate,
    mockOriginatorPredicate,
    mockAppConfig
  )

  "Calling the register action" when {

    "the request is authorised" should {

      "the request is valid" should {

        "return 202" in {

          (mockAuth
            .authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *, *)
            .returns(Future.successful((): Unit))

          (mockService
            .registerNino(_: NinoApplication)(_: HeaderCarrier, _: ExecutionContext))
            .expects(maxRegisterNinoRequestModel, *, *)
            .returns(Future.successful(Right(())))

          val request = fakeRequest
            .withHeaders(
              "Accept"               -> "application/vnd.hmrc.1.0+json",
              "OriginatorId"         -> "DA2_DWP_REG",
              "CorrelationId"        -> "DBABB1dB-7DED-b5Dd-19ce-5168C9E8fff9",
              HeaderNames.xRequestId -> "1234567890",
              HeaderNames.xSessionId -> "0987654321"
            )
            .withMethod("POST")
            .withJsonBody(maxRegisterNinoRequestJson(false))

          val result = controller.register()(request)

          status(result) shouldBe Status.ACCEPTED

          MDC.get(HeaderNames.xRequestId) shouldBe "1234567890"
          MDC.get(HeaderNames.xSessionId) shouldBe "0987654321"
        }
      }

      "when the request body is not json" should {

        "return 415" in {

          (mockAuth
            .authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *, *)
            .returning(Future.successful((): Unit))

          val request = fakeRequest
            .withHeaders(
              "Accept"        -> "application/vnd.hmrc.1.0+json",
              "CorrelationId" -> "DBABB1dB-7DED-b5Dd-19ce-5168C9E8fff9",
              "OriginatorId"  -> "DA2_DWP_REG"
            )
            .withMethod("POST")

          val result = controller.register()(request)

          status(result)        shouldBe Status.UNSUPPORTED_MEDIA_TYPE
          contentAsJson(result) shouldBe contentAsJson(Future.successful(InvalidBodyTypeError.result))
        }
      }

      "when the request body is valid json, but cannot be validated as a NinoApplication" should {
        "return a 400" in {

          (mockAuth
            .authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *, *)
            .returning(Future.successful((): Unit))

          val request = fakeRequest
            .withHeaders(
              "Accept"        -> "application/vnd.hmrc.1.0+json",
              "CorrelationId" -> "DBABB1dB-7DED-b5Dd-19ce-5168C9E8fff9",
              "OriginatorId"  -> "DA2_DWP_REG"
            )
            .withMethod("POST")
            .withJsonBody(
              Json.obj(
                "aField" -> "aValue"
              )
            )

          val result = controller.register()(request)

          status(result)        shouldBe Status.BAD_REQUEST
          contentAsJson(result) shouldBe Json.toJson(
            NinoJsonValidationError(
              JsError(
                Seq("addresses", "entryDate", "gender", "officeNumber", "nino", "birthDate", "names").map { field =>
                  (JsPath \ field, Seq(JavaJsonValidationError("error.path.missing")))
                }
              )
            )
          )(NinoError.validationWrites)
        }
      }

    }

    "the request is not authorised" should {

      "return an unauthorised response" in {

        (mockAuth
          .authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *)
          .returns(Future.failed(InvalidBearerToken()))

        val request = fakeRequest
          .withHeaders(
            "Accept"               -> "application/vnd.hmrc.1.0+json",
            HeaderNames.xRequestId -> "1234567890",
            HeaderNames.xSessionId -> "0987654321"
          )
          .withMethod("POST")
          .withJsonBody(maxRegisterNinoRequestJson(false))

        val result = controller.register()(request)

        status(result) shouldBe Status.UNAUTHORIZED
      }
    }
  }
}
