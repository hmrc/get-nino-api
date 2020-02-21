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

package utils

import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json.{JsObject, Json}
import v1.models.request._

object NinoApplicationTestData {

  private def writeOrReadDate(implicit isWrite: Boolean): String = {
    if (isWrite) "2000-10-10" else "10-10-2000"
  }

  private def writeOrReadBirthDate(implicit isWrite: Boolean): String = {
    if (isWrite) "2000-10-10" else "10-10-2000"
  }

  val minRegisterNinoRequestJson: Boolean => JsObject = implicit isWrite => {
    val nameJsObject: (String, JsValueWrapper) = if (isWrite) {
      "applicantNames" -> Json.arr(Json.obj(
        "surname" -> "ASurname",
        "nameType" -> "REGISTERED"
      ))
    } else {
      "names" -> Json.arr(Json.obj(
        "surname" -> "ASurname",
        "nameType" -> "REGISTERED"
      ))
    }

    val addressJsObject: (String, JsValueWrapper) = if (isWrite) {
      "applicantAddresses" -> Json.arr(Json.obj(
        "addressLine1" -> "4 AStreetName",
        "countryCode" -> "USA",
        "startDate" -> writeOrReadDate
      ))
    } else {
      "addresses" -> Json.arr(Json.obj(
        "line1" -> "4 AStreetName",
        "countryCode" -> "USA",
        "startDate" -> writeOrReadDate
      ))
    }

    Json.obj(
      "nino" -> "TC452994B",
      "gender" -> "MALE",
      "entryDate" -> writeOrReadDate,
      "birthDate" -> writeOrReadBirthDate,
      "officeNumber" -> "1234",
      nameJsObject,
      addressJsObject
    )
  }


  val faultyRegisterNinoRequestJson: Boolean => JsObject = implicit isWrite => Json.obj(
    "nino" -> "TC452994BAAAAAAAAA",
    "gender" -> "MALE",
    "entryDate" -> writeOrReadDate,
    "birthDate" -> writeOrReadBirthDate,
    "birthDateVerification" -> "VERIFIED",
    "officeNumber" -> "1234KJAHSDKJHA*SHDH£HA{DA:SLLFJKALSJF",
    "contactNumber" -> "'sd;f][a;w3#'f;#'s",
    "name" -> Json.obj(
      "surname" -> "ASurname",
      "nameType" -> "REGISTERED"
    ),
    "addresses" -> Json.arr(Json.obj(
      "line1" -> "4 AStreetName",
      "startDate" -> writeOrReadDate
    )),
    "nationalityCode" -> "NOTACODE"
  )

