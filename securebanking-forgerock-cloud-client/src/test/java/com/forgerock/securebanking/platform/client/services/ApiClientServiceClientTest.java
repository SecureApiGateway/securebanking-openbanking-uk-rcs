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
package com.forgerock.securebanking.platform.client.services;

import com.forgerock.securebanking.platform.client.exceptions.ErrorType;
import com.forgerock.securebanking.platform.client.exceptions.ExceptionClient;
import com.forgerock.securebanking.platform.client.models.base.ApiClient;
import com.forgerock.securebanking.platform.client.test.support.ApiClientTestDataFactory;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

/**
 * Unit test for {@link ApiClientServiceClient}
 */
public class ApiClientServiceClientTest extends BaseServiceClientTest {

    @InjectMocks
    private ApiClientServiceClient apiClientServiceClient;

    
    public void shouldGetApiClient() throws ExceptionClient {
        // Given
        ApiClient apiClient = ApiClientTestDataFactory.aValidApiClient();

        when(restTemplate.exchange(
                anyString(),
                eq(GET),
                nullable(HttpEntity.class),
                eq(ApiClient.class))
        ).thenReturn(ResponseEntity.ok(apiClient));

        // When
        ApiClient apiClientResponse = apiClientServiceClient.getApiClient(apiClient.getOauth2ClientId());

        // Then
        assertThat(apiClientResponse).isNotNull();
    }

    
    public void shouldGetNotFoundApiClient() {
        // Given
        ApiClient apiClient = ApiClientTestDataFactory.aValidApiClient();

        // When
        ExceptionClient exception = catchThrowableOfType(() -> apiClientServiceClient.getApiClient(apiClient.getOauth2ClientId()), ExceptionClient.class);

        // Then
        assertThat(exception.getErrorClient().getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        assertThat(exception.getErrorClient().getErrorType().getErrorCode()).isEqualTo(ErrorType.NOT_FOUND.getErrorCode());
        assertThat(exception.getErrorClient().getErrorType().getInternalCode()).isEqualTo(ErrorType.NOT_FOUND.getInternalCode());
    }
}
