/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cookieinformation.mobileconsents.sdk.ui.ui.data

import com.cookieinformation.mobileconsents.core.domain.entities.ConsentItem
import com.cookieinformation.mobileconsents.core.domain.entities.ConsentType

/**
 * Data class that represents the current UI state in terms of [consents], [userId],
 */
data class ConsentsUiState(
    val isLoading: Boolean = false,
    val consents: List<ConsentItem> = emptyList(),
) {
    val privacyPolicy: ConsentItem?
        get() = consents.firstOrNull { it.type == ConsentType.PRIVACY_POLICY }
}

