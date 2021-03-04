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

package v1.controllers.predicates

import javax.inject.Inject
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import v1.models.errors.{OriginatorIdIncorrectError, OriginatorIdMissingError}

import scala.concurrent.{ExecutionContext, Future}

class OriginatorIdPredicate @Inject()(
                                       ec: ExecutionContext,
                                       val controllerComponents: ControllerComponents
                                     ) extends ActionFilter[Request] with BaseController {

  override protected def filter[A](request: Request[A]): Future[Option[Result]] = {
    request.headers.get("OriginatorId") match {
      case Some(originatorId) => if (originatorId == "DA2_DWP_REG") {
        Future.successful(None)
      } else {
        Logger.warn("[OriginatorIdPredicate][Filter] - OriginatorId does not match regex")
        Future.successful(Some(OriginatorIdIncorrectError.result))
      }

      case None =>
        Logger.warn("[OriginatorIdPredicate][Filter] - OriginatorId is missing")
        Future.successful(Some(BadRequest(Json.toJson(OriginatorIdMissingError))))
    }
  }

  override protected def executionContext: ExecutionContext = ec
}
