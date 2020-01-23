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

package v1.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import config.AppConfig
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import support.IntegrationBaseSpec
import utils.ItNinoApplicationTestData.{faultyRegisterNinoRequestJson, maxRegisterNinoRequestJson}
import v1.stubs.{AuditStub, DesStub}

class RegisterNinoISpec extends IntegrationBaseSpec {

  val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  private trait Test {

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest("/register")
        .withHttpHeaders((ACCEPT, "application/vnd.hmrc.1.0+json"))
    }
  }

  "Calling the /api/register endpoint" when {

    "the feature switch is on" when {

      "any valid request is made" should {

        "return a 200 status code" in new Test {
          appConfig.features.useDesStub(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            DesStub.stubCall(Status.OK, Json.obj("message" -> "A response"), stubbed = true)
          }

          lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(false)))
          response.status shouldBe Status.OK
        }

        "return 'A response'" in new Test {
          appConfig.features.useDesStub(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            DesStub.stubCall(Status.OK, Json.obj("message" -> "A response"), stubbed = true)
          }

          lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(false)))
          response.body[JsValue] shouldBe Json.obj("message" -> "A response")
        }

        "return an error status" when {

          "DES returns an error" in new Test {
            appConfig.features.useDesStub(true)

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              DesStub.stubCall(Status.BAD_REQUEST, Json.obj("message" -> "A response"), stubbed = true)
            }

            lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(false)))
            response.body[JsValue] shouldBe Json.obj(
              "code" -> s"${Status.BAD_REQUEST}",
              "message" -> "Downstream error returned from DES when submitting a NINO register request"
            )
          }
        }
      }

      "a request is made with valid json format, but invalid field values" should {

        "return an error code" in new Test {
          appConfig.features.useDesStub(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
          }

          lazy val response: WSResponse = await(request().post(faultyRegisterNinoRequestJson(false)))
          response.body[JsValue] shouldBe Json.obj(
            "code" -> "JSON_VALIDATION_ERROR",
            "message" -> "The provided JSON was unable to be validated as the selected model.",
            "errors" -> Json.arr(
              Json.obj(
                "code" -> "BAD_REQUEST",
                "message" -> "Error parsing gender",
                "path" -> "gender"
              )
            )
          )
        }
      }
    }

    "the feature switch is off" when {

      "any valid request is made" should {

        "return a 200 status code" in new Test {
          appConfig.features.useDesStub(false)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            DesStub.stubCall(Status.OK, Json.obj("message" -> "A response"))
          }

          lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(false)))
          response.status shouldBe Status.OK
        }

        "return 'A response'" in new Test {
          appConfig.features.useDesStub(false)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            DesStub.stubCall(Status.OK, Json.obj("message" -> "A response"))
          }

          lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(false)))
          response.body[JsValue] shouldBe Json.obj("message" -> "A response")
        }

        "return an error status" when {

          "DES returns an error" in new Test {
            appConfig.features.useDesStub(false)

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              DesStub.stubCall(Status.BAD_REQUEST, Json.obj("message" -> "A response"))
            }

            lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(false)))
            response.body[JsValue] shouldBe Json.obj(
              "code" -> s"${Status.BAD_REQUEST}",
              "message" -> "Downstream error returned from DES when submitting a NINO register request"
            )
          }
        }
      }

      "a request is made with valid json format, but invalid field values" should {

        "return an error code" in new Test {
          appConfig.features.useDesStub(false)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
          }

          lazy val response: WSResponse = await(request().post(faultyRegisterNinoRequestJson(false)))
          response.body[JsValue] shouldBe Json.obj(
            "code" -> "JSON_VALIDATION_ERROR",
            "message" -> "The provided JSON was unable to be validated as the selected model.",
            "errors" -> Json.arr(
              Json.obj(
                "code" -> "BAD_REQUEST",
                "message" -> "Error parsing gender",
                "path" -> "gender"
              )
            )
          )
        }
      }
    }

  }
}
