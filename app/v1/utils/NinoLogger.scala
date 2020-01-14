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
import uk.gov.hmrc.http.HeaderCarrier

class NinoLogger(loggerName: String = getClass.getSimpleName) {
  val requestIdKey = "x-request-id"
  val sessionIdKey = "x-session-id"

  val playLogger = play.api.Logger(loggerName)

  def info(message: String)(implicit hc: HeaderCarrier): Unit = {
    removeIdsFromMdc()
    addIdsToMdcFromSession()
    playLogger.info(message)
  }

  def info(message: String, throwable: Throwable)(implicit hc: HeaderCarrier): Unit = {
    removeIdsFromMdc()
    addIdsToMdcFromSession()
    playLogger.info(message, throwable)
  }

  def warn(message: String)(implicit hc: HeaderCarrier): Unit = {
    removeIdsFromMdc()
    addIdsToMdcFromSession()
    playLogger.warn(message)
  }

  def warn(message: String, throwable: Throwable)(implicit hc: HeaderCarrier): Unit = {
    removeIdsFromMdc()
    addIdsToMdcFromSession()
    playLogger.warn(message, throwable)
  }

  def debug(message: String)(implicit hc: HeaderCarrier): Unit = {
    removeIdsFromMdc()
    addIdsToMdcFromSession()
    playLogger.debug(message)
  }

  def debug(message: String, throwable: Throwable)(implicit hc: HeaderCarrier): Unit = {
    removeIdsFromMdc()
    addIdsToMdcFromSession()
    playLogger.debug(message, throwable)
  }

  def error(message: String)(implicit hc: HeaderCarrier): Unit = {
    removeIdsFromMdc()
    addIdsToMdcFromSession()
    playLogger.error(message)
  }

  def error(message: String, throwable: Throwable)(implicit hc: HeaderCarrier): Unit = {
    removeIdsFromMdc()
    addIdsToMdcFromSession()
    playLogger.error(message, throwable)
  }

  private[utils] def addIdsToMdcFromSession()(implicit hc: HeaderCarrier): Unit = {
    val requestId = hc.requestId
    val sessionId = hc.sessionId
    if(requestId.nonEmpty) MDC.put(requestIdKey, requestId.get.value)
    if(sessionId.nonEmpty) MDC.put(sessionIdKey, sessionId.get.value)
  }

  private[utils] def removeIdsFromMdc(): Unit = {
    MDC.remove(requestIdKey)
    MDC.remove(sessionIdKey)
  }
}

object NinoLogger {
  val Logger = new NinoLogger("nino-automation-logger")

  def apply(loggerName: String): NinoLogger = new NinoLogger(loggerName)
}
