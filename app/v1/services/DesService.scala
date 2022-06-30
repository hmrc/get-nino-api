/*
 * Copyright 2022 HM Revenue & Customs
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

package v1.services

import javax.inject.{Inject, Singleton}
import play.api.Logging
import uk.gov.hmrc.http.{BadGatewayException, GatewayTimeoutException, HeaderCarrier}
import v1.connectors.DesConnector
import v1.connectors.httpParsers.HttpResponseTypes.HttpPostResponse
import v1.models.errors.ServiceUnavailableError
import v1.models.request.NinoApplication

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DesService @Inject()(
                            desConnector: DesConnector
                          ) extends Logging{

  def registerNino(ninoApplication: NinoApplication)
                  (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpPostResponse] = {
    desConnector.sendRegisterRequest(ninoApplication).recover {
      case ex: GatewayTimeoutException =>
        logger.warn(s"[DesService][registerNino] Message to DES timed out. GatewayTimeoutException: $ex")
        Left(ServiceUnavailableError)
      case ex: BadGatewayException =>
        logger.warn(s"[DesService][registerNino] Message to DES failed. BadGatewayException: $ex")
        Left(ServiceUnavailableError)
    }
  }

}
