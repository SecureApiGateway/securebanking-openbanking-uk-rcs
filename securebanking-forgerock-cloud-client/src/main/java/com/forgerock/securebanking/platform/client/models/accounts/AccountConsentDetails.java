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

package com.forgerock.securebanking.platform.client.models.accounts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.forgerock.securebanking.platform.client.IntentType;
import com.forgerock.securebanking.platform.client.models.base.Consent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AccountConsentDetails extends Consent {

    private AccountConsentDataDetails data;
    private List<String> accountIds;

    @Override
    public IntentType getIntentType() {
        return IntentType.ACCOUNT_ACCESS_CONSENT;
    }

}
