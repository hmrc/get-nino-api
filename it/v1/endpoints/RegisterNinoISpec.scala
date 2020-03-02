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
import v1.models.errors.{OriginatorIdIncorrectError, OriginatorIdMissingError}
import v1.stubs.{AuditStub, AuthStub, DesStub}

class RegisterNinoISpec extends IntegrationBaseSpec {

  val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  private trait Test {

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest("/process-nino")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.1.0+json"),
          ("OriginatorId", "DA2_DWP_REG")
        )
    }
  }

  "Calling the /api/register endpoint" when {

    "the desStub feature switch is on" when {

      "any valid request is made" should {

        "return a 202 status code" in new Test {
          appConfig.features.useDesStub(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            DesStub.stubCall(Status.ACCEPTED, None, stubbed = true)
            AuthStub.authorised()
          }

          lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(false)))
          response.status shouldBe Status.ACCEPTED
        }


        "contain a correlation ID in the outbound request and return 202" in new Test {
          appConfig.features.useDesStub(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            DesStub.stubCallWithOriginatorId(Status.ACCEPTED, None, stubbed = true)
            AuthStub.authorised()
          }

          lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(false)))
          response.status shouldBe Status.ACCEPTED
        }

        "return an error status" when {

          "DES returns an error" in new Test {
            appConfig.features.useDesStub(true)

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              DesStub.stubCall(Status.BAD_REQUEST, Some(Json.obj("code" -> "OK", "message" -> "A response")), stubbed = true)
              AuthStub.authorised()
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

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.notAuthorised()
          }

          lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(true)))
          response.body[JsValue] shouldBe Json.obj(
            "code" -> "UNAUTHORISED",
            "message" -> "someReason"
          )
        }
      }

      "auth is down" should {

        "return a BadGateway(AuthDownError)" in new Test {
          appConfig.features.useDesStub(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authDown()
          }

          lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(true)))
          response.body[JsValue] shouldBe Json.obj(
            "code" -> "BAD_GATEWAY",
            "message" -> "Auth is currently down."
          )
        }
      }

      "any other error code is returned" should {

        "return a InternalServerError(DownstreamError)" in new Test {
          appConfig.features.useDesStub(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.otherStatus()
          }

          lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(true)))
          response.body[JsValue] shouldBe Json.obj(
            "code" -> "INTERNAL_SERVER_ERROR",
            "message" -> "An internal server error occurred"
          )
        }
      }
    }

    "the desStub feature switch is off" when {

      "any valid request is made" should {

        "return a 200 status code" in new Test {
          appConfig.features.useDesStub(false)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            DesStub.stubCall(Status.ACCEPTED, None)
            AuthStub.authorised()
          }

          lazy val response: WSResponse = await(request().post(maxRegisterNinoRequestJson(false)))
          response.status shouldBe Status.ACCEPTED
        }

        "return an error status" when {

          "DES returns an error" in new Test {
            appConfig.features.useDesStub(false)

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              DesStub.stubCall(Status.BAD_REQUEST, Some(Json.obj("code" -> s"${Status.BAD_REQUEST}", "message" -> "A response")))
              AuthStub.authorised()
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

      "a request is made with an invalid originator id" should {

        "return a OriginatorIdIncorrectError" in new Test {
          appConfig.features.useDesStub(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            DesStub.stubCall(Status.ACCEPTED, None, stubbed = true)
            AuthStub.authorised()
          }

          def requestWithIncorrectOriginatorId(): WSRequest = {
            setupStubs()
            buildRequest("/process-nino")
              .withHttpHeaders(
                (ACCEPT, "application/vnd.hmrc.1.0+json"),
                ("OriginatorId", "NOT-CORRECT")
              )
          }

          lazy val result: WSResponse = await(requestWithIncorrectOriginatorId().post(maxRegisterNinoRequestJson(false)))
          result.json shouldBe Json.toJson(OriginatorIdIncorrectError)
        }
      }

      "a request is made with a missing correlation id" should {

        "return a OriginatorIdMissingError" in new Test {
          appConfig.features.useDesStub(true)

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            DesStub.stubCall(Status.ACCEPTED, None, stubbed = true)
            AuthStub.authorised()
          }

          def requestWithMissingOriginatorId(): WSRequest = {
            setupStubs()
            buildRequest("/process-nino")
              .withHttpHeaders((ACCEPT, "application/vnd.hmrc.1.0+json"))
          }

          lazy val result: WSResponse = await(requestWithMissingOriginatorId().post(maxRegisterNinoRequestJson(false)))
          result.json shouldBe Json.toJson(OriginatorIdMissingError)
        }
      }
    }

  }
}
