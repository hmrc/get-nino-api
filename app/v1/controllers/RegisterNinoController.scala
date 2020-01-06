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
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import v1.models.errors.Error
import v1.models.request.NinoApplication
import v1.services.DesService
import v1.utils.JsonBodyUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RegisterNinoController @Inject()(
                                        cc: ControllerComponents,
                                        desService: DesService
                                      )(implicit ec: ExecutionContext) extends BackendController(cc) with JsonBodyUtil {

  def register(): Action[AnyContent] = Action.async { implicit request =>
    Future(parsedJsonBody[NinoApplication]).flatMap {
      case Left(errors) => Future.successful(BadRequest(Json.toJson(errors)))
      case Right(ninoModel) => desService.registerNino(ninoModel).map{
        case Right(responseModel) => Ok(Json.toJson(responseModel))
        case Left(error) => BadRequest(Json.toJson(error))
      }
    }.recover {
      case t: Throwable => BadRequest(Json.toJson(Error(
        s"$BAD_REQUEST", t.getMessage
      )))
    }
  }

}
