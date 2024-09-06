package com.cookieinformation.mobileconsents.sdk.ui.ui

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cookieinformation.mobileconsents.core.domain.entities.ConsentType
import com.cookieinformation.mobileconsents.sdk.ui.toUIConsentItem
import kotlinx.coroutines.launch

/**
 * enum values that represent the screens in the app
 */
enum class AppScreen {
    Consents, PrivacyPolicy,
}

@Composable
fun ConsentsWrappingScreen(
    viewModel: ConsentsViewModel,
    navController: NavHostController = rememberNavController(),
    userId: String?
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.getConsents(userId).onFailure {
            (context as? ComponentActivity)?.finish()
        }
    }

    BackHandler(enabled = true) {
        coroutineScope.launch {
            viewModel.canNavigateBack(userId).fold(onFailure = {
                if (context is Activity) {
                    context.finish()
                }
            },
                onSuccess = {
                    if (it) {
                        if (context is Activity) {
                            context.finish()
                        }
                    }
                })
        }
    }

    Scaffold(
    ) { innerPadding ->
        val consentsUiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = AppScreen.Consents.name,
            modifier = Modifier.padding(innerPadding).background(MaterialTheme.colorScheme.surface)
        ) {
            composable(route = AppScreen.Consents.name) {
                ConsentsScreen(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxSize(),
                    isLoading = consentsUiState.isLoading,
                    privacyPolicy = consentsUiState.privacyPolicy?.toUIConsentItem(),
                    consents = consentsUiState.consents.filter { consent -> consent.type != ConsentType.PRIVACY_POLICY }.map { it.toUIConsentItem() },
                    acceptConsents = {
                        coroutineScope.launch {
                           viewModel.saveConsents(userId, it)
                        }.invokeOnCompletion {
                            (context as? ComponentActivity)?.finish()
                        }
                    },
                    showPolicy = {
                        navController.navigate(AppScreen.PrivacyPolicy.name)
                    }
                )
            }
            composable(route = AppScreen.PrivacyPolicy.name) {
                consentsUiState.privacyPolicy?.let {
                    PrivacyPolicyScreen(
                        policy = it.description
                    )
                }
            }
        }
    }
}