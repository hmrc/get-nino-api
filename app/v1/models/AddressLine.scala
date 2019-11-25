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

import play.api.libs.json.{JsString, Reads, Writes, __}

import scala.util.matching.Regex

case class AddressLine(addressLine: String)

object AddressLine {

  implicit val reads: Reads[AddressLine] = __.read[String] map regexCheck

  implicit val writes: Writes[AddressLine] = Writes { value => JsString(value.addressLine) }

  val regex: Regex = "^(?=.{1,35}$)([A-Z0-9][A-Za-z0-9-'.& ]+[A-Za-z0-9])$".r

  def regexCheck(value: String): AddressLine = regex.findFirstIn(value) match {
    case Some(_) => AddressLine(value)
    case None => throw new IllegalArgumentException("Invalid AddressLine")
  }
}
