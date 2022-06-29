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

final case class NameModel(
                      title: Option[String] = None,
                      firstName: Option[String] = None,
                      middleName: Option[String] = None,
                      surname: String,
                      startDate: Option[DateModel] = None,
                      endDate: Option[DateModel] = None,
                      nameType: String
                    )

object NameModel extends Logging{
  private lazy val titlePath = __ \ "title"
  private lazy val forenamePath = __ \ "forename"
  private lazy val secondForenamePath = __ \ "secondForename"
  private lazy val surnamePath = __ \ "surname"
  private lazy val startDatePath = __ \ "startDate"
  private lazy val endDatePath = __ \ "endDate"
  private lazy val typePath = __ \ "nameType"

  private val titleValidationError: JsonValidationError = JsonValidationError("Title failed validation. Must be one of: \'NOT KNOWN, MR, " +
    "MRS, MISS, MS, DR, REV\'")

  private def nameValidationError: String => JsonValidationError = fieldName => JsonValidationError(s"Unable to validate $fieldName. " +
    s"Ensure it matches the regex.")

  private def typeValidationError: JsonValidationError = JsonValidationError("Name Type failed validation. Must be one of: \'REGISTERED, ALIAS\'")

  private def dateNonPriorError: JsonValidationError = JsonValidationError("The date provided is after today. The date must be today or before.")

  private def startDateAfterEndDateError: JsonValidationError = JsonValidationError("The given start date is after the given end date.")

  private val validTitles = Seq(
    "NOT KNOWN",
    "MR",
    "MRS",
    "MISS",
    "MS",
    "DR",
    "REV"
  )

  private[models] def validateTitle: Option[String] => Boolean = {
    case Some(inputString) =>
      val passedValidation = validTitles.contains(inputString)
      if (!passedValidation) logger.warn(s"[NameModel][validateTitle] Unable to parse entered title.")
      passedValidation
    case None => true
  }

  private val validTypes = Seq(
    "REGISTERED",
    "ALIAS"
  )

  private[models] def validateType: String => Boolean = { inputString =>
    val passedValidation = validTypes.contains(inputString)
    if (!passedValidation) logger.warn(s"[NameModel][validateType] Unable to parse entered name type.")
    passedValidation
  }

  private val nameRegex = "^(?=.{1,99}$)([A-Z]([-'. ]{0,1}[A-Za-z ]+)*[A-Za-z]?)$"

  private[models] def validateName[T](input: T, fieldName: String): Boolean = {
    val checkValidity: String => Boolean = { fieldValue =>
      val passedValidation = fieldValue.matches(nameRegex)
      if (!passedValidation) logger.warn(s"[NameModel][validateName] Unable to validate the field: $fieldName")
      passedValidation
    }

    input match {
      case stringValue: String => checkValidity(stringValue)
      case Some(stringValue: String) => checkValidity(stringValue)
      case None => true
      case _ => false
    }
  }

  private[models] def validateDateAsPriorDate(earlierDate: Option[DateModel], laterDate: Option[DateModel], canBeEqual: Boolean = true) = {
    (earlierDate, laterDate) match {
      case (Some(earlierDateModel), Some(laterDateModel)) =>
        val earlierModelAsDate = LocalDate.parse(earlierDateModel.dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val laterModelAsDate = LocalDate.parse(laterDateModel.dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val passedValidation = earlierModelAsDate.isBefore(laterModelAsDate) || (canBeEqual && earlierModelAsDate.isEqual(laterModelAsDate))
        if(!passedValidation) logger.warn("[NameModel][validateDateAsPriorDate] The provided earlierDate is after the laterDate.")
        passedValidation
      case _ => true
    }
  }

  private def currentDate: Option[DateModel] = Some(DateModel(LocalDate.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))))

  implicit val reads: Reads[NameModel] = for {
    title <- titlePath.readNullable[String].filter(titleValidationError)(validateTitle)
    foreName <- forenamePath.readNullable[String].filter(nameValidationError("forename"))(validateName(_, "forename"))
    secondForename <- secondForenamePath.readNullable[String].filter(nameValidationError("second forename"))(validateName(_, "secondForename"))
    surname <- surnamePath.read[String].filter(nameValidationError("surname"))(validateName(_, "surname"))
    startDate <- startDatePath.readNullable[DateModel].filter(dateNonPriorError)(validateDateAsPriorDate(_, currentDate))
    endDate <- endDatePath.readNullable[DateModel]
            .filter(dateNonPriorError)(validateDateAsPriorDate(_, currentDate))
            .filter(startDateAfterEndDateError)(validateDateAsPriorDate(startDate, _, canBeEqual = false))
    nameType <- typePath.read[String].filter(typeValidationError)(validateType)
  } yield {
    NameModel(
      title,
      foreName,
      secondForename,
      surname,
      startDate,
      endDate,
      nameType
    )
  }

  implicit val writes: Writes[NameModel] = Json.writes[NameModel]
}
