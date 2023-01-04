/*
 * Copyright 2022 HM Revenue & Customs
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
import java.time.{LocalDate, ZoneId}

import play.api.libs.json._
import support.UnitSpec

class NameModelSpec extends UnitSpec {

  private def readWriteDate(
    isWrite: Boolean,
    writeDate: String = "2000-10-10",
    readDate: String = "10-10-2000"
  ): String =
    if (isWrite) writeDate else readDate

  private val minJson: JsObject = Json.obj(
    "surname"  -> "MinimumMan",
    "nameType" -> "REGISTERED"
  )

  private val maxJson: Boolean => JsObject = isWrite => {
    val firstNamePath  = if (isWrite) "firstName" else "forename"
    val middleNamePath = if (isWrite) "middleName" else "secondForename"

    Json.obj(
      "title"        -> "MR",
      firstNamePath  -> "First",
      middleNamePath -> "Middle",
      "surname"      -> "Last",
      "startDate"    -> readWriteDate(isWrite, "1990-10-10", "10-10-1990"),
      "endDate"      -> readWriteDate(isWrite),
      "nameType"     -> "REGISTERED"
    )
  }

  private val faultyStartDateModelJson: JsObject = Json.obj(
    "surname"   -> "Miles",
    "nameType"  -> "REGISTERED",
    "startDate" -> LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
  )

  private val faultyEndDateModelJson: JsObject = Json.obj(
    "surname"  -> "Miles",
    "nameType" -> "REGISTERED",
    "endDate"  -> LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
  )

  private val beforeDateAfterEndDateModelJson: JsObject = Json.obj(
    "surname"   -> "Miles",
    "nameType"  -> "REGISTERED",
    "startDate" -> LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
    "endDate"   -> LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
  )

  private val maxModel: NameModel = NameModel(
    Some("MR"),
    Some("First"),
    Some("Middle"),
    "Last",
    Some(DateModel("10-10-1990")),
    Some(DateModel("10-10-2000")),
    nameType = "REGISTERED"
  )

  private val minModel: NameModel = NameModel(
    surname = "MinimumMan",
    nameType = "REGISTERED"
  )

  "NameModel" when {
    ".reads" should {
      "correctly parse from Json" when {
        "all optional fields are present" in {
          maxJson(false).as[NameModel] shouldBe maxModel
        }

        "no optional fields are present" in {
          minJson.as[NameModel] shouldBe minModel
        }
      }

      "fail to parse from json" when {
        "the start date given is after the current date" in {
          val result: JsResultException = intercept[JsResultException] {
            faultyStartDateModelJson.as[NameModel]
          }

          result.getMessage should include("startDate")
          result.getMessage should include("The date provided is after today. The date must be today or before.")
        }

        "the end date given is after the current date" in {
          val result: JsResultException = intercept[JsResultException] {
            faultyEndDateModelJson.as[NameModel]
          }

          result.getMessage should include("endDate")
          result.getMessage should include("The date provided is after today. The date must be today or before.")
        }

        "the start date given is after the end date given" in {
          val result: JsResultException = intercept[JsResultException] {
            beforeDateAfterEndDateModelJson.as[NameModel]
          }

          result.getMessage should include("endDate")
          result.getMessage should include("The given start date is after the given end date.")
        }
      }
    }

    ".writes" should {
      "correctly parse to Json" when {
        "all optional fields are present" in {
          Json.toJson(maxModel) shouldBe maxJson(true)
        }

        "no optional fields are present" in {
          Json.toJson(minModel) shouldBe minJson
        }
      }
    }

    ".validateTitle" should {
      "return true" when {
        "a valid title is input" which {
          val validTitle: Seq[String] = Seq(
            "NOT KNOWN",
            "MR",
            "MRS",
            "MISS",
            "MS",
            "DR",
            "REV"
          )

          validTitle.foreach(title =>
            s"is $title" in {
              NameModel.validateTitle(Some(title)) shouldBe true
            }
          )
        }
      }

      "return false" when {
        "an invalid title is entered" in {
          NameModel.validateTitle(Some("NOT A TITLE")) shouldBe false
        }
      }
    }

    ".validateType" should {
      "return true" when {
        "a valid type is input" which {
          val validTitle: Seq[String] = Seq(
            "REGISTERED",
            "ALIAS"
          )

          validTitle.foreach(nameType =>
            s"is $nameType" in {
              NameModel.validateType(nameType) shouldBe true
            }
          )
        }
      }

      "return false" when {
        "an invalid type is entered" in {
          NameModel.validateType("NOT A TYPE") shouldBe false
        }
      }
    }

    ".validateName" should {
      def runValidation[T](input: T, fieldName: String): Boolean = NameModel.validateName(input, fieldName)

      "return true" when {
        "a valid String is entered" which {
          "is less than the maximum length (35)" in {
            runValidation("Immaname", "SomeNameType") shouldBe true
          }

          "is equal to the maximum length (35)" in {
            runValidation("Thisnameissolongthatitmightjustcuto", "SomeNameType") shouldBe true
          }

          "is equal to the minimum length (3)" in {
            runValidation("Hij", "SomeNameType") shouldBe true
          }
        }

        "a valid Optional String is entered" in {
          runValidation(Some("Immaoptionalname"), "SomeNameType") shouldBe true
        }

        "a None is entered" in {
          runValidation(None, "SomeNameType") shouldBe true
        }
      }

      "return false" when {
        "the name has disallowed special characters" in {
          runValidation("-=[];#'/./", "SomeNameType") shouldBe false
        }

        "the name is longer than 99 characters" in {
          runValidation(
            "Thisnameisreallyreallylongmorethanthanninentyninecharacterskindoflongnobodyshouldhaveanamethatisthis",
            "SomeNameType"
          ) shouldBe false
        }

        "the name is less than 3 characters" in {
          runValidation("as", "SomeNameType") shouldBe false
        }

        "a value is entered that is not a String, or an Optional String (including None)" in {
          runValidation(1, "SomeNameType") shouldBe false
        }
      }
    }

    ".validateDateAsPriorDate" should {
      val currentDate: DateModel =
        DateModel(LocalDate.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))

      "return true" when {
        "only one or neither date is provided" in {
          NameModel.validateDateAsPriorDate(Some(currentDate), None) shouldBe true
          NameModel.validateDateAsPriorDate(None, Some(currentDate)) shouldBe true
          NameModel.validateDateAsPriorDate(None, None)              shouldBe true
        }

        "the first date provided is before the second" in {
          val validDate: DateModel = DateModel("01-01-2000")

          NameModel.validateDateAsPriorDate(Some(validDate), Some(currentDate)) shouldBe true
        }

        "the provided dates are equal and canBeEqual is true" in {
          NameModel.validateDateAsPriorDate(Some(currentDate), Some(currentDate)) shouldBe true
        }
      }

      "return false" when {
        "the first date provided is after the second" in {
          val invalidDate: DateModel = DateModel(
            LocalDate.now(ZoneId.of("UTC")).plusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
          )

          NameModel.validateDateAsPriorDate(Some(invalidDate), Some(currentDate)) shouldBe false
        }

        "the provided dates are equal and canBeEqual is false" in {
          NameModel.validateDateAsPriorDate(
            Some(currentDate),
            Some(currentDate),
            canBeEqual = false
          ) shouldBe false
        }
      }
    }
  }
}
