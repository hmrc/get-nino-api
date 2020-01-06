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

package v1.services

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import v1.connectors.DesConnector
import v1.connectors.httpParsers.HttpResponseTypes.HttpPostResponse
import v1.models.request.NinoApplication
import v1.models.response.DesResponseModel

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DesService @Inject()(
                            desConnector: DesConnector
                          ) {

  def registerNino(ninoApplication: NinoApplication)
                  (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpPostResponse[DesResponseModel]] = {
    desConnector.sendRegisterRequest(ninoApplication)
  }

}
