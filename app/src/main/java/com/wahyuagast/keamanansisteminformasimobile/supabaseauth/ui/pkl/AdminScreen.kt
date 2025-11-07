package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.pkl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun AdminScreen(vm: PklViewModel) {
    val submissions by vm.submissions.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(Unit) { vm.loadAdminRegistrations() }

    AdminDashboard(
        submissions = submissions,
        onRefresh = { vm.loadAdminRegistrations() },
        onAction = { id, action, comment -> vm.adminReviewRegistration(id, action, comment) },
        isLoading = isLoading,
        error = error,
        onErrorShown = { vm.clearError() }
    )
}