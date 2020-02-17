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

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class NinoApplication(
                            nino: String,
                            gender: Gender,
                            entryDate: DateModel,
                            birthDate: DateModel,
                            birthDateVerification: BirthDateVerification,
                            officeNumber: String,
                            contactNumber: Option[String],
                            country: Int,
                            applicantNames: Seq[NameModel],
                            applicantHistoricNames: Option[Seq[NameModel]],
                            applicantAddresses: Seq[AddressModel],
                            applicantHistoricAddresses: Option[Seq[AddressModel]],
                            marriages: Option[Seq[Marriage]],
                            originData: Option[OriginData],
                            priorResidency: Option[Seq[PriorResidencyModel]],
                            abroadLiability: Option[AbroadLiabilityModel]
                          )

object NinoApplication {
  private val ninoRegex = "^((?!(BG|GB|KN|NK|NT|TN|ZZ)|(D|F|I|Q|U|V)[A-Z]|[A-Z](D|F|I|O|Q|U|V))[A-Z]{2})[0-9]{6}[A-D]$"
  private val officeNumberRegex = "^([0-9]{1,4})$"
  private val contactNumberRegex = "^([+]{0,1}[0-9 ]{1,70}[0-9])$"

  private[models] def validateAgainstRegex(value: String, regex: String): Boolean = {
    value.matches(regex)
  }

  private[models] def validateCountry(input: Int): Boolean = {
    input >= 0 && input <= 286
  }

  implicit val writes: Writes[NinoApplication] = Json.writes[NinoApplication]

  private val ninoPath = __ \ "nino"
  private val genderPath = __ \ "gender"
  private val entryDatePath = __ \ "entryDate"
  private val birthDatePath = __ \ "birthDate"
  private val birthDateVerificationPath = __ \ "birthDateVerification"
  private val officeNumberPath = __ \ "officeNumber"
  private val contactNumberPath = __ \ "contactNumber"
  private val countryPath = __ \ "country"
  private val namesPath = __ \ "name"
  private val historicalNamesPath = __ \ "historicNames"
  private val addressesPath = __ \ "address"
  private val historicalAddressesPath = __ \ "historicAddresses"
  private val marriagesPath = __ \ "marriages"
  private val originDataPath = __ \ "originData"
  private val priorResidencyPath = __ \ "priorResidency"
  private val abroadLiabilityPath = __ \ "abroadLiability"

  private def commonError(fieldName: String) = {
    JsonValidationError(s"There has been an error parsing the $fieldName field. Please check against the regex.")
  }

  implicit val reads: Reads[NinoApplication] = (
    ninoPath.read[String].filter(commonError("nino"))(validateAgainstRegex(_, ninoRegex)) and
      genderPath.read[Gender] and
      entryDatePath.read[DateModel] and
      birthDatePath.read[DateModel] and
      birthDateVerificationPath.read[BirthDateVerification] and
      officeNumberPath.read[String].filter(commonError("office number"))(validateAgainstRegex(_, officeNumberRegex)) and
      contactNumberPath.readNullable[String].filter(commonError("contact number"))(_.fold(true)(validateAgainstRegex(_, contactNumberRegex))) and
      countryPath.read[Int] and
      namesPath.read[NameModel].map(Seq(_)) and
      historicalNamesPath.readNullable[Seq[NameModel]] and
      addressesPath.read[AddressModel].map(Seq(_)) and
      historicalAddressesPath.readNullable[Seq[AddressModel]] and
      marriagesPath.readNullable[Seq[Marriage]] and
      originDataPath.readNullable[OriginData] and
      priorResidencyPath.readNullable[Seq[PriorResidencyModel]] and
      abroadLiabilityPath.readNullable[AbroadLiabilityModel]
    ) (NinoApplication.apply _)
}
