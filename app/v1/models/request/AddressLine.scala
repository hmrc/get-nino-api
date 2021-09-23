/*
 * Copyright 2021 HM Revenue & Customs
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

import play.api.Logging
import play.api.libs.json._


final case class AddressLine(addressLine: String)

object AddressLine extends Logging{

  val regex: String = "^(?=.{1,35}$)([A-Za-z0-9]([-'.& ]?[A-Za-z0-9 ]+)*)$"

  def addressLineValidation: String => Boolean = addressInput => {
    val passedValidation = addressInput.matches(regex)
    if (!passedValidation) {
      logger.warn("[AddressLine][regexCheck] Unable to parse the provided address line.")
    }
    passedValidation
  }

  implicit val reads: Reads[AddressLine] = for {
    addressLineString <- __.read[String].filter(JsonValidationError("Address line has failed validation"))(addressLineValidation)
  } yield {
    AddressLine(addressLineString)
  }

  implicit val writes: Writes[AddressLine] = Writes { value => JsString(value.addressLine) }
}
