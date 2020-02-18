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

package v1.connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import config.AppConfig
import play.api.http.Status
import play.api.libs.json.Json
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import utils.ItNinoApplicationTestData._
import v1.connectors.httpParsers.HttpResponseTypes.HttpPostResponse
import v1.models.errors.Error

import scala.concurrent.ExecutionContext.Implicits.global

class DesConnectorISpec extends IntegrationBaseSpec {

  implicit lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  implicit lazy val http: HttpClient = app.injector.instanceOf[HttpClient]
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  lazy val connector = new DesConnector(http, appConfig)

  trait Test {
    def stubSuccess(url: String): StubMapping = {
      stubFor(post(url)
        .willReturn(
          aResponse()
            .withStatus(Status.ACCEPTED)
        ))
    }

    def stubFailure(url: String): StubMapping = {
      stubFor(post(url)
        .willReturn(
          aResponse()
            .withHeader("Content-Type", "application/json")
            .withStatus(Status.INTERNAL_SERVER_ERROR)
        ))
    }

  }

  ".sendRegisterRequest" when {

    "the feature switch is on" should {

      "return a DesResponse model" when {

        "the request is successful" in new Test {
          stubSuccess("/register")

          val response: HttpPostResponse[Boolean] = {
            appConfig.features.useDesStub(true)
            await(connector.sendRegisterRequest(maxRegisterNinoRequestModel))
          }

          response shouldBe Right(true)
        }
      }
      "return an Error model" when {

        "the request is unsuccessful" in new Test {
          stubFailure("/register")

          val response: HttpPostResponse[Boolean] = {
            appConfig.features.useDesStub(true)
            await(connector.sendRegisterRequest(maxRegisterNinoRequestModel))
          }

          response shouldBe Left(Error(s"${Status.INTERNAL_SERVER_ERROR}", "Downstream error returned from DES when submitting a NINO register request"))
        }
      }
    }

    "the feature switch is off" should {

      "return a DesResponse model" when {

        "the request is successful" in new Test {
          stubSuccess("/desContext")

          val response: HttpPostResponse[Boolean] = {
            appConfig.features.useDesStub(false)
            await(connector.sendRegisterRequest(maxRegisterNinoRequestModel))
          }

          response shouldBe Right(true)
        }
      }
      "return an Error model" when {

        "the request is unsuccessful" in new Test {
          stubFailure("/desContext")

          val response: HttpPostResponse[Boolean] = {
            appConfig.features.useDesStub(false)
            await(connector.sendRegisterRequest(maxRegisterNinoRequestModel))
          }

          response shouldBe Left(Error(s"${Status.INTERNAL_SERVER_ERROR}", "Downstream error returned from DES when submitting a NINO register request"))
        }
      }
    }
  }

}
