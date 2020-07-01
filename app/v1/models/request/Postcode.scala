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

package v1.models.request

import play.api.Logger
import play.api.libs.json.{JsString, JsonValidationError, Reads, Writes, __}

final case class Postcode(postCode: String)

object Postcode {

  val regex: String = "^(([A-Z]{1,2}[0-9][0-9A-Z]? [0-9][A-Z]{2})|(BFPO ?[0-9]{1,4}))$"

  def regexCheckValidation: String => Boolean = postCodeInput => {
    val passedValidation = postCodeInput.matches(regex)

    if(!passedValidation) Logger.warn("[Postcode][regexCheckValidation] - Invalid postcode has been provided.")

    passedValidation
  }

  implicit val reads: Reads[Postcode] = for {
    postCodeString <- __.read[String].filter(JsonValidationError("PostCode has failed validation"))(regexCheckValidation)
  } yield {
    Postcode(postCodeString)
  }

  implicit val writes: Writes[Postcode] = Writes {
    value => JsString(value.postCode)
  }

}
