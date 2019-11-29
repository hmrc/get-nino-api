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

package utils

import play.api.libs.json.{JsObject, Json}
import v1.models._

object NinoApplicationTestData {

  private def thisOrThatDate(implicit isWrite: Boolean): String = {
    if (isWrite) "2020-10-10" else "10-10-2020"
  }

  val jsonMin: Boolean => JsObject = implicit isWrite => Json.obj(
    "nino" -> "TC452994B",
    "gender" -> "MALE",
    "entryDate" -> thisOrThatDate,
    "birthDate" -> thisOrThatDate,
    "birthDateVerification" -> "VERIFIED",
    "officeNumber" -> "1234",
    "country" -> 1,
    "name" -> Json.obj(
      "surname" -> "ASurname",
      "startDate" -> thisOrThatDate
    ),
    "address" -> Json.obj(
      "line1" -> "4 AStreetName",
      "startDate" -> thisOrThatDate
    )
  )

  val jsonFaulty: Boolean => JsObject = implicit isWrite => Json.obj(
    "nino" -> "TC452994BAAAAAAAAA",
    "gender" -> "MALE",
    "entryDate" -> thisOrThatDate,
    "birthDate" -> thisOrThatDate,
    "birthDateVerification" -> "VERIFIED",
    "officeNumber" -> "1234KJAHSDKJHA*SHDH£HA{DA:SLLFJKALSJF",
    "contactNumber" -> "'sd;f][a;w3#'f;#'s",
    "country" -> 1,
    "name" -> Json.obj(
      "surname" -> "ASurname",
      "startDate" -> thisOrThatDate
    ),
    "address" -> Json.obj(
      "line1" -> "4 AStreetName",
      "startDate" -> thisOrThatDate
    )
  )

