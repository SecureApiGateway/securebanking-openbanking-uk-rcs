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
package com.forgerock.sapi.gateway.rcs.conent.store.client.payment.internationalstandingorder.v3_1_10;

import static com.forgerock.sapi.gateway.rcs.conent.store.client.TestConsentStoreClientConfigurationFactory.createConsentStoreClientConfiguration;
import static com.forgerock.sapi.gateway.rcs.consent.store.api.payment.PaymentConsentValidationHelpers.validateAuthorisedConsent;
import static com.forgerock.sapi.gateway.rcs.consent.store.api.payment.PaymentConsentValidationHelpers.validateConsumedConsent;
import static com.forgerock.sapi.gateway.rcs.consent.store.api.payment.PaymentConsentValidationHelpers.validateCreateConsentAgainstCreateRequest;
import static com.forgerock.sapi.gateway.rcs.consent.store.api.payment.PaymentConsentValidationHelpers.validateRejectedConsent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.common.FRAmount;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.common.FRCharge;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.common.FRChargeBearerType;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.payment.FRWriteInternationalStandingOrderConsentConverter;
import com.forgerock.sapi.gateway.rcs.conent.store.client.ConsentStoreClientException;
import com.forgerock.sapi.gateway.rcs.conent.store.client.ConsentStoreClientException.ErrorType;
import com.forgerock.sapi.gateway.rcs.conent.store.datamodel.RejectConsentRequest;
import com.forgerock.sapi.gateway.rcs.conent.store.datamodel.payment.AuthorisePaymentConsentRequest;
import com.forgerock.sapi.gateway.rcs.conent.store.datamodel.payment.ConsumePaymentConsentRequest;
import com.forgerock.sapi.gateway.rcs.conent.store.datamodel.payment.internationalstandingorder.v3_1_10.CreateInternationalStandingOrderConsentRequest;
import com.forgerock.sapi.gateway.rcs.conent.store.datamodel.payment.internationalstandingorder.v3_1_10.InternationalStandingOrderConsent;

