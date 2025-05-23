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

package org.eclipse.tractusx.bpdm.pool.controller.v6

import org.eclipse.tractusx.bpdm.common.dto.PageDto
import org.eclipse.tractusx.bpdm.common.dto.PaginationRequest
import org.eclipse.tractusx.bpdm.pool.api.v6.PoolAddressApi
import org.eclipse.tractusx.bpdm.pool.api.v6.model.LogisticAddressVerboseDto
import org.eclipse.tractusx.bpdm.pool.api.model.request.AddressPartnerCreateRequest
import org.eclipse.tractusx.bpdm.pool.api.model.request.AddressPartnerUpdateRequest
import org.eclipse.tractusx.bpdm.pool.api.model.request.AddressSearchRequest
import org.eclipse.tractusx.bpdm.pool.api.v6.model.response.AddressPartnerCreateResponseWrapper
import org.eclipse.tractusx.bpdm.pool.api.v6.model.response.AddressPartnerUpdateResponseWrapper
import org.eclipse.tractusx.bpdm.pool.config.PermissionConfigProperties
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController("AddressControllerLegacy")
class AddressController(
    private val addressLegacyServiceMapper: AddressLegacyServiceMapper
) : PoolAddressApi {

    @PreAuthorize("hasAuthority(${PermissionConfigProperties.READ_PARTNER})")
    override fun getAddresses(addressSearchRequest: AddressSearchRequest, paginationRequest: PaginationRequest): PageDto<LogisticAddressVerboseDto> {
        return searchAddresses(addressSearchRequest, paginationRequest)
    }

    @PreAuthorize("hasAuthority(${PermissionConfigProperties.READ_PARTNER})")
    override fun getAddress(
        bpna: String
    ): LogisticAddressVerboseDto {
        return addressLegacyServiceMapper.findByBpn(bpna.uppercase())
    }

    @PreAuthorize("hasAuthority(${PermissionConfigProperties.READ_PARTNER})")
    override fun searchAddresses(
        searchRequest: AddressSearchRequest,
        paginationRequest: PaginationRequest
    ): PageDto<LogisticAddressVerboseDto> {
        return addressLegacyServiceMapper.searchAddresses(
            AddressLegacyServiceMapper.AddressSearchRequest(
                addressBpns = searchRequest.addressBpns,
                siteBpns = searchRequest.siteBpns,
                legalEntityBpns = searchRequest.legalEntityBpns,
                name = searchRequest.name,
                isCatenaXMemberData = null
            ),
            paginationRequest
        )
    }

    @PreAuthorize("hasAuthority(${PermissionConfigProperties.WRITE_PARTNER})")
    override fun createAddresses(
        requests: Collection<AddressPartnerCreateRequest>
    ): AddressPartnerCreateResponseWrapper {
        return addressLegacyServiceMapper.createAddresses(requests)
    }

    @PreAuthorize("hasAuthority(${PermissionConfigProperties.WRITE_PARTNER})")
    override fun updateAddresses(
        requests: Collection<AddressPartnerUpdateRequest>
    ): AddressPartnerUpdateResponseWrapper {
        return addressLegacyServiceMapper.updateAddresses(requests)
    }
}
