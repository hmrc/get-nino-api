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
    if (isWrite) "2020-10-10" else "10-10-2020"
  }

  private def nationalityJsObject(implicit isWrite: Boolean): (String, JsValueWrapper) = if (isWrite) {
    "nationalityCode" -> 1
  } else {
    "country" -> 1
  }

  val minRegisterNinoRequestJson: Boolean => JsObject = implicit isWrite => Json.obj(
    "nino" -> "TC452994B",
    "gender" -> "MALE",
    "entryDate" -> writeOrReadDate,
    "birthDate" -> writeOrReadDate,
    "birthDateVerification" -> "VERIFIED",
    "officeNumber" -> "1234",
    nationalityJsObject,
    "name" -> Json.obj(
      "surname" -> "ASurname",
      "startDate" -> writeOrReadDate
    ),
    "address" -> Json.obj(
      "line1" -> "4 AStreetName",
      "startDate" -> writeOrReadDate
    )
  )

  val faultyRegisterNinoRequestJson: Boolean => JsObject = implicit isWrite => Json.obj(
    "nino" -> "TC452994BAAAAAAAAA",
    "gender" -> "MALE",
    "entryDate" -> writeOrReadDate,
    "birthDate" -> writeOrReadDate,
    "birthDateVerification" -> "VERIFIED",
    "officeNumber" -> "1234KJAHSDKJHA*SHDH£HA{DA:SLLFJKALSJF",
    "contactNumber" -> "'sd;f][a;w3#'f;#'s",
    nationalityJsObject,
    "name" -> Json.obj(
      "surname" -> "ASurname",
      "startDate" -> writeOrReadDate
    ),
    "address" -> Json.obj(
      "line1" -> "4 AStreetName",
      "startDate" -> writeOrReadDate
    )
  )

  val maxRegisterNinoRequestJson: Boolean => JsObject = implicit isWrite => {

    val applicantMarriageJsObject: (String, JsValueWrapper) = if (isWrite) {
      "applicantMarriages" -> Json.arr(
        Json.obj(
          "maritalStatus" -> 1,
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate,
          "partnerNino" -> "AA000000B",
          "spouseDateOfBirth" -> writeOrReadDate,
          "spouseFirstName" -> "Testforename",
          "secondForename" -> "Testsecondforename",
          "spouseSurname" -> "Testsurname"),
        Json.obj(
          "maritalStatus" -> 1,
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate,
          "partnerNino" -> "AA000000C",
          "spouseDateOfBirth" -> writeOrReadDate,
          "spouseFirstName" -> "Othertestforename",
          "secondForename" -> "Testsecondforename",
          "spouseSurname" -> "Testsurname")
      )
    } else {
      "marriages" -> Json.arr(
        Json.obj(
          "maritalStatus" -> 1,
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate,
          "partnerNino" -> "AA000000B",
          "birthDate" -> writeOrReadDate,
          "forename" -> "Testforename",
          "secondForename" -> "Testsecondforename",
          "surname" -> "Testsurname"),
        Json.obj(
          "maritalStatus" -> 1,
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate,
          "partnerNino" -> "AA000000C",
          "birthDate" -> writeOrReadDate,
          "forename" -> "Othertestforename",
          "secondForename" -> "Testsecondforename",
          "surname" -> "Testsurname")
      )
    }

    Json.obj(
      "nino" -> "TC452994B",
      "gender" -> "MALE",
      "entryDate" -> writeOrReadDate,
      "birthDate" -> writeOrReadDate,
      "birthDateVerification" -> "VERIFIED",
      "officeNumber" -> "1234",
      "contactNumber" -> "1234567890",
      nationalityJsObject,
      "name" -> Json.obj(
        "title" -> "MR",
        "forename" -> "AForename",
        "secondForename" -> "NotSure",
        "surname" -> "ASurname",
        "startDate" -> writeOrReadDate,
        "endDate" -> writeOrReadDate
      ),
      "historicNames" -> Json.arr(
        Json.obj(
          "title" -> "MRS",
          "forename" -> "AForename",
          "secondForename" -> "NotSure",
          "surname" -> "ASurname",
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate
        ),
        Json.obj(
          "title" -> "MISS",
          "forename" -> "AForename",
          "secondForename" -> "NotSure",
          "surname" -> "ASurname",
          "startDate" -> writeOrReadDate,
          "endDate" -> writeOrReadDate
        )
      ),
      "address" -> Json.obj(
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
      ),
      applicantMarriageJsObject,
      "originData" -> Json.obj(
        "birthTown" -> "ATown",
        "birthProvince" -> "SomeProvince",
        "birthCountryCode" -> 200,
        "nationality" -> 2,
        "birthSurname" -> "ASurname",
        "maternalForename" -> "MotherForename",
        "maternalSurname" -> "AnotherSurname",
        "paternalForename" -> "AForename",
        "paternalSurname" -> "ASurname",
        "foreignSocialSecurity" -> "SomeSocialSecurityNumber",
        "lastEUAddress" -> Json.obj(
          "line1" -> "4 AStreetName",
          "line2" -> "Some",
          "line3" -> "Old",
          "line4" -> "Place",
          "line5" -> "ItsTheFinalLine"
        )
      ),
      "priorResidency" -> Json.arr(
        Json.obj("priorStartDate" -> writeOrReadDate, "priorEndDate" -> writeOrReadDate),
        Json.obj("priorStartDate" -> writeOrReadDate, "priorEndDate" -> writeOrReadDate)
      ),
      "abroadLiability" -> Json.obj("liabilityStartDate" -> writeOrReadDate, "liabilityEndDate" -> writeOrReadDate)
    )
  }

  val minRegisterNinoRequestModel: NinoApplication = {
    implicit val isWrite: Boolean = false
    NinoApplication(
      nino = "TC452994B",
      gender = Male,
      entryDate = DateModel(writeOrReadDate),
      birthDate = DateModel(writeOrReadDate),
      birthDateVerification = Verified,
      officeNumber = "1234",
      contactNumber = None,
      nationalityCode = 1,
      name = NameModel(
        surname = "ASurname",
        startDate = DateModel(writeOrReadDate)
      ),
      historicNames = None,
      address = AddressModel(
        None,
        AddressLine("4 AStreetName"),
        None, None, None, None, None, None,
        DateModel(writeOrReadDate), None
      ),
      historicAddresses = None,
      applicantMarriages = None,
      originData = None,
      priorResidency = None,
      abroadLiability = None
    )
  }

  val faultyRegisterNinoRequestModel: NinoApplication = {
    implicit val isWrite: Boolean = false
    NinoApplication(
      nino = "TC452994BAAAAAAAAA",
      gender = Male,
      entryDate = DateModel(writeOrReadDate),
      birthDate = DateModel(writeOrReadDate),
      birthDateVerification = Verified,
      officeNumber = "1234",
      contactNumber = None,
      nationalityCode = 1,
      name = NameModel(
        surname = "ASurname",
        startDate = DateModel(writeOrReadDate)
      ),
      historicNames = None,
      address = AddressModel(
        None,
        AddressLine("4 AStreetName"),
        None, None, None, None, None, None,
        DateModel(writeOrReadDate), None
      ),
      historicAddresses = None,
      applicantMarriages = None,
      originData = None,
      priorResidency = None,
      abroadLiability = None
    )
  }

  val maxRegisterNinoRequestModel: NinoApplication = {
    implicit val isWrite: Boolean = false
    NinoApplication(
      nino = "TC452994B",
      gender = Male,
      entryDate = DateModel(writeOrReadDate),
      birthDate = DateModel(writeOrReadDate),
      birthDateVerification = Verified,
      officeNumber = "1234",
      contactNumber = Some("1234567890"),
      nationalityCode = 1,
      name = NameModel(
        title = Some("MR"),
        forename = Some("AForename"),
        secondForename = Some("NotSure"),
        surname = "ASurname",
        startDate = DateModel(writeOrReadDate),
        endDate = Some(DateModel(writeOrReadDate))
      ),
      historicNames = Some(Seq(
        NameModel(
          Some("MRS"),
          Some("AForename"),
          Some("NotSure"),
          "ASurname",
          DateModel(writeOrReadDate),
          Some(DateModel(writeOrReadDate))
        ),
        NameModel(
          Some("MISS"),
          Some("AForename"),
          Some("NotSure"),
          "ASurname",
          DateModel(writeOrReadDate),
          Some(DateModel(writeOrReadDate))
        )
      )),
      address = AddressModel(
        addressType = Some(Residential),
        line1 = AddressLine("4 AStreetName"),
        line2 = Some(AddressLine("Some")),
        line3 = Some(AddressLine("Old")),
        line4 = Some(AddressLine("Place")),
        line5 = Some(AddressLine("ItsTheFinalLine")),
        postcode = Some(Postcode("AA11AA")),
        countryCode = Some("GBR"),
        startDate = DateModel(writeOrReadDate),
        endDate = Some(DateModel(writeOrReadDate))
      ),
      historicAddresses = Some(Seq(
        AddressModel(
          Some(Residential),
          AddressLine("1 AStreetName"),
          Some(AddressLine("Some")),
          Some(AddressLine("Old")),
          Some(AddressLine("Place")),
          Some(AddressLine("ItsTheFinalLine")),
          Some(Postcode("AA11AA")),
          Some("GBR"),
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
          Some("GBR"),
          DateModel(writeOrReadDate),
          Some(DateModel(writeOrReadDate))
        )
      )),
      applicantMarriages = Some(Seq(
        Marriage(
          maritalStatus = Some(1),
          startDate = Some(DateModel(writeOrReadDate)),
          endDate = Some(DateModel(writeOrReadDate)),
          partnerNino = "AA000000B",
          spouseDateOfBirth = DateModel(writeOrReadDate),
          spouseFirstName = Some("Testforename"),
          secondForename = Some("Testsecondforename"),
          spouseSurname = Some("Testsurname")
        ),
        Marriage(
          maritalStatus = Some(1),
          startDate = Some(DateModel(writeOrReadDate)),
          endDate = Some(DateModel(writeOrReadDate)),
          partnerNino = "AA000000C",
          spouseDateOfBirth = DateModel(writeOrReadDate),
          spouseFirstName = Some("Othertestforename"),
          secondForename = Some("Testsecondforename"),
          spouseSurname = Some("Testsurname")
        )
      )),
      originData = Some(OriginData(
        birthTown = Some("ATown"),
        birthProvince = Some("SomeProvince"),
        birthCountryCode = Some(200),
        nationality = Some(2),
        birthSurname = Some("ASurname"),
        maternalForename = Some("MotherForename"),
        maternalSurname = Some("AnotherSurname"),
        paternalForename = Some("AForename"),
        paternalSurname = Some("ASurname"),
        foreignSocialSecurity = Some("SomeSocialSecurityNumber"),
        lastEUAddress = Some(LastEUAddress(
          Some(AddressLine("4 AStreetName")),
          Some(AddressLine("Some")),
          Some(AddressLine("Old")),
          Some(AddressLine("Place")),
          Some(AddressLine("ItsTheFinalLine"))
        ))
      )),
      priorResidency = Some(Seq(
        PriorResidencyModel(Some(DateModel(writeOrReadDate)), Some(DateModel(writeOrReadDate))),
        PriorResidencyModel(Some(DateModel(writeOrReadDate)), Some(DateModel(writeOrReadDate)))
      )),
      abroadLiability = Some(
        AbroadLiabilityModel(Some(DateModel(writeOrReadDate)), Some(DateModel(writeOrReadDate)))
      )
    )
  }
}
