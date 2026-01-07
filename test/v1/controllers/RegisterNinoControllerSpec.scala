/*
 * Copyright 2026 HM Revenue & Customs
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

import mocks.MockAppConfig
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import org.slf4j.MDC
import play.api.libs.json.{JsonValidationError => JavaJsonValidationError, _}
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import support.ControllerBaseSpec
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http._
import utils.NinoApplicationTestData._
import v1.controllers.predicates._
import v1.models.errors.{JsonValidationError => NinoJsonValidationError, _}
import v1.services.DesService

import scala.concurrent._

class RegisterNinoControllerSpec extends ControllerBaseSpec with MockAppConfig {

  private implicit val executionContext: ExecutionContext = stubControllerComponents().executionContext

  private val mockAuthConnector: AuthConnector = mock[AuthConnector]

  private val mockDesService: DesService = mock[DesService]

  private val privilegedApplicationPredicate: PrivilegedApplicationPredicate = new PrivilegedApplicationPredicate(
    authConnector = mockAuthConnector,
    controllerComponents = stubControllerComponents(),
    executionContext = executionContext
  )

  private val originatorIdPredicate: OriginatorIdPredicate = new OriginatorIdPredicate(
    ec = executionContext,
    controllerComponents = stubControllerComponents()
  )

  private val correlationIdPredicate: CorrelationIdPredicate = new CorrelationIdPredicate(
    ec = executionContext,
    controllerComponents = stubControllerComponents()
  )

  private val controller: RegisterNinoController = new RegisterNinoController(
    cc = stubControllerComponents(),
    desService = mockDesService,
    privilegedApplicationPredicate = privilegedApplicationPredicate,
    correlationIdPredicate = correlationIdPredicate,
    originatorIdPredicate = originatorIdPredicate,
    appConfig = mockAppConfig
  )

  private class Setup(logDwpJson: Boolean) {
    MockedAppConfig.logDwpJson().thenReturn(logDwpJson)
  }

  "RegisterNinoController" when {
    ".register" when {
      "the request is authorised" when {
        "the request is valid" when {
          "the service call is successful" should {
            "return 202 ACCEPTED" in new Setup(logDwpJson = true) {
              when(
                mockAuthConnector
                  .authorise(any[Predicate](), any[Retrieval[Unit]]())(any[HeaderCarrier](), any[ExecutionContext]())
              )
                .thenReturn(Future.successful((): Unit))

              when(
                mockDesService
                  .registerNino(ArgumentMatchers.eq(maxRegisterNinoRequestModel))(
                    any[HeaderCarrier](),
                    any[ExecutionContext]()
                  )
              )
                .thenReturn(Future.successful(Right(())))

              val request: FakeRequest[AnyContent] = fakeRequest
                .withHeaders(
                  "Accept"               -> "application/vnd.hmrc.1.0+json",
                  "OriginatorId"         -> "DA2_DWP_REG",
                  "CorrelationId"        -> "DBABB1dB-7DED-b5Dd-19ce-5168C9E8fff9",
                  HeaderNames.xRequestId -> "1234567890",
                  HeaderNames.xSessionId -> "0987654321"
                )
                .withMethod("POST")
                .withJsonBody(maxRegisterNinoRequestJson(false))

              val result: Future[Result] = controller.register()(request)

              status(result) shouldBe ACCEPTED

              MDC.get(HeaderNames.xRequestId) shouldBe "1234567890"
              MDC.get(HeaderNames.xSessionId) shouldBe "0987654321"
            }
          }

          "the service call returns 503 SERVICE_UNAVAILABLE with ServiceUnavailableError response" should {
            "return 503 SERVICE_UNAVAILABLE with ServiceUnavailableError response" in new Setup(logDwpJson = false) {
              when(
                mockAuthConnector
                  .authorise(any[Predicate](), any[Retrieval[Unit]]())(any[HeaderCarrier](), any[ExecutionContext]())
              )
                .thenReturn(Future.successful((): Unit))

              when(
                mockDesService
                  .registerNino(ArgumentMatchers.eq(maxRegisterNinoRequestModel))(
                    any[HeaderCarrier](),
                    any[ExecutionContext]()
                  )
              )
                .thenReturn(Future.successful(Left(ServiceUnavailableError)))

              val request: FakeRequest[AnyContent] = fakeRequest
                .withHeaders(
                  "Accept"               -> "application/vnd.hmrc.1.0+json",
                  "OriginatorId"         -> "DA2_DWP_REG",
                  "CorrelationId"        -> "DBABB1dB-7DED-b5Dd-19ce-5168C9E8fff9",
                  HeaderNames.xRequestId -> "1234567890",
                  HeaderNames.xSessionId -> "0987654321"
                )
                .withMethod("POST")
                .withJsonBody(maxRegisterNinoRequestJson(false))

              val result: Future[Result] = controller.register()(request)

              status(result)        shouldBe SERVICE_UNAVAILABLE
              contentAsJson(result) shouldBe contentAsJson(Future.successful(ServiceUnavailableError.result))
            }
          }
        }

        "the request" that {
          "is supplied has originator ID absent" should {
            "return 400 BAD_REQUEST with OriginatorIdMissingError response" in {
              when(
                mockAuthConnector
                  .authorise(any[Predicate](), any[Retrieval[Unit]]())(any[HeaderCarrier](), any[ExecutionContext]())
              )
                .thenReturn(Future.successful((): Unit))

              val request: FakeRequest[AnyContent] = fakeRequest
                .withHeaders(
                  "Accept"        -> "application/vnd.hmrc.1.0+json",
                  "CorrelationId" -> "DBABB1dB-7DED-b5Dd-19ce-5168C9E8fff9"
                )
                .withMethod("POST")
                .withJsonBody(maxRegisterNinoRequestJson(false))

              val result: Future[Result] = controller.register()(request)

              status(result)        shouldBe BAD_REQUEST
              contentAsJson(result) shouldBe contentAsJson(Future.successful(OriginatorIdMissingError.result))
            }
          }

          "is supplied has an incorrect originator ID" should {
            "return 400 BAD_REQUEST with OriginatorIdIncorrectError response" in {
              when(
                mockAuthConnector
                  .authorise(any[Predicate](), any[Retrieval[Unit]]())(any[HeaderCarrier](), any[ExecutionContext]())
              )
                .thenReturn(Future.successful((): Unit))

              val request: FakeRequest[AnyContent] = fakeRequest
                .withHeaders(
                  "Accept"        -> "application/vnd.hmrc.1.0+json",
                  "OriginatorId"  -> "id",
                  "CorrelationId" -> "DBABB1dB-7DED-b5Dd-19ce-5168C9E8fff9"
                )
                .withMethod("POST")
                .withJsonBody(maxRegisterNinoRequestJson(false))

              val result: Future[Result] = controller.register()(request)

              status(result)        shouldBe BAD_REQUEST
              contentAsJson(result) shouldBe contentAsJson(Future.successful(OriginatorIdIncorrectError.result))
            }
          }

          "is supplied has correlation ID absent" should {
            "return 400 BAD_REQUEST with CorrelationIdMissingError response" in {
              when(
                mockAuthConnector
                  .authorise(any[Predicate](), any[Retrieval[Unit]]())(any[HeaderCarrier](), any[ExecutionContext]())
              )
                .thenReturn(Future.successful((): Unit))

              val request: FakeRequest[AnyContent] = fakeRequest
                .withHeaders(
                  "Accept"       -> "application/vnd.hmrc.1.0+json",
                  "OriginatorId" -> "DA2_DWP_REG"
                )
                .withMethod("POST")
                .withJsonBody(maxRegisterNinoRequestJson(false))

              val result: Future[Result] = controller.register()(request)

              status(result)        shouldBe BAD_REQUEST
              contentAsJson(result) shouldBe contentAsJson(Future.successful(CorrelationIdMissingError.result))
            }
          }

          "is supplied has an incorrect correlation ID" should {
            "return 400 BAD_REQUEST with CorrelationIdIncorrectError response" in {
              when(
                mockAuthConnector
                  .authorise(any[Predicate](), any[Retrieval[Unit]]())(any[HeaderCarrier](), any[ExecutionContext]())
              )
                .thenReturn(Future.successful((): Unit))

              val request: FakeRequest[AnyContent] = fakeRequest
                .withHeaders(
                  "Accept"        -> "application/vnd.hmrc.1.0+json",
                  "OriginatorId"  -> "DA2_DWP_REG",
                  "CorrelationId" -> "id"
                )
                .withMethod("POST")
                .withJsonBody(maxRegisterNinoRequestJson(false))

              val result: Future[Result] = controller.register()(request)

              status(result)        shouldBe BAD_REQUEST
              contentAsJson(result) shouldBe contentAsJson(Future.successful(CorrelationIdIncorrectError.result))
            }
          }
        }

        "the request body is not JSON" should {
          "return 415 UNSUPPORTED_MEDIA_TYPE with InvalidBodyTypeError response" in new Setup(logDwpJson = true) {
            when(
              mockAuthConnector
                .authorise(any[Predicate](), any[Retrieval[Unit]]())(any[HeaderCarrier](), any[ExecutionContext]())
            )
              .thenReturn(Future.successful((): Unit))

            val request: FakeRequest[AnyContent] = fakeRequest
              .withHeaders(
                "Accept"        -> "application/vnd.hmrc.1.0+json",
                "CorrelationId" -> "DBABB1dB-7DED-b5Dd-19ce-5168C9E8fff9",
                "OriginatorId"  -> "DA2_DWP_REG"
              )
              .withMethod("POST")

            val result: Future[Result] = controller.register()(request)

            status(result)        shouldBe UNSUPPORTED_MEDIA_TYPE
            contentAsJson(result) shouldBe contentAsJson(Future.successful(InvalidBodyTypeError.result))
          }
        }

        "the request body is a valid JSON, but cannot be validated as a NinoApplication" should {
          "return a 400 BAD_REQUEST with JsonValidationError response" in new Setup(logDwpJson = false) {
            when(
              mockAuthConnector
                .authorise(any[Predicate](), any[Retrieval[Unit]]())(any[HeaderCarrier](), any[ExecutionContext]())
            )
              .thenReturn(Future.successful((): Unit))

            val request: FakeRequest[AnyContent] = fakeRequest
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

            val result: Future[Result] = controller.register()(request)

            status(result)        shouldBe BAD_REQUEST
            contentAsJson(result) shouldBe Json.toJson(
              NinoJsonValidationError(
                JsError(
                  Seq("names", "entryDate", "officeNumber", "birthDate", "addresses", "nino", "gender").map { field =>
                    (JsPath \ field, Seq(JavaJsonValidationError("error.path.missing")))
                  }
                )
              )
            )(ErrorResponse.validationWrites)
          }
        }
      }

      "the request is not authorised" when {
        "authorisation failed with AuthorisationException" should {
          "return 401 UNAUTHORIZED with UnauthorisedError response" in {
            when(
              mockAuthConnector
                .authorise(any[Predicate](), any[Retrieval[Unit]]())(any[HeaderCarrier](), any[ExecutionContext]())
            )
              .thenReturn(Future.failed(InvalidBearerToken()))

            val request: FakeRequest[AnyContent] = fakeRequest
              .withHeaders(
                "Accept"               -> "application/vnd.hmrc.1.0+json",
                "OriginatorId"         -> "DA2_DWP_REG",
                "CorrelationId"        -> "DBABB1dB-7DED-b5Dd-19ce-5168C9E8fff9",
                HeaderNames.xRequestId -> "1234567890",
                HeaderNames.xSessionId -> "0987654321"
              )
              .withMethod("POST")
              .withJsonBody(maxRegisterNinoRequestJson(false))

            val result: Future[Result] = controller.register()(request)

            status(result)        shouldBe UNAUTHORIZED
            contentAsJson(result) shouldBe contentAsJson(
              Future.successful(UnauthorisedError("Invalid bearer token").result)
            )
          }
        }

        "authorisation failed with another exception" should {
          "return 503 SERVICE_UNAVAILABLE with ServiceUnavailableError response" in {
            when(
              mockAuthConnector
                .authorise(any[Predicate](), any[Retrieval[Unit]]())(any[HeaderCarrier](), any[ExecutionContext]())
            )
              .thenReturn(Future.failed(new Exception("")))

            val request: FakeRequest[AnyContent] = fakeRequest
              .withHeaders(
                "Accept"               -> "application/vnd.hmrc.1.0+json",
                "OriginatorId"         -> "DA2_DWP_REG",
                "CorrelationId"        -> "DBABB1dB-7DED-b5Dd-19ce-5168C9E8fff9",
                HeaderNames.xRequestId -> "1234567890",
                HeaderNames.xSessionId -> "0987654321"
              )
              .withMethod("POST")
              .withJsonBody(maxRegisterNinoRequestJson(false))

            val result: Future[Result] = controller.register()(request)

            status(result)        shouldBe SERVICE_UNAVAILABLE
            contentAsJson(result) shouldBe contentAsJson(Future.successful(ServiceUnavailableError.result))
          }
        }
      }
    }
  }
}
