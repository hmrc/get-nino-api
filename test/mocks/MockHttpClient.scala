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

package mocks

import izumi.reflect.Tag
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.libs.json._
import play.api.libs.ws.BodyWritable
import support.UnitSpec
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads}
import v1.connectors.httpParsers.HttpResponseTypes.HttpPostResponse

import java.net.URL
import scala.concurrent._

trait MockHttpClient extends UnitSpec {

  val mockHttpClient: HttpClientV2       = mock[HttpClientV2]
  val mockRequestBuilder: RequestBuilder = mock[RequestBuilder]

  object MockedHttpClient {
    def post(response: HttpPostResponse): Unit = {

      when(mockHttpClient.post(any[URL]())(any[HeaderCarrier]())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.setHeader(any[(String, String)]())).thenReturn(mockRequestBuilder)

      when(
        mockRequestBuilder
          .withBody(any[JsValue]())(any[BodyWritable[JsValue]](), any[Tag[JsValue]](), any[ExecutionContext]())
      )
        .thenReturn(mockRequestBuilder)

      when(mockRequestBuilder.execute(any[HttpReads[HttpPostResponse]](), any[ExecutionContext]()))
        .thenReturn(Future.successful(response))
    }
  }
}
