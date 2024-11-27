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

package v1.connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import config.AppConfig
import play.api.http.Status._
import support.IntegrationBaseSpec
import uk.gov.hmrc.http._
import uk.gov.hmrc.http.client.HttpClientV2
import utils.ItNinoApplicationTestData._
import v1.connectors.httpParsers.HttpResponseTypes.HttpPostResponse
import v1.models.errors.ServiceUnavailableError
import v1.stubs.AuditStub

import scala.concurrent.ExecutionContext.Implicits.global

class DesConnectorISpec extends IntegrationBaseSpec {

  private lazy val appConfig: AppConfig       = app.injector.instanceOf[AppConfig]
  private lazy val httpClient: HttpClientV2   = app.injector.instanceOf[HttpClientV2]
  private implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val connector: DesConnector = new DesConnector(httpClient, appConfig)

  private trait Test {
    def stubSuccess(): StubMapping =
      stubFor(
        post("/register")
          .willReturn(
            aResponse()
              .withStatus(ACCEPTED)
          )
      )

    def stubFailure(): StubMapping =
      stubFor(
        post("/register")
          .willReturn(
            aResponse()
              .withHeader("Content-Type", "application/json")
              .withStatus(INTERNAL_SERVER_ERROR)
          )
      )
  }

  "DesConnector" when {
    ".sendRegisterRequest" should {
      "send an Environment header in the request" in new Test {
        AuditStub.audit()
        stubSuccess()

        await(connector.sendRegisterRequest(maxRegisterNinoRequestModel))

        verify(postRequestedFor(urlEqualTo("/register")).withHeader("Environment", equalTo("local")))
      }

      "return a successful response" when {
        "the request is successful" in new Test {
          stubSuccess()
          AuditStub.audit()

          val response: HttpPostResponse = await(connector.sendRegisterRequest(maxRegisterNinoRequestModel))

          response shouldBe Right(())
        }
      }

      "return an error response" when {
        "the request is unsuccessful" in new Test {
          stubFailure()
          AuditStub.audit()

          val response: HttpPostResponse = await(connector.sendRegisterRequest(maxRegisterNinoRequestModel))

          response shouldBe Left(ServiceUnavailableError)
        }
      }
    }
  }
}
