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

import play.api.libs.json.{JsObject, Json}
import v1.models.request._

object ItNinoApplicationTestData {

  private def writeOrReadDate(implicit isWrite: Boolean): String = {
    if (isWrite) "2020-10-10" else "10-10-2020"
  }

  val minRegisterNinoRequestJson: Boolean => JsObject = implicit isWrite => Json.obj(
    "nino" -> "TC452994B",
    "gender" -> "MALE",
    "entryDate" -> writeOrReadDate,
    "birthDate" -> writeOrReadDate,
    "birthDateVerification" -> "VERIFIED",
    "officeNumber" -> "1234",
    "country" -> 1,
    "names" -> Json.arr(Json.obj(
      "surname" -> "ASurname",
      "startDate" -> writeOrReadDate
    )),
    "addresses" -> Json.arr(Json.obj(
      "line1" -> "4 AStreetName",
      "startDate" -> writeOrReadDate
    ))
  )

  val faultyRegisterNinoRequestJson: Boolean => JsObject = implicit isWrite => Json.obj(
    "nino" -> "TC452994B",
    "gender" -> "INVALID",
    "entryDate" -> writeOrReadDate,
    "birthDate" -> writeOrReadDate,
    "birthDateVerification" -> "VERIFIED",
    "officeNumber" -> "1234",
    "country" -> 1,
    "names" -> Json.arr(Json.obj(
      "surname" -> "ASurname",
      "nameType" -> "REGISTERED"
    )),
    "addresses" -> Json.arr(Json.obj(
      "line1" -> "4 AStreetName",
      "countryCode" -> "USA",
      "startDate" -> writeOrReadDate
    ))
  )

  val maxRegisterNinoRequestJson: Boolean => JsObject = implicit isWrite => Json.obj(
    "nino" -> "TC452994B",
    "gender" -> "MALE",
    "entryDate" -> writeOrReadDate,
    "birthDate" -> writeOrReadDate,
    "birthDateVerification" -> "VERIFIED",
    "officeNumber" -> "1234",
    "contactNumber" -> "1234567890",
    "country" -> 1,
    "names" -> Json.arr(Json.obj(
      "title" -> "MR",
      "forename" -> "AForename",
      "secondForename" -> "NotSure",
      "surname" -> "ASurname",
      "startDate" -> writeOrReadDate,
      "endDate" -> writeOrReadDate,
      "nameType" -> "REGISTERED"
    )),
    "historicNames" -> Json.arr(
      Json.obj(
        "title" -> "MRS",
        "forename" -> "AForename",
        "secondForename" -> "NotSure",
        "surname" -> "ASurname",
        "startDate" -> writeOrReadDate,
        "endDate" -> writeOrReadDate,
        "nameType" -> "REGISTERED"
      ),
      Json.obj(
        "title" -> "MISS",
        "forename" -> "AForename",
        "secondForename" -> "NotSure",
        "surname" -> "ASurname",
        "startDate" -> writeOrReadDate,
        "endDate" -> writeOrReadDate,
        "nameType" -> "REGISTERED"
      )
    ),
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
    )),
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
    ),
    "originData" -> Json.obj(
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

  val minRegisterNinoRequestModel: NinoApplication = {
    implicit val isWrite: Boolean = false
    NinoApplication(
      nino = "TC452994B",
      gender = Male,
      entryDate = DateModel(writeOrReadDate),
      birthDate = DateModel(writeOrReadDate),
      birthDateVerification = Some(Verified),
      officeNumber = "1234",
      contactNumber = None,
      applicantNames = Seq(NameModel(surname = "ASurname", nameType = "REGISTERED")),
      applicantHistoricNames = None,
      applicantAddresses = Seq(AddressModel(
        None,
        AddressLine("4 AStreetName"),
        None, None, None, None, None, "NGA",
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
      birthDate = DateModel(writeOrReadDate),
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
        None, None, None, None, None, "NGA",
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


  val maxRegisterNinoRequestModel: NinoApplication = {
    implicit val isWrite: Boolean = false
    NinoApplication(
      nino = "TC452994B",
      gender = Male,
      entryDate = DateModel(writeOrReadDate),
      birthDate = DateModel(writeOrReadDate),
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
      )),
      applicantHistoricNames = Some(Seq(
        NameModel(
          Some("MRS"),
          Some("AForename"),
          Some("NotSure"),
          "ASurname",
          Some(DateModel(writeOrReadDate)),
          Some(DateModel(writeOrReadDate)),
          "REGISTERED"
        ),
        NameModel(
          Some("MISS"),
          Some("AForename"),
          Some("NotSure"),
          "ASurname",
          Some(DateModel(writeOrReadDate)),
          Some(DateModel(writeOrReadDate)),
          "REGISTERED"
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
        OriginData(
          birthTown = Some("ATown"), birthProvince = Some("SomeProvince"), birthCountryCode = Some(200),
          birthSurname = Some("ASurname"), maternalForename = Some("MotherForename"), maternalSurname = Some("AnotherSurname"),
          paternalForename = Some("AForename"), paternalSurname = Some("ASurname"), foreignSocialSecurity = Some("SomeSocialSecurityNumber"),
          lastEUAddress = Some(LastEUAddress(
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
