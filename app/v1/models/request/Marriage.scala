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

case class Marriage(
                     maritalStatus: Option[MaritalStatus] = None,
                     startDate: Option[DateModel] = None,
                     endDate: Option[DateModel] = None,
                     spouseNino: String,
                     spouseDateOfBirth: DateModel,
                     spouseFirstName: Option[String] = None,
                     spouseSurname: Option[String] = None
                   )

object Marriage {

  private lazy val maritalStatusPath = __ \ "maritalStatus"
  private lazy val startDatePath = __ \ "startDate"
  private lazy val endDatePath = __ \ "endDate"
  private lazy val partnerNinoPath = __ \ "partnerNino"
  private lazy val spouseDateOfBirthPath = __ \ "birthDate"
  private lazy val spouseFirstNamePath = __ \ "forename"
  private lazy val secondForenamePath = __ \ "secondForename"
  private lazy val spouseSurnamePath = __ \ "surname"

  private val ninoRegex = "^([ACEHJLMOPRSWXY][A-CEGHJ-NPR-TW-Z]|B[A-CEHJ-NPR-TW-Z]|" +
    "G[ACEGHJ-NPR-TW-Z]|[KT][A-CEGHJ-MPR-TW-Z]|N[A-CEGHJL-NPR-SW-Z]|Z[A-CEGHJ-NPR-TW-Y])[0-9]{6}[A-D]$"

  private val stringRegex = "^(?=.{1,35}$)([A-Z]([-'.&\\\\/ ]{0,1}[A-Za-z]+)*[A-Za-z]?)$"

  private[models] def stringValidation(item: Option[String], itemName: String): Boolean = {
    if (item.fold(true)(dataItem => dataItem.matches(stringRegex))) {
      true
    } else {
      Logger.warn(s"[OriginData][stringValidation] - $itemName does not match regex")
      false
    }
  }

  private[models] def commonError(fieldName: String) = {
    JsonValidationError(s"There has been an error parsing the $fieldName field. Please check against the regex.")
  }

  implicit val reads: Reads[Marriage] = (
    maritalStatusPath.readNullable[MaritalStatus] and
      startDatePath.readNullable[DateModel] and
      endDatePath.readNullable[DateModel] and
      partnerNinoPath.read[String].filter(commonError("Partner NINO"))(_.matches(ninoRegex)) and
      spouseDateOfBirthPath.read[DateModel] and
      spouseFirstNamePath.readNullable[String].filter(commonError("Forename"))(stringValidation(_, "forename")) and
      spouseSurnamePath.readNullable[String].filter(commonError("Surname"))(stringValidation(_, "surname"))
    ) (Marriage.apply _)

  implicit val writes: Writes[Marriage] = Json.writes[Marriage]

}
