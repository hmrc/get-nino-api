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

import play.api.Logger
import play.api.libs.json._

sealed trait Gender {
  val value: String
}

object Gender {
  def valueCheck(input: String): Gender = {
    input match {
      case Male.value => Male
      case Female.value => Female
      case GenderNotKnown.value => GenderNotKnown
      case _ =>
        Logger.debug(s"[Gender][valueCheck] Provided gender failed check: $input")
        Logger.warn("[Gender][valueCheck] Provided gender failed check")
        throw new IllegalArgumentException(s"Provided gender invalid")
    }
  }

  implicit val reads: Reads[Gender] = __.read[String] map valueCheck

  implicit val writes: Writes[Gender] = Writes[Gender] { gender => JsString(gender.value) }
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
