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
import play.api.libs.json._

sealed trait Gender {
  val value: String
}

object Gender extends Logging {
  private[models] def validGenderCheck: String => Boolean = {
    case Male.value           => true
    case Female.value         => true
    case GenderNotKnown.value => true
    case _                    =>
      logger.warn("[Gender][valueCheck] Provided gender failed check")
      false

  }

  implicit val reads: Reads[Gender] = for {
    genderValue <- __.read[String].filter(JsonValidationError("Error parsing gender"))(validGenderCheck)
  } yield (genderValue: @unchecked) match {
    case Male.value           => Male
    case Female.value         => Female
    case GenderNotKnown.value => GenderNotKnown
  }

  implicit val writes: Writes[Gender] = Writes[Gender](gender => JsString(gender.value))
}

case object Male extends Gender {
  val value: String = "MALE"
}

case object Female extends Gender {
  val value: String = "FEMALE"
}

case object GenderNotKnown extends Gender {
  val value: String = "NOT-KNOWN"
}
