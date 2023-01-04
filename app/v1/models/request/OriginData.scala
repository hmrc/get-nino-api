/*
 * Copyright 2023 HM Revenue & Customs
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

import play.api.Logging
import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class OriginData(
  birthTown: Option[String] = None,
  birthProvince: Option[String] = None,
  birthCountryCode: Option[String] = None,
  birthSurname: Option[String] = None,
  maternalForename: Option[String] = None,
  maternalSurname: Option[String] = None,
  paternalForename: Option[String] = None,
  paternalSurname: Option[String] = None,
  foreignSocialSecurity: Option[String] = None,
  lastEUAddress: Option[LastEUAddress] = None
)

object OriginData extends Logging {

  private lazy val birthTownPath             = __ \ "birthTown"
  private lazy val birthProvincePath         = __ \ "birthProvince"
  private lazy val birthCountryCodePath      = __ \ "birthCountryCode"
  private lazy val birthSurnamePath          = __ \ "birthSurname"
  private lazy val maternalForenamePath      = __ \ "maternalForename"
  private lazy val maternalSurnamePath       = __ \ "maternalSurname"
  private lazy val paternalForenamePath      = __ \ "paternalForename"
  private lazy val paternalSurnamePath       = __ \ "paternalSurname"
  private lazy val foreignSocialSecurityPath = __ \ "foreignSocialSecurity"
  private lazy val lastEUAddressPath         = __ \ "lastEUAddress"

  private val foreignSocialSecurityRegex = """^(?=.{1,29}$)([A-Za-z0-9]([-'.&\/ ]{0,1}[A-Za-z0-9]+)*)$"""

  private[models] def foreignSocialSecurityValidation(item: Option[String]): Boolean =
    if (item.forall(_.matches(foreignSocialSecurityRegex))) {
      true
    } else {
      logger.warn("[OriginData][foreignSocialSecurityRegex] - foreignSocialSecurity does not match regex")
      false
    }

  private val birthTownProvinceRegex = """^(?=.{1,35}$)([A-Z]([-'.&\/ ]{0,1}[A-Za-z ]+)*[A-Za-z]?)$"""

  private[models] def birthTownProvinceValidation(item: Option[String], itemName: String): Boolean =
    if (item.forall(_.matches(birthTownProvinceRegex))) {
      true
    } else {
      logger.warn(s"[OriginData][birthTownProvinceValidation] - $itemName does not match regex")
      false
    }

  private val nameElementRegex = """^(?=.{1,99}$)([A-Z]([-'. ]{0,1}[A-Za-z ]+)*[A-Za-z]?)$"""

  private[models] def nameElementValidation(item: Option[String], itemName: String): Boolean =
    if (item.forall(_.matches(nameElementRegex))) {
      true
    } else {
      logger.warn(s"[OriginData][nameElementValidation] - $itemName does not match regex")
      false
    }

  private val countryCodeRegex = "^[A-Z]{3}$"

  private[models] def countryCodeValidation(item: Option[String]): Boolean =
    if (item.forall(_.matches(countryCodeRegex))) {
      true
    } else {
      logger.warn("[OriginData][countryCodeRegex] - countryCode does not match regex")
      false
    }

  implicit val reads: Reads[OriginData] = (
    birthTownPath
      .readNullable[String]
      .filter(JsonValidationError("Birth town does not match regex"))(birthTownProvinceValidation(_, "birth town")) and
      birthProvincePath
        .readNullable[String]
        .filter(JsonValidationError("Birth province does not match regex"))(
          birthTownProvinceValidation(_, "birth province")
        ) and
      birthCountryCodePath
        .readNullable[String]
        .filter(JsonValidationError("Birth country code is not valid"))(countryCodeValidation) and
      birthSurnamePath
        .readNullable[String]
        .filter(JsonValidationError("Birth surname does not match regex"))(
          nameElementValidation(_, "birth surname")
        ) and
      maternalForenamePath
        .readNullable[String]
        .filter(JsonValidationError("Maternal forename does not match regex"))(
          nameElementValidation(_, "maternal forename")
        ) and
      maternalSurnamePath
        .readNullable[String]
        .filter(JsonValidationError("Maternal surname does not match regex"))(
          nameElementValidation(_, "maternal surname")
        ) and
      paternalForenamePath
        .readNullable[String]
        .filter(JsonValidationError("Paternal forename does not match regex"))(
          nameElementValidation(_, "paternal forename")
        ) and
      paternalSurnamePath
        .readNullable[String]
        .filter(JsonValidationError("Paternal surname does not match regex"))(
          nameElementValidation(_, "paternal surname")
        ) and
      foreignSocialSecurityPath
        .readNullable[String]
        .filter(JsonValidationError("Foreign social security does not match regex"))(
          foreignSocialSecurityValidation
        ) and
      lastEUAddressPath.readNullable[LastEUAddress]
  )(OriginData.apply _)

  implicit val writes: Writes[OriginData] = Json.writes[OriginData]

}
