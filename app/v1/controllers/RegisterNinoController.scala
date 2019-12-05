/*
 * Copyright 2019 HM Revenue & Customs
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
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import v1.connectors.httpParsers.HttpResponseTypes.HttpPostResponse
import v1.models.errors.Error
import v1.models.request.NinoApplication
import v1.models.response.DesResponseModel
import v1.services.DesService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RegisterNinoController @Inject()(
                                        cc: ControllerComponents,
                                        desService: DesService
                                      )(implicit ec: ExecutionContext) extends MicroserviceBaseController(cc) {

  def register(): Action[AnyContent] = Action.async { implicit request =>
    Future(parsedJsonBody[NinoApplication]).flatMap {
      case Left(errors) => Future.successful(BadRequest(Json.toJson(errors)))
      case Right(ninoModel) => desService.registerNino(ninoModel).map(handleResponse)
    }.recover {
      case t: Throwable => BadRequest(Json.toJson(Error(
        s"$BAD_REQUEST", t.getMessage
      )))
    }
  }

  private[controllers] def handleResponse(httpPostResponse: HttpPostResponse[DesResponseModel]): Result = {
    httpPostResponse match {
      case Right(responseModel) => Ok(Json.toJson(responseModel))
      case Left(error) => BadRequest(Json.toJson(error))
    }
  }

}
