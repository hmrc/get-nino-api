/*
 * Copyright 2022 HM Revenue & Customs
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
import play.api.libs.json._

final case class Marriage(
  maritalStatus: Option[MaritalStatus] = None,
  startDate: Option[DateModel] = None,
  endDate: Option[DateModel] = None,
  spouseNino: Option[String] = None,
  spouseDateOfBirth: Option[DateModel] = None,
  spouseFirstName: Option[String] = None,
  spouseSurname: Option[String] = None
)

object Marriage extends Logging {

  private lazy val maritalStatusPath     = __ \ "maritalStatus"
  private lazy val startDatePath         = __ \ "startDate"
  private lazy val endDatePath           = __ \ "endDate"
  private lazy val partnerNinoPath       = __ \ "partnerNino"
  private lazy val spouseDateOfBirthPath = __ \ "birthDate"
  private lazy val spouseFirstNamePath   = __ \ "forename"
  private lazy val spouseSurnamePath     = __ \ "surname"

  private val ninoRegex = "^([ACEHJLMOPRSWXY][A-CEGHJ-NPR-TW-Z]|B[A-CEHJ-NPR-TW-Z]|" +
    "G[ACEGHJ-NPR-TW-Z]|[KT][A-CEGHJ-MPR-TW-Z]|N[A-CEGHJL-NPR-SW-Z]|Z[A-CEGHJ-NPR-TW-Y])[0-9]{6}[A-D]$"

  private val stringRegex = "^(?=.{1,35}$)([A-Z]([-'.&\\\\/ ]{0,1}[A-Za-z]+)*[A-Za-z]?)$"

  private[models] def validateDateAsPriorDate(
    maybeEarlierDate: Option[DateModel],
    maybeLaterDate: Option[DateModel],
    canBeEqual: Boolean = true
  ): Boolean =
    (maybeEarlierDate.map(_.asLocalDate), maybeLaterDate.map(_.asLocalDate)) match {
      case (Some(earlierDate), Some(laterDate)) =>
        val passedValidation = earlierDate.isBefore(laterDate) || (canBeEqual && earlierDate.isEqual(laterDate))
        if (!passedValidation)
          logger.warn("[AddressModel][validateDateAsPriorDate] The provided earlierDate is after the laterDate.")
        passedValidation
      case _                                    => true
    }

  private def dateNonPriorError: JsonValidationError = JsonValidationError(
    "The date provided is after today. The date must be today or before."
  )

  private def startDateAfterEndDateError = JsonValidationError("The given start date is after the given end date.")

  private def currentDate: Option[DateModel] = Some(
    DateModel(LocalDate.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
  )

  private[models] def stringValidation(item: Option[String], itemName: String): Boolean =
    if (item.forall(_.matches(stringRegex))) {
      true
    } else {
      logger.warn(s"[Marriage][stringValidation] - $itemName does not match regex")
      false
    }

  private[models] def commonError(fieldName: String) =
    JsonValidationError(s"There has been an error parsing the $fieldName field. Please check against the regex.")

  implicit val reads: Reads[Marriage] = for {
    status          <- maritalStatusPath.readNullable[MaritalStatus]
    startDate       <- startDatePath
                         .readNullable[DateModel]
                         .filter(dateNonPriorError)(validateDateAsPriorDate(_, currentDate))
    endDate         <- endDatePath
                         .readNullable[DateModel]
                         .filter(startDateAfterEndDateError)(validateDateAsPriorDate(startDate, _, canBeEqual = false))
                         .filter(dateNonPriorError)(_.fold(true)(date => validateDateAsPriorDate(Some(date), currentDate)))
    partnerNino     <-
      partnerNinoPath.readNullable[String].filter(commonError("Partner NINO"))(_.forall(_.matches(ninoRegex)))
    spouseDob       <- spouseDateOfBirthPath.readNullable[DateModel]
    spouseFirstName <-
      spouseFirstNamePath.readNullable[String].filter(commonError("Forename"))(stringValidation(_, "forename"))
    spouseSurname   <-
      spouseSurnamePath.readNullable[String].filter(commonError("Surname"))(stringValidation(_, "surname"))
  } yield Marriage(status, startDate, endDate, partnerNino, spouseDob, spouseFirstName, spouseSurname)

  implicit val writes: Writes[Marriage] = Json.writes[Marriage]

}
