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

package v1.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import config.AppConfig
import play.api.http.HeaderNames.ACCEPT
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import support.IntegrationBaseSpec
import v1.stubs.AuditStub

class FeatureSwitchISpec extends IntegrationBaseSpec {

  val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  private trait Test {

    def setupStubs(): StubMapping = {
      AuditStub.audit()
    }

    def request(): WSRequest = {
      setupStubs()
      buildRequest("/test-only/featureSwitch")
        .withHttpHeaders((ACCEPT, "application/vnd.hmrc.1.0+json"))
    }
  }

  "Calling the /test-only/featureSwitch endpoint" should {

    "return the state of the feature switches" when {

      "they do not get updated" in new Test {
        appConfig.features.useDesStub(false)

        val result: WSResponse = await(request().post(""))

        result.body[JsValue] shouldBe Json.obj(
          "useDesStub" -> false
        )
      }

      "they get updated" in new Test {
        appConfig.features.useDesStub(false)

        val result: WSResponse = await(request().post(Json.obj(
          "useDesStub" -> true
        )))

        result.body[JsValue] shouldBe Json.obj(
          "useDesStub" -> true
        )
      }
    }
  }

}
