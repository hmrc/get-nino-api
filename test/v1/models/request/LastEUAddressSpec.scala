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

import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsObject, Json}

class LastEUAddressSpec extends WordSpec with Matchers {

  "LastEUAddress.formats" when {


    val maximumLastEUAddressModel = LastEUAddress(
      Some(AddressLine("1 line address")),
      Some(AddressLine("2 line address")),
      Some(AddressLine("3 line address")),
      Some(AddressLine("4 line address")),
      Some(AddressLine("5 line address"))
    )

    val maximumLastEUAddressJson: Boolean => JsObject = implicit isWrite => {
      val addressLinePrefix = (lineNo: Int) => if (isWrite) s"addressLine$lineNo" else s"line$lineNo"
      Json.obj(
        addressLinePrefix(1) -> "1 line address",
        addressLinePrefix(2) -> "2 line address",
        addressLinePrefix(3) -> "3 line address",
        addressLinePrefix(4) -> "4 line address",
        addressLinePrefix(5) -> "5 line address"
      )
    }

    val minimumLastEUAddressJson = Json.obj()

    "reading JSON" when {
      "provided with the maximum number of data items" should {

        "return a LastEUAddress model" in {

          maximumLastEUAddressJson(false).as[LastEUAddress] shouldBe maximumLastEUAddressModel
        }
      }

      "provided with the minimum number of data items" should {

        "return a LastEUAddress model" in {

          minimumLastEUAddressJson.as[LastEUAddress] shouldBe LastEUAddress()
        }
      }
    }

    "writing to JSON" when {
      "provided with the maximum number of data items" should {

        "parse to json correctly" in {

         Json.toJson(maximumLastEUAddressModel) shouldBe maximumLastEUAddressJson(true)

        }
      }

      "provided with the minimum number of data items" should {

        "parse to json correctly" in {

          Json.toJson(LastEUAddress()) shouldBe minimumLastEUAddressJson
        }
      }
    }
  }
}
