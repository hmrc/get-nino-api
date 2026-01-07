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

import play.api.libs.json.{JsObject, Json}
import support.UnitSpec

class LastEUAddressSpec extends UnitSpec {

  private val (lineNo1, lineNo2, lineNo3, lineNo4, lineNo5): (Int, Int, Int, Int, Int) = (1, 2, 3, 4, 5)

  private val maximumLastEUAddressModel: LastEUAddress = LastEUAddress(
    Some(AddressLine("1 line address")),
    Some(AddressLine("2 line address")),
    Some(AddressLine("3 line address")),
    Some(AddressLine("4 line address")),
    Some(AddressLine("5 line address"))
  )

  private val minimumLastEUAddressModel: LastEUAddress = LastEUAddress()

  private val maximumLastEUAddressJson: Boolean => JsObject = implicit isWrite => {
    val addressLinePrefix = (lineNo: Int) => if (isWrite) s"addressLine$lineNo" else s"line$lineNo"
    Json.obj(
      addressLinePrefix(lineNo1) -> "1 line address",
      addressLinePrefix(lineNo2) -> "2 line address",
      addressLinePrefix(lineNo3) -> "3 line address",
      addressLinePrefix(lineNo4) -> "4 line address",
      addressLinePrefix(lineNo5) -> "5 line address"
    )
  }

  private val minimumLastEUAddressJson: JsObject = JsObject.empty

  "LastEUAddress" when {
    ".reads" should {
      "return a LastEUAddress model" when {
        "provided with the maximum number of data items" in {
          maximumLastEUAddressJson(false).as[LastEUAddress] shouldBe maximumLastEUAddressModel
        }

        "provided with the minimum number of data items" in {
          minimumLastEUAddressJson.as[LastEUAddress] shouldBe minimumLastEUAddressModel
        }
      }
    }

    ".writes" should {
      "parse to json correctly" when {
        "provided with the maximum number of data items" in {
          Json.toJson(maximumLastEUAddressModel) shouldBe maximumLastEUAddressJson(true)
        }

        "provided with the minimum number of data items" in {
          Json.toJson(LastEUAddress()) shouldBe minimumLastEUAddressJson
        }
      }
    }
  }
}
