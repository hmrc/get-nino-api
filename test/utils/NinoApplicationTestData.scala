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
    "country" -> 1,
    "name" -> Json.obj(
      "surname" -> "ASurname",
      "startDate" -> writeOrReadDate
    ),
    "address" -> Json.obj(
      "line1" -> "4 AStreetName",
      "startDate" -> writeOrReadDate
    )
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

  val minRegisterNinoRequestModel: NinoApplication = {
    implicit val isWrite: Boolean = false
    NinoApplication(
      "TC452994B",
      Male,
      DateModel(writeOrReadDate),
      DateModel(writeOrReadDate),
      Verified,
      "1234",
      None,
      1,
      NameModel(
        surname = "ASurname",
        startDate = DateModel(writeOrReadDate)
      ),
      None,
      AddressModel(
        None,
        AddressLine("4 AStreetName"),
        None, None, None, None, None, None,
        DateModel(writeOrReadDate), None
      ),
      None, None, None, None
    )
  }

  val faultyRegisterNinoRequestModel: NinoApplication = {
    implicit val isWrite: Boolean = false
    NinoApplication(
      "TC452994BAAAAAAAAA",
      Male,
      DateModel(writeOrReadDate),
      DateModel(writeOrReadDate),
      Verified,
      "1234",
      None,
      1,
      NameModel(
        surname = "ASurname",
        startDate = DateModel(writeOrReadDate)
      ),
      None,
      AddressModel(
        None,
        AddressLine("4 AStreetName"),
        None, None, None, None, None, None,
        DateModel(writeOrReadDate), None
      ),
      None, None, None, None
    )
  }


  val maxRegisterNinoRequestModel: NinoApplication = {
    implicit val isWrite: Boolean = false
    NinoApplication(
      "TC452994B",
      Male,
      DateModel(writeOrReadDate),
      DateModel(writeOrReadDate),
      Verified,
      "1234",
      Some("1234567890"),
      1,
      NameModel(
        Some("MR"),
        Some("AForename"),
        Some("NotSure"),
        "ASurname",
        DateModel(writeOrReadDate),
        Some(DateModel(writeOrReadDate))
      ),
      Some(Seq(
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
        PriorResidencyModel(Some(DateModel(writeOrReadDate)), Some(DateModel(writeOrReadDate))),
        PriorResidencyModel(Some(DateModel(writeOrReadDate)), Some(DateModel(writeOrReadDate)))
      )),
      Some(
        AbroadLiabilityModel(Some(DateModel(writeOrReadDate)), Some(DateModel(writeOrReadDate)))
      )
    )
  }
}
