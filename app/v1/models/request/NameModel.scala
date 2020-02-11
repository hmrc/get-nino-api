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

case class NameModel(
                      title: Option[String] = None,
                      forename: Option[String] = None,
                      secondForename: Option[String] = None,
                      surname: String,
                      startDate: DateModel,
                      endDate: Option[DateModel] = None
                    )

object NameModel {
  private lazy val titlePath = __ \ "title"
  private lazy val forename = __ \ "forename"
  private lazy val secondForename = __ \ "secondForename"
  private lazy val surname = __ \ "surname"
  private lazy val startDate = __ \ "startDate"
  private lazy val endDate = __ \ "endDate"

  private val titleValidationError: JsonValidationError = JsonValidationError("Title failed validation. Must be one of: \'NOT KNOWN, MR, " +
    "MRS, MISS, MS, DR, REV\'")
  private def nameValidationError: String => JsonValidationError = fieldName => JsonValidationError(s"Unable to validate $fieldName. " +
    s"Ensure it matches the regex.")

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
      if (!passedValidation) {
        Logger.debug(s"[NameModel][validateTitle] The following title failed validation: $inputString")
        Logger.warn(s"[NameModel][validateTitle] Unable to parse entered title.")
      }
      passedValidation
    case None => true
  }

  private val nameRegex = "^(?=.{1,99}$)([A-Z]([-'. ]{0,1}[A-Za-z ]+)*[A-Za-z]?)$"

  private[models] def validateName[T](input: T, fieldName: String): Boolean = {
    val checkValidity: String => Boolean = { fieldValue =>
      val passedValidation = fieldValue.matches(nameRegex)
      if (!passedValidation) {
        Logger.debug(s"[NameModel][validateName] Unable to validate the name: $fieldValue")
        Logger.warn(s"[NameModel][validateName] Unable to validate the field: $fieldName")
      }
      passedValidation
    }

    input match {
      case stringValue: String => checkValidity(stringValue)
      case Some(stringValue: String) => checkValidity(stringValue)
      case None => true
      case _ => false
    }
  }

  implicit val reads: Reads[NameModel] = (
    titlePath.readNullable[String].filter(titleValidationError)(validateTitle) and
      forename.readNullable[String].filter(nameValidationError("forename"))(validateName(_, "forename")) and
      secondForename.readNullable[String].filter(nameValidationError("second forename"))(validateName(_, "secondForename")) and
      surname.read[String].filter(nameValidationError("surname"))(validateName(_, "surname")) and
      startDate.read[DateModel] and
      endDate.readNullable[DateModel]
    ) (NameModel.apply _)

  implicit val writes: Writes[NameModel] = Json.writes[NameModel]
}
