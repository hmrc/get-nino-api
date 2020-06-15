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

import java.time.{LocalDate, ZoneId}
import java.time.format.DateTimeFormatter

import play.api.Logger
import play.api.libs.json._

final case class AddressModel(addressType: Option[AddressType],
                        addressLine1: AddressLine,
                        addressLine2: Option[AddressLine],
                        addressLine3: Option[AddressLine],
                        addressLine4: Option[AddressLine],
                        addressLine5: Option[AddressLine],
                        postCode: Option[Postcode],
                        countryCode: String,
                        startDate: Option[DateModel],
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

  private[models] def checkPostcodeMandated(postcode: Option[Postcode], countryCode: String): Boolean = {
    countryCode.toUpperCase match {
      case "GBR" | "GGY" | "IMN" => postcode.fold({
        Logger.warn(s"[AddressModel][postcodeValidation] - $postcode is required if country code is GBR")
        false
      })(
        _ => true
      )
      case _ => true
    }

  }

  private[models] def validateDateAsPriorDate(maybeEarlierDate: Option[DateModel], maybeLaterDate: Option[DateModel], canBeEqual: Boolean = true): Boolean =
    (maybeEarlierDate.map(_.asLocalDate), maybeLaterDate.map(_.asLocalDate)) match {
      case (Some(earlierDate), Some(laterDate)) =>
        val passedValidation = earlierDate.isBefore(laterDate) || (canBeEqual && earlierDate.isEqual(laterDate))
        if(!passedValidation) Logger.warn("[AddressModel][validateDateAsPriorDate] The provided earlierDate is after the laterDate.")
        passedValidation
      case _ => true
    }

  private def commonError(fieldName: String) = JsonValidationError(s"There has been an error parsing the $fieldName field. Please check against the regex.")

  private def startDateAfterEndDateError = JsonValidationError("The given start date is after the given end date.")

  private def currentDate = Some(DateModel(LocalDate.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))))

  implicit val reads: Reads[AddressModel] = for {
    addressType <- addressTypePath.readNullable[AddressType]
    line1 <- line1Path.read[AddressLine]
    line2 <- line2Path.readNullable[AddressLine]
    line3 <- line3Path.readNullable[AddressLine]
    line4 <- line4Path.readNullable[AddressLine]
    line5 <- line5Path.readNullable[AddressLine]
    countryCode <- countryCodePath.read[String]
    postcode <- postcodePath.readNullable[Postcode].filter(commonError("Post code"))(checkPostcodeMandated(_, countryCode))
    startDate <- startDatePath.readNullable[DateModel].filter(startDateAfterEndDateError) { startDate =>
      validateDateAsPriorDate(startDate, Some(DateModel(LocalDate.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))))
    }
    endDate <- endDatePath.readNullable[DateModel]
      .filter(startDateAfterEndDateError)(validateDateAsPriorDate(startDate, _, canBeEqual = false))
      .filter(startDateAfterEndDateError)(_.fold(true)(date => validateDateAsPriorDate(Some(date), currentDate)))
  } yield {
    AddressModel(addressType, line1, line2, line3, line4, line5, postcode, countryCode, startDate, endDate)
  }

  implicit val writes: Writes[AddressModel] = Json.writes[AddressModel]
}
