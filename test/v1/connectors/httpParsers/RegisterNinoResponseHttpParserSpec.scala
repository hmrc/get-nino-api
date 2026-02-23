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

package v1.connectors.httpParsers

import play.api.http.Status.*
import play.api.libs.json.*
import support.UnitSpec
import uk.gov.hmrc.http.HttpResponse
import v1.connectors.httpParsers.RegisterNinoResponseHttpParser.*
import v1.models.errors.*

class RegisterNinoResponseHttpParserSpec extends UnitSpec {

  private val successfulResponse: HttpResponse = HttpResponse(ACCEPTED, "")

  private def unsuccessfulResponse(status: Int = FORBIDDEN, jsonBody: JsObject): HttpResponse = HttpResponse(
    status,
    json = jsonBody,
    headers = Map.empty
  )

  "RegisterNinoResponseHttpParser" should {
    "return successful response" when {
      "DES returns 202 ACCEPTED" in {
        RegisterNinoResponseReads.read("", "", successfulResponse) shouldBe Right(())
      }
    }

    "return DownstreamValidationError response" when {
      def test(desCode: String, desReason: String, expectedReason: String): Unit =
        s"DES returns 403 FORBIDDEN with code $desCode" in {
          val desJsonBody: JsObject = Json.obj(
            "code"   -> desCode,
            "reason" -> desReason
          )

          RegisterNinoResponseReads
            .read(
              method = "",
              url = "",
              response = unsuccessfulResponse(jsonBody = desJsonBody)
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

      inputArgs.foreach(args => test.tupled(args))
    }

    "return ServiceUnavailableError response" when {
      "DES returns 400 BAD_REQUEST with a body" that {
        "cannot be parsed" in {
          RegisterNinoResponseReads
            .read(
              method = "",
              url = "",
              response = unsuccessfulResponse(
                status = BAD_REQUEST,
                jsonBody = JsObject.empty
              )
            ) shouldBe Left(ServiceUnavailableError)
        }

        "results in an exception when read" in {
          RegisterNinoResponseReads
            .read(
              method = "",
              url = "",
              response = unsuccessfulResponse(
                status = BAD_REQUEST,
                jsonBody = None.orNull
              )
            ) shouldBe Left(ServiceUnavailableError)
        }
      }

      "DES returns 503 SERVICE_UNAVAILABLE with code SERVICE_UNAVAILABLE" in {
        val desJsonBody: JsObject = Json.obj(
          "code"   -> "SERVICE_UNAVAILABLE",
          "reason" -> "Dependent systems are currently not responding."
        )

        RegisterNinoResponseReads
          .read(
            method = "",
            url = "",
            response = unsuccessfulResponse(
              status = SERVICE_UNAVAILABLE,
              jsonBody = desJsonBody
            )
          ) shouldBe Left(ServiceUnavailableError)
      }
    }
  }

}
