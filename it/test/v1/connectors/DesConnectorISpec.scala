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

package v1.connectors

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import config.{AppConfig, AppConfigImpl}
import play.api.http.Status.*
import play.api.libs.json.Json
import play.api.{Configuration, Logger}
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.*
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing
import utils.ItNinoApplicationTestData.*
import v1.connectors.httpParsers.HttpResponseTypes.HttpPostResponse
import v1.models.errors.{DownstreamValidationError, ServiceUnavailableError}
import v1.stubs.AuditStub

import scala.concurrent.ExecutionContext.Implicits.global
import scala.jdk.CollectionConverters.*

class DesConnectorISpec extends IntegrationBaseSpec with LogCapturing {

  private val desEndpoint: String = "/register"
  private val requestId: String   = "dcba0000-ij12-df34-jk56"

  private val uuidPattern: String = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"

  private val logDesJsonAppConfig: AppConfig = {
    val configuration = Configuration("feature-switch.logDesJson" -> true).withFallback(fakeApp.configuration)
    new AppConfigImpl(using fakeApp.injector.instanceOf[ServicesConfig], configuration)
  }

  private def desConnector(config: AppConfig = appConfig): DesConnector = new DesConnector(httpClient, config)

  private def sendRegisterRequest(
    hc: HeaderCarrier = HeaderCarrier(),
    connector: DesConnector = desConnector()
  ): HttpPostResponse =
    await(connector.sendRegisterRequest(maxRegisterNinoRequestModel)(using hc, global))

  private def stubDes(status: Int, body: String = ""): StubMapping = {
    AuditStub.audit()
    stubFor(
      post(urlEqualTo(desEndpoint))
        .willReturn(
          aResponse()
            .withStatus(status)
            .withHeader("Content-Type", "application/json")
            .withBody(body)
        )
    )
  }

  private def desError(code: String, reason: String): String = Json.obj("code" -> code, "reason" -> reason).toString

  private def sentCorrelationIds: Seq[String] =
    findAll(postRequestedFor(urlEqualTo(desEndpoint))).asScala.toSeq.map(_.getHeader("CorrelationId"))

  private def verifyCorrelationId(pattern: String): Unit =
    verify(1, postRequestedFor(urlEqualTo(desEndpoint)).withHeader("CorrelationId", matching(pattern)))

  "DesConnector" when {
    ".sendRegisterRequest" should {
      "POST the NINO application to DES with the DES headers and JSON body" in {
        stubDes(ACCEPTED)

        sendRegisterRequest()

        verify(
          1,
          postRequestedFor(urlEqualTo(desEndpoint))
            .withHeader("Environment", equalTo(appConfig.desEnvironment()))
            .withHeader("Authorization", equalTo(s"Bearer ${appConfig.desToken()}"))
            .withHeader("Content-Type", containing("application/json"))
            .withRequestBody(equalToJson(Json.toJson(maxRegisterNinoRequestModel).toString))
        )
      }

      "send a CorrelationId" which {
        "prefixes a new UUID suffix with the request ID when it matches the format (8-4-4-4)" in {
          stubDes(ACCEPTED)

          sendRegisterRequest(HeaderCarrier(requestId = Some(RequestId(requestId))))

          verifyCorrelationId(s"$requestId-[0-9a-f]{12}")
        }

        "is a new UUID when the request ID does not match the format (8-4-4-4)" in {
          stubDes(ACCEPTED)

          sendRegisterRequest(HeaderCarrier(requestId = Some(RequestId("not-a-request-id"))))

          verifyCorrelationId(uuidPattern)
        }

        "is a new UUID when there is no request ID" in {
          stubDes(ACCEPTED)

          sendRegisterRequest()

          verifyCorrelationId(uuidPattern)
        }

        "is different on every request" in {
          stubDes(ACCEPTED)

          sendRegisterRequest()
          sendRegisterRequest()

          sentCorrelationIds                 should have size 2
          sentCorrelationIds.distinct.size shouldBe 2
        }
      }

      "log the outgoing request body when the logDesJson feature switch is enabled" in {
        stubDes(ACCEPTED)

        withCaptureOfLoggingFrom(Logger(classOf[DesConnector])) { logs =>
          sendRegisterRequest(connector = desConnector(logDesJsonAppConfig))

          logs.map(_.getMessage) should contain(
            "[DesConnector][sendRegisterRequest] Logging JSON body of outgoing DES request: " +
              Json.toJson(maxRegisterNinoRequestModel)
          )
        }
      }

      "not log the outgoing request body when the logDesJson feature switch is disabled" in {
        stubDes(ACCEPTED)

        withCaptureOfLoggingFrom(Logger(classOf[DesConnector])) { logs =>
          sendRegisterRequest()

          logs.map(_.getMessage).filter(_.contains("Logging JSON body")) shouldBe empty
        }
      }

      "return a successful response" when {
        "DES returns 202 ACCEPTED" in {
          stubDes(ACCEPTED)

          sendRegisterRequest() shouldBe Right(())
        }
      }

      "return a DownstreamValidationError" when {
        "DES returns 403 FORBIDDEN with a DES error" in {
          val reason = "The remote endpoint has indicated that the account already exists."
          stubDes(FORBIDDEN, desError("ACCOUNT_ALREADY_EXISTS", reason))

          sendRegisterRequest() shouldBe Left(DownstreamValidationError("ACCOUNT_ALREADY_EXISTS", reason))
        }

        "DES returns 403 FORBIDDEN with INVALID_ENTRY_DATE, translating the reason" in {
          stubDes(
            FORBIDDEN,
            desError(
              "INVALID_ENTRY_DATE",
              "The remote endpoint has indicated that the entry date for the customer is less than 13 years."
            )
          )

          sendRegisterRequest() shouldBe Left(
            DownstreamValidationError(
              "INVALID_ENTRY_DATE",
              "Validation Error: the applicant is less than 13 years of age at the date of entry"
            )
          )
        }
      }

      "return a ServiceUnavailableError" when {
        "DES returns an unexpected status with a DES error" in {
          stubDes(BAD_REQUEST, desError("INVALID_PAYLOAD", "Submission has not passed validation."))

          sendRegisterRequest() shouldBe Left(ServiceUnavailableError)
        }

        "DES returns an unexpected status with JSON that is not a DES error" in {
          stubDes(SERVICE_UNAVAILABLE, Json.obj("unexpected" -> "body").toString)

          sendRegisterRequest() shouldBe Left(ServiceUnavailableError)
        }

        "DES returns an unexpected status with an empty body" in {
          stubDes(INTERNAL_SERVER_ERROR)

          sendRegisterRequest() shouldBe Left(ServiceUnavailableError)
        }
      }
    }
  }

}
