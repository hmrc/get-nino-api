/*
 * Copyright 2025 HM Revenue & Customs
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

import mocks._
import support.UnitSpec
import uk.gov.hmrc.http._
import utils.NinoApplicationTestData._
import v1.connectors.httpParsers.HttpResponseTypes.HttpPostResponse
import v1.models.errors.DownstreamValidationError

import scala.concurrent.ExecutionContext.Implicits.global

class DesConnectorSpec extends UnitSpec with MockAppConfig with MockHttpClient {

  private implicit val hc: HeaderCarrier = HeaderCarrier()

  private val uuid: String = "123f4567-g89c-42c3-b456-557742330000"

  private class CorrelationIdSetup(requestId: Option[RequestId]) {
    private val desConnector: DesConnector = new DesConnector(mockHttpClient, mockAppConfig) {
      override def generateNewUUID: String = uuid
    }

    val correlationId: String = desConnector.correlationId(HeaderCarrier(requestId = requestId))
  }

  private class SendRegisterRequestSetup(logDesJson: Boolean) {
    MockedAppConfig.desToken().thenReturn("des-token")
    MockedAppConfig.desEnvironment().thenReturn("des-environment")
    MockedAppConfig.desBaseUrl().thenReturn("http://des-base-url")
    MockedAppConfig.desEndpoint().thenReturn("/register")
    MockedAppConfig.logDesJson().thenReturn(logDesJson)

    val desConnector: DesConnector = new DesConnector(mockHttpClient, mockAppConfig)
  }

  "DesConnector" when {
    ".correlationId" when {
      "request ID is present in the headerCarrier" should {
        "return a new UUID prepending the request ID" when {
          "the request ID matches the format(8-4-4-4)" in new CorrelationIdSetup(
            Some(RequestId("dcba0000-ij12-df34-jk56"))
          ) {
            val requestId: String   = "dcba0000-ij12-df34-jk56"
            val uuidBeginIndex: Int = 24

            correlationId shouldBe s"$requestId-${uuid.substring(uuidBeginIndex)}"
          }
        }

        "return a new UUID" when {
          "the request ID does not match the format(8-4-4-4)" in new CorrelationIdSetup(Some(RequestId(""))) {
            correlationId shouldBe uuid
          }
        }
      }

      "request ID is absent in the headerCarrier" should {
        "return a new UUID" in new CorrelationIdSetup(None) {
          correlationId shouldBe uuid
        }
      }
    }

    ".sendRegisterRequest" should {
      "return a successful response" when {
        "the request is successful" in new SendRegisterRequestSetup(logDesJson = true) {
          val expectedResponse: HttpPostResponse = Right(())

          MockedHttpClient.post(expectedResponse)

          val result: HttpPostResponse = await(desConnector.sendRegisterRequest(maxRegisterNinoRequestModel))

          result shouldBe expectedResponse
        }
      }

      "return an error response" when {
        "the request is unsuccessful" in new SendRegisterRequestSetup(logDesJson =  false) {
          val expectedResponse: HttpPostResponse = Left(
            DownstreamValidationError(
              code = "ACCOUNT_ALREADY_EXISTS",
              message = "The remote endpoint has indicated that the account already exists."
            )
          )

          MockedHttpClient.post(expectedResponse)

          val result: HttpPostResponse = await(desConnector.sendRegisterRequest(maxRegisterNinoRequestModel))

          result shouldBe expectedResponse
        }
      }
    }
  }
}
