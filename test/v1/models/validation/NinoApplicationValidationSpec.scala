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

import java.time._

import support.UnitSpec
import utils.NinoApplicationTestData._
import v1.models.errors.ModelValidationError
import v1.models.request.DateModel

class NinoApplicationValidationSpec extends UnitSpec {

  private val fixedInstant: Instant = LocalDateTime.parse("2020-08-20T01:02:03.456").toInstant(ZoneOffset.UTC)
  private val stubClock: Clock      = Clock.fixed(fixedInstant, ZoneId.systemDefault)

  private val testFutureDateModel: DateModel = DateModel("21-08-2020")

  private val testValidator = new NinoApplicationValidation(stubClock)

  "NinoApplicationValidation" should {

    "return a NinoApplication model" when {

      "a minimal model passes validation" in {
        testValidator.validateNinoApplication(minRegisterNinoRequestModel) shouldBe Right(minRegisterNinoRequestModel)
      }

      "a maximal model passes validation" in {
        testValidator.validateNinoApplication(maxRegisterNinoRequestModel) shouldBe Right(maxRegisterNinoRequestModel)
      }
    }

    "return a ModelValidationError" when {

      "an applicantNames.startDate is in the future" in {
        val testApplicantNames = maxRegisterNinoRequestModel.applicantNames
        val testModel = maxRegisterNinoRequestModel.copy(
          applicantNames = testApplicantNames.head.copy(startDate = Some(testFutureDateModel)) :: testApplicantNames.tail.toList
        )

        testValidator.validateNinoApplication(testModel) shouldBe Left(ModelValidationError("names.startDate", "Start date cannot be in the future"))
      }

      "an applicantNames.endDate is in the future" in {
        val testApplicantNames = maxRegisterNinoRequestModel.applicantNames
        val testModel = maxRegisterNinoRequestModel.copy(
          applicantNames = testApplicantNames.head.copy(endDate = Some(testFutureDateModel)) :: testApplicantNames.tail.toList
        )

        testValidator.validateNinoApplication(testModel) shouldBe Left(ModelValidationError("names.endDate", "End date cannot be in the future"))
      }

      "an applicantHistoricName.startDate is in the future" in {
        val testApplicantNames = maxRegisterNinoRequestModel.applicantHistoricNames.get
        val testModel = maxRegisterNinoRequestModel.copy(
          applicantHistoricNames = Some(testApplicantNames.head.copy(startDate = Some(testFutureDateModel)) :: testApplicantNames.tail.toList)
        )

        testValidator.validateNinoApplication(testModel) shouldBe Left(ModelValidationError("historicNames.startDate", "Start date cannot be in the future"))
      }

      "an applicantHistoricName.endDate is in the future" in {
        val testApplicantNames = maxRegisterNinoRequestModel.applicantHistoricNames.get
        val testModel = maxRegisterNinoRequestModel.copy(
          applicantHistoricNames = Some(testApplicantNames.head.copy(endDate = Some(testFutureDateModel)) :: testApplicantNames.tail.toList)
        )

        testValidator.validateNinoApplication(testModel) shouldBe Left(ModelValidationError("historicNames.endDate", "End date cannot be in the future"))
      }

      "an applicantAddresses.startDate is in the future" in {
        val testApplicantAddresses = maxRegisterNinoRequestModel.applicantAddresses
        val testModel = maxRegisterNinoRequestModel.copy(
          applicantAddresses = testApplicantAddresses.head.copy(startDate = Some(testFutureDateModel)) :: testApplicantAddresses.tail.toList
        )

        testValidator.validateNinoApplication(testModel) shouldBe Left(ModelValidationError("addresses.startDate", "Start date cannot be in the future"))
      }

      "an applicantAddresses.endDate is in the future" in {
        val testApplicantAddresses = maxRegisterNinoRequestModel.applicantAddresses
        val testModel = maxRegisterNinoRequestModel.copy(
          applicantAddresses = testApplicantAddresses.head.copy(endDate = Some(testFutureDateModel)) :: testApplicantAddresses.tail.toList
        )

        testValidator.validateNinoApplication(testModel) shouldBe Left(ModelValidationError("addresses.endDate", "End date cannot be in the future"))
      }

      "an applicantHistoricAddresses.startDate is in the future" in {
        val testApplicantAddresses = maxRegisterNinoRequestModel.applicantHistoricAddresses.get
        val testModel = maxRegisterNinoRequestModel.copy(
          applicantHistoricAddresses = Some(testApplicantAddresses.head.copy(startDate = Some(testFutureDateModel)) :: testApplicantAddresses.tail.toList)
        )

        testValidator.validateNinoApplication(testModel) shouldBe Left(ModelValidationError("historicAddresses.startDate", "Start date cannot be in the future"))
      }

      "an applicantHistoricAddresses.endDate is in the future" in {
        val testApplicantAddresses = maxRegisterNinoRequestModel.applicantHistoricAddresses.get
        val testModel = maxRegisterNinoRequestModel.copy(
          applicantHistoricAddresses = Some(testApplicantAddresses.head.copy(endDate = Some(testFutureDateModel)) :: testApplicantAddresses.tail.toList)
        )

        testValidator.validateNinoApplication(testModel) shouldBe Left(ModelValidationError("historicAddresses.endDate", "End date cannot be in the future"))
      }

      "an applicantMarriages.startDate is in the future" in {
        val testApplicantMarriages = maxRegisterNinoRequestModel.applicantMarriages.get
        val testModel = maxRegisterNinoRequestModel.copy(
          applicantMarriages = Some(testApplicantMarriages.head.copy(startDate = Some(testFutureDateModel)) :: testApplicantMarriages.tail.toList)
        )

        testValidator.validateNinoApplication(testModel) shouldBe Left(ModelValidationError("marriages.startDate", "Start date cannot be in the future"))
      }

      "an applicantMarriages.endDate is in the future" in {
        val testApplicantMarriages = maxRegisterNinoRequestModel.applicantMarriages.get
        val testModel = maxRegisterNinoRequestModel.copy(
          applicantMarriages = Some(testApplicantMarriages.head.copy(endDate = Some(testFutureDateModel)) :: testApplicantMarriages.tail.toList)
        )

        testValidator.validateNinoApplication(testModel) shouldBe Left(ModelValidationError("marriages.endDate", "End date cannot be in the future"))
      }

      "an applicantPriorResidency.startDate is in the future" in {
        val testApplicantPriorResidency = maxRegisterNinoRequestModel.applicantPriorResidency.get
        val testModel = maxRegisterNinoRequestModel.copy(
          applicantPriorResidency = Some(testApplicantPriorResidency.head.copy(startDate = Some(testFutureDateModel)) ::
            testApplicantPriorResidency.tail.toList)
        )

        testValidator.validateNinoApplication(testModel) shouldBe Left(ModelValidationError("priorResidency.startDate", "Start date cannot be in the future"))
      }

      "an applicantPriorResidency.endDate is in the future" in {
        val testApplicantPriorResidency = maxRegisterNinoRequestModel.applicantPriorResidency.get
        val testModel = maxRegisterNinoRequestModel.copy(
          applicantPriorResidency = Some(testApplicantPriorResidency.head.copy(endDate = Some(testFutureDateModel)) ::
            testApplicantPriorResidency.tail.toList)
        )

        testValidator.validateNinoApplication(testModel) shouldBe Left(ModelValidationError("priorResidency.endDate", "End date cannot be in the future"))
      }

    }
  }
}