import uk.org.openbanking.datamodel.payment.OBWriteInternationalConsentResponse5Data.StatusEnum;
import uk.org.openbanking.testsupport.payment.OBWriteInternationalStandingOrderConsentTestDataFactory;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"rcs.consent.store.api.baseUri= 'ignored'"})
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class InternationalStandingOrderConsentStoreClientTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    private RestInternationalStandingOrderConsentStoreClient apiClient;

    @BeforeEach
    public void beforeEach() {
        apiClient = new RestInternationalStandingOrderConsentStoreClient(createConsentStoreClientConfiguration(port), restTemplateBuilder, objectMapper);
    }

    @Test
    void testCreateConsent() {
        final CreateInternationalStandingOrderConsentRequest createConsentRequest = buildCreateConsentRequest();
        final InternationalStandingOrderConsent consent = apiClient.createConsent(createConsentRequest);

        validateCreateConsentAgainstCreateRequest(consent, createConsentRequest);
    }

    @Test
    void failsToCreateConsentWhenFieldIsMissing() {
        final CreateInternationalStandingOrderConsentRequest requestMissingIdempotencyField = buildCreateConsentRequest();
        requestMissingIdempotencyField.setIdempotencyKey(null);

        final ConsentStoreClientException clientException = assertThrows(ConsentStoreClientException.class,
                () -> apiClient.createConsent(requestMissingIdempotencyField));
        assertThat(clientException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        assertThat(clientException.getObError1()).isNotNull();
        assertThat(clientException.getObError1().getErrorCode()).isEqualTo("UK.OBIE.Field.Invalid");
        assertThat(clientException.getObError1().getMessage()).isEqualTo("The field received is invalid. Reason 'must not be null'");
        assertThat(clientException.getObError1().getPath()).isEqualTo("idempotencyKey");
    }

    @Test
    void testAuthoriseConsent() {
        final CreateInternationalStandingOrderConsentRequest createConsentRequest = buildCreateConsentRequest();
        final InternationalStandingOrderConsent consent = apiClient.createConsent(createConsentRequest);

        final AuthorisePaymentConsentRequest authRequest = buildAuthoriseConsentRequest(consent, "psu4test", "acc-12345");
        final InternationalStandingOrderConsent authResponse = apiClient.authoriseConsent(authRequest);

        validateAuthorisedConsent(authResponse, authRequest, consent);
    }

    @Test
    void testRejectConsent() {
        final CreateInternationalStandingOrderConsentRequest createConsentRequest = buildCreateConsentRequest();
        final InternationalStandingOrderConsent consent = apiClient.createConsent(createConsentRequest);

        final RejectConsentRequest rejectRequest = buildRejectRequest(consent, "joe.bloggs");
        final InternationalStandingOrderConsent rejectedConsent = apiClient.rejectConsent(rejectRequest);
        validateRejectedConsent(rejectedConsent, rejectRequest, consent);
    }

    @Test
    void testConsumeConsent() {
        final CreateInternationalStandingOrderConsentRequest createConsentRequest = buildCreateConsentRequest();
        final InternationalStandingOrderConsent consent = apiClient.createConsent(createConsentRequest);

        final AuthorisePaymentConsentRequest authRequest = buildAuthoriseConsentRequest(consent, "psu4test", "acc-12345");
        final InternationalStandingOrderConsent authResponse = apiClient.authoriseConsent(authRequest);
        assertThat(authResponse.getStatus()).isEqualTo(StatusEnum.AUTHORISED.toString());

        final InternationalStandingOrderConsent consumedConsent = apiClient.consumeConsent(buildConsumeRequest(consent));
        assertThat(consumedConsent.getStatus()).isEqualTo(StatusEnum.CONSUMED.toString());

        validateConsumedConsent(consumedConsent, authResponse);
    }

    @Test
    void testGetConsent() {
        final CreateInternationalStandingOrderConsentRequest createConsentRequest = buildCreateConsentRequest();
        final InternationalStandingOrderConsent consent = apiClient.createConsent(createConsentRequest);
        final InternationalStandingOrderConsent getResponse = apiClient.getConsent(consent.getId(), consent.getApiClientId());
        assertThat(getResponse).usingRecursiveComparison().isEqualTo(consent);
    }

    private static CreateInternationalStandingOrderConsentRequest buildCreateConsentRequest() {
        final CreateInternationalStandingOrderConsentRequest createConsentRequest = new CreateInternationalStandingOrderConsentRequest();
        createConsentRequest.setIdempotencyKey(UUID.randomUUID().toString());
        createConsentRequest.setApiClientId("test-client-1");
        createConsentRequest.setCharges(List.of(
                FRCharge.builder().type("fee")
                        .chargeBearer(FRChargeBearerType.BORNEBYCREDITOR)
                        .amount(new FRAmount("1.25","GBP"))
                        .build()));
        createConsentRequest.setConsentRequest(FRWriteInternationalStandingOrderConsentConverter.toFRWriteInternationalStandingOrderConsent(OBWriteInternationalStandingOrderConsentTestDataFactory.aValidOBWriteInternationalStandingOrderConsent5()));
        return createConsentRequest;
    }

    private static AuthorisePaymentConsentRequest buildAuthoriseConsentRequest(InternationalStandingOrderConsent consent, String resourceOwnerId, String authorisedDebtorAccountId) {
        final AuthorisePaymentConsentRequest authRequest = new AuthorisePaymentConsentRequest();
        authRequest.setAuthorisedDebtorAccountId(authorisedDebtorAccountId);
        authRequest.setConsentId(consent.getId());
        authRequest.setResourceOwnerId(resourceOwnerId);
        authRequest.setApiClientId(consent.getApiClientId());
        return authRequest;
    }

    private static RejectConsentRequest buildRejectRequest(InternationalStandingOrderConsent consent, String resourceOwnerId) {
        final RejectConsentRequest rejectRequest = new RejectConsentRequest();
        rejectRequest.setApiClientId(consent.getApiClientId());
        rejectRequest.setConsentId(consent.getId());
        rejectRequest.setResourceOwnerId(resourceOwnerId);
        return rejectRequest;
    }

    private static ConsumePaymentConsentRequest buildConsumeRequest(InternationalStandingOrderConsent consent) {
        final ConsumePaymentConsentRequest consumeRequest = new ConsumePaymentConsentRequest();
        consumeRequest.setApiClientId(consent.getApiClientId());
        consumeRequest.setConsentId((consent.getId()));
        return consumeRequest;
    }

}