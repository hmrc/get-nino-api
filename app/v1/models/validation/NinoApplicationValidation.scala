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

package v1.models.validation

import java.time.{Clock, LocalDate}

import javax.inject.Inject
import v1.models.errors.{ErrorResponse, ModelValidationError}
import v1.models.request.{DateModel, NinoApplication}

class NinoApplicationValidation @Inject() (clock: Clock) {
  def validateNinoApplication(ninoApplication: NinoApplication): Either[ErrorResponse, NinoApplication] =
    for {
      _ <- validateNameStartDates(ninoApplication)
      _ <- validateNameEndDates(ninoApplication)
      _ <- validateHistoricNameStartDates(ninoApplication)
      _ <- validateHistoricNameEndDates(ninoApplication)
      _ <- validateAddressStartDates(ninoApplication)
      _ <- validateAddressEndDates(ninoApplication)
      _ <- validateHistoricAddressStartDates(ninoApplication)
      _ <- validateHistoricAddressEndDates(ninoApplication)
      _ <- validateMarriageStartDates(ninoApplication)
      _ <- validateMarriageEndDates(ninoApplication)
      _ <- validatePriorResidencyStartDates(ninoApplication)
      _ <- validatePriorResidencyEndDates(ninoApplication)
    } yield ninoApplication

  private def validateNameStartDates(ninoApplication: NinoApplication): Either[ErrorResponse, NinoApplication] =
    if (ninoApplication.applicantNames.map(_.startDate).forall(_.forall(nonFutureDate))) { Right(ninoApplication) }
    else { Left(ModelValidationError("names.startDate", "Start date cannot be in the future")) }

  private def validateNameEndDates(ninoApplication: NinoApplication): Either[ErrorResponse, NinoApplication] =
    if (ninoApplication.applicantNames.map(_.endDate).forall(_.forall(nonFutureDate))) { Right(ninoApplication) }
    else { Left(ModelValidationError("names.endDate", "End date cannot be in the future")) }

  private def validateHistoricNameStartDates(ninoApplication: NinoApplication): Either[ErrorResponse, NinoApplication] =
    if (ninoApplication.applicantHistoricNames.forall(_.forall(_.startDate.exists(nonFutureDate)))) { Right(ninoApplication) }
    else { Left(ModelValidationError("historicNames.startDate", "Start date cannot be in the future")) }

  private def validateHistoricNameEndDates(ninoApplication: NinoApplication): Either[ErrorResponse, NinoApplication] =
    if (ninoApplication.applicantHistoricNames.forall(_.forall(_.endDate.exists(nonFutureDate)))) { Right(ninoApplication) }
    else { Left(ModelValidationError("historicNames.endDate", "End date cannot be in the future")) }

  private def validateAddressStartDates(ninoApplication: NinoApplication): Either[ErrorResponse, NinoApplication] =
    if (ninoApplication.applicantAddresses.map(_.startDate).forall(_.forall(nonFutureDate))) { Right(ninoApplication) }
    else { Left(ModelValidationError("addresses.startDate", "Start date cannot be in the future")) }

  private def validateAddressEndDates(ninoApplication: NinoApplication): Either[ErrorResponse, NinoApplication] =
    if (ninoApplication.applicantAddresses.map(_.endDate).forall(_.forall(nonFutureDate))) { Right(ninoApplication) }
    else { Left(ModelValidationError("addresses.endDate", "End date cannot be in the future")) }

  private def validateHistoricAddressStartDates(ninoApplication: NinoApplication): Either[ErrorResponse, NinoApplication] =
    if (ninoApplication.applicantHistoricAddresses.forall(_.forall(_.startDate.exists(nonFutureDate)))) { Right(ninoApplication) }
    else { Left(ModelValidationError("historicAddresses.startDate", "Start date cannot be in the future")) }

  private def validateHistoricAddressEndDates(ninoApplication: NinoApplication): Either[ErrorResponse, NinoApplication] =
    if (ninoApplication.applicantHistoricAddresses.forall(_.forall(_.endDate.exists(nonFutureDate)))) { Right(ninoApplication) }
    else { Left(ModelValidationError("historicAddresses.endDate", "End date cannot be in the future")) }

  private def validateMarriageStartDates(ninoApplication: NinoApplication): Either[ErrorResponse, NinoApplication] =
    if (ninoApplication.applicantMarriages.forall(_.forall(_.startDate.exists(nonFutureDate)))) { Right(ninoApplication) }
    else { Left(ModelValidationError("marriages.startDate", "Start date cannot be in the future")) }

  private def validateMarriageEndDates(ninoApplication: NinoApplication): Either[ErrorResponse, NinoApplication] =
    if (ninoApplication.applicantMarriages.forall(_.forall(_.endDate.exists(nonFutureDate)))) { Right(ninoApplication) }
    else { Left(ModelValidationError("marriages.endDate", "End date cannot be in the future")) }

  private def validatePriorResidencyStartDates(ninoApplication: NinoApplication): Either[ErrorResponse, NinoApplication] =
    if (ninoApplication.applicantPriorResidency.forall(_.forall(_.startDate.exists(nonFutureDate)))) { Right(ninoApplication) }
    else { Left(ModelValidationError("priorResidency.startDate", "Start date cannot be in the future")) }

  private def validatePriorResidencyEndDates(ninoApplication: NinoApplication): Either[ErrorResponse, NinoApplication] =
    if (ninoApplication.applicantPriorResidency.forall(_.forall(_.endDate.exists(nonFutureDate)))) { Right(ninoApplication) }
    else { Left(ModelValidationError("priorResidency.endDate", "End date cannot be in the future")) }

  private def nonFutureDate(date: DateModel): Boolean =
    date.asLocalDate.isBefore(LocalDate.now(clock)) || date.asLocalDate.isEqual(LocalDate.now(clock))
}
