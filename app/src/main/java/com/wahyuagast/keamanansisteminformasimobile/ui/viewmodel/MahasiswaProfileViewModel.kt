package com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wahyuagast.keamanansisteminformasimobile.data.local.TokenManager
import com.wahyuagast.keamanansisteminformasimobile.data.model.ProfileResponse
import com.wahyuagast.keamanansisteminformasimobile.data.model.UpdateProfileResponse
import com.wahyuagast.keamanansisteminformasimobile.data.repository.AuthRepository
import com.wahyuagast.keamanansisteminformasimobile.data.repository.DocumentRepository
import com.wahyuagast.keamanansisteminformasimobile.data.repository.MitraRepository
import com.wahyuagast.keamanansisteminformasimobile.data.repository.ProfileRepository
import com.wahyuagast.keamanansisteminformasimobile.data.repository.RegistrationRepository
import com.wahyuagast.keamanansisteminformasimobile.utils.InputSanitizer
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import kotlinx.coroutines.launch
import java.io.File

class MahasiswaProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProfileRepository()
    private val authRepository = AuthRepository(TokenManager(application))
    private val mitraRepository = MitraRepository()
    private val registrationRepository = RegistrationRepository()
    private val documentRepository = DocumentRepository()

    companion object {
        /**
         * Security: Clean up old temporary upload files that may have been orphaned.
         * This should be called on app start to ensure no sensitive data persists.
         * Files older than 24 hours are removed.
         */
        fun cleanupOldTempFiles(context: android.content.Context) {
            try {
                val cacheDir = context.cacheDir
                val oneDayInMillis = 24 * 60 * 60 * 1000L
                val now = System.currentTimeMillis()

                cacheDir.listFiles()?.forEach { file ->
                    if (file.name.startsWith("upload_") && file.name.endsWith(".tmp")) {
                        if (now - file.lastModified() > oneDayInMillis) {
                            file.delete()
                        }
                    }
                }
            } catch (e: Exception) {
                com.wahyuagast.keamanansisteminformasimobile.utils.AppLog.w(
                    "MahasiswaProfileVM",
                    "Cleanup failed"
                )
            }
        }
    }

    var profileState by mutableStateOf<Resource<ProfileResponse>>(
        Resource.Loading
    )
        private set

    var mitraState by mutableStateOf<Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.MitraResponse>>(
        Resource.Loading
    )
        private set

    var registrationState by mutableStateOf<Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.RegistrationStatusResponse>>(
        Resource.Loading
    )
        private set

    var formSubmissionState by mutableStateOf<Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.RegistrationFormResponse>>(
        Resource.Idle
    )
        private set

    var documentTypesState by mutableStateOf<Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentTypeResponse>>(
        Resource.Idle
    )
        private set

    var documentSubmissionState by mutableStateOf<Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentStoreResponse>>(
        Resource.Idle
    )
        private set

    var updateState by mutableStateOf<Resource<UpdateProfileResponse?>>(
        Resource.Idle
    )
        private set

    var periodsState by mutableStateOf<Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.PeriodeResponse>>(
        Resource.Idle
    )
        private set

    fun loadProfile() {
        viewModelScope.launch {
            profileState = Resource.Loading
            profileState = repository.getProfile()
        }
    }

    fun loadMitras() {
        viewModelScope.launch {
            mitraState = Resource.Loading
            mitraState = mitraRepository.getMitras()
        }
    }

    fun loadRegistrationStatus() {
        viewModelScope.launch {
            registrationState = Resource.Loading
            val result = registrationRepository.getRegistrationStatus()
            if (result is Resource.Error) {
                // Avoid logging raw server messages; keep generic log so debugging info isn't leaking
                com.wahyuagast.keamanansisteminformasimobile.utils.AppLog.e(
                    "MahasiswaProfileVM",
                    "Error loading registration status"
                )
            }
            registrationState = result
        }
    }

    fun loadDocumentTypes() {
        viewModelScope.launch {
            documentTypesState = Resource.Loading
            documentTypesState = documentRepository.getDocumentTypes()
        }
    }

    fun loadPeriods() {
        viewModelScope.launch {
            periodsState = Resource.Loading
            periodsState = registrationRepository.getPeriods()
        }
    }

    fun submitDocumentRequest(documentTypeId: Int, description: String) {
        viewModelScope.launch {
            documentSubmissionState = Resource.Loading
            documentSubmissionState =
                documentRepository.submitDocumentRequest(documentTypeId, description)
            if (documentSubmissionState is Resource.Success) {
            }
        }
    }

    fun resetDocumentSubmissionState() {
        documentSubmissionState = Resource.Idle
    }

    fun submitRegistrationForm(
        mitraId: String,
        periodeId: String,
        startDate: String,
        endDate: String
    ) {
        val sdf = java.text.SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault())
        try {
            val start = sdf.parse(startDate)
            val end = sdf.parse(endDate)
            if (start != null && end != null) {
                if (!end.after(start)) { // End date must be strictly AFTER start date (cannot be same or before)
                    formSubmissionState =
                        Resource.Error("Tanggal selesai tidak boleh sebelum tanggal mulai!")
                    return
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        viewModelScope.launch {
            formSubmissionState = Resource.Loading
            // Get user details from profile state
            val currentState = profileState
            val profile = if (currentState is Resource.Success) currentState.data else null
            val userProfile = profile?.user
            val awardee = userProfile?.awardee

            // Sanitize values coming from profile to avoid accidental injection when sending to API
            val fullname = InputSanitizer.sanitizeForApi(awardee?.fullname ?: "")
            val nim = InputSanitizer.sanitizeForApi(awardee?.nim ?: "")
            val email = InputSanitizer.sanitizeForApi(userProfile?.email ?: "")

            formSubmissionState = registrationRepository.submitRegistrationForm(
                fullname,
                nim,
                email,
                mitraId,
                periodeId,
                startDate,
                endDate
            )
            if (formSubmissionState is Resource.Success) {
                loadRegistrationStatus() // Refresh status after successful submission
            }
        }
    }

    fun resetFormSubmissionState() {
        formSubmissionState = Resource.Idle
    }

    fun updateProfile(
        email: String, username: String, nim: String, degree: String,
        phoneNumber: String, studyProgramId: String, year: String, fullname: String,
        imageFile: File?
    ) {
        viewModelScope.launch {
            updateState = Resource.Loading
            // Sanitize inputs before sending to repository (client-side only)
            val safeEmail = InputSanitizer.sanitizeForApi(email)
            val safeUsername = InputSanitizer.sanitizeForApi(username)
            val safeNim = InputSanitizer.sanitizeForApi(nim)
            val safeDegree = InputSanitizer.sanitizeForApi(degree)
            val safePhone = InputSanitizer.sanitizeForApi(phoneNumber)
            val safeStudyProgram = InputSanitizer.sanitizeForApi(studyProgramId)
            val safeYear = InputSanitizer.sanitizeForApi(year)
            val safeFullname = InputSanitizer.sanitizeForApi(fullname)

            updateState = repository.updateProfile(
                safeEmail,
                safeUsername,
                safeNim,
                safeDegree,
                safePhone,
                safeStudyProgram,
                safeYear,
                safeFullname,
                imageFile
            )

            // Reload profile on success
            if (updateState is Resource.Success) {
                loadProfile()
            }
        }
    }

    fun resetUpdateState() {
        updateState = Resource.Idle
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.clearToken()
            onLogoutSuccess()
        }
    }

    var uploadDocumentState by mutableStateOf<Resource<com.wahyuagast.keamanansisteminformasimobile.data.model.DocumentStoreResponse>>(
        Resource.Idle
    )
        private set

    fun uploadDocument(uri: android.net.Uri, documentTypeId: Int) {
        viewModelScope.launch {
            uploadDocumentState = Resource.Loading

            // Security: Use app-private cache directory with unique filename
            val context = getApplication<Application>()
            val contentResolver = context.contentResolver

            // Create temp file with unique name to avoid collisions
            val tempFile = File(
                context.cacheDir, // App-private storage (not accessible by other apps)
                "upload_${System.currentTimeMillis()}_${java.util.UUID.randomUUID()}.tmp"
            )

            try {
                // Validate MIME Type
                val type = contentResolver.getType(uri)
                if (type != "application/pdf") {
                    uploadDocumentState = Resource.Error("File harus berformat PDF")
                    return@launch
                }

                // Copy URI content to temp file with proper resource management
                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    uploadDocumentState = Resource.Error("Gagal membaca file")
                    return@launch
                }

                inputStream.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                // Validate Size (Max 2MB = 2 * 1024 * 1024 bytes)
                if (tempFile.length() > 2 * 1024 * 1024) {
                    uploadDocumentState = Resource.Error("Ukuran file maksimal 2MB")
                    return@launch
                }

                // Upload document
                uploadDocumentState =
                    documentRepository.uploadDocument(tempFile, documentTypeId)

                // Refresh status on success
                if (uploadDocumentState is Resource.Success) {
                    loadRegistrationStatus()
                }
            } catch (e: Exception) {
                com.wahyuagast.keamanansisteminformasimobile.utils.AppLog.e(
                    "MahasiswaProfileVM",
                    "Upload failed"
                )
                uploadDocumentState = Resource.Error("Gagal memproses file: ${e.localizedMessage}")
            } finally {
                // Security: Always delete temp file in finally block to ensure cleanup
                // This prevents sensitive data from persisting on disk
                if (tempFile.exists()) {
                    val deleted = tempFile.delete()
                    if (!deleted) {
                        com.wahyuagast.keamanansisteminformasimobile.utils.AppLog.w(
                            "MahasiswaProfileVM",
                            "Failed to delete temp file"
                        )
                    }
                }
            }
        }
    }

    fun resetUploadState() {
        uploadDocumentState = Resource.Idle
    }
}
