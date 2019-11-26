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

import play.api.Logger
import play.api.libs.json.{JsString, Reads, Writes, __}

sealed trait AddressType {
  val value: String
}

object AddressType {

  implicit val reads: Reads[AddressType] = __.read[String] map regexCheck

  implicit val writes: Writes[AddressType] = Writes {
    addressType => JsString(addressType.value)
  }

  def regexCheck(value: String): AddressType = value match {
    case Residential.value => Residential
    case Correspondence.value => Correspondence
    case _ =>
      Logger.warn("[AddressType][regexCheck] - Invalid address line has been provided")
      throw new IllegalArgumentException("Invalid AddressType")
  }
}

case object Residential extends AddressType {
  val value = "RESIDENTIAL"
}

case object Correspondence extends AddressType {
  val value = "CORRESPONDENCE"
}
