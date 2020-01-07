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

package v1.connectors.httpParsers

import play.api.Logger
import play.api.http.Status
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import v1.connectors.httpParsers.HttpResponseTypes.HttpPostResponse
import v1.models.errors.{Error, InvalidJsonResponseError}
import v1.models.response.DesResponseModel

object RegisterNinoResponseHttpParser {

  implicit object RegisterNinoResponseReads extends HttpReads[HttpPostResponse[DesResponseModel]] {
    override def read(method: String, url: String, response: HttpResponse): HttpPostResponse[DesResponseModel] = {
      response.status match {
        case Status.OK =>
          Logger.debug("[RegisterNinoResponseHttpParser][read] Status OK")
          response.json.validate[DesResponseModel].fold(
            invalid => {
              Logger.debug(s"[RegisterNinoResponseHttpParser][read] Invalid json - $invalid")
              Logger.warn(s"[RegisterNinoResponseHttpParser][read] Invalid json")
              Left(InvalidJsonResponseError)
            },
            Right(_)
          )
        case status =>
          Logger.warn(s"[RegisterNinoResponseHttpParser][read] Unexpected $status response returned")
          Left(Error(s"$status", "Downstream error returned from DES when submitting a NINO register request"))
      }
    }
  }

}