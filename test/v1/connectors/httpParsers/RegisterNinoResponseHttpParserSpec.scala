/*
 * Copyright 2023 HM Revenue & Customs
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

import play.api.http.Status._
import play.api.libs.json.Json
import support.UnitSpec
import uk.gov.hmrc.http.HttpResponse
import v1.connectors.httpParsers.RegisterNinoResponseHttpParser._
import v1.models.errors.{DownstreamValidationError, ServiceUnavailableError}

class RegisterNinoResponseHttpParserSpec extends UnitSpec {

  private val successfulResponse: HttpResponse = HttpResponse(ACCEPTED, "")

  private def unsuccessfulResponse(status: Int = FORBIDDEN, code: String, reason: String): HttpResponse = HttpResponse(
    status,
    json = Json.obj(
      "code"   -> code,
      "reason" -> reason
    ),
    Map.empty
  )

  "The RegisterNinoResponseHttpParser" should {
    "return successful response model" when {
      "DES returns 202 ACCEPTED" in {
        RegisterNinoResponseReads.read("", "", successfulResponse) shouldBe Right(())
      }
    }

    "return DownstreamValidationError model" when {
      def test(desCode: String, desReason: String, expectedReason: String): Unit =
        s"DES returns 403 FORBIDDEN with code $desCode" in {
          RegisterNinoResponseReads
            .read(
              method = "",
              url = "",
              response = unsuccessfulResponse(code = desCode, reason = desReason)
            ) shouldBe Left(DownstreamValidationError(desCode, expectedReason))
        }

      val inputArgs = Seq(
        (
          "ACCOUNT_ALREADY_EXISTS",
          "The remote endpoint has indicated that the account already exists.",
          "The remote endpoint has indicated that the account already exists."
        ),
        (
          "INVALID_ENTRY_DATE",
          "The remote endpoint has indicated that the entry date for the customer is less than 13 years.",
          "Validation Error: the applicant is less than 13 years of age at the date of entry"
        )
      )

      inputArgs.foreach(args => (test _).tupled(args))
    }

    "return ServiceUnavailableError model" when {
      "DES returns 503 SERVICE_UNAVAILABLE with code SERVICE_UNAVAILABLE" in {
        RegisterNinoResponseReads
          .read(
            method = "",
            url = "",
            response = unsuccessfulResponse(
              status = SERVICE_UNAVAILABLE,
              code = "SERVICE_UNAVAILABLE",
              reason = "Dependent systems are currently not responding."
            )
          ) shouldBe Left(ServiceUnavailableError)
      }
    }
  }
}
