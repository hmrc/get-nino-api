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

package v1.connectors

import config.AppConfig
import javax.inject._
import play.api.Logging
import play.api.libs.json.Json
import uk.gov.hmrc.http._
import uk.gov.hmrc.http.client.HttpClientV2
import v1.connectors.httpParsers.HttpResponseTypes.HttpPostResponse
import v1.connectors.httpParsers.RegisterNinoResponseHttpParser.RegisterNinoResponseReads
import v1.models.request.NinoApplication

import java.util.UUID.randomUUID
import scala.concurrent._
import scala.util.matching.Regex

@Singleton
class DesConnector @Inject() (
  http: HttpClientV2,
  appConfig: AppConfig
) extends Logging {

  val CorrelationIdPattern: Regex = """.*([A-Za-z0-9]{8}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}).*""".r

  def generateNewUUID: String = randomUUID.toString

  def correlationId(hc: HeaderCarrier): String =
    hc.requestId match {
      case Some(requestId) =>
        requestId.value match {
          case CorrelationIdPattern(prefix) =>
            val uuidLength = 24
            prefix + "-" + generateNewUUID.substring(uuidLength)
          case _                            => generateNewUUID
        }
      case _               => generateNewUUID
    }

  def sendRegisterRequest(
    request: NinoApplication
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpPostResponse] = {
    val url = s"${appConfig.desBaseUrl()}${appConfig.desEndpoint()}"

    def desHeaders(implicit hc: HeaderCarrier): Seq[(String, String)] = Seq(
      "Environment"   -> appConfig.desEnvironment(),
      "Authorization" -> s"Bearer ${appConfig.desToken()}",
      "CorrelationId" -> correlationId(hc)
    )
    val requestBody                                                   = Json.toJson(request)

    if (appConfig.logDesJson()) logger.info(s"Logging JSON body of outgoing DES request: $requestBody")

    http
      .post(url"$url")
      .withBody(requestBody)
      .setHeader(desHeaders: _*)
      .execute[HttpPostResponse]
  }

}
