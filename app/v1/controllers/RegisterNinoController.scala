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

package v1.controllers

import config.AppConfig
import javax.inject.{Inject, Singleton}
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import v1.controllers.predicates.{CorrelationIdPredicate, OriginatorIdPredicate, PrivilegedApplicationPredicate}
import v1.models.request.NinoApplication
import v1.services.DesService
import v1.utils.JsonBodyUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RegisterNinoController @Inject() (
  cc: ControllerComponents,
  desService: DesService,
  privilegedApplicationPredicate: PrivilegedApplicationPredicate,
  correlationIdPredicate: CorrelationIdPredicate,
  originatorIdPredicate: OriginatorIdPredicate,
  appConfig: AppConfig
)(implicit val ec: ExecutionContext)
    extends BackendController(cc)
    with JsonBodyUtil
    with Logging {

  def register(): Action[AnyContent] =
    (privilegedApplicationPredicate andThen originatorIdPredicate andThen correlationIdPredicate).async {
      implicit request =>
        val optionalOriginatorId  = request.headers.get("OriginatorId")
        val optionalCorrelationId = request.headers.get("CorrelationId")

        val hcWithOriginatorIdAndCorrelationId = (optionalOriginatorId, optionalCorrelationId) match {
          case (Some(originatorId), Some(correlationId)) =>
            hc.withExtraHeaders("OriginatorId" -> originatorId, "CorrelationId" -> correlationId)
          case _                                         => hc
        }

        if (appConfig.features.logDwpJson()) request.body match {
          case jsonContent: AnyContentAsJson =>
            logger.info(
              s"[RegisterNinoController][register] Logging JSON body of incoming request: ${jsonContent.json}"
            )
          case _                             =>
            logger.warn("[RegisterNinoController][register] Incoming request did not have a JSON body.")
        }

        Future(parsedJsonBody[NinoApplication]).flatMap {
          case Right(ninoModel) =>
            desService.registerNino(ninoModel)(hcWithOriginatorIdAndCorrelationId, ec).map {
              case Right(_)    => Accepted
              case Left(error) => logErrorResult(error)
            }
          case Left(error)      => Future.successful(logErrorResult(error))
        }
    }

  private def logErrorResult(error: v1.models.errors.ErrorResponse)(implicit hc: HeaderCarrier): Result = {
    logger.debug(s"[RegisterNinoController][logErrorResult] Header Carrier for failed request: $hc")
    logger.warn(s"[RegisterNinoController][logErrorResult] Error JSON: ${Json.prettyPrint(Json.toJson(error))}")
    error.result
  }
}
