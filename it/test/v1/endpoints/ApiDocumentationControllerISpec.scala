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

package v1.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status.OK
import play.api.libs.json._
import play.api.libs.ws.WSResponse
import support.IntegrationBaseSpec
import v1.stubs.AuditStub

class ApiDocumentationControllerISpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping

    def response(): WSResponse = {
      setupStubs()
      await(buildRequest("/api/definition").get())
    }

  }

  "Calling the /api/definition endpoint" should {
    "return a 200 status code" in new Test {
      override def setupStubs(): StubMapping = {
        AuditStub.audit()
        AuditStub.audit()
      }

      response().status shouldBe OK
    }

    "return a JSON response" in new Test {
      override def setupStubs(): StubMapping = AuditStub.audit()

      response().contentType shouldBe "application/json"
    }

    "return a JSON document that describes the API access" in new Test {
      private val expectedJson: JsValue = Json.parse(
        """
          |{
          |  "api": {
          |    "name": "Register NINO",
          |    "description": "Register NINO information for downstream service",
          |    "context": "misc/register-nino",
          |    "categories": ["PRIVATE_GOVERNMENT"],
          |    "versions": [
          |      {
          |        "version": "1.0",
          |        "status": "BETA",
          |        "endpointsEnabled": true,
          |        "access" : {
          |          "type": "PRIVATE"
          |        }
          |      }
          |    ]
          |  }
          |}
        """.stripMargin
      )

      override def setupStubs(): StubMapping = AuditStub.audit()

      response().json shouldBe expectedJson
    }
  }

}
