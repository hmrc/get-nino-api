/*
 * Copyright 2026 HM Revenue & Customs
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

import play.api.Logging
import play.api.http.Status
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import v1.connectors.httpParsers.HttpResponseTypes.HttpPostResponse
import v1.models.errors.{DesError, DesErrorTranslator, ServiceUnavailableError}

import scala.util.{Failure, Success, Try}

object RegisterNinoResponseHttpParser {

  implicit object RegisterNinoResponseReads extends HttpReads[HttpPostResponse] with Logging {
    override def read(method: String, url: String, response: HttpResponse): HttpPostResponse =
      (response.status, Try(response.json.validate[DesError])) match {
        case (Status.ACCEPTED, _)                             =>
          logger.info("[RegisterNinoResponseHttpParser][read] Status Accepted")
          Right(())
        case (Status.FORBIDDEN, Success(JsSuccess(error, _))) =>
          logger.warn(
            s"[RegisterNinoResponseHttpParser][read] Downstream validation failed producing" +
              s" Forbidden response returned from DES with error: $error"
          )
          Left(DesErrorTranslator.translate(error))
        case (status, Success(JsSuccess(error, _)))           =>
          logger.warn(
            s"[RegisterNinoResponseHttpParser][read] Unexpected $status response returned." +
              s"DES error code: ${error.code} DES error reason: ${error.reason}"
          )
          Left(ServiceUnavailableError)
        case (status, Success(JsError(err)))                  =>
          logger.warn(
            s"[RegisterNinoResponseHttpParser][read] Unexpected $status response returned." +
              s"Couldn't parse error JSON from DES. Parsing error: $err"
          )
          Left(ServiceUnavailableError)
        case (status, Failure(ex))                            =>
          logger.error(
            s"[RegisterNinoResponseHttpParser][read] Unexpected $status response returned." +
              s"Exception reading JSON body from DES response: $ex"
          )
          Left(ServiceUnavailableError)
      }
  }
}
