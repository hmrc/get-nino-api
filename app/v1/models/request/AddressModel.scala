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
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class AddressModel(addressType: Option[AddressType],
                        addressLine1: AddressLine,
                        addressLine2: Option[AddressLine],
                        addressLine3: Option[AddressLine],
                        addressLine4: Option[AddressLine],
                        addressLine5: Option[AddressLine],
                        postcode: Option[Postcode],
                        countryCode: Option[String],
                        startDate: DateModel,
                        endDate: Option[DateModel])

object AddressModel {

  val addressTypePath: JsPath = __ \ "addressType"
  val line1Path: JsPath = __ \ "line1"
  val line2Path: JsPath = __ \ "line2"
  val line3Path: JsPath = __ \ "line3"
  val line4Path: JsPath = __ \ "line4"
  val line5Path: JsPath = __ \ "line5"
  val postcodePath: JsPath = __ \ "postcode"
  val countryCodePath: JsPath = __ \ "countryCode"
  val startDatePath: JsPath = __ \ "startDate"
  val endDatePath: JsPath = __ \ "endDate"

  private[models] def checkPostcodeMandated(postcode: Option[Postcode], countryCode: Option[String]): Boolean = {
    countryCode.fold(true) {
      case "GBR" => postcode.fold({
        Logger.warn(s"[AddressModel][postcodeValidation] - $postcode is required if country code is GBR")
        false

      })(
        _ => true
      )
      case _ => true
    }

  }

  private def commonError(fieldName: String) = {
    JsonValidationError(s"There has been an error parsing the $fieldName field. Please check against the regex.")
  }

  implicit val reads: Reads[AddressModel] = (
    addressTypePath.readNullable[AddressType] and
      line1Path.read[AddressLine] and
      line2Path.readNullable[AddressLine] and
      line3Path.readNullable[AddressLine] and
      line4Path.readNullable[AddressLine] and
      line5Path.readNullable[AddressLine] and
      countryCodePath.readNullable[String].flatMap(optCountryCode =>
        postcodePath.readNullable[Postcode].filter(commonError("Post code"))(checkPostcodeMandated(_, optCountryCode))) and
      countryCodePath.readNullable[String] and
      startDatePath.read[DateModel] and
      endDatePath.readNullable[DateModel]
    ) (AddressModel.apply _)

  implicit val writes: Writes[AddressModel] = Json.writes[AddressModel]
}
