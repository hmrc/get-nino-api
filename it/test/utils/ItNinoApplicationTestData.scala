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

package utils

import play.api.libs.json._
import v1.models.request._

object ItNinoApplicationTestData {

  val faultyRegisterNinoRequestJson: JsObject = Json.obj(
    "nino"                  -> "TC452994B",
    "gender"                -> "INVALID",
    "entryDate"             -> "10-10-2000",
    "birthDate"             -> "10-10-2000",
    "birthDateVerification" -> "VERIFIED",
    "officeNumber"          -> "1234",
    "country"               -> 1,
    "names"                 -> Json.arr(
      Json.obj(
        "surname"  -> "ASurname",
        "nameType" -> "REGISTERED"
      )
    ),
    "addresses"             -> Json.arr(
      Json.obj(
        "addressType" -> "RESIDENTIAL",
        "line1"       -> "4 AStreetName",
        "countryCode" -> "USA",
        "startDate"   -> "10-10-1990"
      )
    )
  )

  val maxRegisterNinoRequestJson: JsObject = Json.obj(
    "nino"                  -> "TC452994B",
    "gender"                -> "MALE",
    "entryDate"             -> "10-10-2000",
    "birthDate"             -> "10-10-2000",
    "birthDateVerification" -> "VERIFIED",
    "officeNumber"          -> "1234",
    "contactNumber"         -> "1234567890",
    "names"                 -> Json.arr(
      Json.obj(
        "title"          -> "MR",
        "forename"       -> "AForename",
        "secondForename" -> "NotSure",
        "surname"        -> "ASurname",
        "startDate"      -> "10-10-1990",
        "endDate"        -> "10-10-2000",
        "nameType"       -> "REGISTERED"
      ),
      Json.obj(
        "title"          -> "MR",
        "forename"       -> "ASeperateForename",
        "secondForename" -> "NotSure",
        "surname"        -> "ASurname",
        "startDate"      -> "10-10-1990",
        "endDate"        -> "10-10-2000",
        "nameType"       -> "ALIAS"
      )
    ),
    "historicNames"         -> Json.arr(
      Json.obj(
        "title"          -> "MRS",
        "forename"       -> "AForename",
        "secondForename" -> "NotSure",
        "surname"        -> "ASurname",
        "startDate"      -> "10-10-1990",
        "endDate"        -> "10-10-2000",
        "nameType"       -> "REGISTERED"
      ),
      Json.obj(
        "title"          -> "MISS",
        "forename"       -> "AForename",
        "secondForename" -> "NotSure",
        "surname"        -> "ASurname",
        "startDate"      -> "10-10-1990",
        "endDate"        -> "10-10-2000",
        "nameType"       -> "REGISTERED"
      )
    ),
    "addresses"             -> Json.arr(
      Json.obj(
        "addressType" -> "RESIDENTIAL",
        "line1"       -> "4 AStreetName",
        "line2"       -> "Some",
        "line3"       -> "Old",
        "line4"       -> "Place",
        "line5"       -> "ItsTheFinalLine",
        "postcode"    -> "AA1 1AA",
        "countryCode" -> "GBR",
        "startDate"   -> "10-10-1990",
        "endDate"     -> "10-10-2000"
      ),
      Json.obj(
        "addressType" -> "RESIDENTIAL",
        "line1"       -> "4 AStreetName",
        "line2"       -> "Some",
        "line3"       -> "New",
        "line4"       -> "Place",
        "line5"       -> "ItsTheFinalLine",
        "postcode"    -> "AA1 1AA",
        "countryCode" -> "GBR",
        "startDate"   -> "10-10-1990",
        "endDate"     -> "10-10-2000"
      )
    ),
    "historicAddresses"     -> Json.arr(
      Json.obj(
        "addressType" -> "RESIDENTIAL",
        "line1"       -> "1 AStreetName",
        "line2"       -> "Some",
        "line3"       -> "Old",
        "line4"       -> "Place",
        "line5"       -> "ItsTheFinalLine",
        "postcode"    -> "AA1 1AA",
        "countryCode" -> "GBR",
        "startDate"   -> "10-10-1990",
        "endDate"     -> "10-10-2000"
      ),
      Json.obj(
        "addressType" -> "RESIDENTIAL",
        "line1"       -> "4 AStreetName",
        "line2"       -> "Some",
        "line3"       -> "Old",
        "line4"       -> "Place",
        "line5"       -> "ItsTheFinalLine",
        "postcode"    -> "AA1 1AA",
        "countryCode" -> "GBR",
        "startDate"   -> "10-10-1990",
        "endDate"     -> "10-10-2000"
      )
    ),
    "marriages"             -> Json.arr(
      Json.obj(
        "maritalStatus" -> "DIVORCED",
        "startDate"     -> "10-10-1990",
        "endDate"       -> "10-10-2000",
        "partnerNino"   -> "AA000000B",
        "birthDate"     -> "10-10-2000",
        "forename"      -> "Testforename",
        "surname"       -> "Testsurname"
      ),
      Json.obj(
        "maritalStatus" -> "DIVORCED",
        "startDate"     -> "10-10-1990",
        "endDate"       -> "10-10-2000",
        "partnerNino"   -> "AA000000C",
        "birthDate"     -> "10-10-2000",
        "forename"      -> "Othertestforename",
        "surname"       -> "Testsurname"
      )
    ),
    "originData"            -> Json.obj(
      "birthTown"             -> "ATown",
      "birthProvince"         -> "SomeProvince",
      "birthCountryCode"      -> "GBR",
      "birthSurname"          -> "ASurname",
      "maternalForename"      -> "MotherForename",
      "maternalSurname"       -> "AnotherSurname",
      "paternalForename"      -> "AForename",
      "paternalSurname"       -> "ASurname",
      "foreignSocialSecurity" -> "SomeSocialSecurityNumber",
      "lastEUAddress"         -> Json.obj(
        "line1" -> "4 AStreetName",
        "line2" -> "Some",
        "line3" -> "Old",
        "line4" -> "Place",
        "line5" -> "ItsTheFinalLine"
      )
    ),
    "priorResidency"        -> Json.arr(
      Json.obj("priorStartDate" -> "10-10-1990", "priorEndDate" -> "10-10-2000"),
      Json.obj("priorStartDate" -> "10-10-1990", "priorEndDate" -> "10-10-2000")
    ),
    "nationalityCode"       -> "GBR"
  )

