/*
 * Copyright 2019 HM Revenue & Customs
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

package endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.{AuditStub, AuthStub}
import support.IntegrationBaseSpec

class HelloWorldISpec extends IntegrationBaseSpec {

  private trait Test {

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      buildRequest("/get-nino-api/hello-world")
    }
  }

  "Calling the /hello-world endpoint" when {

    "any valid request is made" should {

      "return a 200 status code" in new Test {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
      }

    }

  }

}
