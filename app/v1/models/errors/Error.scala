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

package v1.models.errors

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Error(code: String, message: String)

object Error {
  implicit val validationWrites: Writes[JsonValidationError] = Writes { model =>
    Json.obj(
      "code" -> model.code,
      "message" -> model.message,
      "errors" -> model.getErrors
    )
  }

  implicit val writes: Writes[Error] = Json.writes[Error]
  implicit val reads: Reads[Error] = (
    (__ \ "code").read[String] and
      (__ \ "reason").read[String]
    ) (Error.apply _)
}

object ServiceUnavailableError extends Error("SERVER_ERROR", "Service unavailable.")

object NotFoundError extends Error("MATCHING_RESOURCE_NOT_FOUND", "Matching resource not found")

object DownstreamError extends Error("INTERNAL_SERVER_ERROR", "An internal server error occurred")

object BadRequestError extends Error("INVALID_REQUEST", "Invalid request")

object UnauthorisedError extends Error("CLIENT_OR_AGENT_NOT_AUTHORISED", "The client and/or agent is not authorised")

object InvalidBodyTypeError extends Error("INVALID_BODY_TYPE", "Expecting text/json or application/json body")

object InvalidAcceptHeaderError extends Error("ACCEPT_HEADER_INVALID", "The accept header is missing or invalid")

object UnsupportedVersionError extends Error("NOT_FOUND", "The requested resource could not be found")

object InvalidJsonResponseError extends Error("INVALID_JSON", "The Json returned from DES is invalid")

class JsonValidationError(jsErrors: JsError)
  extends Error("JSON_VALIDATION_ERROR", "The provided JSON was unable to be validated as the selected model.") {
    val getErrors: JsValue = Json.toJson(jsErrors.errors.flatMap {
    case (path, pathErrors) =>
      pathErrors.map(
        validationError => Json.obj("code" -> "BAD_REQUEST", "message" -> validationError.message, "path" -> path.toJsonString.substring(4))
      )
    }
  )
}
