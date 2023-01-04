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

package v1.controllers.predicates

import javax.inject.{Inject, Singleton}
import org.slf4j.MDC
import play.api.Logging
import play.api.mvc.{Result, _}
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HeaderNames.{xRequestId, xSessionId}
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import v1.models.errors.{ServiceUnavailableError, UnauthorisedError}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PrivilegedApplicationPredicate @Inject() (
  val authConnector: AuthConnector,
  val controllerComponents: ControllerComponents,
  override implicit val executionContext: ExecutionContext
) extends ActionBuilder[Request, AnyContent]
    with AuthorisedFunctions
    with BaseController
    with Logging {

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

    hc.requestId.foreach(requestId => if (Option(MDC.get(xRequestId)).isEmpty) MDC.put(xRequestId, requestId.value))
    hc.sessionId.foreach(sessionId => if (Option(MDC.get(xSessionId)).isEmpty) MDC.put(xSessionId, sessionId.value))

    authorised(AuthProviders(PrivilegedApplication)) {
      block(request)
    } recover {
      case error: AuthorisationException =>
        logger.debug(s"Authorization failed. Bearer token sent: ${hc.authorization}")
        UnauthorisedError(error.reason).result
      case ex                            =>
        logger.warn(s"Auth request failed with unexpected exception: $ex")
        ServiceUnavailableError.result
    }
  }

  override def parser: BodyParser[AnyContent] = controllerComponents.parsers.default

}