  val maxRegisterNinoRequestJson: Boolean => JsObject = implicit isWrite => {
    val originDataPath = if (isWrite) "applicantOrigin" else "originData"
    val priorResidencyPath = if (isWrite) "applicantPriorResidency" else "priorResidency"
    val priorStartDatePath = if (isWrite) "startDate" else "priorStartDate"
    val priorEndDatePath = if (isWrite) "endDate" else "priorEndDate"
    val addressLinePrefix = (lineNo: Int) => if (isWrite) s"addressLine$lineNo" else s"line$lineNo"
    val historicNamesPath = if (isWrite) "applicantHistoricNames" else "historicNames"
    val firstNamePath = if (isWrite) "firstName" else "forename"
    val middleNamePath = if (isWrite) "middleName" else "secondForename"

    val applicantMarriageJsObject: (String, JsValueWrapper) = if (isWrite) {
      "applicantMarriages" -> Json.arr(
        Json.obj(
          "maritalStatus" -> "DIVORCED",
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate,
          "partnerNino" -> "AA000000B",
          "spouseDateOfBirth" -> writeOrReadDate,
          "spouseFirstName" -> "Testforename",
          "spouseSurname" -> "Testsurname"),
        Json.obj(
          "maritalStatus" -> "DIVORCED",
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate,
          "partnerNino" -> "AA000000C",
          "spouseDateOfBirth" -> writeOrReadDate,
          "spouseFirstName" -> "Othertestforename",
          "spouseSurname" -> "Testsurname")
      )
    } else {
      "marriages" -> Json.arr(
        Json.obj(
          "maritalStatus" -> "DIVORCED",
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate,
          "partnerNino" -> "AA000000B",
          "birthDate" -> writeOrReadDate,
          "forename" -> "Testforename",
          "surname" -> "Testsurname"),
        Json.obj(
          "maritalStatus" -> "DIVORCED",
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate,
          "partnerNino" -> "AA000000C",
          "birthDate" -> writeOrReadDate,
          "forename" -> "Othertestforename",
          "surname" -> "Testsurname")
      )
    }

    val addressJsObject: (String, JsValueWrapper) = if (isWrite) {
      "applicantAddresses" -> Json.arr(Json.obj(
        "addressType" -> "RESIDENTIAL",
        "addressLine1" -> "4 AStreetName",
        "addressLine2" -> "Some",
        "addressLine3" -> "Old",
        "addressLine4" -> "Place",
        "addressLine5" -> "ItsTheFinalLine",
        "postcode" -> "AA11AA",
        "countryCode" -> "GBR",
        "startDate" -> writeOrReadDate,
        "endDate" -> writeOrReadDate
      ),
        Json.obj(
          "addressType" -> "RESIDENTIAL",
          "addressLine1" -> "4 AStreetName",
          "addressLine2" -> "Some",
          "addressLine3" -> "New",
          "addressLine4" -> "Place",
          "addressLine5" -> "ItsTheFinalLine",
          "postcode" -> "AA11AA",
          "countryCode" -> "GBR",
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate
        ))
    } else {
      "addresses" -> Json.arr(Json.obj(
        "addressType" -> "RESIDENTIAL",
        "line1" -> "4 AStreetName",
        "line2" -> "Some",
        "line3" -> "Old",
        "line4" -> "Place",
        "line5" -> "ItsTheFinalLine",
        "postcode" -> "AA11AA",
        "countryCode" -> "GBR",
        "startDate" -> writeOrReadDate,
        "endDate" -> writeOrReadDate
      ),
        Json.obj(
          "addressType" -> "RESIDENTIAL",
          "line1" -> "4 AStreetName",
          "line2" -> "Some",
          "line3" -> "New",
          "line4" -> "Place",
          "line5" -> "ItsTheFinalLine",
          "postcode" -> "AA11AA",
          "countryCode" -> "GBR",
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate
        ))
    }

    val historicAddressJsObject: (String, JsValueWrapper) = if (isWrite) {
      "applicantHistoricAddresses" -> Json.arr(
        Json.obj(
          "addressType" -> "RESIDENTIAL",
          addressLinePrefix(1) -> "1 AStreetName",
          addressLinePrefix(2) -> "Some",
          addressLinePrefix(3) -> "Old",
          addressLinePrefix(4) -> "Place",
          addressLinePrefix(5) -> "ItsTheFinalLine",
          "postcode" -> "AA11AA",
          "countryCode" -> "GBR",
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate
        ),
        Json.obj(
          "addressType" -> "RESIDENTIAL",
          "addressLine1" -> "4 AStreetName",
          "addressLine2" -> "Some",
          "addressLine3" -> "Old",
          "addressLine4" -> "Place",
          "addressLine5" -> "ItsTheFinalLine",
          "postcode" -> "AA11AA",
          "countryCode" -> "GBR",
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate
        )
      )
    } else {
      "historicAddresses" -> Json.arr(
        Json.obj(
          "addressType" -> "RESIDENTIAL",
          "line1" -> "1 AStreetName",
          "line2" -> "Some",
          "line3" -> "Old",
          "line4" -> "Place",
          "line5" -> "ItsTheFinalLine",
          "postcode" -> "AA11AA",
          "countryCode" -> "GBR",
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate
        ),
        Json.obj(
          "addressType" -> "RESIDENTIAL",
          "line1" -> "4 AStreetName",
          "line2" -> "Some",
          "line3" -> "Old",
          "line4" -> "Place",
          "line5" -> "ItsTheFinalLine",
          "postcode" -> "AA11AA",
          "countryCode" -> "GBR",
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate
        )
      )
    }

    val nameJsObject: (String, JsValueWrapper) = if (isWrite) {
      "applicantNames" -> Json.arr(Json.obj(
        "title" -> "MR",
        "firstName" -> "AForename",
        "middleName" -> "NotSure",
        "surname" -> "ASurname",
        "startDate" -> writeOrReadDate,
        "endDate" -> writeOrReadDate,
        "nameType" -> "REGISTERED"
      ),
        Json.obj(
          "title" -> "MR",
          "firstName" -> "ASeperateForename",
          "middleName" -> "NotSure",
          "surname" -> "ASurname",
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate,
          "nameType" -> "ALIAS"
        ))
    } else {
      "names" -> Json.arr(Json.obj(
        "title" -> "MR",
        "forename" -> "AForename",
        "secondForename" -> "NotSure",
        "surname" -> "ASurname",
        "startDate" -> writeOrReadDate,
        "endDate" -> writeOrReadDate,
        "nameType" -> "REGISTERED"
      ),
        Json.obj(
          "title" -> "MR",
          "forename" -> "ASeperateForename",
          "secondForename" -> "NotSure",
          "surname" -> "ASurname",
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate,
          "nameType" -> "ALIAS"
        ))
    }

    Json.obj(
      "nino" -> "TC452994B",
      "gender" -> "MALE",
      "entryDate" -> writeOrReadDate,
      "birthDate" -> writeOrReadBirthDate,
      "birthDateVerification" -> "VERIFIED",
      "officeNumber" -> "1234",
      "contactNumber" -> "1234567890",
      nameJsObject,
      historicNamesPath -> Json.arr(
        Json.obj(
          "title" -> "MRS",
          firstNamePath -> "AForename",
          middleNamePath -> "NotSure",
          "surname" -> "ASurname",
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate,
          "nameType" -> "REGISTERED"
        ),
        Json.obj(
          "title" -> "MISS",
          firstNamePath -> "AForename",
          middleNamePath -> "NotSure",
          "surname" -> "ASurname",
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate,
          "nameType" -> "REGISTERED"
        )
      ),
      addressJsObject,
      historicAddressJsObject,
      applicantMarriageJsObject,
      originDataPath -> Json.obj(
        "birthTown" -> "ATown",
        "birthProvince" -> "SomeProvince",
        "birthCountryCode" -> 200,
        "birthSurname" -> "ASurname",
        "maternalForename" -> "MotherForename",
        "maternalSurname" -> "AnotherSurname",
        "paternalForename" -> "AForename",
        "paternalSurname" -> "ASurname",
        "foreignSocialSecurity" -> "SomeSocialSecurityNumber",
        "lastEUAddress" -> Json.obj(
          addressLinePrefix(1) -> "4 AStreetName",
          addressLinePrefix(2) -> "Some",
          addressLinePrefix(3) -> "Old",
          addressLinePrefix(4) -> "Place",
          addressLinePrefix(5) -> "ItsTheFinalLine"
        )
      ),
      priorResidencyPath -> Json.arr(
        Json.obj(priorStartDatePath -> writeOrReadDate, priorEndDatePath -> writeOrReadDate),
        Json.obj(priorStartDatePath -> writeOrReadDate, priorEndDatePath -> writeOrReadDate)
      ),
      "abroadLiability" -> Json.obj("liabilityStartDate" -> writeOrReadDate, "liabilityEndDate" -> writeOrReadDate),
      "nationalityCode" -> "GBR"
    )
  }

