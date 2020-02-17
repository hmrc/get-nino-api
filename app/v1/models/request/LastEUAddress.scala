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

import play.api.libs.json.{Json, OFormat, Reads}
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class LastEUAddress(
                          addressLine1: Option[AddressLine] = None,
                          addressLine2: Option[AddressLine] = None,
                          addressLine3: Option[AddressLine] = None,
                          addressLine4: Option[AddressLine] = None,
                          addressLine5: Option[AddressLine] = None
                        )

object LastEUAddress {

  implicit val reads: Reads[LastEUAddress] = (
    (__ \ "line1").readNullable[AddressLine] and
    (__ \ "line2").readNullable[AddressLine] and
    (__ \ "line3").readNullable[AddressLine] and
    (__ \ "line4").readNullable[AddressLine] and
    (__ \ "line5").readNullable[AddressLine]
  )(LastEUAddress.apply _)

  implicit val writes: Writes[LastEUAddress] = Json.writes[LastEUAddress]
}
