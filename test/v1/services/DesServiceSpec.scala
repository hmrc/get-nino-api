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

package v1.services

import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import support.UnitSpec
import uk.gov.hmrc.http.HeaderCarrier
import utils.NinoApplicationTestData._
import v1.connectors.DesConnector
import v1.models.errors.ServiceUnavailableError

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class DesServiceSpec extends UnitSpec {

  implicit val hc: HeaderCarrier  = HeaderCarrier()
  val mockConnector: DesConnector = mock[DesConnector]
  val service                     = new DesService(mockConnector)

  "registerNino" should {
    "return a des response model" when {
      "a response is returned from the connector" in {

        when(
          mockConnector
            .sendRegisterRequest(ArgumentMatchers.eq(maxRegisterNinoRequestModel))(
              any[HeaderCarrier](),
              any[ExecutionContext]()
            )
        )
          .thenReturn(Future.successful(Right(())))

        val response = await(service.registerNino(maxRegisterNinoRequestModel))

        response shouldBe Right(())
      }
    }
    "return an error model" when {
      "an error is returned from the connector" in {
        val returnedError = ServiceUnavailableError

        when(
          mockConnector
            .sendRegisterRequest(ArgumentMatchers.eq(maxRegisterNinoRequestModel))(
              any[HeaderCarrier](),
              any[ExecutionContext]()
            )
        )
          .thenReturn(Future.successful(Left(returnedError)))

        val response = await(service.registerNino(maxRegisterNinoRequestModel))

        response shouldBe Left(returnedError)
      }
    }
  }

}
