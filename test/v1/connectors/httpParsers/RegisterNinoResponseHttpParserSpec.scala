/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.http.Status
import play.api.libs.json.Json
import support.UnitSpec
import uk.gov.hmrc.http.HttpResponse
import v1.connectors.httpParsers.RegisterNinoResponseHttpParser._
import v1.models.errors.ServiceUnavailableError

class RegisterNinoResponseHttpParserSpec extends UnitSpec {

  val successfulResponse: HttpResponse = HttpResponse(Status.ACCEPTED, "")

  val invalidModelJson: HttpResponse = HttpResponse(Status.OK, json = Json.obj("notMessage" -> 5), Map.empty)

  val unsuccessfulResponse: HttpResponse = HttpResponse(
    Status.INTERNAL_SERVER_ERROR,
    json = Json.obj(
      "code"   -> "INVALID_DATE_OF_BIRTH",
      "reason" -> "The remote endpoint has indicated that the name Type needs to be different."
    ),
    Map.empty
  )

  "The RegisterNinoResponseHttpParser" should {
    "return a NpsResponseModel" when {
      "NPS returns an Accepted" in {
        RegisterNinoResponseReads.read("", "", successfulResponse) shouldBe Right(())
      }
    }
    "return an error model" when {
      "an unknown status is returned" in {
        RegisterNinoResponseReads
          .read("", "", unsuccessfulResponse)
          .shouldBe(
            Left(ServiceUnavailableError)
          )
      }
    }
  }
}
