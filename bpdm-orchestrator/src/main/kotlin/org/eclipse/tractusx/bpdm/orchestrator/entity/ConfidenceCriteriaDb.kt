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

package org.eclipse.tractusx.bpdm.orchestrator.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.hibernate.annotations.Type

@Embeddable
data class ConfidenceCriteriaDb(
    @Column(name = "shared_by_owner")
    val sharedByOwner: Boolean?,
    @Column(name = "checked_by_external_datasource")
    val checkedByExternalDataSource: Boolean?,
    @Column(name = "number_of_sharing_members")
    val numberOfSharingMembers: Int?,
    @Type(value = DbTimestampConverter::class)
    @Column(name = "last_confidence_check")
    val lastConfidenceCheckAt: DbTimestamp?,
    @Type(value = DbTimestampConverter::class)
    @Column(name = "next_confidence_check")
    val nextConfidenceCheckAt: DbTimestamp?,
    @Column(name = "confidence_level")
    val confidenceLevel: Int?
) {
    enum class Scope {
        LegalEntity,
        Site,
        LegalAddress,
        SiteMainAddress,
        AdditionalAddress,
        UncategorizedAddress
    }
}