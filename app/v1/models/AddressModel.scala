/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class AddressModel(addressType: Option[AddressType],
                        line1: String,
                        line2: Option[String],
                        line3: Option[String],
                        line4: Option[String],
                        line5: Option[String],
                        postcode: Option[String],
                        countryCode: Option[String],
                        startDate: Option[String],
                        endDate: Option[String])

object AddressModel{

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

  implicit val reads: Reads[AddressModel] = (
    addressTypePath.readNullable[AddressType] and
    line1Path.read[String] and
    line2Path.readNullable[String] and
    line3Path.readNullable[String] and
    line4Path.readNullable[String] and
    line5Path.readNullable[String] and
    postcodePath.readNullable[String] and
    countryCodePath.readNullable[String] and
    startDatePath.readNullable[String] and
    endDatePath.readNullable[String]
  )(AddressModel.apply _)

  implicit val writes: Writes[AddressModel] = (
    addressTypePath.writeNullable[AddressType] and
      line1Path.write[String] and
      line2Path.writeNullable[String] and
      line3Path.writeNullable[String] and
      line4Path.writeNullable[String] and
      line5Path.writeNullable[String] and
      postcodePath.writeNullable[String] and
      countryCodePath.writeNullable[String] and
      startDatePath.writeNullable[String] and
      endDatePath.writeNullable[String]
    )(unlift(AddressModel.unapply))


}
