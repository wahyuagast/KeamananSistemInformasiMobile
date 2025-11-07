package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.DocumentDto
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.ProfileDto
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val loading: Boolean = false,
    val profile: ProfileDto? = null,
    val documents: List<DocumentDto> = emptyList(),
    val allProfiles: List<ProfileDto> = emptyList(),
    val error: String? = null
)

class HomeViewModel(private val repo: AuthRepository) : ViewModel() {
    private val _ui = MutableStateFlow(HomeUiState(loading = true))
    val ui: StateFlow<HomeUiState> = _ui

    fun loadAll() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)
            try {
                val profile = repo.getMyProfile().firstOrNull()
                var docs: List<DocumentDto> = emptyList()
                var profiles: List<ProfileDto> = emptyList()

                // if we have profile, load documents owned by user
                profile?.let {
                    // PostgREST expects query owner_id=eq.<uuid>
                    docs = repo.getDocumentsForOwner("eq.${it.id}")
                }

                // if user is admin, load all profiles (admin-only capability)
                if (profile?.role == "admin") {
                    profiles = repo.getAllProfiles()
                    // optionally load all documents (omitted for brevity)
                }

                _ui.value = _ui.value.copy(
                    loading = false,
                    profile = profile,
                    documents = docs,
                    allProfiles = profiles
                )
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(loading = false, error = e.localizedMessage ?: "Failed")
            }
        }
    }

    fun logout(onDone: ()->Unit) {
        viewModelScope.launch {
            repo.clearToken()
            onDone()
        }
    }
}