package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.pkl

        import androidx.lifecycle.ViewModel
        import androidx.lifecycle.viewModelScope
        import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.*
        import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.repository.PklRepository
        import kotlinx.coroutines.flow.MutableStateFlow
        import kotlinx.coroutines.flow.StateFlow
        import kotlinx.coroutines.launch
        import java.io.File

        class PklViewModel(private val repo: PklRepository, private val currentUserId: String) : ViewModel() {
            private val _submissions = MutableStateFlow<List<SubmissionDto>>(emptyList())
            val submissions: StateFlow<List<SubmissionDto>> = _submissions

            @Suppress("unused")
            private val _registrations = MutableStateFlow<List<RegistrationDto>>(emptyList())
            @Suppress("unused")
            val registrations: StateFlow<List<RegistrationDto>> = _registrations

            private val _isLoading = MutableStateFlow(false)
            val isLoading: StateFlow<Boolean> = _isLoading

            private val _error = MutableStateFlow<String?>(null)
            val error: StateFlow<String?> = _error

            init { refreshAll() }

            fun clearError() { _error.value = null }

            fun refreshAll() {
                loadSubmissions()
                loadRegistrations()
            }

            fun loadSubmissions() = viewModelScope.launch {
                _isLoading.value = true
                try {
                    _submissions.value = repo.getMySubmissions()
                    _error.value = null
                } catch (e: Exception) { _error.value = e.localizedMessage }
                finally { _isLoading.value = false }
            }

            fun loadRegistrations() = viewModelScope.launch {
                _isLoading.value = true
                try {
                    _registrations.value = repo.getMyRegistrations()
                    _error.value = null
                } catch (e: Exception) { _error.value = e.localizedMessage }
                finally { _isLoading.value = false }
            }

            fun submitDocument(type: String, title: String?, desc: String?, file: File?, onDone: (Boolean)->Unit) = viewModelScope.launch {
                _isLoading.value = true
                try {
                    var fileUrl: String? = null
                    var storagePath: String? = null
                    if (file != null) {
                        val bucket = "uploads"
                        val path = "pkl/${currentUserId}/${type}/${file.name}"
                        val up = repo.uploadFileToStorage(file, bucket, path)
                        fileUrl = up.url
                        storagePath = up.storage_path
                        repo.createUploadMetadata(currentUserId, file.name, storagePath ?: path, fileUrl ?: "")
                    }
                    val req = CreateSubmissionRequest(user_id = currentUserId, type = type, title = title, description = desc, file_url = fileUrl)
                    val created = repo.createSubmission(req)
                    loadSubmissions()
                    onDone(created != null)
                } catch (e: Exception) {
                    _error.value = e.localizedMessage ?: "Gagal submit"
                    onDone(false)
                } finally { _isLoading.value = false }
            }

            fun registerPkl(formType: String, fields: Map<String,String>, file: File?, onDone: (Boolean)->Unit) = viewModelScope.launch {
                _isLoading.value = true
                try {
                    var fileUrl: String? = null
                    var storagePath: String? = null
                    if (file != null) {
                        val bucket = "uploads"
                        val path = "pkl/${currentUserId}/registration/${file.name}"
                        val up = repo.uploadFileToStorage(file, bucket, path)
                        fileUrl = up.url
                        storagePath = up.storage_path
                        repo.createUploadMetadata(currentUserId, file.name, storagePath ?: path, fileUrl ?: "")
                    }
                    val req = CreateRegistrationRequest(user_id = currentUserId, form_type = formType, fields = fields, file_url = fileUrl)
                    val created = repo.createRegistration(req)
                    loadRegistrations()
                    onDone(created != null)
                } catch (e: Exception) {
                    _error.value = e.localizedMessage ?: "Gagal daftar"
                    onDone(false)
                } finally { _isLoading.value = false }
            }

            fun adminAction(submissionId: String, action: String, comment: String? = null, onDone: (Boolean)->Unit) = viewModelScope.launch {
                _isLoading.value = true
                try {
                    val ok = repo.doSubmissionAction(submissionId, action, comment)
                    if (ok) loadSubmissions()
                    onDone(ok)
                } catch (e: Exception) { _error.value = e.localizedMessage; onDone(false) }
                finally { _isLoading.value = false }
            }

            fun loadAdminRegistrations() = viewModelScope.launch {
                _isLoading.value = true
                try {
                    _submissions.value = repo.getAllRegistrationsAsSubmissions()
                    _error.value = null
                } catch (e: Exception) {
                    _error.value = e.localizedMessage
                } finally {
                    _isLoading.value = false
                }
            }

            fun adminReviewRegistration(
                submissionId: String,
                action: String,
                comment: String?,
                onDone: (Boolean) -> Unit = {}
            ) = viewModelScope.launch {
                _isLoading.value = true
                try {
                    val ok = repo.reviewRegistration(submissionId, action, comment)
                    if (ok) loadAdminRegistrations()
                    onDone(ok)
                } catch (e: Exception) {
                    _error.value = e.localizedMessage
                    onDone(false)
                } finally {
                    _isLoading.value = false
                }
            }
        }