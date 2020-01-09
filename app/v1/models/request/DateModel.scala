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
import play.api.libs.json._

case class DateModel(
                      dateString: String
                    )

object DateModel {

  private val dwpDateRegex: String = """^\d{2}-\d{2}-\d{4}$"""
  private val npsDateRegex: String =
    """^(((19|20)([2468][048]|[13579][26]|0[48])|2000)[-]02[-]29|((19|20)[0-9]{2}[-]
      |(0[469]|11)[-](0[1-9]|1[0-9]|2[0-9]|30)|(19|20)[0-9]{2}[-](0[13578]|1[02])[-](0[1-9]|[12][0-9]|3[01])|(19|20)[0-9]{2}[-]02[-](0[1-9]|1[0-9]|2[0-8])))$""".stripMargin

  private def changeDateToNpsFormat(dateInput: String): String = {
    dateInput.split("-").reverse.mkString("-")
  }

  implicit val writes: Writes[DateModel] = Writes[DateModel] { model =>
    JsString(changeDateToNpsFormat(model.dateString))
  }

  private def validateDwpDate(dateInput: Reads[String]): Reads[String] = {
    val isValidDwpDate: String => Boolean = dateInput => {
      val passedValidation = dateInput.matches(dwpDateRegex)
      if (!passedValidation) {
        Logger.debug(s"[StartDateEndDate][validateDwpDate] Unable to parse the following date: $dateInput")
        Logger.warn(s"[StartDateEndDate][validateDwpDate] Unable to parse the provided date.")
      }
      passedValidation
    }

    val canConvertIntoNps: String => Boolean = dateInput => changeDateToNpsFormat(dateInput).matches(npsDateRegex)

    dateInput
      .filter(
        JsonValidationError("Date has failed validation. Needs to be in format: dd-MM-yyyy")
      )(dateString => isValidDwpDate(dateString))
      .filter(
        JsonValidationError("Transformed date fails NPS validation.")
      )(dateString => canConvertIntoNps(dateString))
  }

  implicit val reads: Reads[DateModel] = for {
    dateString <- validateDwpDate(__.read[String])
  } yield {
    DateModel(dateString)
  }
}
