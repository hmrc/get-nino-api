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

package mocks

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import izumi.reflect.Tag
import play.api.libs.json._
import play.api.libs.ws.BodyWritable
import support.UnitSpec
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads}
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import v1.connectors.httpParsers.HttpResponseTypes.HttpPostResponse

import java.net.URL
import scala.concurrent._

trait MockHttpClient extends UnitSpec {

  val mockHttpClient: HttpClientV2       = mock[HttpClientV2]
  val mockRequestBuilder: RequestBuilder = mock[RequestBuilder]

  object MockedHttpClient {
    def post(url: String, body: JsValue)(response: HttpPostResponse): Unit = {
      (mockHttpClient.post(_: URL)(_: HeaderCarrier)).expects(*, *).returns(mockRequestBuilder)
      (mockRequestBuilder
        .setHeader(_: (String, String)))
        .expects(*)
        .returns(mockRequestBuilder)
      (mockRequestBuilder
        .withBody(_: JsValue)(_: BodyWritable[JsValue], _: Tag[JsValue], _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(mockRequestBuilder)
      (mockRequestBuilder
        .execute(_: HttpReads[?], _: ExecutionContext))
        .expects(*, *)
        .returns(Future.successful(response))
    }
  }
}
