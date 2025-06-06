/*******************************************************************************
 * Copyright (c) 2021,2024 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ******************************************************************************/

package org.eclipse.tractusx.bpdm.pool.api.v6.model.response

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.swagger.v3.oas.annotations.media.Schema
import org.eclipse.tractusx.bpdm.common.dto.openapidescription.CommonDescription
import org.eclipse.tractusx.bpdm.common.dto.openapidescription.LegalEntityDescription
import org.eclipse.tractusx.bpdm.common.service.DataClassUnwrappedJsonDeserializer
import org.eclipse.tractusx.bpdm.pool.api.v6.model.LegalEntityVerboseDto
import org.eclipse.tractusx.bpdm.pool.api.v6.model.LogisticAddressVerboseDto

@JsonDeserialize(using = DataClassUnwrappedJsonDeserializer::class)
@Schema(description = LegalEntityDescription.headerUpsertResponse)
data class LegalEntityPartnerCreateVerboseDto(

    @field:JsonUnwrapped
    val legalEntity: LegalEntityVerboseDto,

    // TODO OpenAPI description for complex field does not work!!
    @get:Schema(description = LegalEntityDescription.legalAddress)
    val legalAddress: LogisticAddressVerboseDto,

    @get:Schema(description = CommonDescription.index)
    val index: String?
)