  val maxRegisterNinoRequestModel: NinoApplication = NinoApplication(
    nino = "TC452994B",
    gender = Male,
    entryDate = DateModel("10-10-2000"),
    birthDate = DateModel("10-10-2000"),
    birthDateVerification = Some(Verified),
    officeNumber = "1234",
    contactNumber = Some("1234567890"),
    applicantNames = Seq(
      NameModel(
        title = Some("MR"),
        firstName = Some("AForename"),
        middleName = Some("NotSure"),
        surname = "ASurname",
        startDate = Some(DateModel("10-10-1990")),
        endDate = Some(DateModel("10-10-2000")),
        nameType = "REGISTERED"
      )
    ),
    applicantHistoricNames = Some(
      Seq(
        NameModel(
          Some("MRS"),
          Some("AForename"),
          Some("NotSure"),
          "ASurname",
          Some(DateModel("10-10-1990")),
          Some(DateModel("10-10-2000")),
          "REGISTERED"
        ),
        NameModel(
          Some("MISS"),
          Some("AForename"),
          Some("NotSure"),
          "ASurname",
          Some(DateModel("10-10-1990")),
          Some(DateModel("10-10-2000")),
          "REGISTERED"
        )
      )
    ),
    applicantAddresses = Seq(
      AddressModel(
        addressType = Some(Residential),
        addressLine1 = AddressLine("4 AStreetName"),
        addressLine2 = Some(AddressLine("Some")),
        addressLine3 = Some(AddressLine("Old")),
        addressLine4 = Some(AddressLine("Place")),
        addressLine5 = Some(AddressLine("ItsTheFinalLine")),
        postCode = Some(Postcode("AA1 1AA")),
        countryCode = "GBR",
        startDate = Some(DateModel("10-10-1990")),
        endDate = Some(DateModel("10-10-2000"))
      )
    ),
    applicantHistoricAddresses = Some(
      Seq(
        AddressModel(
          Some(Residential),
          AddressLine("1 AStreetName"),
          Some(AddressLine("Some")),
          Some(AddressLine("Old")),
          Some(AddressLine("Place")),
          Some(AddressLine("ItsTheFinalLine")),
          Some(Postcode("AA1 1AA")),
          "GBR",
          Some(DateModel("10-10-1990")),
          Some(DateModel("10-10-2000"))
        ),
        AddressModel(
          Some(Residential),
          AddressLine("4 AStreetName"),
          Some(AddressLine("Some")),
          Some(AddressLine("Old")),
          Some(AddressLine("Place")),
          Some(AddressLine("ItsTheFinalLine")),
          Some(Postcode("AA1 1AA")),
          "GBR",
          Some(DateModel("10-10-1990")),
          Some(DateModel("10-10-2000"))
        )
      )
    ),
    applicantMarriages = Some(
      Seq(
        Marriage(
          maritalStatus = Some(DIVORCED),
          startDate = Some(DateModel("10-10-1990")),
          endDate = Some(DateModel("10-10-2000")),
          spouseNino = Some("AA000000B"),
          spouseDateOfBirth = Some(DateModel("10-10-2000")),
          spouseFirstName = Some("Testforename"),
          spouseSurname = Some("Testsurname")
        ),
        Marriage(
          maritalStatus = Some(DIVORCED),
          startDate = Some(DateModel("10-10-1990")),
          endDate = Some(DateModel("10-10-2000")),
          spouseNino = Some("AA000000C"),
          spouseDateOfBirth = Some(DateModel("10-10-2000")),
          spouseFirstName = Some("Othertestforename"),
          spouseSurname = Some("Testsurname")
        )
      )
    ),
    applicantOrigin = Some(
      OriginData(
        birthTown = Some("ATown"),
        birthProvince = Some("SomeProvince"),
        birthCountryCode = Some("GBR"),
        birthSurname = Some("ASurname"),
        maternalForename = Some("MotherForename"),
        maternalSurname = Some("AnotherSurname"),
        paternalForename = Some("AForename"),
        paternalSurname = Some("ASurname"),
        foreignSocialSecurity = Some("SomeSocialSecurityNumber"),
        lastEUAddress = Some(
          LastEUAddress(
            Some(AddressLine("4 AStreetName")),
            Some(AddressLine("Some")),
            Some(AddressLine("Old")),
            Some(AddressLine("Place")),
            Some(AddressLine("ItsTheFinalLine"))
          )
        )
      )
    ),
    applicantPriorResidency = Some(
      Seq(
        PriorResidencyModel(Some(DateModel("10-10-1990")), Some(DateModel("10-10-2000"))),
        PriorResidencyModel(Some(DateModel("10-10-1990")), Some(DateModel("10-10-2000")))
      )
    ),
    nationalityCode = Some("GBR")
  )

}
