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

package v1.utils

import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Reads}
import play.api.mvc.{AnyContentAsJson, Request}
import v1.models.errors.{Error, InvalidBodyTypeError, JsonValidationError}

trait JsonBodyUtil {

  def parsedJsonBody[T](implicit request: Request[_], reads: Reads[T]): Either[Error, T] = request.body match {
    case body: AnyContentAsJson => body.json.validate[T] match {
      case jsErrors: JsError =>
        Logger.debug(s"[MicroserviceBaseController][parsedJsonBody] Json received, but could not validate. Errors: $jsErrors")
        Logger.warn("[MicroserviceBaseController][parsedJsonBody] Json received, but could not validate.")
        Left(JsonValidationError)
      case validatedModel: JsSuccess[T] => Right(validatedModel.value)
    }
    case _ =>
      Logger.warn("[MicroserviceBaseController][parsedJsonBody] Body of request was not JSON.")
      Left(InvalidBodyTypeError)
  }

}
