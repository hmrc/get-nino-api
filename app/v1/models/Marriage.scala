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
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Marriage(
                     maritalStatus: Option[Int] = None,
                     startDate: Option[DateModel] = None,
                     endDate: Option[DateModel] = None,
                     partnerNino: String,
                     birthDate: DateModel,
                     forename: Option[String] = None,
                     secondForename: Option[String] = None,
                     surname: Option[String] = None
                   )

object Marriage {

  private lazy val maritalStatusPath = __ \ "maritalStatus"
  private lazy val startDatePath = __ \ "startDate"
  private lazy val endDatePath = __ \ "endDate"
  private lazy val partnerNinoPath = __ \ "partnerNino"
  private lazy val birthDatePath = __ \ "birthDate"
  private lazy val forenamePath = __ \ "forename"
  private lazy val secondForenamePath = __ \ "secondForename"
  private lazy val surnamePath = __ \ "surname"

  private val maritalStatusValidation: Option[Int] => Boolean = {
    case Some(maritalValue) =>
      val passedValidation: Boolean = (maritalValue >= 0) && (maritalValue <= 12)
      if (!passedValidation) Logger.warn("[Marriage][maritalStatusValidation] - maritalStatus is not valid")
      passedValidation
    case _ => true
  }

  private val ninoRegex = "^([ACEHJLMOPRSWXY][A-CEGHJ-NPR-TW-Z]|B[A-CEHJ-NPR-TW-Z]|" +
    "G[ACEGHJ-NPR-TW-Z]|[KT][A-CEGHJ-MPR-TW-Z]|N[A-CEGHJL-NPR-SW-Z]|Z[A-CEGHJ-NPR-TW-Y])[0-9]{6}[A-D]$"

  private val nameStringRegex = "^(?=.{1,35}$)([A-Z]([-'.&\\\\/ ]{0,1}[A-Za-z]+)*[A-Za-z]?)$"

  implicit val reads: Reads[Marriage] = (
    maritalStatusPath.readNullable[Int].filter(JsonValidationError("Marital status does not match regex"))(maritalStatusValidation) and
      startDatePath.readNullable[DateModel] and
      endDatePath.readNullable[DateModel] and
      partnerNinoPath.read[String].filter(JsonValidationError("Partner NINO does not match regex"))(_.matches(ninoRegex)) and
      birthDatePath.read[DateModel] and
      forenamePath.readNullable[String].filter(JsonValidationError("Forename does not match regex"))(_.fold(true)(_.matches(nameStringRegex))) and
      secondForenamePath.readNullable[String].filter(JsonValidationError("Second forename does not match regex"))(_.fold(true)(_.matches(nameStringRegex))) and
      surnamePath.readNullable[String].filter(JsonValidationError("Surname does not match regex"))(_.fold(true)(_.matches(nameStringRegex)))
    ) (Marriage.apply _)

}