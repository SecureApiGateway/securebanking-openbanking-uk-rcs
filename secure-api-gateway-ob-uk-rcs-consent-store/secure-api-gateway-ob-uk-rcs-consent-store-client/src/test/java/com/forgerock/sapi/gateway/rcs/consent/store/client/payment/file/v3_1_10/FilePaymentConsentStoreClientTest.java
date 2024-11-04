/*
 * Copyright © 2020-2024 ForgeRock AS (obst@forgerock.com)
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
package com.forgerock.sapi.gateway.rcs.consent.store.client.payment.file.v3_1_10;

import com.forgerock.sapi.gateway.rcs.consent.store.client.payment.file.BaseFilePaymentConsentStoreClientTest;
import com.forgerock.sapi.gateway.rcs.consent.store.client.payment.file.BaseRestFilePaymentConsentStoreClient;
import com.forgerock.sapi.gateway.rcs.consent.store.client.payment.file.v3_1_10.RestFilePaymentConsentStoreClient;
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion;

import static com.forgerock.sapi.gateway.rcs.consent.store.client.TestConsentStoreClientConfigurationFactory.createConsentStoreClientConfiguration;

class FilePaymentConsentStoreClientTest extends BaseFilePaymentConsentStoreClientTest {

    protected FilePaymentConsentStoreClientTest() {
        super(OBVersion.v3_1_10);
    }

    @Override
    protected BaseRestFilePaymentConsentStoreClient createApiClient() {
        return new RestFilePaymentConsentStoreClient(createConsentStoreClientConfiguration(port),
                restTemplateBuilder,
                objectMapper);
    }

}