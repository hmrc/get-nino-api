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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import play.api.libs.json.{JsResultException, Json}
import support.UnitSpec
import utils.NinoApplicationTestData._

class NinoApplicationSpec extends UnitSpec {

  val validNino = "YG285464A"
  val invalidNino = "1234567890ASDFGHJKL"

  val validOfficeNumberMin = "1"
  val validOfficeNumberMid = "123456"
  val validOfficeNumberMax = "1234567890"
  val invalidOfficeNumberCharacters = "thishasletterssoisntanumber"
  val invalidOfficeNumberLength = "12345678901"
  val invalidOfficeNumberNoLength = ""

  val validContactNumberMin = "1"
  val validContactNumberMid = "12345678901234567893456"
  val validContactNumberMax = "123456789012345678901234567890123456789012345678901234567890123456789012"
  val invalidContactNumberCharacters = "thishasletterssoisntanumber"
  val invalidContactNumberLength = "1234567890123456789012345678901234567890123456789012345678901234567890123"
  val invalidContactNumberNoLength = ""

  private val ninoRegex = "^([ACEHJLMOPRSWXY][A-CEGHJ-NPR-TW-Z]|B[A-CEHJ-NPR-TW-Z]|" +
    "G[ACEGHJ-NPR-TW-Z]|[KT][A-CEGHJ-MPR-TW-Z]|N[A-CEGHJL-NPR-SW-Z]|Z[A-CEGHJ-NPR-TW-Y])[0-9]{6}[A-D]$"
  private val officeNumberRegex = "^([0-9]{1,10})$"
  private val contactNumberRegex = "^([0-9]{1,72})$"

  private def validateNino: String => Boolean = input => NinoApplication.validateAgainstRegex(input, ninoRegex)

  private def validateOffice: String => Boolean = input => NinoApplication.validateAgainstRegex(input, officeNumberRegex)

  private def validateContact: String => Boolean = input => NinoApplication.validateAgainstRegex(input, contactNumberRegex)

  ".validateAgainstRegex" when {

    "using the nino regex" should {

      "return true" when {

        "the NINO passes validation" in {
          validateNino(validNino) shouldBe true
        }
      }

      "return false" when {

        "the NINO fails validation" in {
          validateNino(invalidNino) shouldBe false
        }
      }
    }

    "using the officeNumber regex" should {

      "return true" when {

        "the number passes validation with maximum length" in {
          validateOffice(validOfficeNumberMax) shouldBe true
        }

        "the number passes validation when in between length limits" in {
          validateOffice(validOfficeNumberMid) shouldBe true
        }

        "the number passes validation with minimum length" in {
          validateOffice(validOfficeNumberMin) shouldBe true
        }
      }

      "return false" when {

        "the number fails validation due to invalid characters" in {
          validateOffice(invalidOfficeNumberCharacters) shouldBe false
        }

        "the number fails validation due to length" in {
          validateOffice(invalidOfficeNumberLength) shouldBe false
        }

        "the number fails validation due to having zero length" in {
          validateOffice(invalidOfficeNumberNoLength) shouldBe false
        }
      }
    }

    "using the contactNumber regex" should {

      "return true" when {

        "the number passes validation with maximum length" in {
          validateContact(validContactNumberMax) shouldBe true
        }

        "the number passes validation when in between the length limits" in {
          validateContact(validContactNumberMid) shouldBe true
        }

        "the number passes validation with minimum length" in {
          validateContact(validContactNumberMin) shouldBe true
        }
      }

      "return false" when {

        "the number fails validation due to invalid characters" in {
          validateContact(invalidContactNumberCharacters) shouldBe false
        }

        "the number fails validation due to length" in {
          validateContact(invalidContactNumberLength) shouldBe false
        }

        "the number fails validation due to having zero length" in {
          validateContact(invalidContactNumberNoLength) shouldBe false
        }
      }
    }
  }

  ".validateCountry" should {

    "return true" when {

      "the country value is at the minimum" in {
        NinoApplication.validateCountry(0) shouldBe true
      }

      "the country value is at the maximum" in {
        NinoApplication.validateCountry(286) shouldBe true
      }

      "the country value is within range" in {
        NinoApplication.validateCountry(143) shouldBe true
      }
    }

    "return false" when {

      "the number is below the minimum" in {
        NinoApplication.validateCountry(-1) shouldBe false
      }

      "the number is above the maximum" in {
        NinoApplication.validateCountry(287) shouldBe false
      }
    }
  }

