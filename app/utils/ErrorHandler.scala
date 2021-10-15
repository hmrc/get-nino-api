/*
 * Copyright 2021 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}
import play.api.http.Status._
import play.api.mvc.{RequestHeader, Result}
import play.api.{Configuration, Logging}
import uk.gov.hmrc.auth.core.AuthorisationException
import uk.gov.hmrc.http._
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.config.HttpAuditEvent
import uk.gov.hmrc.play.bootstrap.backend.http.JsonErrorHandler
import v1.models.errors._

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ErrorHandler @Inject()(
                              config: Configuration,
                              auditConnector: AuditConnector,
                              httpAuditEvent: HttpAuditEvent
                            )
                            (implicit ec: ExecutionContext) extends JsonErrorHandler(auditConnector, httpAuditEvent, config) with Logging{

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    logger.warn(s"[ErrorHandler][onClientError] error in version 1, for (${request.method}) [${request.uri}] with status:" +
      s" $statusCode and message: $message")

    statusCode match {
      case BAD_REQUEST => Future.successful(BadRequestError.result)
      case NOT_FOUND => Future.successful(NotFoundError.result)
      case UNAUTHORIZED => Future.successful(UnauthorisedError(message).result)
      case METHOD_NOT_ALLOWED => Future.successful(MethodNotAllowedError.result)
      case UNSUPPORTED_MEDIA_TYPE => Future.successful(InvalidBodyTypeError.result)
      case _ =>
        logger.warn(s"[ErrorHandler][onClientError] Unexpected client error type")
        Future.successful(ServiceUnavailableError.result)
    }
  }

  override def onServerError(request: RequestHeader, ex: Throwable): Future[Result] = {
    logger.warn(s"[ErrorHandler][onServerError] Internal server error in version 1, for (${request.method}) [${request.uri}] -> ", ex)

    ex match {
      case _: NotFoundException => Future.successful(NotFoundError.result)
      case ex: AuthorisationException => Future.successful(UnauthorisedError(ex.reason).result)
      case _: JsValidationException => Future.successful(BadRequestError.result)
      case ex =>
        logger.warn(s"[ErrorHandler][onServerError] Server error due to unexpected exception: $ex")
        Future.successful(ServiceUnavailableError.result)
    }
  }
}
