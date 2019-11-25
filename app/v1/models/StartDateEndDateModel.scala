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

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class StartDateEndDateModel(
                             startDate: String,
                             endDate: String
                           )

object StartDateEndDateModel {
  private val npsDateRegex: String =
    """^(((19|20)([2468][048]|[13579][26]|0[48])|2000)[-]02[-]29|((19|20)[0-9]{2}[-]
      |(0[469]|11)[-](0[1-9]|1[0-9]|2[0-9]|30)|(19|20)[0-9]{2}[-](0[13578]|1[02])[-](0[1-9]|[12][0-9]|3[01])|(19|20)[0-9]{2}[-]02[-](0[1-9]|1[0-9]|2[0-8])))$""".stripMargin

  private val startDatePath: JsPath = __ \ "startDate"
  private val endDatePath: JsPath = __ \ "endDate"

  private def changeDateFormatAndValidateNps(dateInput: OWrites[String], fieldName: String): OWrites[String] = {
    dateInput.transform { jsObj =>
      val transformedDateString = (jsObj \ fieldName).as[String].split("-").reverse.mkString("-")
      if (transformedDateString.matches(npsDateRegex)) {
        Json.obj(fieldName -> transformedDateString)
      } else {
        throw new IllegalArgumentException(
          "[StartDateEndDate][changeDateFormatAndValidateNps] The following date failed validation against NPS regex:" + transformedDateString
        )
      }
    }
  }

  implicit val writes: Writes[StartDateEndDateModel] = (
    changeDateFormatAndValidateNps(startDatePath.write[String], "startDate") and
      changeDateFormatAndValidateNps(endDatePath.write[String], "endDate")
    ) (unlift(StartDateEndDateModel.unapply))

  private def validateDwpDate(dateInput: Reads[String]): Reads[String] = {
    val dwpDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val isValidDwpDate: String => Boolean = dateInput => {
      try {
        LocalDate.parse(dateInput, dwpDateFormatter)
        true
      } catch {
        case e: Throwable =>
          Logger.warn(s"[StartDateEndDate][validateDwpDate] Unable to parse the following date: $dateInput", e)
          false
      }
    }
    dateInput.filter(
      JsonValidationError("Date has failed validation. Needs to be in format: dd-MM-yyyy")
    )(dateString => isValidDwpDate(dateString))
  }

  implicit val reads: Reads[StartDateEndDateModel] = (
    validateDwpDate(startDatePath.read[String]) and
      validateDwpDate(endDatePath.read[String])
    ) (StartDateEndDateModel.apply _)
}
