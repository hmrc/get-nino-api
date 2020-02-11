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

import play.api.libs.json._
import support.UnitSpec

class NameModelSpec extends UnitSpec {

  private def readWriteDate(isWrite: Boolean): String = {
    if (isWrite) "2020-10-10" else "10-10-2020"
  }

  val minJson: Boolean => JsObject = isWrite =>
    Json.obj(
      "surname" -> "MinimumMan",
      "startDate" -> readWriteDate(isWrite)
    )

  val maxJson: Boolean => JsObject = isWrite => {
    val firstNamePath = if (isWrite) "firstName" else "forename"
    val middleNamePath = if (isWrite) "middleName" else "secondForename"
      Json.obj(
        "title" -> "MR",
        firstNamePath -> "First",
        middleNamePath -> "Middle",
        "surname" -> "Last",
        "startDate" -> readWriteDate(isWrite),
        "endDate" -> readWriteDate(isWrite)
      )
  }

  val maxModel = NameModel(
    Some("MR"),
    Some("First"),
    Some("Middle"),
    "Last",
    DateModel("10-10-2020"),
    Some(DateModel("10-10-2020"))
  )

  val minModel = NameModel(
    surname = "MinimumMan",
    startDate = DateModel("10-10-2020")
  )

  "NameModelSpec" should {
    "correctly parse to Json" when {
      "all optional fields are present" in {
        Json.toJson(maxModel) shouldBe maxJson(true)
      }

      "no optional fields are present" in {
        Json.toJson(minModel) shouldBe minJson(true)
      }
    }

    "correctly parse from Json" when {
      "all optional fields are present" in {
        maxJson(false).as[NameModel] shouldBe maxModel
      }

      "no optional fields are present" in {
        minJson(false).as[NameModel] shouldBe minModel
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

          validTitle.foreach(title => s"is $title" in {
            NameModel.validateTitle(Some(title)) shouldBe true
          })
        }
      }

      "return false" when {
        "an invalid title is entered" in {
          NameModel.validateTitle(Some("NOT A TITLE")) shouldBe false
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

        "the name is longer 35 characters" in {
          runValidation("Thisnameisreallyreallylongmorethanth", "SomeNameType") shouldBe false
        }

        "the name is less than 3 characters" in {
          runValidation("as", "SomeNameType") shouldBe false
        }

        "a value is entered that is not a String, or an Optional String (including None)" in {
          runValidation(123, "SomeNameType") shouldBe false
        }
      }
    }
  }
}
