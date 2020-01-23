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

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Result, _}
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core.{AuthConnector, AuthProviders, AuthorisedFunctions, UnsupportedAuthProvider}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter
import v1.models.errors.{UnsupportedAuthProvider => ApiUnsupportedAuthProvider}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PrivilegedApplicationPredicate @Inject()(
                                                val authConnector: AuthConnector,
                                                val controllerComponents: ControllerComponents,
                                                override implicit val executionContext: ExecutionContext
                                              )
  extends ActionBuilder[Request, AnyContent] with AuthorisedFunctions with BaseController {

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    println(Console.YELLOW + "Headers in Action: " + request.headers + Console.RESET)
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))
    println(Console.YELLOW + "Header Carrier in Action: " + hc + Console.RESET)

    authorised(AuthProviders(PrivilegedApplication)) {
      block(request)
    } recover {
      case _: UnsupportedAuthProvider => Unauthorized(Json.toJson(ApiUnsupportedAuthProvider))
    }
  }

  override def parser: BodyParser[AnyContent] = controllerComponents.parsers.default

}
