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

package org.eclipse.tractusx.bpdm.gate.auth

import org.eclipse.tractusx.bpdm.gate.Application
import org.eclipse.tractusx.bpdm.gate.api.client.GateClient
import org.eclipse.tractusx.bpdm.test.containers.CreateNewSelfClientInitializer
import org.eclipse.tractusx.bpdm.test.containers.KeyCloakInitializer
import org.eclipse.tractusx.bpdm.test.containers.PostgreSQLContextInitializer
import org.eclipse.tractusx.bpdm.test.util.AuthAssertionHelper
import org.eclipse.tractusx.bpdm.test.util.AuthExpectationType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [Application::class])
@ContextConfiguration(initializers = [
    PostgreSQLContextInitializer::class,
    KeyCloakInitializer::class,
    AuthInputManagerIT.SelfClientAsInputManagerInitializer::class
])
@ActiveProfiles("test")
class AuthInputManagerIT @Autowired constructor(
    gateClient: GateClient,
    authAssertionHelper: AuthAssertionHelper
): AuthTestBase(
    gateClient,
    authAssertionHelper,
    authExpectations = GateAuthExpectations(
        businessPartner = BusinessPartnerAuthExpectations(
            getInput = AuthExpectationType.Authorized,
            getOutput = AuthExpectationType.Forbidden,
            putInput = AuthExpectationType.Authorized
        ),
        changelog = ChangelogAuthExpectations(
            getInput = AuthExpectationType.Authorized,
            getOutput = AuthExpectationType.Forbidden
        ),
        sharingState = SharingStateAuthExpectations(
            getSharingState = AuthExpectationType.Authorized,
            postReady = AuthExpectationType.Authorized
        ),
        stats = StatsAuthExpectations(
            getSharingState = AuthExpectationType.Authorized,
            getStage = AuthExpectationType.Authorized,
            getAddressType = AuthExpectationType.Authorized,
            getConfidenceCriteria = AuthExpectationType.Authorized
        ),
        uploadPartner = UploadPartnerAuthExpections(
            postInput = AuthExpectationType.Authorized,
            getInputTemplate = AuthExpectationType.Authorized
        ),
        relation = RelationAuthExpectations(
            postSearch = AuthExpectationType.Authorized,
            put = AuthExpectationType.Authorized
        ),
        relationOutput = RelationOutputAuthExpectations(
            postSearch = AuthExpectationType.Forbidden
        ),
        relationSharingState = RelationSharingStateExpectations(
            get = AuthExpectationType.Authorized
        ),
        relationChangelog = RelationChangelogExpectations(
            getInput = AuthExpectationType.Authorized,
            getOutput = AuthExpectationType.Forbidden
        )
    )
) {

    class SelfClientAsInputManagerInitializer : CreateNewSelfClientInitializer() {
        override val clientId: String
            get() = "EDC-GATE-INPUT-MANAGER"

        override val roleName: String
            get() = "BPDM Sharing Input Manager"
    }
}



