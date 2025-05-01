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

package mocks

import config.ApiDefinitionConfig
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import org.scalatestplus.mockito.MockitoSugar.mock
import support.UnitSpec

trait MockApiDefinitionConfig extends UnitSpec {

  val mockApiDefinitionConfig: ApiDefinitionConfig = mock[ApiDefinitionConfig]

  object MockedApiDefinitionConfig {
    def status(): OngoingStubbing[String] =
      when(mockApiDefinitionConfig.status())

    def accessType(): OngoingStubbing[String] =
      when(mockApiDefinitionConfig.accessType())

    def endpointsEnabled(): OngoingStubbing[Boolean] =
      when(mockApiDefinitionConfig.endpointsEnabled())
  }
}
