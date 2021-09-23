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

package v1.connectors

import config.AppConfig
import javax.inject.{Inject, Singleton}
import play.api.Logging
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v1.connectors.httpParsers.HttpResponseTypes.HttpPostResponse
import v1.connectors.httpParsers.RegisterNinoResponseHttpParser.RegisterNinoResponseReads
import v1.models.request.NinoApplication

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DesConnector @Inject()(
                              http: HttpClient,
                              appConfig: AppConfig
                            ) extends Logging{

  def sendRegisterRequest(request: NinoApplication)
                         (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpPostResponse] = {

    val url = if(appConfig.features.useDesStub()) {
      s"${appConfig.desStubUrl}/${appConfig.desStubContext}"
    } else {
      s"${appConfig.desBaseUrl()}${appConfig.desContext()}"
    }

    val headers = Seq("Environment" -> appConfig.desEnvironment, "Authorization" -> s"Bearer ${appConfig.desToken()}")

    val requestBody = Json.toJson(request)

    if (appConfig.features.logDesJson()) logger.info(s"Logging JSON body of outgoing DES request: $requestBody")

    http.POST(url, requestBody, headers = headers)
  }

}