  val jsonMax: Boolean => JsObject = implicit isWrite => Json.obj(
    "nino" -> "TC452994B",
    "gender" -> "MALE",
    "entryDate" -> thisOrThatDate,
    "birthDate" -> thisOrThatDate,
    "birthDateVerification" -> "VERIFIED",
    "officeNumber" -> "1234",
    "contactNumber" -> "1234567890",
    "country" -> 1,
    "name" -> Json.obj(
      "title" -> "MR",
      "forename" -> "AForename",
      "secondForename" -> "NotSure",
      "surname" -> "ASurname",
      "startDate" -> thisOrThatDate,
      "endDate" -> thisOrThatDate
    ),
    "historicNames" -> Json.arr(
      Json.obj(
        "title" -> "MRS",
        "forename" -> "AForename",
        "secondForename" -> "NotSure",
        "surname" -> "ASurname",
        "startDate" -> thisOrThatDate,
        "endDate" -> thisOrThatDate
      ),
      Json.obj(
        "title" -> "MISS",
        "forename" -> "AForename",
        "secondForename" -> "NotSure",
        "surname" -> "ASurname",
        "startDate" -> thisOrThatDate,
        "endDate" -> thisOrThatDate
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
      "startDate" -> thisOrThatDate,
      "endDate" -> thisOrThatDate
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
        "startDate" -> thisOrThatDate,
        "endDate" -> thisOrThatDate
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
        "startDate" -> thisOrThatDate,
        "endDate" -> thisOrThatDate
      )
    ),
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
      Json.obj("priorStartDate" -> thisOrThatDate, "priorEndDate" -> thisOrThatDate),
      Json.obj("priorStartDate" -> thisOrThatDate, "priorEndDate" -> thisOrThatDate)
    ),
    "abroadLiability" -> Json.obj("liabilityStartDate" -> thisOrThatDate, "liabilityEndDate" -> thisOrThatDate)
  )

  val modelMin: NinoApplication = {
    implicit val isWrite: Boolean = false
    NinoApplication(
      "TC452994B",
      Male,
      DateModel(thisOrThatDate),
      DateModel(thisOrThatDate),
      Verified,
      "1234",
      None,
      1,
      NameModel(
        surname = "ASurname",
        startDate = DateModel(thisOrThatDate)
      ),
      None,
      AddressModel(
        None,
        AddressLine("4 AStreetName"),
        None, None, None, None, None, None,
        DateModel(thisOrThatDate), None
      ),
      None, None, None, None
    )
  }

  val modelFaulty: NinoApplication = {
    implicit val isWrite: Boolean = false
    NinoApplication(
      "TC452994BAAAAAAAAA",
      Male,
      DateModel(thisOrThatDate),
      DateModel(thisOrThatDate),
      Verified,
      "1234",
      None,
      1,
      NameModel(
        surname = "ASurname",
        startDate = DateModel(thisOrThatDate)
      ),
      None,
      AddressModel(
        None,
        AddressLine("4 AStreetName"),
        None, None, None, None, None, None,
        DateModel(thisOrThatDate), None
      ),
      None, None, None, None
    )
  }


  val modelMax: NinoApplication = {
    implicit val isWrite: Boolean = false
    NinoApplication(
      "TC452994B",
      Male,
      DateModel(thisOrThatDate),
      DateModel(thisOrThatDate),
      Verified,
      "1234",
      Some("1234567890"),
      1,
      NameModel(
        Some("MR"),
        Some("AForename"),
        Some("NotSure"),
        "ASurname",
        DateModel(thisOrThatDate),
        Some(DateModel(thisOrThatDate))
      ),
      Some(Seq(
        NameModel(
          Some("MRS"),
          Some("AForename"),
          Some("NotSure"),
          "ASurname",
          DateModel(thisOrThatDate),
          Some(DateModel(thisOrThatDate))
        ),
        NameModel(
          Some("MISS"),
          Some("AForename"),
          Some("NotSure"),
          "ASurname",
          DateModel(thisOrThatDate),
          Some(DateModel(thisOrThatDate))
        )
      )),
      AddressModel(
        Some(Residential),
        AddressLine("4 AStreetName"),
        Some(AddressLine("Some")),
        Some(AddressLine("Old")),
        Some(AddressLine("Place")),
        Some(AddressLine("ItsTheFinalLine")),
        Some(Postcode("AA11AA")),
        Some("GBR"),
        DateModel(thisOrThatDate),
        Some(DateModel(thisOrThatDate))
      ),
      Some(Seq(
        AddressModel(
          Some(Residential),
          AddressLine("1 AStreetName"),
          Some(AddressLine("Some")),
          Some(AddressLine("Old")),
          Some(AddressLine("Place")),
          Some(AddressLine("ItsTheFinalLine")),
          Some(Postcode("AA11AA")),
          Some("GBR"),
          DateModel(thisOrThatDate),
          Some(DateModel(thisOrThatDate))
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
          DateModel(thisOrThatDate),
          Some(DateModel(thisOrThatDate))
        )
      )),
      Some(OriginData(
        Some("ATown"),
        Some("SomeProvince"),
        Some(200),
        Some(2),
        Some("ASurname"),
        Some("MotherForename"),
        Some("AnotherSurname"),
        Some("AForename"),
        Some("ASurname"),
        Some("SomeSocialSecurityNumber"),
        Some(LastEUAddress(
          Some(AddressLine("4 AStreetName")),
          Some(AddressLine("Some")),
          Some(AddressLine("Old")),
          Some(AddressLine("Place")),
          Some(AddressLine("ItsTheFinalLine"))
        ))
      )),
      Some(Seq(
        PriorResidencyModel(Some(DateModel(thisOrThatDate)), Some(DateModel(thisOrThatDate))),
        PriorResidencyModel(Some(DateModel(thisOrThatDate)), Some(DateModel(thisOrThatDate)))
      )),
      Some(
        AbroadLiabilityModel(Some(DateModel(thisOrThatDate)), Some(DateModel(thisOrThatDate)))
      )
    )
  }
}
