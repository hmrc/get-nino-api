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
                        line1: AddressLine,
                        line2: Option[AddressLine],
                        line3: Option[AddressLine],
                        line4: Option[AddressLine],
                        line5: Option[AddressLine],
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

  private[models] def checkPostcodeMandated(postcode: Option[Postcode], countryCode: Option[String]): Option[Postcode] = {
    countryCode.fold(postcode)(
      countryCde => countryCde.toUpperCase match {
        case "GBR" => postcode.fold({
          Logger.warn("[AddressModel][checkPostcodeMandated] - postcode is required if country code is GBR")
          throw new IllegalArgumentException("Postcode required if Country code is GBR")
        })(postcode => Some(postcode))
        case _ => postcode
      }
    )
  }

  implicit val reads: Reads[AddressModel] = for {
    addressType <- addressTypePath.readNullable[AddressType]
    line1 <- line1Path.read[AddressLine]
    line2 <- line2Path.readNullable[AddressLine]
    line3 <- line3Path.readNullable[AddressLine]
    line4 <- line4Path.readNullable[AddressLine]
    line5 <- line5Path.readNullable[AddressLine]
    postcode <- postcodePath.readNullable[Postcode]
    countryCode <- countryCodePath.readNullable[String]
    startDate <- startDatePath.read[DateModel]
    endDate <- endDatePath.readNullable[DateModel]
  } yield AddressModel(
    addressType,
    line1,
    line2,
    line3,
    line4,
    line5,
    checkPostcodeMandated(postcode, countryCode),
    countryCode,
    startDate,
    endDate
  )

  implicit val writes: Writes[AddressModel] = (
    addressTypePath.writeNullable[AddressType] and
      line1Path.write[AddressLine] and
      line2Path.writeNullable[AddressLine] and
      line3Path.writeNullable[AddressLine] and
      line4Path.writeNullable[AddressLine] and
      line5Path.writeNullable[AddressLine] and
      postcodePath.writeNullable[Postcode] and
      countryCodePath.writeNullable[String] and
      startDatePath.write[DateModel] and
      endDatePath.writeNullable[DateModel]
    ) (unlift(AddressModel.unapply))
}