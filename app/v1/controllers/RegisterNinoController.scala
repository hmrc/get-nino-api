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

package v1.controllers

import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import v1.controllers.predicates.{CorrelationIdPredicate, OriginatorIdPredicate, PrivilegedApplicationPredicate}
import v1.models.errors.{JsonValidationError, Error => NinoError}
import v1.models.request.NinoApplication
import v1.services.DesService
import v1.utils.JsonBodyUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RegisterNinoController @Inject()(
                                        cc: ControllerComponents,
                                        desService: DesService,
                                        privilegedApplicationPredicate: PrivilegedApplicationPredicate,
                                        correlationIdPredicate: CorrelationIdPredicate,
                                        originatorIdPredicate: OriginatorIdPredicate
                                      )
                                      (implicit val ec: ExecutionContext) extends BackendController(cc) with JsonBodyUtil {

  def register(): Action[AnyContent] = (privilegedApplicationPredicate andThen originatorIdPredicate andThen correlationIdPredicate).async { implicit request =>

    val optionalOriginatorId = request.headers.get("OriginatorId")
    val optionalCorrelationId = request.headers.get("CorrelationId")


    val hcWithOriginatorIdAndCorrelationId = (optionalOriginatorId, optionalCorrelationId) match {
      case (Some(originatorId), Some(correlationId)) =>
        hc.withExtraHeaders("OriginatorId" -> originatorId, "CorrelationId" -> correlationId)
      case _ => hc
    }

    Future(parsedJsonBody[NinoApplication]).flatMap {
      case Right(ninoModel) => desService.registerNino(ninoModel)(hcWithOriginatorIdAndCorrelationId, ec).map {
        case Right(_) => Accepted
        case Left(errors) => badGatewayWithLog(Json.toJson(errors))
      }
      case Left(errors) => Future.successful(badRequestWithLog(convertJsErrorsToReadableFormat(errors)))
    }

  }

  private def badGatewayWithLog[T <: JsValue](input: T)(implicit hc: HeaderCarrier): Result = {
    Logger.debug(s"Header Carrier for failed request: $hc")
    Logger.warn(Json.prettyPrint(input))
    BadGateway(input)
  }

  private def badRequestWithLog[T <: JsValue](input: T)(implicit hc: HeaderCarrier): Result = {
    Logger.debug(s"Header Carrier for failed request: $hc")
    Logger.warn(Json.prettyPrint(input))
    BadRequest(input)
  }

  private[controllers] def convertJsErrorsToReadableFormat(error: NinoError): JsValue = {
    error match {
      case validationError: JsonValidationError => Json.toJson(validationError)(NinoError.validationWrites)
      case _ => Json.toJson(error)
    }
  }

}
