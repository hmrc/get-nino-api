/*
 * Copyright 2021 HM Revenue & Customs
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

package v1.stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.JsValue
import support.WireMockMethods

object DesStub extends WireMockMethods {

  private val desUrl = "/individuals/create"
  private val desStubUrl = "/register"

  def stubCall(responseStatus: Int, maybeReturnBody: Option[JsValue], stubbed: Boolean = false): StubMapping = {
    maybeReturnBody match {
      case Some(returnBody) =>
        when(method = POST, uri = if (stubbed) desStubUrl else desUrl)
          .thenReturn(responseStatus, returnBody)
      case None =>
        when(method = POST, uri = if (stubbed) desStubUrl else desUrl)
          .thenReturn(responseStatus)
    }

  }

  def stubCallWithOriginatorIdAndCorrelationId(responseStatus: Int, maybeReturnBody: Option[JsValue], stubbed: Boolean = false): StubMapping = {
    maybeReturnBody match {
      case Some(returnBody) =>
        when(method = POST, uri = if (stubbed) desStubUrl else desUrl,
          headers = Map("OriginatorId" -> "DA2_DWP_REG", "CorrelationId" -> "DBABB1dB-7DED-b5Dd-19ce-5168C9E8fff9"))
          .thenReturn(responseStatus, returnBody)
      case None =>
        when(method = POST, uri = if (stubbed) desStubUrl else desUrl,
          headers = Map("OriginatorId" -> "DA2_DWP_REG", "CorrelationId" -> "DBABB1dB-7DED-b5Dd-19ce-5168C9E8fff9"))
          .thenReturn(responseStatus)
    }
  }
}
