/*
 * Copyright 2025 HM Revenue & Customs
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

package v1.controllers

import controllers._
import mocks.MockApiDefinitionConfig
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http._
import play.api.libs.json._
import play.api.mvc.Result
import play.api.test.Helpers._
import support.ControllerBaseSpec

import scala.concurrent.Future

class ApiDocumentationControllerSpec extends ControllerBaseSpec with MockApiDefinitionConfig {

  private val mockHttpErrorHandler: HttpErrorHandler = mock[HttpErrorHandler]
  private val assetsMetadata: DefaultAssetsMetadata  = new DefaultAssetsMetadata(
    config = AssetsConfiguration(),
    resource = _ => None,
    fileMimeTypes = new DefaultFileMimeTypes(FileMimeTypesConfiguration())
  )

  private val assets: Assets = new Assets(
    errorHandler = mockHttpErrorHandler,
    meta = assetsMetadata
  )

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

  private trait Setup {
    MockedApiDefinitionConfig.status().thenReturn("BETA")
    MockedApiDefinitionConfig.accessType().thenReturn("PRIVATE")
    MockedApiDefinitionConfig.endpointsEnabled().thenReturn(true)

    val apiDocumentationController: ApiDocumentationController = new ApiDocumentationController(
      cc = stubControllerComponents(),
      assets = assets,
      errorHandler = mockHttpErrorHandler,
      apiConfig = mockApiDefinitionConfig
    )
  }

  "ApiDocumentationController" when {
    ".definition" should {
      "return the expected API Definition JSON document" in new Setup {
        val result: Future[Result] = apiDocumentationController.definition()(fakeRequest)

        status(result)        shouldBe OK
        contentAsJson(result) shouldBe expectedJson
      }
    }
  }
}
