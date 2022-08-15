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
package com.forgerock.securebanking.openbanking.uk.rcs.converters;

import com.forgerock.securebanking.openbanking.uk.rcs.api.dto.consent.details.AccountsConsentDetails;
import com.google.gson.JsonObject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import static com.forgerock.securebanking.openbanking.uk.rcs.converters.UtilConverter.isNotNull;

/**
 * Converter class to map {@link JsonObject} to {@link AccountsConsentDetails}
 */
@Slf4j
@NoArgsConstructor
public class AccountConsentDetailsConverter {

    private static volatile AccountConsentDetailsConverter instance;

    /*
     * Double checked locking principle to ensure that only one instance 'AccountConsentDetailsConverter' is created
     */
    public static AccountConsentDetailsConverter getInstance() {
        if (instance == null) {
            synchronized (AccountConsentDetailsConverter.class) {
                if (instance == null) {
                    instance = new AccountConsentDetailsConverter();
                }
            }
        }
        return instance;
    }

    public AccountsConsentDetails mapping(JsonObject consentDetails) {
        AccountsConsentDetails details = new AccountsConsentDetails();
        details.setAispName(isNotNull(consentDetails.get("oauth2ClientName")) ?
                consentDetails.get("oauth2ClientName").getAsString() :
                null);
        if (!isNotNull(consentDetails.get("data"))) {
            details.setFromTransaction(null);
            details.setToTransaction(null);
            details.setExpiredDate(null);
            details.setPermissions(null);
        } else {
            JsonObject data = consentDetails.getAsJsonObject("data");

            details.setFromTransaction(isNotNull(data.get("TransactionFromDateTime")) ?
                    DateTime.parse(data.get("TransactionFromDateTime").getAsString()) :
                    null);

            details.setToTransaction(isNotNull(data.get("TransactionToDateTime")) ?
                    DateTime.parse(data.get("TransactionToDateTime").getAsString()) :
                    null);

            details.setExpiredDate(isNotNull(data.get("ExpirationDateTime")) ?
                    DateTime.parse(data.get("ExpirationDateTime").getAsString()) :
                    null);

            details.setPermissions(isNotNull(data.get("Permissions")) ?
                    data.getAsJsonArray("Permissions") :
                    null);
        }

        return details;
    }

    public final AccountsConsentDetails toAccountConsentDetails(JsonObject consentDetails) {
        return mapping(consentDetails);
    }
}
