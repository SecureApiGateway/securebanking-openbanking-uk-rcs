/*
 * Copyright © 2020-2022 ForgeRock AS (obst@forgerock.com)
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
package com.forgerock.sapi.gateway.rcs.conent.store.datamodel.payment.vrp.v3_1_10;

import org.springframework.validation.annotation.Validated;

import com.forgerock.sapi.gateway.ob.uk.common.datamodel.vrp.FRDomesticVRPConsent;
import com.forgerock.sapi.gateway.rcs.conent.store.datamodel.payment.BasePaymentConsent;

/**
 * OBIE Domestic VRP Consent: https://openbankinguk.github.io/read-write-api-site3/v3.1.10/resources-and-data-models/vrp/domestic-vrp-consents.html
 */
@Validated
public class DomesticVRPConsent extends BasePaymentConsent<FRDomesticVRPConsent> {
}
