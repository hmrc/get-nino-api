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

package v1.connectors.httpParsers

import support.UnitSpec
import RegisterNinoResponseHttpParser._
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import v1.models.response.DesResponseModel
import v1.models.errors.{Error, InvalidJsonResponseError}

class RegisterNinoResponseHttpParserSpec extends UnitSpec {

  val successfulResponse = HttpResponse(Status.OK, Some(
    Json.obj("message" -> "this is a valid response"))
  )

  val invalidModelJson = HttpResponse(Status.OK, Some(
    Json.obj("notMessage" -> 5))
  )

  val unsuccessfulResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR)

  "The RegisterNinoResponseHttpParser" should {
    "return a NpsResponseModel" when {
      "NPS returns an OK" in {
        RegisterNinoResponseReads.read("", "", successfulResponse) shouldBe Right(DesResponseModel("this is a valid response"))
      }
    }
    "return an error model" when {
      "valid json is passed in that fails validation as model" in {
        RegisterNinoResponseReads.read("", "", invalidModelJson) shouldBe Left(InvalidJsonResponseError)
      }
      "an unknown status is returned" in {
        RegisterNinoResponseReads.read("", "", unsuccessfulResponse)
          .shouldBe(
            Left(Error(s"${Status.INTERNAL_SERVER_ERROR}", "Downstream error returned from DES when submitting a NINO register request"))
          )
      }
    }
  }
}