  val minRegisterNinoRequestModel: NinoApplication = {
    implicit val isWrite: Boolean = false
    NinoApplication(
      nino = "TC452994B",
      gender = Male,
      entryDate = DateModel(writeOrReadDate),
      birthDate = DateModel(writeOrReadBirthDate),
      birthDateVerification = None,
      officeNumber = "1234",
      contactNumber = None,
      applicantNames = Seq(NameModel(
        surname = "ASurname",
        nameType = "REGISTERED"
      )),
      applicantHistoricNames = None,
      applicantAddresses = Seq(AddressModel(
        None,
        AddressLine("4 AStreetName"),
        None, None, None, None, None, "USA",
        DateModel(writeOrReadDate), None
      )),
      applicantHistoricAddresses = None,
      applicantMarriages = None,
      applicantOrigin = None,
      applicantPriorResidency = None,
      abroadLiability = None,
      nationalityCode = None
    )
  }

  val faultyRegisterNinoRequestModel: NinoApplication = {
    implicit val isWrite: Boolean = false
    NinoApplication(
      nino = "TC452994BAAAAAAAAA",
      gender = Male,
      entryDate = DateModel(writeOrReadDate),
      birthDate = DateModel(writeOrReadBirthDate),
      birthDateVerification = Some(Verified),
      officeNumber = "1234",
      contactNumber = None,
      applicantNames = Seq(NameModel(
        surname = "ASurname",
        nameType = "REGISTERED"
      )),
      applicantHistoricNames = None,
      applicantAddresses = Seq(AddressModel(
        None,
        AddressLine("4 AStreetName"),
        None, None, None, None, None, "GBR",
        DateModel(writeOrReadDate), None
      )),
      applicantHistoricAddresses = None,
      applicantMarriages = None,
      applicantOrigin = None,
      applicantPriorResidency = None,
      abroadLiability = None,
      nationalityCode = Some("GBR")
    )
  }

