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

package org.eclipse.tractusx.bpdm.pool.api.model

import io.swagger.v3.oas.annotations.media.Schema
import org.eclipse.tractusx.bpdm.common.dto.openapidescription.IdentifierTypeDescription
import java.util.*

@Schema(description = IdentifierTypeDescription.header)
data class IdentifierTypeDto(

    @get:Schema(description = IdentifierTypeDescription.technicalKey)
    val technicalKey: String,

    @get:Schema(description = IdentifierTypeDescription.businessPartnerType)
    val businessPartnerType: IdentifierBusinessPartnerType,

    @get:Schema(description = IdentifierTypeDescription.name)
    val name: String,

    @get:Schema(description = IdentifierTypeDescription.abbreviation)
    val abbreviation: String?,

    @get:Schema(description = IdentifierTypeDescription.transliteratedName)
    val transliteratedName: String?,

    @get:Schema(description = IdentifierTypeDescription.transliteratedAbbreviation)
    val transliteratedAbbreviation: String?,

    @get:Schema(description = IdentifierTypeDescription.format)
    val format: String?,

    @get:Schema(description = IdentifierTypeDescription.categories)
    val categories: SortedSet<IdentifierTypeCategory>,

    @get:Schema(description = IdentifierTypeDescription.details)
    val details: Collection<IdentifierTypeDetailDto> = listOf()
)
