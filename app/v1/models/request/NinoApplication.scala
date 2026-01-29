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
import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalDateTime, ZoneId}

import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class NinoApplication(
  nino: String,
  gender: Gender,
  entryDate: DateModel,
  birthDate: DateModel,
  birthDateVerification: Option[BirthDateVerification],
  officeNumber: String,
  contactNumber: Option[String],
  applicantNames: Seq[NameModel],
  applicantHistoricNames: Option[Seq[NameModel]],
  applicantAddresses: Seq[AddressModel],
  applicantHistoricAddresses: Option[Seq[AddressModel]],
  applicantMarriages: Option[Seq[Marriage]],
  applicantOrigin: Option[OriginData],
  applicantPriorResidency: Option[Seq[PriorResidencyModel]],
  nationalityCode: Option[String]
)

object NinoApplication {
  private val ninoRegex            = "^((?!(BG|GB|KN|NK|NT|TN|ZZ)|(D|F|I|Q|U|V)[A-Z]|[A-Z](D|F|I|O|Q|U|V))[A-Z]{2})[0-9]{6}[A-D]$"
  private val officeNumberRegex    = "^([0-9]{1,6})$"
  private val contactNumberRegex   = "^([+]{0,1}[0-9 ]{1,70}[0-9])$"
  private val nationalityCodeRegex = "^[A-Z]{3}$"

  private val historicSeqLength       = 5
  private val marriagesSeqLength      = 5
  private val priorResidencySeqLength = 5

  private[models] def validateAgainstRegex(value: String, regex: String): Boolean =
    value.matches(regex)

  private[models] def validateCountry(input: Int): Boolean =
    input >= 0 && input <= 286

  private[models] def seqMinMaxValidation[T](seqInput: Seq[T], minLength: Int, maxLength: Int): Boolean =
    seqInput.length >= minLength && seqInput.length <= maxLength

  private[models] def seqMinMaxValidation[T](
    seqInputOptional: Option[Seq[T]],
    minLength: Int,
    maxLength: Int
  ): Boolean =
    seqInputOptional.fold(true)(seqInput => seqInput.length >= minLength && seqInput.length <= maxLength)

  private[models] def validateAge(birthDate: DateModel) = {
    val allowedYears: Int  = 15
    val allowedMonths: Int = 8

    val minimumDaysOfAge: Long = ChronoUnit.DAYS
      .between(LocalDateTime.now().minusYears(allowedYears).minusMonths(allowedMonths), LocalDateTime.now())

    val dateOfBirth: LocalDate = LocalDate.parse(birthDate.dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"))

    ChronoUnit.DAYS.between(dateOfBirth, LocalDate.now(ZoneId.of("UTC"))) > minimumDaysOfAge
  }

  private[models] def validateOneRegisteredName(input: Seq[NameModel]) = input.exists(_.nameType == "REGISTERED")

  private[models] def validateOneResidentialAddress(input: Seq[AddressModel]) =
    input.exists(_.addressType.contains(Residential))

  implicit val writes: Writes[NinoApplication] = Json.writes[NinoApplication]

  private val ninoPath                  = __ \ "nino"
  private val genderPath                = __ \ "gender"
  private val entryDatePath             = __ \ "entryDate"
  private val birthDatePath             = __ \ "birthDate"
  private val birthDateVerificationPath = __ \ "birthDateVerification"
  private val officeNumberPath          = __ \ "officeNumber"
  private val contactNumberPath         = __ \ "contactNumber"
  private val namesPath                 = __ \ "names"
  private val historicalNamesPath       = __ \ "historicNames"
  private val addressesPath             = __ \ "addresses"
  private val historicalAddressesPath   = __ \ "historicAddresses"
  private val applicantMarriagesPath    = __ \ "marriages"
  private val originDataPath            = __ \ "originData"
  private val priorResidencyPath        = __ \ "priorResidency"
  private val nationalityCodePath       = __ \ "nationalityCode"

  private def commonError(fieldName: String) =
    JsonValidationError(s"There has been an error parsing the $fieldName field. Please check against the regex.")

  private def minMaxError(fieldName: String) =
    JsonValidationError(
      s"The $fieldName sequence has an incorrect number of elements. Please check the validation rules."
    )

  private def tooYoungError =
    JsonValidationError("The applicant needs to be over 15 years and 8 months old.")

  private def noRegisteredNamesError = JsonValidationError("At least one provided name must be REGISTERED.")

  private def noResidentialAddressError = JsonValidationError("At least one provided address must be RESIDENTIAL.")

  implicit val reads: Reads[NinoApplication] = (
    ninoPath.read[String].filter(commonError("nino"))(validateAgainstRegex(_, ninoRegex)) and
      genderPath.read[Gender] and
      entryDatePath.read[DateModel] and
      birthDatePath.read[DateModel].filter(tooYoungError)(validateAge) and
      birthDateVerificationPath.readNullable[BirthDateVerification] and
      officeNumberPath.read[String].filter(commonError("office number"))(validateAgainstRegex(_, officeNumberRegex)) and
      contactNumberPath
        .readNullable[String]
        .filter(commonError("contact number"))(_.fold(true)(validateAgainstRegex(_, contactNumberRegex))) and
      namesPath
        .read[Seq[NameModel]]
        .filter(minMaxError("names"))(seqMinMaxValidation(_, 1, 2))
        .filter(noRegisteredNamesError)(validateOneRegisteredName) and
      historicalNamesPath
        .readNullable[Seq[NameModel]]
        .filter(minMaxError("historicNames"))(seqMinMaxValidation(_, 1, historicSeqLength)) and
      addressesPath
        .read[Seq[AddressModel]]
        .filter(minMaxError("addresses"))(seqMinMaxValidation(_, 1, 2))
        .filter(noResidentialAddressError)(validateOneResidentialAddress) and
      historicalAddressesPath
        .readNullable[Seq[AddressModel]]
        .filter(minMaxError("historicAddresses"))(seqMinMaxValidation(_, 1, historicSeqLength)) and
      applicantMarriagesPath
        .readNullable[Seq[Marriage]]
        .filter(minMaxError("marriages"))(seqMinMaxValidation(_, 1, marriagesSeqLength)) and
      originDataPath.readNullable[OriginData] and
      priorResidencyPath
        .readNullable[Seq[PriorResidencyModel]]
        .filter(minMaxError("priorResidency"))(seqMinMaxValidation(_, 1, priorResidencySeqLength)) and
      nationalityCodePath
        .readNullable[String]
        .filter(commonError("nationality code"))(nationalityCode =>
          nationalityCode.fold(true)(validateAgainstRegex(_, nationalityCodeRegex))
        )
  )(NinoApplication.apply _)

}
