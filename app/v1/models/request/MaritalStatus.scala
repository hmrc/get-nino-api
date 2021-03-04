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

package v1.models.request
import play.api.Logger
import play.api.libs.json._

sealed trait MaritalStatus {
  val value: String
}

object MaritalStatus {
  private[models] val allStatuses: Map[String, MaritalStatus] = Seq(
    MARRIAGE_TERMINATED,
    MARRIAGE_ANNULLED,
    VOID,
    DIVORCED,
    WIDOWED,
    MARRIED,
    SINGLE,
    CIVIL_PARTNERSHIP,
    CIVIL_PARTNERSHIP_DISSOLVED,
    SURVIVING_CIVIL_PARTNERSHIP,
    CIVIL_PARTNERSHIP_TERMINATED,
    CIVIL_PARTNERSHIP_ANNULLED
  ).map(status => status.value -> status).toMap

  val maritalStatusErrorMessage = "Marital Status is invalid. Please check against valid types."
  val maritalStatusError = new JsonValidationError(Seq(maritalStatusErrorMessage))

  def validateMaritalStatus(maritalStatus: String): Boolean = {
    val passedValidation = allStatuses.values.map(_.value).toSeq.contains(maritalStatus)

    if(!passedValidation) Logger.info(s"[MaritalStatus][validateMaritalStatus] $maritalStatusErrorMessage")

    passedValidation
  }

  implicit val reads: Reads[MaritalStatus] = for {
    maritalStatusValue <- __.read[String].filter(maritalStatusError)(validateMaritalStatus)
  } yield {
    allStatuses(maritalStatusValue)
  }

  implicit val writes: Writes[MaritalStatus] = Writes { model => JsString(model.value) }
}

case object MARRIAGE_TERMINATED extends MaritalStatus {
  val value: String = "MARRIAGE-TERMINATED"
}

case object MARRIAGE_ANNULLED extends MaritalStatus {
  val value: String = "MARRIAGE-ANNULLED"
}

case object VOID extends MaritalStatus {
  val value: String = "VOID"
}

case object DIVORCED extends MaritalStatus {
  val value: String = "DIVORCED"
}

case object WIDOWED extends MaritalStatus {
  val value: String = "WIDOWED"
}

case object MARRIED extends MaritalStatus {
  val value: String = "MARRIED"
}

case object SINGLE extends MaritalStatus {
  val value: String = "SINGLE"
}

case object CIVIL_PARTNERSHIP extends MaritalStatus {
  val value: String = "CIVIL-PARTNERSHIP"
}

case object CIVIL_PARTNERSHIP_DISSOLVED extends MaritalStatus {
  val value: String = "CIVIL-PARTNERSHIP-DISSOLVED"
}

case object SURVIVING_CIVIL_PARTNERSHIP extends MaritalStatus {
  val value: String = "SURVIVING-CIVIL-PARTNERSHIP"
}

case object CIVIL_PARTNERSHIP_TERMINATED extends MaritalStatus {
  val value: String = "CIVIL-PARTNERSHIP-TERMINATED"
}

case object CIVIL_PARTNERSHIP_ANNULLED extends MaritalStatus {
  val value: String = "CIVIL-PARTNERSHIP-ANNULLED"
}
