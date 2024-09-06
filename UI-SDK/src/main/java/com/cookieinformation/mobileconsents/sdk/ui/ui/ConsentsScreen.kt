package com.cookieinformation.mobileconsents.sdk.ui.ui

import android.text.SpannableStringBuilder
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import com.cookieinformation.mobileconsents.sdk.ui.R
import com.cookieinformation.mobileconsents.sdk.ui.UIConsentItem
import com.cookieinformation.mobileconsents.sdk.ui.ui.components.TextViewComposable


/**
 * Composable that displays the list of consents items as with the current user's state,
 */
@Composable
fun ConsentsScreen(
    isLoading: Boolean,
    privacyPolicy: UIConsentItem?,
    consents: List<UIConsentItem>,
    showPolicy: () -> Unit,
    acceptConsents: (MutableMap<Long, Boolean>) -> Unit,
    modifier: Modifier = Modifier
) {

    val selectedValues by rememberSaveable {
        mutableStateOf<MutableMap<Long, Boolean>>(
            mutableMapOf()
        )
    }

    val requiredSectionHeader = stringResource(R.string.required_consents_header)
    val optionalSectionHeader = stringResource(R.string.optional_consents_header)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(64.dp)
                            .align(Alignment.Center),
                        trackColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxSize()
        ) {


            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(weight = 1f, fill = true)
            ) {
                PrivacyPolicySection(privacyPolicy, showPolicy)

                val requiredConsents = consents.filter { it.required }
                val optionalConsents = consents.filter { !it.required }

                if (requiredConsents.isNotEmpty()) {
                    SectionHeader(requiredSectionHeader)
                    requiredConsents.forEach { item ->
                        ConsentRow(
                            item.title,
                            item.description,
                            item.required,
                            item.accepted
                        ) { checked ->
                            selectedValues[item.id] = checked
                        }
                        Divider()
                    }
                }

                if (optionalConsents.isNotEmpty()) {
                    SectionHeader(optionalSectionHeader)
                    optionalConsents.forEach { item ->
                        Column {
                            ConsentRow(
                                item.title,
                                item.description,
                                item.required,
                                item.accepted
                            ) { checked ->
                                selectedValues[item.id] = checked
                            }
                            Divider()
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small))
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
                verticalAlignment = Alignment.Bottom
            ) {
                FilledIconButton(
                    modifier = Modifier
                        .weight(1f, true),
                    onClick = { acceptConsents(selectedValues) },
                    shape = RoundedCornerShape(20),
                ) {
                    Text(
                        stringResource(R.string.accept_selected),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                FilledIconButton(
                    modifier = Modifier
                        .weight(1f, true),
                    onClick = {
                        acceptConsents(consents.associate { it.id to true }
                            .toMutableMap())
                    },
                    shape = RoundedCornerShape(20),
                ) {

                    val acceptALlText = stringResource(id = R.string.accept_all)
                    Text(
                        acceptALlText,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Text(
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                text = buildAnnotatedString {
                    withStyle(style = MaterialTheme.typography.labelSmall.toSpanStyle()) {
                        append(stringResource(id = R.string.powered_by))
                        append(" ")
                    }

                    withStyle(
                        style = MaterialTheme.typography.labelSmall.toSpanStyle().copy(
                            fontWeight = FontWeight.ExtraBold
                        )

                    ) {
                        append(stringResource(id = R.string.cookie_information))
                    }
                }
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentHeight()
    )
    Divider()
}

@Composable
fun ConsentRow(
    title: String,
    description: String,
    required: Boolean,
    accepted: Boolean,
    onChange: (checked: Boolean) -> Unit
) {
    var checked by remember { mutableStateOf(required || accepted) }
    val stringBuilder =
        SpannableStringBuilder(HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_COMPACT))

    val itemAccepted = stringResource(id = R.string.item_accepted)
    val itemRejected = stringResource(id = R.string.item_rejected)

    Column(modifier = Modifier
        .background(MaterialTheme.colorScheme.surface)
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .semantics(mergeDescendants = true) {
            contentDescription = "$title \n\n$stringBuilder"
        })
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.clearAndSetSemantics {},
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
            onChange(checked)
            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    onChange(it)
                },
                modifier = Modifier.semantics {
                    val desc = when (checked) {
                        true -> itemAccepted
                        false -> itemRejected
                    }
                    this.stateDescription = "$title $desc"
                },
                enabled = !required,
                colors = SwitchDefaults.colors(
                    disabledCheckedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    disabledCheckedThumbColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                ),
            )
        }
        if (containsHtmlTags(description)) {
            TextViewComposable(description = description)
        }
        else {
            Text(
                text = description,
                modifier = Modifier.clearAndSetSemantics {},
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }

    }
}

@Composable
fun PrivacyPolicySection(policy: UIConsentItem?, showPolicy: () -> Unit) {
    policy?.let {
        Column(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)) {
            Text(
                text = stringResource(id = R.string.privacy_policy_header),
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall

            )
            Text(text = policy.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurface)
            OutlinedButton(
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
                border = null,
                shape = RoundedCornerShape(20),
                onClick = { showPolicy() },
                content = {
                    Text(
                        text = stringResource(id = R.string.read_more),

                        style = MaterialTheme.typography.labelLarge.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        textAlign = TextAlign.Center,
                    )
                    // Specify the icon using the icon parameter
                    Image(
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                        painter = painterResource(R.drawable.navigate_next),
                        contentDescription = null
                    )
                }
            )
            Divider()
        }
    }
}
fun containsHtmlTags(input: String): Boolean {
    // This regex pattern looks for strings that contain "<" followed by any characters,
    // and then ">", indicating the presence of HTML tags.
    val htmlPattern = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>".toRegex()
    return htmlPattern.containsMatchIn(input)
}
