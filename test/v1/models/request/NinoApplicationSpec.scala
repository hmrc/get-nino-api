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

import play.api.libs.json.{JsResultException, Json}
import support.UnitSpec
import utils.NinoApplicationTestData.*

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NinoApplicationSpec extends UnitSpec {

  private val validNino   = "YG285464A"
  private val invalidNino = "1234567890ASDFGHJKL"

  private val validOfficeNumberMin          = "1"
  private val validOfficeNumberMid          = "123456"
  private val validOfficeNumberMax          = "1234567890"
  private val invalidOfficeNumberCharacters = "thishasletterssoisntanumber"
  private val invalidOfficeNumberLength     = "12345678901"
  private val invalidOfficeNumberNoLength   = ""

  private val validContactNumberMin          = "1"
  private val validContactNumberMid          = "12345678901234567893456"
  private val validContactNumberMax          = "123456789012345678901234567890123456789012345678901234567890123456789012"
  private val invalidContactNumberCharacters = "thishasletterssoisntanumber"
  private val invalidContactNumberLength     = "1234567890123456789012345678901234567890123456789012345678901234567890123"
  private val invalidContactNumberNoLength   = ""

  private val ninoRegex = "^([ACEHJLMOPRSWXY][A-CEGHJ-NPR-TW-Z]|B[A-CEHJ-NPR-TW-Z]|" +
    "G[ACEGHJ-NPR-TW-Z]|[KT][A-CEGHJ-MPR-TW-Z]|N[A-CEGHJL-NPR-SW-Z]|Z[A-CEGHJ-NPR-TW-Y])[0-9]{6}[A-D]$"

  private val officeNumberRegex  = "^([0-9]{1,10})$"
  private val contactNumberRegex = "^([0-9]{1,72})$"

  private def validateNino: String => Boolean = input => NinoApplication.validateAgainstRegex(input, ninoRegex)

  private def validateOffice: String => Boolean = input =>
    NinoApplication.validateAgainstRegex(input, officeNumberRegex)

  private def validateContact: String => Boolean = input =>
    NinoApplication.validateAgainstRegex(input, contactNumberRegex)

  "NinoApplication" when {
    ".reads" should {
      "correctly parse from json" when {
        "using json with all optional fields filled in" in {
          maxRegisterNinoRequestJson(false).as[NinoApplication] shouldBe maxRegisterNinoRequestModel
        }

        "using json with no optional fields filled in" in {
          minRegisterNinoRequestJson(false).as[NinoApplication] shouldBe minRegisterNinoRequestModel
        }
      }

      "fail to parse from json" when {
        "one of the fields fails validation" which {
          val expectedException: JsResultException = intercept[JsResultException] {
            faultyRegisterNinoRequestJson(false).as[NinoApplication]
          }

          "generates an exception for the nino field" in {
            expectedException.getMessage should include(
              "There has been an error parsing the nino field. Please check against the regex."
            )
          }

          "generates an exception for the country code field" in {
            expectedException.getMessage should include(
              "There has been an error parsing the nationality code field. Please check against the regex."
            )
          }

          "generates an exception for the office number field" in {
            expectedException.getMessage should include(
              "There has been an error parsing the office number field. Please check against the regex."
            )
          }

          "generates an exception for the contact number field" in {
            expectedException.getMessage should include(
              "There has been an error parsing the contact number field. Please check against the regex."
            )
          }
        }
      }
    }

    ".writes" should {
      "correctly parse to json" when {
        "all optional fields are filled in" in {
          Json.toJson(maxRegisterNinoRequestModel) shouldBe maxRegisterNinoRequestJson(true)
        }

        "no optional fields are filled in" in {
          Json.toJson(minRegisterNinoRequestModel) shouldBe minRegisterNinoRequestJson(true)
        }
      }
    }

    ".validateAgainstRegex" should {
      "return true" when {
        "the NINO passes validation using the nino regex" in {
          validateNino(validNino) shouldBe true
        }

        "the number passes validation with maximum length using the officeNumber regex" in {
          validateOffice(validOfficeNumberMax) shouldBe true
        }

        "the number passes validation when in between length limits using the officeNumber regex" in {
          validateOffice(validOfficeNumberMid) shouldBe true
        }

        "the number passes validation with minimum length using the officeNumber regex" in {
          validateOffice(validOfficeNumberMin) shouldBe true
        }

        "the number passes validation with maximum length using the contactNumber regex" in {
          validateContact(validContactNumberMax) shouldBe true
        }

        "the number passes validation when in between the length limits using the contactNumber regex" in {
          validateContact(validContactNumberMid) shouldBe true
        }

        "the number passes validation with minimum length using the contactNumber regex" in {
          validateContact(validContactNumberMin) shouldBe true
        }
      }

      "return false" when {
        "the NINO fails validation using the nino regex" in {
          validateNino(invalidNino) shouldBe false
        }

        "the number fails validation due to invalid characters using the officeNumber regex" in {
          validateOffice(invalidOfficeNumberCharacters) shouldBe false
        }

        "the number fails validation due to length using the officeNumber regex" in {
          validateOffice(invalidOfficeNumberLength) shouldBe false
        }

        "the number fails validation due to having zero length using the officeNumber regex" in {
          validateOffice(invalidOfficeNumberNoLength) shouldBe false
        }

        "the number fails validation due to invalid characters using the contactNumber regex" in {
          validateContact(invalidContactNumberCharacters) shouldBe false
        }

        "the number fails validation due to length using the contactNumber regex" in {
          validateContact(invalidContactNumberLength) shouldBe false
        }

        "the number fails validation due to having zero length using the contactNumber regex" in {
          validateContact(invalidContactNumberNoLength) shouldBe false
        }
      }
    }

    ".validateAge" should {
      val (year, month, day): (Long, Long, Long) = (15L, 8L, 1L)
      val today: LocalDateTime                   = LocalDateTime.now()

      "return true" when {
        "the provided age is above 15 years and 8 months" in {
          val validAge: LocalDateTime        = today.minusYears(year).minusMonths(month).minusDays(day)
          val validAgeAsDateModel: DateModel = DateModel(validAge.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))

          NinoApplication.validateAge(validAgeAsDateModel) shouldBe true
        }
      }

      "return false" when {
        "the provided age is exactly 15 years and 8 months" in {
          val invalidAge: LocalDateTime        = today.minusYears(year).minusMonths(month)
          val invalidAgeAsDateModel: DateModel = DateModel(invalidAge.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))

          NinoApplication.validateAge(invalidAgeAsDateModel) shouldBe false
        }

        "the provided age is less than 15 years and 8 months" in {
          val invalidAge: LocalDateTime        = today.minusYears(year).minusMonths(month).plusDays(day)
          val invalidAgeAsDateModel: DateModel = DateModel(invalidAge.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))

          NinoApplication.validateAge(invalidAgeAsDateModel) shouldBe false
        }
      }
    }

    ".validateCountry" should {
      val (minCountryIndex, maxCountryIndex): (Int, Int) = (0, 286)

      "return true" when {
        "the country value is at the minimum" in {
          NinoApplication.validateCountry(minCountryIndex) shouldBe true
        }

        "the country value is at the maximum" in {
          NinoApplication.validateCountry(maxCountryIndex) shouldBe true
        }

        "the country value is within range" in {
          NinoApplication.validateCountry((maxCountryIndex + minCountryIndex) / 2) shouldBe true
        }
      }

      "return false" when {
        "the number is below the minimum" in {
          NinoApplication.validateCountry(minCountryIndex - 1) shouldBe false
        }

        "the number is above the maximum" in {
          NinoApplication.validateCountry(maxCountryIndex + 1) shouldBe false
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
          val seqInput = Seq(1, 2)

          NinoApplication.seqMinMaxValidation(seqInput, 1, 3) shouldBe true
        }

        "a sequence length is on the lower bound of the limits" in {
          val seqInput = Seq(1)

          NinoApplication.seqMinMaxValidation(seqInput, 1, 3) shouldBe true
        }

        "a sequence length is on the upper bound of the limits" in {
          val seqInput = Seq(1, 2, 3)

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

    ".validateOneResidentialAddress" should {
      "return true" when {
        "at least one NameModel provided contains a REGISTERED name" in {
          NinoApplication.validateOneResidentialAddress(
            Seq(
              AddressModel(
                Some(Residential),
                AddressLine("Some address"),
                None,
                None,
                None,
                None,
                None,
                "GBR",
                Some(DateModel("01-01-2000")),
                None
              ),
              AddressModel(
                None,
                AddressLine("Some address"),
                None,
                None,
                None,
                None,
                None,
                "GBR",
                Some(DateModel("01-01-2000")),
                None
              )
            )
          ) shouldBe true
        }
      }

      "return false" when {
        "none of the provided names are REGISTERED" in {
          NinoApplication.validateOneResidentialAddress(
            Seq(
              AddressModel(
                None,
                AddressLine("Some address"),
                None,
                None,
                None,
                None,
                None,
                "GBR",
                Some(DateModel("01-01-2000")),
                None
              ),
              AddressModel(
                None,
                AddressLine("Some address"),
                None,
                None,
                None,
                None,
                None,
                "GBR",
                Some(DateModel("01-01-2000")),
                None
              )
            )
          ) shouldBe false
        }
      }
    }
  }

}
