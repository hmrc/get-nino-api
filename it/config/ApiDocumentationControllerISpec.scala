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

package config

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import support.IntegrationBaseSpec
import v1.stubs.AuditStub

class ApiDocumentationControllerISpec extends IntegrationBaseSpec {


  private trait Test {

    def setupStubs(): StubMapping

    def response(): WSResponse = {
      setupStubs()
      await(buildRequest(s"/api/definition").get())
    }
  }

  "Calling the /api/definition endpoint" should {

    "return a 200 status code" in new Test {

      override def setupStubs(): StubMapping = {
        AuditStub.audit()
        AuditStub.audit()
      }

      response().status shouldBe Status.OK
    }

    "return a JSON response" in new Test {

      override def setupStubs(): StubMapping = {
        AuditStub.audit()
      }

      response().contentType shouldBe "application/json"
    }

    "return a JSON document that describes the API access" in new Test {

      private val expectedJson = Json.parse(

        s"""
           |{
           |  "scopes": [
           |    {
           |      "key": "read:get-nino",
           |      "name": "Get NINO",
           |      "description": "Get NINO"
           |    }
           |  ],
           |  "api": {
           |    "name": "Get NINO",
           |    "description": "Get NINO information for downstream service",
           |    "context": "get-nino-api",
           |    "categories": ["PRIVATE_GOVERNMENT"],
           |    "versions": [
           |      {
           |        "version": "1.0",
           |        "status": "ALPHA",
           |        "endpointsEnabled": true,
           |        "access" : {
           |          "type": "PRIVATE",
           |          "whitelistedApplicationIds": []
           |        }
           |      }
           |    ]
           |  }
           |}
      """.stripMargin
      )

      override def setupStubs(): StubMapping = {
        AuditStub.audit()
      }

      response().json shouldBe expectedJson
    }
  }
}
