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
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import v1.connectors.httpParsers.HttpResponseTypes.HttpPostResponse
import v1.models.errors.{DesError, ServiceUnavailableError}

import scala.util.{Failure, Success, Try}

object RegisterNinoResponseHttpParser {

  implicit object RegisterNinoResponseReads extends HttpReads[HttpPostResponse] {
    override def read(method: String, url: String, response: HttpResponse): HttpPostResponse = {
      response.status match {
        case Status.ACCEPTED =>
          Logger.debug("[RegisterNinoResponseHttpParser][read] Status Accepted")
          Right(())
        case status =>
          Logger.warn(s"[RegisterNinoResponseHttpParser][read] Unexpected $status response returned from DES")
          Try(response.json.validate[DesError]) match {
            case Success(JsError(_)) =>
              Logger.warn(s"[RegisterNinoResponseHttpParser][read] Unexpected $status response returned." +
                s"Couldn't parse error from DES.")
            case Success(JsSuccess(error, _)) =>
              Logger.warn(s"[RegisterNinoResponseHttpParser][read] Unexpected $status response returned." +
                    s"DES error code: ${error.code} DES error reason: ${error.reason}")
            case Failure(_) =>
              Logger.warn(s"[RegisterNinoResponseHttpParser][read] Unexpected $status response returned." +
                s"Error getting body from DES response")

          }
          Left(ServiceUnavailableError)
      }
    }
  }
}
