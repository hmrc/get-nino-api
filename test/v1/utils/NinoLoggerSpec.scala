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

package v1.utils

import org.slf4j.MDC
import support.UnitSpec
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.logging.{RequestId, SessionId}
import v1.utils.NinoLogger.Logger

class NinoLoggerSpec extends UnitSpec {

  val requestIdKey = "x-request-id"
  val sessionIdKey = "x-session-id"

  trait test {
    MDC.clear()
  }

  val loggerFunctions: Seq[((String, HeaderCarrier) => Unit, String)] = Seq(
    (Logger.debug(_)(_), "debug"),
    (Logger.warn(_)(_), "warn"),
    (Logger.info(_)(_), "info"),
    (Logger.error(_)(_), "error")
  )

  val loggerFunctionsWithThrowable: Seq[((String, Throwable, HeaderCarrier) => Unit, String)] = Seq(
    (Logger.debug(_, _)(_), "debug"),
    (Logger.warn(_, _)(_), "warn"),
    (Logger.info(_, _)(_), "info"),
    (Logger.error(_, _)(_), "error")
  )

  val fullHeaderCarrier = HeaderCarrier(requestId = Some(RequestId("1234567890")), sessionId = Some(SessionId("0987654321")))
  val requestHeaderCarrier = HeaderCarrier(requestId = Some(RequestId("1234567890")))
  val sessionHeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("0987654321")))
  val emptyHeaderCarrier = HeaderCarrier()

  "logger functions (without throwable)" when {

    loggerFunctions.foreach { case(f, functionName) =>
      s"the $functionName function is called" should {

        "add session id and request id to the MDC" when {

          "they exist in the HC but not the MDC" in new test {
            Option(MDC.get(requestIdKey)) shouldBe None
            Option(MDC.get(sessionIdKey)) shouldBe None

            f("some logged message", fullHeaderCarrier)

            MDC.get(requestIdKey) shouldBe "1234567890"
            MDC.get(sessionIdKey) shouldBe "0987654321"
          }

          "they exist in the HC and the MDC" in new test {
            MDC.put(requestIdKey, "asdfghjkl")
            MDC.put(sessionIdKey, "lkjhgfdsa")

            f("some logger message", fullHeaderCarrier)

            MDC.get(requestIdKey) shouldBe "1234567890"
            MDC.get(sessionIdKey) shouldBe "0987654321"
          }
        }
        "add a single field" when {

          "only the request id is in the HC" in new test {
            f("some logged message", requestHeaderCarrier)

            MDC.get(requestIdKey) shouldBe "1234567890"
            Option(MDC.get(sessionIdKey)) shouldBe None
          }

          "only the session id is in the HC" in new test {
            f("some logged message", sessionHeaderCarrier)

            Option(MDC.get(requestIdKey)) shouldBe None
            MDC.get(sessionIdKey) shouldBe "0987654321"
          }
        }
      }
    }
  }

  "logger functions (with throwable)" when {

    loggerFunctionsWithThrowable.foreach { case(f, functionName) =>
      s"the $functionName function is called" should {

        "add session id and request id to the MDC" when {

          "they exist in the HC but not the MDC" in new test {
            Option(MDC.get(requestIdKey)) shouldBe None
            Option(MDC.get(sessionIdKey)) shouldBe None

            f("some logged message", new Throwable("some throwable"), fullHeaderCarrier)

            MDC.get(requestIdKey) shouldBe "1234567890"
            MDC.get(sessionIdKey) shouldBe "0987654321"
          }

          "they exist in the HC and the MDC" in new test {
            MDC.put(requestIdKey, "asdfghjkl")
            MDC.put(sessionIdKey, "lkjhgfdsa")

            f("some logger message", new Throwable("some throwable"), fullHeaderCarrier)

            MDC.get(requestIdKey) shouldBe "1234567890"
            MDC.get(sessionIdKey) shouldBe "0987654321"
          }
        }
        "add a single field" when {

          "only the request id is in the HC" in new test {
            f("some logged message", new Throwable("some throwable"), requestHeaderCarrier)

            MDC.get(requestIdKey) shouldBe "1234567890"
            Option(MDC.get(sessionIdKey)) shouldBe None
          }

          "only the session id is in the HC" in new test {
            f("some logged message", new Throwable("some throwable"), sessionHeaderCarrier)

            Option(MDC.get(requestIdKey)) shouldBe None
            MDC.get(sessionIdKey) shouldBe "0987654321"
          }
        }
      }
    }
  }

  ".addIdsToMdcFromSession" should {

    "add the values from header carrier into the mdc" when {

      "the header carrier values exist" in new test {
        Option(MDC.get(requestIdKey)) shouldBe None
        Option(MDC.get(sessionIdKey)) shouldBe None

        Logger.addIdsToMdcFromSession()(fullHeaderCarrier)

        MDC.get(requestIdKey) shouldBe "1234567890"
        MDC.get(sessionIdKey) shouldBe "0987654321"
      }
    }

    "not add the values from the header carrier" when {

      "the header carrier values are missing" in new test {
        Option(MDC.get(requestIdKey)) shouldBe None
        Option(MDC.get(sessionIdKey)) shouldBe None

        Logger.addIdsToMdcFromSession()(emptyHeaderCarrier)

        Option(MDC.get(requestIdKey)) shouldBe None
        Option(MDC.get(sessionIdKey)) shouldBe None
      }
    }
  }

  ".removeIdsFromMdc" should {

    "remove and session or request id from the MDC, ready to be repopulated" in {
      MDC.put(requestIdKey, "1234567890")
      MDC.put(sessionIdKey, "0987654321")

      Logger.removeIdsFromMdc()

      Option(MDC.get(requestIdKey)) shouldBe None
      Option(MDC.get(sessionIdKey)) shouldBe None
    }
  }

  ".apply" should {
    "create a NinoLogger" in {
      val newLogger = NinoLogger("Some Logger")

      newLogger.getClass.getSimpleName shouldBe "NinoLogger"
    }
  }

}
