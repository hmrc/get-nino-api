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

package v1.connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import config.AppConfig
import play.api.http.Status._
import support.IntegrationBaseSpec
import uk.gov.hmrc.http._
import utils.ItNinoApplicationTestData._
import v1.connectors.httpParsers.HttpResponseTypes.HttpPostResponse
import v1.models.errors.ServiceUnavailableError
import v1.stubs.AuditStub

import scala.concurrent.ExecutionContext.Implicits.global

class DesConnectorISpec extends IntegrationBaseSpec {

  implicit lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  implicit lazy val http: HttpClient     = app.injector.instanceOf[HttpClient]
  implicit lazy val hc: HeaderCarrier    = HeaderCarrier()

  private val uuid: String                 = "123f4567-g89c-42c3-b456-557742330000"
  private lazy val connector: DesConnector = new DesConnector(http, appConfig) {
    override def generateNewUUID: String = uuid
  }

  private trait Test {
    def stubSuccess(url: String): StubMapping =
      stubFor(
        post(url)
          .willReturn(
            aResponse()
              .withStatus(ACCEPTED)
          )
      )

    def stubFailure(url: String): StubMapping =
      stubFor(
        post(url)
          .willReturn(
            aResponse()
              .withHeader("Content-Type", "application/json")
              .withStatus(INTERNAL_SERVER_ERROR)
          )
      )
  }

  "DesConnector" when {
    ".correlationId" when {
      "requestID is present in the headerCarrier" should {
        "return new ID pre-appending the requestID when the requestID matches the format(8-4-4-4)" in {
          val requestId: String   = "dcba0000-ij12-df34-jk56"
          val uuidBeginIndex: Int = 24

          connector.correlationId(HeaderCarrier(requestId = Some(RequestId(requestId)))) shouldBe
            s"$requestId-${uuid.substring(uuidBeginIndex)}"
        }

        "return new ID when the requestID does not match the format(8-4-4-4)" in {
          val requestId: String = "1a2b-ij12-df34-jk56"

          connector.correlationId(HeaderCarrier(requestId = Some(RequestId(requestId)))) shouldBe uuid
        }
      }

      "requestID is not present in the headerCarrier should return a new ID" should {
        "return the uuid" in {
          connector.correlationId(HeaderCarrier()) shouldBe uuid
        }
      }
    }

    ".sendRegisterRequest" when {
      "the feature switch is on" should {
        "send an Environment header in the request" in new Test {
          AuditStub.audit()
          stubSuccess("/register")

          appConfig.features.useDesStub(true)
          appConfig.features.logDesJson(true)

          await(connector.sendRegisterRequest(maxRegisterNinoRequestModel))

          verify(postRequestedFor(urlEqualTo("/register")).withHeader("Environment", equalTo("local")))
        }

        "return a DesResponse model" when {
          "the request is successful" in new Test {
            stubSuccess("/register")
            AuditStub.audit()

            appConfig.features.useDesStub(true)
            appConfig.features.logDesJson(true)

            val response: HttpPostResponse = await(connector.sendRegisterRequest(maxRegisterNinoRequestModel))

            response shouldBe Right(())
          }
        }

        "return an Error model" when {
          "the request is unsuccessful" in new Test {
            stubFailure("/register")
            AuditStub.audit()

            appConfig.features.useDesStub(true)
            appConfig.features.logDesJson(true)

            val response: HttpPostResponse = await(connector.sendRegisterRequest(maxRegisterNinoRequestModel))

            response shouldBe Left(ServiceUnavailableError)
          }
        }
      }

      "the feature switch is off" should {
        "return a DesResponse model" when {
          "the request is successful" in new Test {
            stubSuccess("/individuals/create")
            AuditStub.audit()

            appConfig.features.useDesStub(false)
            appConfig.features.logDesJson(false)

            val response: HttpPostResponse = await(connector.sendRegisterRequest(maxRegisterNinoRequestModel))

            response shouldBe Right(())
          }
        }

        "return an Error model" when {
          "the request is unsuccessful" in new Test {
            stubFailure("/individuals/create")
            AuditStub.audit()

            appConfig.features.useDesStub(false)
            appConfig.features.logDesJson(false)

            val response: HttpPostResponse = await(connector.sendRegisterRequest(maxRegisterNinoRequestModel))

            response shouldBe Left(ServiceUnavailableError)
          }
        }
      }
    }
  }
}
