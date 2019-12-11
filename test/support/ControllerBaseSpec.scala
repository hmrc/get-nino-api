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

package support

import akka.stream.Materializer
import config.AppConfig
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents}
import play.api.test.Helpers.stubControllerComponents
import play.api.test._

class ControllerBaseSpec extends UnitSpec with GuiceOneAppPerSuite
  with Status
  with MimeTypes
  with HeaderNames
  with ResultExtractors {

  implicit lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  lazy val cc: ControllerComponents = stubControllerComponents()

  lazy val mockAppConfig: AppConfig = app.injector.instanceOf[AppConfig]

  implicit lazy val materializer: Materializer = app.injector.instanceOf[Materializer]

  def fakePostRequest[T](body: T): FakeRequest[T] = fakeRequest.withBody(body)
}
