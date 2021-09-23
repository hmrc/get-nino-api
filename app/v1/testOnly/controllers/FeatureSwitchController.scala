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

package v1.testOnly.controllers

import config.AppConfig
import javax.inject.Inject
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import v1.config.featureSwitch.FeatureSwitchModel

class FeatureSwitchController @Inject()(
                                         appConfig: AppConfig,
                                         cc: ControllerComponents
                                       ) extends BackendController(cc) with Logging{

  lazy val update: Action[AnyContent] = Action { implicit request =>
    request.body.asJson match {
      case Some(jsonBody) => jsonBody.asOpt[FeatureSwitchModel] match {
        case Some(model) =>
          appConfig.features.useDesStub(model.useDesStub)
          result
        case None =>
          logger.warn("[FeatureSwitchController][update] Unable to parse json as FeatureSwitchModel")
          result
      }
      case None =>
        logger.warn("[FeatureSwitchController][update] Unable to validate body as JSON")
        result
    }
  }

  def result: Result = {
    Ok(Json.toJson(FeatureSwitchModel(
      appConfig.features.useDesStub()
    )))
  }

}
