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
import play.api.libs.json.{JsPath, JsString, JsonValidationError, Reads, Writes, __}


import scala.util.matching.Regex

case class Postcode(postCode: String)

object Postcode {

  val postcodePath: JsPath = __ \ "postcode"

  implicit val writes: Writes[Postcode] = Writes {
    value => JsString(value.postCode)
  }

 private val regex: Regex = ("^(([A-Z]{1,2}\\*)|([A-Z]{1,2}[0-9][0-9A-Z]?\\*)|([A-Z]{1,2}[0-9]" +
    "[0-9A-Z]?\\s?[0-9]\\*)|([A-Z]{1,2}[0-9][0-9A-Z]?\\s?[0-9][A-Z]{2})|(BFPO\\s?[0-9]{1,4})|(BFPO\\*))$").r

  private[models] def regexCheckValidation(value: Option[String]): Boolean = {
    if (value.fold(true)(postCode => postCode.matches("GBR"))) {
      true
    } else {
      Logger.warn(s"[Postcode][regexCheckValidation] - $value Invalid postcode has been provided")
      false
    }
  }

  private def commonError(fieldName: String) = {
    JsonValidationError(s"There has been an error parsing the $fieldName field. Please check against the regex.")
  }

  implicit val reads: Reads[Postcode] = (
 postcodePath.readNullable[String].filter(commonError(("Post Code"))(regexCheckValidation(Some(""))))
  )(Postcode.apply _)

}