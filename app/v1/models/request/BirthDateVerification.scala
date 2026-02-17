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

import play.api.Logging
import play.api.libs.json.*

sealed trait BirthDateVerification {
  val value: String
}

object BirthDateVerification extends Logging {

  private[models] def birthDateValidation: String => Boolean = {
    case Unverified.value           => true
    case Verified.value             => true
    case VerificationNotKnown.value => true
    case CoegConfirmed.value        => true
    case _                          =>
      logger.warn("[BirthDateVerification][valueCheck] birthDateVerification field is invalid")
      false
  }

  implicit val reads: Reads[BirthDateVerification] = for {
    birthDateValue <-
      __.read[String].filter(JsonValidationError("Invalid Birth Date verification"))(birthDateValidation)
  } yield (birthDateValue: @unchecked) match {
    case Unverified.value           => Unverified
    case Verified.value             => Verified
    case VerificationNotKnown.value => VerificationNotKnown
    case CoegConfirmed.value        => CoegConfirmed
  }

  implicit val writes: Writes[BirthDateVerification] =
    Writes[BirthDateVerification](verificationValue => JsString(verificationValue.value))

}

case object Unverified extends BirthDateVerification {
  val value = "UNVERIFIED"
}

case object Verified extends BirthDateVerification {
  val value = "VERIFIED"
}

case object VerificationNotKnown extends BirthDateVerification {
  val value = "NOT KNOWN"
}

case object CoegConfirmed extends BirthDateVerification {
  val value = "COEG CONFIRMED"
}
