package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.pkl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.repository.PklRepository

class PklViewModelFactory(
    private val repo: PklRepository,
    private val currentUserId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PklViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PklViewModel(repo, currentUserId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}