  ".seqMinMaxValidation" should {

    "return true" when {

      "a None is passed in" in {
        NinoApplication.seqMinMaxValidation(None, 1, 2) shouldBe true
      }

      "an optional sequence length is within the limits" in {
        val seqInput = Some(Seq(1, 2))

        NinoApplication.seqMinMaxValidation(seqInput, 1, 3) shouldBe true
      }

      "an optional sequence length is on the lower bound of the limits" in {
        val seqInput = Some(Seq(1))

        NinoApplication.seqMinMaxValidation(seqInput, 1, 3) shouldBe true
      }

      "an optional sequence length is on the upper bound of the limits" in {
        val seqInput = Some(Seq(1, 2, 3))

        NinoApplication.seqMinMaxValidation(seqInput, 1, 3) shouldBe true
      }

      "a sequence length is within the limits" in {
        val seqInput = Some(Seq(1, 2))

        NinoApplication.seqMinMaxValidation(seqInput, 1, 3) shouldBe true
      }

      "a sequence length is on the lower bound of the limits" in {
        val seqInput = Some(Seq(1))

        NinoApplication.seqMinMaxValidation(seqInput, 1, 3) shouldBe true
      }

      "a sequence length is on the upper bound of the limits" in {
        val seqInput = Some(Seq(1, 2, 3))

        NinoApplication.seqMinMaxValidation(seqInput, 1, 3) shouldBe true
      }

    }

    "return false" when {

      "an optional sequence is below the lower bound of the limits" in {
        val seqInput = Some(Seq())

        NinoApplication.seqMinMaxValidation(seqInput, 1, 3) shouldBe false
      }

      "an optional sequence is above the upper bound of the limits" in {
        val seqInput = Some(Seq("one", "two", "three", "four"))

        NinoApplication.seqMinMaxValidation(seqInput, 1, 3) shouldBe false
      }

      "a sequence is below the lower bound of the limits" in {
        val seqInput = Seq()

        NinoApplication.seqMinMaxValidation(seqInput, 1, 3) shouldBe false
      }

      "a sequence is above the upper bound of the limits" in {
        val seqInput = Seq("one", "two", "three", "four")

        NinoApplication.seqMinMaxValidation(seqInput, 1, 3) shouldBe false
      }
    }
  }

  ".validateAge" should {

    "return true" when {

      "the provided age is above 15 years and 8 months" in {
        val today = LocalDateTime.now()
        val validAge = today.minusYears(15).minusMonths(8).minusDays(1)
        val validAgeAsDateModel = DateModel(validAge.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))

        NinoApplication.validateAge(validAgeAsDateModel) shouldBe true
      }
    }

    "return false" when {

      "the provided age is exactly 15 years and 8 months" in {
        val today = LocalDateTime.now()
        val invalidAge = today.minusYears(15).minusMonths(8)
        val invalidAgeAsDateModel = DateModel(invalidAge.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))

        NinoApplication.validateAge(invalidAgeAsDateModel) shouldBe false
      }

      "the provided age is less than 15 years and 8 months" in {
        val today = LocalDateTime.now()
        val invalidAge = today.minusYears(15).minusMonths(8).plusDays(1)
        val invalidAgeAsDateModel = DateModel(invalidAge.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))

        NinoApplication.validateAge(invalidAgeAsDateModel) shouldBe false
      }
    }
  }

  ".validateOneRegisteredName" should {

    "return true" when {

      "at least one NameModel provided contains a REGISTERED name" in {
        NinoApplication.validateOneRegisteredName(
          Seq(
            NameModel(surname = "Miles", nameType = "REGISTERED"),
            NameModel(surname = "Miles", nameType = "ALIAS")
          )
        ) shouldBe true
      }
    }

    "return false" when {

      "none of the provided names are REGISTERED" in {
        NinoApplication.validateOneRegisteredName(
          Seq(
            NameModel(surname = "Miles", nameType = "ALIAS"),
            NameModel(surname = "Miles", nameType = "ALIAS")
          )
        ) shouldBe false
      }
    }

  }

  "NinoApplication" should {

    "correctly parse from json" when {

      "using json with all optional fields filled in" in {
        maxRegisterNinoRequestJson(false).as[NinoApplication] shouldBe maxRegisterNinoRequestModel
      }

      "using json with no optional fields filled in" in {
        minRegisterNinoRequestJson(false).as[NinoApplication] shouldBe minRegisterNinoRequestModel
      }
    }

    "correctly parse to json" when {

      "all optional fields are filled in" in {
        Json.toJson(maxRegisterNinoRequestModel) shouldBe maxRegisterNinoRequestJson(true)
      }

      "no optional fields are filled in" in {
        Json.toJson(minRegisterNinoRequestModel) shouldBe minRegisterNinoRequestJson(true)
      }
    }

    "fail to parse from json" when {

      "one of the fields fails validation" which {

        val expectedException: JsResultException = intercept[JsResultException] {
          faultyRegisterNinoRequestJson(false).as[NinoApplication]
        }

        "generates an exception for the nino field" in {
          expectedException.getMessage should include("There has been an error parsing the nino field. Please check against the regex.")
        }

        "generates an exception for the country code field" in {
          expectedException.getMessage should include("There has been an error parsing the nationality code field. Please check against the regex.")
        }

        "generates an exception for the office number field" in {
          expectedException.getMessage should include("There has been an error parsing the office number field. Please check against the regex.")
        }

        "generates an exception for the contact number field" in {
          expectedException.getMessage should include("There has been an error parsing the contact number field. Please check against the regex.")
        }
      }
    }
  }
}
