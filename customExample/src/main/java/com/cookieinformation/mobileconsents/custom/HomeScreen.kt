package com.cookieinformation.mobileconsents.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

/** Demo entry screen. Colors come from the theme; the actions trigger the `consent` package. */
@Composable
fun HomeScreen(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onShowConsents: () -> Unit,
    onShowIfNeeded: () -> Unit,
    onDeleteData: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(R.string.label_dark_mode))
            Switch(checked = darkMode, onCheckedChange = onDarkModeChange)
        }

        Button(onClick = onShowConsents, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.action_show_consents))
        }
        Button(onClick = onShowIfNeeded, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.action_show_if_needed))
        }
        OutlinedButton(onClick = onDeleteData, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.action_delete_data))
        }
    }
}
