/*
 * Copyright 2026 HM Revenue & Customs
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

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZoneId}

import play.api.Logging
import play.api.libs.json.{Json, Reads, _}

final case class PriorResidencyModel(
  startDate: Option[DateModel] = None,
  endDate: Option[DateModel] = None
)

object PriorResidencyModel extends Logging {

  private[models] def validateDateAsPriorDate(
    maybeEarlierDate: Option[DateModel],
    maybeLaterDate: Option[DateModel],
    canBeEqual: Boolean = true
  ): Boolean =
    (maybeEarlierDate.map(_.asLocalDate), maybeLaterDate.map(_.asLocalDate)) match {
      case (Some(earlierDate), Some(laterDate)) =>
        val passedValidation = earlierDate.isBefore(laterDate) || (canBeEqual && earlierDate.isEqual(laterDate))
        if (!passedValidation) {
          logger.warn("[AddressModel][validateDateAsPriorDate] The provided earlierDate is after the laterDate.")
        }
        passedValidation
      case _                                    => true
    }

  private def dateNonPriorError: JsonValidationError = JsonValidationError(
    "The date provided is after today. The date must be today or before."
  )

  private def startDateAfterEndDateError = JsonValidationError(
    "The given end date should be after the given start date."
  )

  private def currentDate: Option[DateModel] = Some(
    DateModel(LocalDate.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
  )

  implicit val reads: Reads[PriorResidencyModel] = for {
    startDate <- (__ \ "priorStartDate")
                   .readNullable[DateModel]
                   .filter(dateNonPriorError)(validateDateAsPriorDate(_, currentDate))
    endDate   <- (__ \ "priorEndDate")
                   .readNullable[DateModel]
                   .filter(startDateAfterEndDateError)(validateDateAsPriorDate(startDate, _, canBeEqual = false))
                   .filter(dateNonPriorError)(_.fold(true)(date => validateDateAsPriorDate(Some(date), currentDate)))
  } yield PriorResidencyModel(startDate, endDate)

  implicit val writes: Writes[PriorResidencyModel] = Json.writes[PriorResidencyModel]
}
