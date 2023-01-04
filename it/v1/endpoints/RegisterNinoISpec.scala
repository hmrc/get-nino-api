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
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.{AUTHORIZATION, contentAsJson}
import support.IntegrationBaseSpec
import utils.ItNinoApplicationTestData._
import v1.models.errors._
import v1.stubs._

import scala.concurrent.Future

class RegisterNinoISpec extends IntegrationBaseSpec {

  private val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  private trait Test {
    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest("/process-nino")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.1.0+json"),
          (AUTHORIZATION, "Bearer 123"),
          ("OriginatorId", "DA2_DWP_REG"),
          ("CorrelationId", "DBABB1dB-7DED-b5Dd-19ce-5168C9E8fff9")
        )
    }
  }

  "Calling the register nino endpoint" when {
    "the desStub feature switch is on" when {
      "any valid request is made" should {
        "return a 202 status code" in new Test {
          appConfig.features.useDesStub(true)
          appConfig.features.logDwpJson(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            DesStub.stubCall(ACCEPTED, None, stubbed = true)
            AuthStub.authorised()
          }

          lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(false)))
          response.status shouldBe ACCEPTED
        }

        "contain a correlation ID in the outbound request and return 202" in new Test {
          appConfig.features.useDesStub(true)
          appConfig.features.logDwpJson(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            DesStub.stubCallWithOriginatorIdAndCorrelationId(ACCEPTED, None, stubbed = true)
            AuthStub.authorised()
          }

          lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(false)))
          response.status shouldBe ACCEPTED
        }

        "return an error status" when {
          "DES returns an error" in new Test {
            appConfig.features.useDesStub(true)
            appConfig.features.logDwpJson(true)

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              DesStub.stubCall(BAD_REQUEST, Some(Json.obj("code" -> "OK", "message" -> "A response")), stubbed = true)
              AuthStub.authorised()
            }

            lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(false)))
            response.body[JsValue] shouldBe Json.obj(
              "code" -> "SERVER_ERROR",
              "message" -> "Service unavailable."
            )
          }
        }
      }

      "a request is made with valid json format, but invalid field values" should {
        "return an error code" in new Test {
          appConfig.features.useDesStub(true)
          appConfig.features.logDwpJson(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
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

      "the user is not authorised" should {
        "return an Unauthorised error with the reason provided by the AuthClient" in new Test {
          appConfig.features.useDesStub(true)
          appConfig.features.logDwpJson(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.notAuthorised()
          }

          lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(true)))
          response.body[JsValue] shouldBe Json.obj(
            "code" -> "CLIENT_OR_AGENT_NOT_AUTHORISED",
            "message" -> "someReason"
          )
        }
      }

      "auth is down" should {
        "return Service Unavailable" in new Test {
          appConfig.features.useDesStub(true)
          appConfig.features.logDwpJson(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authDown()
          }

          lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(true)))
          response.body[JsValue] shouldBe Json.obj(
            "code" -> "SERVER_ERROR",
            "message" -> "Service unavailable."
          )
        }
      }

      "any other error code is returned" should {
        "return a Service Unavailable" in new Test {
          appConfig.features.useDesStub(true)
          appConfig.features.logDwpJson(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.otherStatus()
          }

          lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(true)))
          response.body[JsValue] shouldBe Json.obj(
            "code" -> "SERVER_ERROR",
            "message" -> "Service unavailable."
          )
        }
      }

      "a request is made with an invalid originator id" should {
        "return a OriginatorIdIncorrectError" in new Test {
          appConfig.features.useDesStub(true)
          appConfig.features.logDwpJson(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            DesStub.stubCall(ACCEPTED, None, stubbed = true)
            AuthStub.authorised()
          }

          def requestWithIncorrectOriginatorId(): WSRequest = {
            setupStubs()
            buildRequest("/process-nino")
              .withHttpHeaders(
                (ACCEPT, "application/vnd.hmrc.1.0+json"),
                (AUTHORIZATION, "Bearer 123"),
                ("CorrelationId", "DBABB1dB-7DED-b5Dd-19ce-5168C9E8fff9"),
                ("OriginatorId", "NOT-CORRECT")
              )
          }

          lazy val result: WSResponse = await(requestWithIncorrectOriginatorId().post(maxRegisterNinoRequestJson(false)))
          result.json shouldBe contentAsJson(Future.successful(OriginatorIdIncorrectError.result))
        }
      }

      "a request is made with a missing originator id" should {
        "return a OriginatorIdMissingError" in new Test {
          appConfig.features.useDesStub(true)
          appConfig.features.logDwpJson(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            DesStub.stubCall(ACCEPTED, None, stubbed = true)
            AuthStub.authorised()
          }

          def requestWithMissingOriginatorId(): WSRequest = {
            setupStubs()
            buildRequest("/process-nino")
              .withHttpHeaders(
                (ACCEPT, "application/vnd.hmrc.1.0+json"),
                (AUTHORIZATION, "Bearer 123"),
                ("CorrelationId", "DBABB1dB-7DED-b5Dd-19ce-5168C9E8fff9")
              )
          }

          lazy val result: WSResponse = await(requestWithMissingOriginatorId().post(maxRegisterNinoRequestJson(false)))
          result.json shouldBe contentAsJson(Future.successful(OriginatorIdMissingError.result))
        }
      }

      "a request is made with an invalid correlation id" should {
        "return a CorrelationIdIncorrectError" in new Test {
          appConfig.features.useDesStub(true)
          appConfig.features.logDwpJson(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            DesStub.stubCall(ACCEPTED, None, stubbed = true)
            AuthStub.authorised()
          }

          def requestWithIncorrectCorrelationId(): WSRequest = {
            setupStubs()
            buildRequest("/process-nino")
              .withHttpHeaders(
                (ACCEPT, "application/vnd.hmrc.1.0+json"),
                (AUTHORIZATION, "Bearer 123"),
                ("OriginatorId", "DA2_DWP_REG"),
                ("CorrelationId", "12234567-0987654321-b5Dd-19ce-5168C9E8fff9")
              )
          }

          lazy val result: WSResponse = await(requestWithIncorrectCorrelationId().post(maxRegisterNinoRequestJson(false)))
          result.json shouldBe contentAsJson(Future.successful(CorrelationIdIncorrectError.result))
        }
      }

      "a request is made with a missing correlation id" should {
        "return a CorrelationIdMissingError" in new Test {
          appConfig.features.useDesStub(true)
          appConfig.features.logDwpJson(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            DesStub.stubCall(ACCEPTED, None, stubbed = true)
            AuthStub.authorised()
          }

          def requestWithMissingCorrelationId(): WSRequest = {
            setupStubs()
            buildRequest("/process-nino")
              .withHttpHeaders(
                (ACCEPT, "application/vnd.hmrc.1.0+json"),
                (AUTHORIZATION, "Bearer 123"),
                ("OriginatorId", "DA2_DWP_REG")
              )
          }

          lazy val result: WSResponse = await(requestWithMissingCorrelationId().post(maxRegisterNinoRequestJson(false)))
          result.json shouldBe contentAsJson(Future.successful(CorrelationIdMissingError.result))
        }
      }
    }

    "the desStub feature switch is off" when {
      "any valid request is made" should {
        "return a 200 status code" in new Test {
          appConfig.features.useDesStub(false)
          appConfig.features.logDwpJson(false)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            DesStub.stubCall(ACCEPTED, None)
            AuthStub.authorised()
          }

          lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(false)))
          response.status shouldBe ACCEPTED
        }

        "return an error status" when {
          "DES returns an error" in new Test {
            appConfig.features.useDesStub(false)
            appConfig.features.logDwpJson(false)

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              DesStub.stubCall(BAD_REQUEST, Some(Json.obj("code" -> s"$BAD_REQUEST", "message" -> "A response")))
              AuthStub.authorised()
            }

            lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(false)))
            response.body[JsValue] shouldBe Json.obj(
              "code" -> "SERVER_ERROR",
              "message" -> "Service unavailable."
            )
          }
        }
      }

      "a request is made with valid json format, but invalid field values" should {
        "return an error code" in new Test {
          appConfig.features.useDesStub(false)
          appConfig.features.logDwpJson(false)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
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
