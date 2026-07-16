package com.cookieinformation.mobileconsents.custom.consent

import android.widget.TextView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.cookieinformation.mobileconsents.core.domain.entities.ConsentItem
import com.cookieinformation.mobileconsents.core.domain.entities.ConsentItemOption
import com.cookieinformation.mobileconsents.custom.R

private val CATEGORY_SPACING = 32.dp // vertical gap between categories

/**
 * Consent screen: a title, one row per category (title, description, toggle) and two
 * action buttons pinned to the bottom.
 *
 * Colors come from [consentColors] (Color.kt); text lives in strings.xml.
 *
 * Button behaviour:
 *  - Only Necessary: accept required categories, reject every optional one.
 *  - Save choices:   persist exactly what the user toggled.
 */
@Composable
fun ConsentScreen(
    isLoading: Boolean,
    items: List<ConsentItem>,
    onSaveChoices: (List<ConsentItemOption>) -> Unit,
    onOnlyNecessary: (List<ConsentItemOption>) -> Unit,
    darkTheme: Boolean = false,
    colors: ConsentColors = consentColors(darkTheme),
) {
    // Current position of each toggle, seeded from the loaded items.
    val selection = remember { mutableStateMapOf<Long, Boolean>() }
    LaunchedEffect(items) {
        items.forEach { selection[it.id] = it.required || it.accepted }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .systemBarsPadding(),
    ) {
        Column(Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(CATEGORY_SPACING),
            ) {
                Text(
                    text = stringResource(R.string.consent_title),
                    color = colors.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )

                items.forEach { item ->
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = item.title,
                                color = colors.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                            )
                            Spacer(Modifier.width(8.dp))
                            Switch(
                                checked = selection[item.id] ?: item.required,
                                onCheckedChange = { selection[item.id] = it },
                                // Required categories are locked on: disabled (greyed out) and
                                // cannot be toggled. Disabled colors are neutral grey so no purple
                                // leaks in from the default Material theme.
                                enabled = !item.required,
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = colors.accent,
                                    checkedThumbColor = Color.White,
                                    checkedBorderColor = Color.Transparent,
                                    uncheckedTrackColor = colors.trackOff,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedBorderColor = Color.Transparent,
                                    disabledCheckedTrackColor = colors.trackOff,
                                    disabledCheckedThumbColor = Color.White,
                                    disabledCheckedBorderColor = Color.Transparent,
                                    disabledUncheckedTrackColor = colors.trackOff,
                                    disabledUncheckedThumbColor = Color.White,
                                    disabledUncheckedBorderColor = Color.Transparent,
                                ),
                            )
                        }
                        HtmlText(html = item.description, color = colors.description)
                    }
                }
            }

            HorizontalDivider(color = colors.divider)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = { onOnlyNecessary(items.map { ConsentItemOption(it.id, it.required) }) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, colors.secondary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.secondary),
                ) { Text(stringResource(R.string.button_only_necessary)) }

                Button(
                    onClick = { onSaveChoices(items.map { ConsentItemOption(it.id, selection[it.id] ?: it.accepted) }) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent, contentColor = colors.onAccent),
                ) { Text(stringResource(R.string.button_save_choices)) }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().background(colors.background),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = colors.accent)
            }
        }
    }
}

/** Category descriptions can contain HTML, so they are rendered through a TextView. */
@Composable
private fun HtmlText(html: String, color: Color) {
    AndroidView(
        factory = { context -> TextView(context) },
        update = {
            it.setTextColor(color.toArgb())
            it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
        },
    )
}
