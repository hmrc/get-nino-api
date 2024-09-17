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

import config.ApiDefinitionConfig
import controllers.Assets
import javax.inject._
import play.api.http.HttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.api.controllers.DocumentationController

import scala.concurrent.Future

@Singleton
class ApiDocumentationController @Inject() (
  cc: ControllerComponents,
  assets: Assets,
  errorHandler: HttpErrorHandler,
  apiConfig: ApiDefinitionConfig
) extends DocumentationController(cc, assets, errorHandler) {

  override def definition(): Action[AnyContent] = Action.async {
    val apiDefinition = Json.parse(
      s"""
         |{
         |  "api": {
         |    "name": "Register NINO",
         |    "description": "Register NINO information for downstream service",
         |    "context": "misc/register-nino",
         |    "categories": ["PRIVATE_GOVERNMENT"],
         |    "versions": [
         |      {
         |        "version": "1.0",
         |        "status": "${apiConfig.status()}",
         |        "endpointsEnabled": ${apiConfig.endpointsEnabled()},
         |        "access" : {
         |          "type": "${apiConfig.accessType()}"
         |        }
         |      }
         |    ]
         |  }
         |}
      """.stripMargin
    )

    Future.successful(Ok(apiDefinition))
  }
}
