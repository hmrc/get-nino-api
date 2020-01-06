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

case class OriginData(
                       birthTown: Option[String] = None,
                       birthProvince: Option[String] = None,
                       birthCountryCode: Option[Int] = None,
                       nationality: Option[Int] = None,
                       birthSurname: Option[String] = None,
                       maternalForename: Option[String] = None,
                       maternalSurname: Option[String] = None,
                       paternalForename: Option[String] = None,
                       paternalSurname: Option[String] = None,
                       foreignSocialSecurity: Option[String] = None,
                       lastEUAddress: Option[LastEUAddress] = None
                     )

object OriginData {

  private lazy val birthTownPath = __ \ "birthTown"
  private lazy val birthProvincePath = __ \ "birthProvince"
  private lazy val birthCountryCodePath = __ \ "birthCountryCode"
  private lazy val nationalityPath = __ \ "nationality"
  private lazy val birthSurnamePath = __ \ "birthSurname"
  private lazy val maternalForenamePath = __ \ "maternalForename"
  private lazy val maternalSurnamePath = __ \ "maternalSurname"
  private lazy val paternalForenamePath = __ \ "paternalForename"
  private lazy val paternalSurnamePath = __ \ "paternalSurname"
  private lazy val foreignSocialSecurityPath = __ \ "foreignSocialSecurity"
  private lazy val lastEUAddressPath = __ \ "lastEUAddress"

  private val stringRegex = "^(?=.{1,35}$)([A-Z]([-'.&\\\\/ ]{0,1}[A-Za-z]+)*[A-Za-z]?)$"

  private[models] def stringValidation(item: Option[String], itemName: String): Boolean = {
    if (item.fold(true)(dataItem => dataItem.matches(stringRegex))) {
      true
    } else {
      Logger.warn(s"[OriginData][stringValidation] - $itemName does not match regex")
      false
    }
  }

  private[models] def countryCodeValidation: Option[Int] => Boolean = {
    case Some(countryCode) =>
      val passedValidation: Boolean = (countryCode >= 0) && (countryCode <= 286)
      if (!passedValidation) Logger.warn("[OriginData][countryCodeValidation] - country code is not valid")
      passedValidation
    case _ => true
  }


  implicit val reads: Reads[OriginData] = (
    birthTownPath.readNullable[String]
      .filter(JsonValidationError("Birth town does not match regex"))(stringValidation(_, "birth town")) and
      birthProvincePath.readNullable[String]
        .filter(JsonValidationError("Birth province does not match regex"))(stringValidation(_, "birth town")) and
      birthCountryCodePath.readNullable[Int]
        .filter(JsonValidationError("Country code is not valid"))(countryCodeValidation) and
      nationalityPath.readNullable[Int]
        .filter(JsonValidationError("Nationality code is not valid"))(countryCodeValidation) and
      birthSurnamePath.readNullable[String]
        .filter(JsonValidationError("Birth surname does not match regex"))(stringValidation(_, "birth surname")) and
      maternalForenamePath.readNullable[String]
        .filter(JsonValidationError("Maternal forename does not match regex"))(stringValidation(_, "maternal forename")) and
      maternalSurnamePath.readNullable[String]
        .filter(JsonValidationError("Maternal surname does not match regex"))(stringValidation(_, "maternal surname")) and
      paternalForenamePath.readNullable[String]
        .filter(JsonValidationError("Paternal forename does not match regex"))(stringValidation(_, "paternal forename")) and
      paternalSurnamePath.readNullable[String]
        .filter(JsonValidationError("Paternal surname does not match regex"))(stringValidation(_, "paternal surname")) and
      foreignSocialSecurityPath.readNullable[String]
        .filter(JsonValidationError("Foreign social security does not match regex"))(stringValidation(_, "social security")) and
      lastEUAddressPath.readNullable[LastEUAddress]
    ) (OriginData.apply _)

  implicit val writes: Writes[OriginData] = Json.writes[OriginData]

}
