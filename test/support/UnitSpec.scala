/*
 * Copyright 2021 HM Revenue & Customs
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

import org.scalamock.scalatest.MockFactory
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.{JsObject, JsString}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

trait UnitSpec extends AnyWordSpecLike
  with MockFactory
  with EitherValues
  with Matchers
  with FutureAwaits
  with DefaultAwaitTimeout {

  protected def flatJsObject(properties: (String, String)*): JsObject = JsObject(
    properties.map(prop => (prop._1, JsString(prop._2)))
  )
}
