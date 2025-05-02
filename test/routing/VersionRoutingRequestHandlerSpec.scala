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

package routing

import com.typesafe.config.ConfigFactory
import mocks.MockAppConfig
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import org.mockito.ArgumentMatchers.any
import org.mockito.{ArgumentMatchers, Mockito}
import org.mockito.Mockito.when
import org.scalatest.Inside
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.Configuration
import play.api.http.HeaderNames.ACCEPT
import play.api.http.{HttpConfiguration, HttpFilters}
import play.api.libs.json.JsValue
import play.api.mvc._
import play.api.routing.Router
import play.api.test.FakeRequest
import play.api.test.Helpers._
import support.UnitSpec
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.config.HttpAuditEvent
import utils.ErrorHandler
import v1.models.errors.{ErrorResponse, InvalidAcceptHeaderError, UnsupportedVersionError}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VersionRoutingRequestHandlerSpec extends UnitSpec with Matchers with Inside with MockAppConfig {
  test =>

  implicit private val actorSystem: ActorSystem = ActorSystem("test")
  implicit private val mat: Materializer        = Materializer(actorSystem)

  private val defaultRouter = mock[Router]
  private val v1Router      = mock[Router]
  private val v2Router      = mock[Router]
  private val v3Router      = mock[Router]

  private val routingMap = new VersionRoutingMap {
    override val defaultRouter: Router    = test.defaultRouter
    override val map: Map[String, Router] = Map("1.0" -> v1Router, "2.0" -> v2Router, "3.0" -> v3Router)
  }

  class Test(implicit acceptHeader: Option[String]) {
    val httpConfiguration: HttpConfiguration = HttpConfiguration("context")
    val auditConnector: AuditConnector       = mock[AuditConnector]
    val httpAuditEvent: HttpAuditEvent       = mock[HttpAuditEvent]
    val configuration: Configuration         =
      Configuration(
        "appName"                                         -> "myApp",
        "bootstrap.errorHandler.warnOnly.statusCodes"     -> Seq.empty[Int],
        "metrics.enabled"                                 -> false,
        "bootstrap.errorHandler.suppress4xxErrorMessages" -> false,
        "bootstrap.errorHandler.suppress5xxErrorMessages" -> false
      )

    private val errorHandler = new ErrorHandler(configuration, auditConnector, httpAuditEvent)
    private val filters      = mock[HttpFilters]
    when(filters.filters).thenReturn(Seq.empty)

    private val actionBuilder: DefaultActionBuilder = DefaultActionBuilder(new play.api.mvc.BodyParsers.Default())

    MockedAppConfig.featureSwitch.thenReturn(Some(Configuration(ConfigFactory.parseString("""
        |version-1.enabled = true
        |version-2.enabled = true
      """.stripMargin))))

    val requestHandler: VersionRoutingRequestHandler =
      new VersionRoutingRequestHandler(
        routingMap,
        errorHandler,
        httpConfiguration,
        mockAppConfig,
        filters,
        actionBuilder
      )

    def stubHandling(router: Router, path: String, handler: Option[Handler], rh: RequestHeader): Unit = {
//      Mockito
//        .doAnswer { invocation =>
//          val requestHeader = invocation.getArgument[RequestHeader](0)
//          if (requestHeader.path == path) {
//            handler
//          } else {
//            None
//          }
//        }
//        .when(router)
//        .handlerFor(any[RequestHeader]())

      val routes = new PartialFunction[RequestHeader, Handler] {
        //NOT USED but required to be override
        override def isDefinedAt(x: RequestHeader): Boolean = throw new IllegalArgumentException(
          "This method is not used"
        )
        override def apply(v1: RequestHeader): Handler      = throw new IllegalArgumentException("This method is not used")
        //USED
        override def lift: RequestHeader => Option[Handler] = requestHeader =>
          if (requestHeader.path == path) handler else None
      }

      when(router.handlerFor(rh)).thenReturn(routes.lift(rh))

    }

    def buildRequest(path: String): RequestHeader =
      acceptHeader
        .foldLeft(FakeRequest("GET", path)) { (req, accept) =>
          req.withHeaders((ACCEPT, accept))
        }
  }

  def errorToJson(error: ErrorResponse): JsValue =
    contentAsJson(Future.successful(error.result))

  "Routing requests with no version" should {
    implicit val acceptHeader: None.type = None

    handleWithDefaultRoutes(defaultRouter)
  }

//  "Routing requests with valid version" should {
//    implicit val acceptHeader: Some[String] = Some("application/vnd.hmrc.1.0+json")
//
//    handleWithDefaultRoutes(defaultRouter)
//  }
//
//  "Routing requests to non default router with no version" should {
//    implicit val acceptHeader: None.type = None
//
//    "return 406" in new Test {
//      stubHandling(defaultRouter, "path", None)
//
//      val request: RequestHeader = buildRequest("path")
//      inside(requestHandler.routeRequest(request)) { case Some(a: EssentialAction) =>
//        val result = a.apply(request)
//
//        status(result)        shouldBe NOT_ACCEPTABLE
//        contentAsJson(result) shouldBe errorToJson(InvalidAcceptHeaderError)
//      }
//    }
//  }
//
//  "Routing requests with v1" should {
//    implicit val acceptHeader: Some[String] = Some("application/vnd.hmrc.1.0+json")
//
//    handleWithVersionRoutes(v1Router)
//  }
//
//  "Routing requests with v2" should {
//    implicit val acceptHeader: Some[String] = Some("application/vnd.hmrc.2.0+json")
//    handleWithVersionRoutes(v2Router)
//  }
//
//  "Routing requests with unsupported version" should {
//    implicit val acceptHeader: Some[String] = Some("application/vnd.hmrc.5.0+json")
//
//    "return 404" in new Test {
//      stubHandling(defaultRouter, "path", None)
//
//      private val request = buildRequest("path")
//
//      inside(requestHandler.routeRequest(request)) { case Some(a: EssentialAction) =>
//        val result = a.apply(request)
//
//        status(result)        shouldBe NOT_FOUND
//        contentAsJson(result) shouldBe errorToJson(UnsupportedVersionError)
//      }
//    }
//  }
//
//  "Routing requests for supported version but not enabled" when {
//    implicit val acceptHeader: Some[String] = Some("application/vnd.hmrc.3.0+json")
//
//    "the version has a route for the resource" must {
//      "return 404 Not Found" in new Test {
//        stubHandling(defaultRouter, "path", None)
//
//        private val request = buildRequest("path")
//        inside(requestHandler.routeRequest(request)) { case Some(a: EssentialAction) =>
//          val result = a.apply(request)
//
//          status(result)        shouldBe NOT_FOUND
//          contentAsJson(result) shouldBe errorToJson(UnsupportedVersionError)
//
//        }
//      }
//    }
//  }

  private def handleWithDefaultRoutes(router: Router)(implicit acceptHeader: Option[String]): Unit =
    "if the request ends with a trailing slash" when {
      "handler found" should {
        "use it" in new Test {
          val handler: Handler = mock[Handler]

          val requestHeader: RequestHeader = buildRequest("path/")

          stubHandling(router, "path/", Some(handler), requestHeader)

          requestHandler.routeRequest(requestHeader) shouldBe Some(handler)
        }
      }

//      "handler not found" should {
//        "try without the trailing slash" in new Test {
//          val handler: Handler = mock[Handler]
//
//          val requestHeader = buildRequest("path/")
//
////          Mockito.inOrder(
////            stubHandling(router, "path/", None),
////              stubHandling(router, "path", Some(handler))
////          )
//
//          requestHandler.routeRequest(requestHeader).get shouldBe a[Handler]
//
//        }
//      }
    }

//  private def handleWithVersionRoutes(router: Router)(implicit acceptHeader: Option[String]): Unit =
//    "if the request ends with a trailing slash" when {
//      "handler found" should {
//        "use it" in new Test {
//          val handler: Handler = mock[Handler]
//
//          stubHandling(defaultRouter, "path/", None)
//          stubHandling(defaultRouter, "path", None)
//          stubHandling(router, "path/", Some(handler))
//
//          requestHandler.routeRequest(buildRequest("path/")) shouldBe Some(handler)
//        }
//      }

//      "handler not found" should {
//        "try without the trailing slash" in new Test {
//
////          val handler: Handler = mock[Handler]
//
////          stubHandling(defaultRouter, "path/", None)
////          stubHandling(defaultRouter, "path", None)
//
//
//
////             stubHandling(router, "path/", None)
////            stubHandling(router, "path", Some(handler))
//
////          val inOrder = Mockito.inOrder(router)
////          requestHandler.routeRequest(buildRequest("path/")).get shouldBe a[Handler]
//
//
////          inOrder.verify(router).handlerFor(buildRequest("path/"))
////          inOrder.verify(router).handlerFor(buildRequest("path"))
//
//        }
//      }
//    }
}
