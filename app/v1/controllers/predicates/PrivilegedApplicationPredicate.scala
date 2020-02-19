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

package v1.controllers.predicates

import config.AppConfig
import javax.inject.{Inject, Singleton}
import org.slf4j.MDC
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Result, _}
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.http.HeaderNames.{xRequestId, xSessionId}
import uk.gov.hmrc.http.{HeaderCarrier, Upstream5xxResponse}
import uk.gov.hmrc.play.HeaderCarrierConverter
import v1.models.errors.{AuthDownError, DownstreamError, Error => APIError}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PrivilegedApplicationPredicate @Inject()(
                                                val authConnector: AuthConnector,
                                                val controllerComponents: ControllerComponents,
                                                appConfig: AppConfig,
                                                override implicit val executionContext: ExecutionContext
                                              )
  extends ActionBuilder[Request, AnyContent] with AuthorisedFunctions with BaseController {

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, None)

    if (hc.requestId.nonEmpty && Option(MDC.get(xRequestId)).isEmpty) MDC.put(xRequestId, hc.requestId.get.value)
    if (hc.sessionId.nonEmpty && Option(MDC.get(xSessionId)).isEmpty) MDC.put(xSessionId, hc.sessionId.get.value)

    authorised(AuthProviders(PrivilegedApplication)) {
      block(request)
    } recover {
      case error: AuthorisationException =>
        Logger.debug(s"Authorization failed. Bearer token sent: ${hc.authorization}")
        Unauthorized(Json.toJson(APIError("UNAUTHORISED", error.reason)))
      case Upstream5xxResponse(_, BAD_GATEWAY, _) => BadGateway(Json.toJson(AuthDownError))
      case _ => InternalServerError(Json.toJson(DownstreamError))
    }
  }

  override def parser: BodyParser[AnyContent] = controllerComponents.parsers.default

}
