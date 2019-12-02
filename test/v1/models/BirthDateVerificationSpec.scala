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

package v1.models

import play.api.libs.json.{JsString, Json}
import support.UnitSpec

class BirthDateVerificationSpec extends UnitSpec {
  val verifiedString = JsString("VERIFIED")
  val unverifiedString = JsString("UNVERIFIED")
  val notKnownString = JsString("NOT KNOWN")
  val coegConfirmedString = JsString("COEG CONFIRMED")

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
        Json.toJson(Verified) shouldBe verifiedString
      }
      "value provided is UNVERIFIED" in {
        Json.toJson(Unverified) shouldBe unverifiedString
      }
      "value provided is NOT KNOWN" in {
        Json.toJson(VerificationNotKnown) shouldBe notKnownString
      }
      "value provided is COEG CONFIRMED" in {
        Json.toJson(CoegConfirmed) shouldBe coegConfirmedString
      }
    }

    "fail to parse from json" when {
      "the value provided is not one of the valid values" in {
        val expectedException = intercept[IllegalArgumentException] {
          JsString("I SAID SO").as[BirthDateVerification]
        }

        expectedException.getMessage shouldBe "birthDateVerification field is invalid"
      }
    }
  }
}