  val maxRegisterNinoRequestModel: NinoApplication = {
    implicit val isWrite: Boolean = false
    NinoApplication(
      nino = "TC452994B",
      gender = Male,
      entryDate = DateModel(writeOrReadDate),
      birthDate = DateModel(writeOrReadBirthDate),
      birthDateVerification = Some(Verified),
      officeNumber = "1234",
      contactNumber = Some("1234567890"),
      applicantNames = Seq(NameModel(
        title = Some("MR"),
        firstName = Some("AForename"),
        middleName = Some("NotSure"),
        surname = "ASurname",
        startDate = Some(DateModel(writeOrReadDate)),
        endDate = Some(DateModel(writeOrReadDate)),
        nameType = "REGISTERED"
      ),
        NameModel(
          title = Some("MR"),
          firstName = Some("ASeperateForename"),
          middleName = Some("NotSure"),
          surname = "ASurname",
          startDate = Some(DateModel(writeOrReadDate)),
          endDate = Some(DateModel(writeOrReadDate)),
          nameType = "ALIAS"
        )),
      applicantHistoricNames = Some(Seq(
        NameModel(
          Some("MRS"),
          Some("AForename"),
          Some("NotSure"),
          "ASurname",
          Some(DateModel(writeOrReadDate)),
          Some(DateModel(writeOrReadDate)),
          nameType = "REGISTERED"
        ),
        NameModel(
          Some("MISS"),
          Some("AForename"),
          Some("NotSure"),
          "ASurname",
          Some(DateModel(writeOrReadDate)),
          Some(DateModel(writeOrReadDate)),
          nameType = "REGISTERED"
        )
      )),
      applicantAddresses = Seq(AddressModel(
        addressType = Some(Residential),
        addressLine1 = AddressLine("4 AStreetName"),
        addressLine2 = Some(AddressLine("Some")),
        addressLine3 = Some(AddressLine("Old")),
        addressLine4 = Some(AddressLine("Place")),
        addressLine5 = Some(AddressLine("ItsTheFinalLine")),
        postcode = Some(Postcode("AA11AA")),
        countryCode = "GBR",
        startDate = DateModel(writeOrReadDate),
        endDate = Some(DateModel(writeOrReadDate))
      ),
        AddressModel(
          addressType = Some(Residential),
          addressLine1 = AddressLine("4 AStreetName"),
          addressLine2 = Some(AddressLine("Some")),
          addressLine3 = Some(AddressLine("New")),
          addressLine4 = Some(AddressLine("Place")),
          addressLine5 = Some(AddressLine("ItsTheFinalLine")),
          postcode = Some(Postcode("AA11AA")),
          countryCode = "GBR",
          startDate = DateModel(writeOrReadDate),
          endDate = Some(DateModel(writeOrReadDate))
        )),
      applicantHistoricAddresses = Some(Seq(
        AddressModel(
          Some(Residential),
          AddressLine("1 AStreetName"),
          Some(AddressLine("Some")),
          Some(AddressLine("Old")),
          Some(AddressLine("Place")),
          Some(AddressLine("ItsTheFinalLine")),
          Some(Postcode("AA11AA")),
          "GBR",
          DateModel(writeOrReadDate),
          Some(DateModel(writeOrReadDate))
        ),
        AddressModel(
          Some(Residential),
          AddressLine("4 AStreetName"),
          Some(AddressLine("Some")),
          Some(AddressLine("Old")),
          Some(AddressLine("Place")),
          Some(AddressLine("ItsTheFinalLine")),
          Some(Postcode("AA11AA")),
          "GBR",
          DateModel(writeOrReadDate),
          Some(DateModel(writeOrReadDate))
        )
      )),
      applicantMarriages = Some(Seq(
        Marriage(
          maritalStatus = Some(DIVORCED),
          startDate = Some(DateModel(writeOrReadDate)),
          endDate = Some(DateModel(writeOrReadDate)),
          partnerNino = "AA000000B",
          spouseDateOfBirth = DateModel(writeOrReadDate),
          spouseFirstName = Some("Testforename"),
          spouseSurname = Some("Testsurname")
        ),
        Marriage(
          maritalStatus = Some(DIVORCED),
          startDate = Some(DateModel(writeOrReadDate)),
          endDate = Some(DateModel(writeOrReadDate)),
          partnerNino = "AA000000C",
          spouseDateOfBirth = DateModel(writeOrReadDate),
          spouseFirstName = Some("Othertestforename"),
          spouseSurname = Some("Testsurname")
        )
      )),
      applicantOrigin = Some(
        OriginData(birthTown = Some("ATown"), birthProvince = Some("SomeProvince"), birthCountryCode = Some(200),
          birthSurname = Some("ASurname"), maternalForename = Some("MotherForename"),
          maternalSurname = Some("AnotherSurname"), paternalForename = Some("AForename"), paternalSurname = Some("ASurname"),
          foreignSocialSecurity = Some("SomeSocialSecurityNumber"), lastEUAddress = Some(LastEUAddress(
            Some(AddressLine("4 AStreetName")),
            Some(AddressLine("Some")),
            Some(AddressLine("Old")),
            Some(AddressLine("Place")),
            Some(AddressLine("ItsTheFinalLine"))
          )))),
      applicantPriorResidency = Some(Seq(
        PriorResidencyModel(Some(DateModel(writeOrReadDate)), Some(DateModel(writeOrReadDate))),
        PriorResidencyModel(Some(DateModel(writeOrReadDate)), Some(DateModel(writeOrReadDate)))
      )),
      abroadLiability = Some(
        AbroadLiabilityModel(Some(DateModel(writeOrReadDate)), Some(DateModel(writeOrReadDate)))
      ),
      nationalityCode = Some("GBR")
    )
  }
}
