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

package utils

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import support.UnitSpec
import uk.gov.hmrc.auth.core.InsufficientEnrolments
import uk.gov.hmrc.http.{JsValidationException, NotFoundException}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.config.HttpAuditEvent
import v1.models.errors._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NoStackTrace

class ErrorHandlerSpec extends UnitSpec with GuiceOneAppPerSuite {

  private def versionHeader: (String, String) = ACCEPT -> "application/vnd.hmrc.1.0+json"

  private trait Test {
    val method = "some-method"

    val requestHeader: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withHeaders(versionHeader)

    val auditConnector: AuditConnector = mock[AuditConnector]
    val httpAuditEvent: HttpAuditEvent = mock[HttpAuditEvent]

    val configuration: Configuration = Configuration(
      "appName"                                         -> "myApp",
      "bootstrap.errorHandler.suppress4xxErrorMessages" -> false,
      "bootstrap.errorHandler.suppress5xxErrorMessages" -> false,
      "bootstrap.errorHandler.warnOnly.statusCodes"     -> List.empty,
      "metrics.enabled"                                 -> false
    )
    val handler                      = new ErrorHandler(configuration, auditConnector, httpAuditEvent)
  }

  "ErrorHandler" when {
    ".onClientError" should {
      "return 404 with error body" when {
        "URI not found" in new Test {
          val result: Future[Result] = handler.onClientError(requestHeader, NOT_FOUND, "test")
          status(result) shouldBe NOT_FOUND

          contentAsJson(result) shouldBe contentAsJson(Future.successful(NotFoundError.result))
        }
      }

      "return 400 with error body" when {
        "a bad request and header is supplied" in new Test {
          val result: Future[Result] = handler.onClientError(requestHeader, BAD_REQUEST, "test")
          status(result) shouldBe BAD_REQUEST

          contentAsJson(result) shouldBe contentAsJson(Future.successful(BadRequestError.result))
        }
      }

      "return 401 with error body" when {
        "unauthorised and header is supplied" in new Test {
          val result: Future[Result] = handler.onClientError(requestHeader, UNAUTHORIZED, "test")
          status(result) shouldBe UNAUTHORIZED

          contentAsJson(result) shouldBe flatJsObject("code" -> "CLIENT_OR_AGENT_NOT_AUTHORISED", "message" -> "test")
        }
      }

      "return 415 with error body" when {
        "unsupported body and header is supplied" in new Test {
          val result: Future[Result] = handler.onClientError(requestHeader, UNSUPPORTED_MEDIA_TYPE, "test")
          status(result) shouldBe UNSUPPORTED_MEDIA_TYPE

          contentAsJson(result) shouldBe contentAsJson(Future.successful(InvalidBodyTypeError.result))
        }
      }

      "return 405 with error body" when {
        "invalid method type" in new Test {
          val result: Future[Result] = handler.onClientError(requestHeader, METHOD_NOT_ALLOWED, "test")

          status(result)        shouldBe METHOD_NOT_ALLOWED
          contentAsJson(result) shouldBe flatJsObject(
            "code"    -> "METHOD_NOT_ALLOWED",
            "message" -> "The HTTP method is not valid on this endpoint"
          )
        }
      }

      "return 503 with error body" when {
        "service unavailable and header is supplied" in new Test {
          val result: Future[Result] = handler.onClientError(requestHeader, SERVICE_UNAVAILABLE, "test")
          status(result) shouldBe SERVICE_UNAVAILABLE

          contentAsJson(result) shouldBe contentAsJson(Future.successful(ServiceUnavailableError.result))
        }
      }
    }

    ".onServerError" should {
      "return 404 with error body" when {
        "NotFoundException thrown" in new Test {
          val result: Future[Result] =
            handler.onServerError(requestHeader, new NotFoundException("test") with NoStackTrace)

          status(result)        shouldBe NOT_FOUND
          contentAsJson(result) shouldBe contentAsJson(Future.successful(NotFoundError.result))
        }
      }

      "return 401 with error body" when {
        "AuthorisationException thrown" in new Test {
          val result: Future[Result] =
            handler.onServerError(requestHeader, new InsufficientEnrolments("test") with NoStackTrace)

          status(result)        shouldBe UNAUTHORIZED
          contentAsJson(result) shouldBe flatJsObject("code" -> "CLIENT_OR_AGENT_NOT_AUTHORISED", "message" -> "test")
        }
      }

      "return 400 with error body" when {
        "JsValidationException thrown" in new Test {
          val result: Future[Result] = handler.onServerError(
            requestHeader,
            new JsValidationException("test", "test", classOf[String], "errs") with NoStackTrace
          )

          status(result)        shouldBe BAD_REQUEST
          contentAsJson(result) shouldBe contentAsJson(Future.successful(BadRequestError.result))
        }
      }

      "return 503 with error body" when {
        "other exception thrown" in new Test {
          val result: Future[Result] = handler.onServerError(requestHeader, new Exception with NoStackTrace)

          status(result)        shouldBe SERVICE_UNAVAILABLE
          contentAsJson(result) shouldBe contentAsJson(Future.successful(ServiceUnavailableError.result))
        }
      }
    }
  }
}
