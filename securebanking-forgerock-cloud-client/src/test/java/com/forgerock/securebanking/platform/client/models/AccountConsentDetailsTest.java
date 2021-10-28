/**
 * Copyright © 2020-2021 ForgeRock AS (obst@forgerock.com)
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
package com.forgerock.securebanking.platform.client.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.account.FRExternalPermissionsCode;
import com.forgerock.securebanking.platform.client.ConsentStatusCode;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Unit Test for {@link AccountConsentDetails}
 */
@Slf4j
public class AccountConsentDetailsTest {

    private ObjectMapper mapper;
    private static final String CONSENT_ID = "AAC_886511e2-78f0-4a14-9ab8-221360815aac";
    private static final String CLIENT_ID = "7e47a733-005b-4031-8622-18064ac373b7";

    @BeforeEach
    public void setup() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JodaModule());
        mapper.setTimeZone(TimeZone.getDefault());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldDeserialize() throws JsonProcessingException {
        // Given
        String json = getJson();

        // When
        AccountConsentDetails accountConsentDetails = mapper.readValue(json, AccountConsentDetails.class);

        // Then
        assertThat(accountConsentDetails).isNotNull();
    }

    @Test
    public void shouldSerialize() throws JsonProcessingException {
        // Given
        AccountConsentDataDetails accountConsentDataDetails = AccountConsentDataDetails.builder()
                .consentId(CONSENT_ID)
                .permissions(List.of(
                                FRExternalPermissionsCode.READACCOUNTSDETAIL,
                                FRExternalPermissionsCode.READBALANCES,
                                FRExternalPermissionsCode.READTRANSACTIONSDETAIL
                        )
                )
                .expirationDateTime(DateTime.now().plusDays(1))
                .creationDateTime(DateTime.now())
                .statusUpdateDateTime(DateTime.now())
                .transactionFromDateTime(DateTime.now().minusDays(1))
                .transactionToDateTime(DateTime.now())
                .status(ConsentStatusCode.AWAITINGAUTHORISATION.toString()).build();

        AccountConsentDetails accountConsentDetails = AccountConsentDetails.builder()
                .id(CONSENT_ID)
                .data(accountConsentDataDetails)
                .resourceOwnerUsername(null)
                .oauth2ClientId(CLIENT_ID)
                .oauth2ClientName("AISP Name")
                .accountIds(List.of(UUID.randomUUID().toString()))
                .build();

        // When
        String json = mapper.writeValueAsString(accountConsentDetails);
        log.info("Json Serialize as String \n{}", json);

        // Then
        assertThat(json).containsPattern("\"ConsentId\".:.\"" + CONSENT_ID + "\"");
        assertThat(json).containsPattern("\"oauth2ClientId\".:.\"" + CLIENT_ID + "\"");
    }

    private String getJson() {
        return getJson(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }

    private String getJson(String consentId, String clientId) {
        return "{" +
                "\"type\" : \"AccountConsentDetails\"," +
                "\"id\" : \"" + consentId + "\"," +
                "\"data\" : {" +
                "\"Permissions\" : [ \"ReadAccountsDetail\", \"ReadBalances\", \"ReadTransactionsDetail\" ]," +
                "\"ExpirationDateTime\" : \"2021-10-02T14:31:14.923+01:00\"," +
                "\"TransactionFromDateTime\" : \"2021-09-30T14:31:14.935+01:00\"," +
                "\"TransactionToDateTime\" : \"2021-10-01T14:31:14.935+01:00\"," +
                "\"ConsentId\" : \"" + consentId + "\"," +
                "\"Status\" : \"AwaitingAuthorisation\"," +
                "\"CreationDateTime\" : \"2021-10-01T14:31:14.935+01:00\"," +
                "\"StatusUpdateDateTime\" : \"2021-10-01T14:31:14.935+01:00\"}," +
                "\"accountIds\" : [ \"8f10f873-2b32-4306-aeea-d11004f92200\" ]," +
                "\"oauth2ClientId\" : \"" + clientId + "\"," +
                "\"oauth2ClientName\" : \"AISP Name\"" +
                "}";
    }

}
