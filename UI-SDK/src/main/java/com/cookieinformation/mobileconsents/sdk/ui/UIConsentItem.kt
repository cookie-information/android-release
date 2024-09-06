package com.cookieinformation.mobileconsents.sdk.ui

import com.cookieinformation.mobileconsents.core.domain.entities.ConsentItem
import com.cookieinformation.mobileconsents.core.domain.entities.ConsentType

data class UIConsentItem(
    val id: Long,
    val universalId: String,
    val title: String,
    val description: String,
    val required: Boolean,
    val type: UIConsentType,
    val accepted: Boolean
)

internal fun ConsentItem.toUIConsentItem(): UIConsentItem =
    UIConsentItem(id, universalId, title, description, required, type.toUIConsentType(), accepted)

internal fun ConsentType.toUIConsentType(): UIConsentType {
    return UIConsentType.fromString(type)
}

enum class UIConsentType(val type: String) {
    STATISTICS("statistical"),
    PRIVACY_POLICY("privacy policy"),
    NECESSARY("necessary"),
    MARKETING("marketing"),
    FUNCTIONAL("functional"),
    CUSTOM("custom");

    companion object {
        fun fromString(value: String) = entries.firstOrNull { it.type == value } ?: CUSTOM

    }
}
