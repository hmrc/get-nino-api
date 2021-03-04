/*
 * Copyright 2021 HM Revenue & Customs
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

sealed trait AddressType {
  val value: String
}

object AddressType {

  implicit val reads: Reads[AddressType] = for {
    addressValue <- __.read[String].filter(JsonValidationError("Unable to parse Address Type"))(validAddressTypeCheck)
  } yield {
    addressValue match {
      case Residential.value => Residential
      case Correspondence.value => Correspondence
    }
  }

  implicit val writes: Writes[AddressType] = Writes {
    addressType => JsString(addressType.value)
  }

  def validAddressTypeCheck: String => Boolean = {
    case Residential.value => true
    case Correspondence.value => true
    case _ => false
  }
}

case object Residential extends AddressType {
  val value = "RESIDENTIAL"
}

case object Correspondence extends AddressType {
  val value = "CORRESPONDENCE"
}
