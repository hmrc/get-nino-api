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

import org.scalamock.scalatest.MockFactory
import play.api.libs.json._
import uk.gov.hmrc.http._
import v1.connectors.httpParsers.HttpResponseTypes.HttpPostResponse

import scala.concurrent._

trait MockHttpClient extends MockFactory {

  val mockHttpClient: HttpClient = mock[HttpClient]

  object MockedHttpClient {
    def post(url: String, body: JsValue)(response: HttpPostResponse): Unit =
      (mockHttpClient
        .POST(_: String, _: JsValue, _: Seq[(String, String)])(
          _: Writes[JsValue],
          _: HttpReads[HttpPostResponse],
          _: HeaderCarrier,
          _: ExecutionContext
        ))
        .expects(url, body, *, *, *, *, *)
        .returns(Future.successful(response))
  }
}
