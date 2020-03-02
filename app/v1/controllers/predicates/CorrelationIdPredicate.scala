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

import javax.inject.Inject
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import v1.models.errors.{CorrelationIdIncorrectError, CorrelationIdMissingError}

import scala.concurrent.{ExecutionContext, Future}

class CorrelationIdPredicate @Inject()(
                                        ec: ExecutionContext,
                                        val controllerComponents: ControllerComponents
                                      ) extends ActionFilter[Request] with BaseController {

  val CORRELATION_ID = "CorrelationId"

  val correlationIdRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"

  override protected def filter[A](request: Request[A]): Future[Option[Result]] = {
    val correlationId = request.headers.get(CORRELATION_ID)

    correlationId match {
      case Some(id) => if (id.matches(correlationIdRegex)) {
        Future.successful(None)
      } else {
        Logger.warn("[CorrelationIdPredicate][Filter] - CorrelationId does not match regex")
        Future.successful(Some(BadRequest(Json.toJson(CorrelationIdIncorrectError))))
      }
      case _ =>
        Logger.warn("[CorrelationIdPredicate][Filter] - CorrelationId is missing")
        Future.successful(Some(BadRequest(Json.toJson(CorrelationIdMissingError))))
    }
  }

  override protected def executionContext: ExecutionContext = ec
}
