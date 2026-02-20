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

package v1.controllers.predicates

import play.api.Logging
import play.api.mvc.*
import v1.models.errors.{OriginatorIdIncorrectError, OriginatorIdMissingError}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OriginatorIdPredicate @Inject() (
  ec: ExecutionContext,
  val controllerComponents: ControllerComponents
) extends ActionFilter[Request] with BaseController with Logging {

  override protected def filter[A](request: Request[A]): Future[Option[Result]] =
    request.headers.get("OriginatorId") match {
      case Some(originatorId) =>
        if (originatorId == "DA2_DWP_REG") {
          logger.info("[OriginatorIdPredicate][Filter] - OriginatorId matches with the regex")
          Future.successful(None)
        } else {
          logger.warn("[OriginatorIdPredicate][Filter] - OriginatorId does not match regex")
          Future.successful(Some(OriginatorIdIncorrectError.result))
        }

      case None =>
        logger.warn("[OriginatorIdPredicate][Filter] - OriginatorId is missing")
        Future.successful(Some(OriginatorIdMissingError.result))
    }

  override protected def executionContext: ExecutionContext = ec
}
