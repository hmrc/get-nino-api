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

package v1.models.errors

import play.api.http.Status.*
import play.api.libs.json.*
import play.api.mvc.Result
import play.api.mvc.Results.Status

sealed trait ErrorResponse {
  def statusCode: Int
  def code: String
  def message: String

  private def convertJsErrorsToReadableFormat: JsValue =
    this match {
      case validationError: JsonValidationError => Json.toJson(validationError)(using ErrorResponse.validationWrites)
      case _                                    => Json.toJson(this)
    }

  def result: Result = Status(statusCode)(Json.toJson(convertJsErrorsToReadableFormat))
}

object ErrorResponse {

  implicit val validationWrites: Writes[JsonValidationError] = Writes { model =>
    Json.obj(
      "code"    -> model.code,
      "message" -> model.message,
      "errors"  -> model.getErrors
    )
  }

  implicit val writes: Writes[ErrorResponse] = (o: ErrorResponse) =>
    JsObject(Seq("code" -> JsString(o.code), "message" -> JsString(o.message)))

}

object ServiceUnavailableError extends ErrorResponse {
  val statusCode: Int = SERVICE_UNAVAILABLE
  val code: String    = "SERVER_ERROR"
  val message: String = "Service unavailable."
}

object NotFoundError extends ErrorResponse {
  val statusCode: Int = NOT_FOUND
  val code: String    = "MATCHING_RESOURCE_NOT_FOUND"
  val message: String = "Matching resource not found"
}

final case class DownstreamValidationError(code: String, message: String) extends ErrorResponse {
  val statusCode: Int = BAD_REQUEST
}

object BadRequestError extends ErrorResponse {
  val statusCode: Int = BAD_REQUEST
  val code: String    = "INVALID_REQUEST"
  val message: String = "Invalid request"
}

final case class UnauthorisedError(message: String) extends ErrorResponse {
  val statusCode: Int = UNAUTHORIZED
  val code: String    = "CLIENT_OR_AGENT_NOT_AUTHORISED"
}

object InvalidBodyTypeError extends ErrorResponse {
  val statusCode: Int = UNSUPPORTED_MEDIA_TYPE
  val code: String    = "INVALID_BODY_TYPE"
  val message: String = "Expecting text/json or application/json body"
}

object InvalidAcceptHeaderError extends ErrorResponse {
  val statusCode: Int = NOT_ACCEPTABLE
  val code: String    = "ACCEPT_HEADER_INVALID"
  val message: String = "The accept header is missing or invalid"
}

object MethodNotAllowedError extends ErrorResponse {
  val statusCode: Int = METHOD_NOT_ALLOWED
  val code: String    = "METHOD_NOT_ALLOWED"
  val message: String = "The HTTP method is not valid on this endpoint"
}

object UnsupportedVersionError extends ErrorResponse {
  val statusCode: Int = NOT_FOUND
  val code: String    = "MATCHING_RESOURCE_NOT_FOUND"

  val message: String =
    "The version of the requested resource specified in the Accept header does not exist or is not supported"

}

object OriginatorIdMissingError extends ErrorResponse {
  val statusCode: Int = BAD_REQUEST
  val code: String    = "BAD_REQUEST"
  val message: String = "Originator ID is missing from the request headers."
}

object OriginatorIdIncorrectError extends ErrorResponse {
  val statusCode: Int = BAD_REQUEST
  val code: String    = "BAD_REQUEST"
  val message: String = "Originator ID is incorrect."
}

object CorrelationIdMissingError extends ErrorResponse {
  val statusCode: Int = BAD_REQUEST
  val code: String    = "BAD_REQUEST"
  val message: String = "The correlation ID is missing."
}

object CorrelationIdIncorrectError extends ErrorResponse {
  val statusCode: Int = BAD_REQUEST
  val code: String    = "BAD_REQUEST"
  val message: String = "The correlation ID does not match the expected regex"
}

final case class JsonValidationError(jsErrors: JsError) extends ErrorResponse {
  val statusCode: Int = BAD_REQUEST
  val code: String    = "JSON_VALIDATION_ERROR"
  val message: String = "The provided JSON was unable to be validated as the selected model."

  val getErrors: JsValue = Json.toJson(jsErrors.errors.flatMap { case (path, pathErrors) =>
    val dropObjDot = 4
    pathErrors.map(validationError =>
      Json.obj(
        "code"    -> "BAD_REQUEST",
        "message" -> validationError.message,
        "path"    -> path.toJsonString.drop(dropObjDot)
      )
    )
  })

}
