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

import play.api.libs.json.JsString
import support.UnitSpec

class BirthDateVerificationSpec extends UnitSpec {
  val verifiedString: JsString      = JsString("VERIFIED")
  val unverifiedString: JsString    = JsString("UNVERIFIED")
  val notKnownString: JsString      = JsString("NOT KNOWN")
  val coegConfirmedString: JsString = JsString("COEG CONFIRMED")

  "BirthDateVerification" should {

    "correctly parse from Json" when {

      "value provided is VERIFIED" in {
        verifiedString.as[BirthDateVerification] shouldBe Verified
      }

      "value provided is UNVERIFIED" in {
        unverifiedString.as[BirthDateVerification] shouldBe Unverified
      }

      "value provided is NOT KNOWN" in {
        notKnownString.as[BirthDateVerification] shouldBe VerificationNotKnown
      }

      "value provided is COEG CONFIRMED" in {
        coegConfirmedString.as[BirthDateVerification] shouldBe CoegConfirmed
      }
    }

    "correctly parse to Json" when {
      "value provided is VERIFIED" in {
        Verified shouldBe verifiedString.as[BirthDateVerification]
      }

      "value provided is UNVERIFIED" in {
        Unverified shouldBe unverifiedString.as[BirthDateVerification]
      }

      "value provided is NOT KNOWN" in {
        VerificationNotKnown shouldBe notKnownString.as[BirthDateVerification]
      }

      "value provided is COEG CONFIRMED" in {
        CoegConfirmed shouldBe coegConfirmedString.as[BirthDateVerification]
      }
    }

    "fail to parse from json" when {
      "the value provided is not one of the valid values" in {
        JsString("Incorrect value").validate[BirthDateVerification].isError shouldBe true
      }
    }
  }

  "BirthDateVerification .birthDateValidation" when {

    "provided with VERIFIED" should {

      "return true" in {
        BirthDateVerification.birthDateValidation(Verified.value) shouldBe true
      }
    }

    "provided with UNVERIFIED" should {

      "return true" in {
        BirthDateVerification.birthDateValidation(Unverified.value) shouldBe true
      }
    }

    "provided with NOT KNOWN" should {

      "return true" in {
        BirthDateVerification.birthDateValidation(VerificationNotKnown.value) shouldBe true
      }
    }

    "provided with COEG CONFIRMED" should {

      "return true" in {
        BirthDateVerification.birthDateValidation(CoegConfirmed.value) shouldBe true
      }
    }

    "provided with an invalid value" should {

      "return false" in {
        BirthDateVerification.birthDateValidation("Invalid value") shouldBe false
      }
    }
  }
}
