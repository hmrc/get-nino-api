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

import play.api.Logger
import play.api.libs.json._
import play.api.libs.functional.syntax._

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

  private val validTitles = Seq(
    "NOT KNOWN",
    "MR",
    "MRS",
    "MISS",
    "MS",
    "DR",
    "REV"
  )

  private def validateTitle(titleInput: Reads[Option[String]]): Reads[Option[String]] = {
    val validationCheck: Option[String] => Boolean = {
      case Some(inputString) =>
        val passedValidation = validTitles.contains(inputString)
        if(!passedValidation) Logger.warn(s"[NameModel][]validateTitle] The following title failed validation: $inputString")
        passedValidation
      case None => true
    }

    titleInput.filter(
      JsonValidationError("Title failed validation. Must be one of: \'NOT KNOWN, MR, MRS, MISS, MS, DR, REV\'"))(
      titleInputString => validationCheck(titleInputString)
    )
  }

  private val nameRegex = "^(?=.{1,35}$)([A-Z]([-'.&\\\\/ ]{0,1}[A-Za-z]+)*[A-Za-z]?)$"
  private def jsError: String => JsonValidationError = fieldName => JsonValidationError(s"Unable to validate $fieldName. Ensure it matches the regex.")

  private def checkAgainstNameRegex(nameString: String): Boolean = {
    nameString.matches(nameRegex)
  }

  private def validateName[T](input: Reads[T], fieldName: String): Reads[T] = {
    val checkValidity: String => Boolean = { fieldValue =>
            val passedValidation = checkAgainstNameRegex(fieldValue)
            if(!passedValidation) Logger.warn(s"Unable to validate the name: $fieldValue")
            passedValidation
    }

    input.filter(jsError(fieldName)) {
      case stringValue: String => checkValidity(stringValue)
      case Some(stringValue: String) => checkValidity(stringValue)
      case None => true
    }
  }

  implicit val reads: Reads[NameModel] = (
    validateTitle(titlePath.readNullable[String]) and
    validateName(forename.readNullable[String], "forename") and
    validateName(secondForename.readNullable[String], "second forename") and
    validateName(surname.read[String], "surname") and
    startDate.read[DateModel] and
    endDate.readNullable[DateModel]
  )(NameModel.apply _)

  implicit val writes: Writes[NameModel] = Json.writes[NameModel]
}
