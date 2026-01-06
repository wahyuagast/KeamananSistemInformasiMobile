package com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wahyuagast.keamanansisteminformasimobile.data.local.TokenManager
import com.wahyuagast.keamanansisteminformasimobile.data.model.RegisterRequest
import com.wahyuagast.keamanansisteminformasimobile.data.model.AuthRegisterResponse
import com.wahyuagast.keamanansisteminformasimobile.data.repository.AuthRepository
import com.wahyuagast.keamanansisteminformasimobile.utils.Resource
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(TokenManager(application))

    // Keep a concrete typed state so UI can access fields like `message` without casts
    var registerState by mutableStateOf<Resource<AuthRegisterResponse>?>(null)
        private set

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var passwordConfirmation by mutableStateOf("")
    var fullname by mutableStateOf("")
    var username by mutableStateOf("")
    var nim by mutableStateOf("")
    var degree by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var studyProgramId by mutableStateOf("")
    var year by mutableStateOf("")

    fun onEmailChange(v: String) { email = v }
    fun onPasswordChange(v: String) { password = v }
    fun onPasswordConfirmationChange(v: String) { passwordConfirmation = v }
    fun onFullnameChange(v: String) { fullname = v }
    fun onUsernameChange(v: String) { username = v }
    fun onNimChange(v: String) { nim = v }
    fun onDegreeChange(v: String) { degree = v }
    fun onPhoneNumberChange(v: String) { phoneNumber = v }
    fun onStudyProgramIdChange(v: String) { studyProgramId = v }
    fun onYearChange(v: String) { year = v }

    fun register() {
        viewModelScope.launch {
            // local trimming and normalization
            val e = email.trim().lowercase()
            val pw = password
            val pwc = passwordConfirmation
            val fn = fullname.trim()
            val un = username.trim()
            val n = nim.trim()
            val dg = degree.trim()
            val ph = phoneNumber.trim()
            val sp = studyProgramId.trim()
            val yr = year.trim()

            // basic validation with field-level errors
            val fieldErrors = mutableMapOf<String, MutableList<String>>()

            if (e.isEmpty()) fieldErrors.getOrPut("email") { mutableListOf() }.add("Email tidak boleh kosong")
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()) fieldErrors.getOrPut("email") { mutableListOf() }.add("Format email tidak valid")

            if (pw.isEmpty()) fieldErrors.getOrPut("password") { mutableListOf() }.add("Password tidak boleh kosong")
            else if (pw.length < 8) fieldErrors.getOrPut("password") { mutableListOf() }.add("Password minimal 8 karakter")

            if (pwc.isEmpty()) fieldErrors.getOrPut("password_confirmation") { mutableListOf() }.add("Konfirmasi password tidak boleh kosong")
            else if (pw != pwc) fieldErrors.getOrPut("password_confirmation") { mutableListOf() }.add("Konfirmasi password tidak cocok")

            if (fn.isEmpty()) fieldErrors.getOrPut("fullname") { mutableListOf() }.add("Nama lengkap tidak boleh kosong")
            if (un.isEmpty()) fieldErrors.getOrPut("username") { mutableListOf() }.add("Username tidak boleh kosong")
            if (n.isEmpty()) fieldErrors.getOrPut("nim") { mutableListOf() }.add("NIM tidak boleh kosong")
            if (dg.isEmpty()) fieldErrors.getOrPut("degree") { mutableListOf() }.add("Gelar tidak boleh kosong")
            if (ph.isEmpty()) fieldErrors.getOrPut("phoneNumber") { mutableListOf() }.add("No. HP tidak boleh kosong")
            if (sp.isEmpty()) fieldErrors.getOrPut("studyProgramId") { mutableListOf() }.add("ID Program Studi tidak boleh kosong")
            if (yr.isEmpty()) fieldErrors.getOrPut("year") { mutableListOf() }.add("Angkatan tidak boleh kosong")

            if (fieldErrors.isNotEmpty()) {
                // Convert to immutable map of lists
                val im = fieldErrors.mapValues { it.value.toList() }
                registerState = Resource.Error("Validasi gagal", im)
                return@launch
            }

            registerState = Resource.Loading
            val req = RegisterRequest(
                email = e,
                password = pw,
                passwordConfirmation = pwc,
                fullname = fn,
                username = un,
                nim = n,
                degree = dg,
                phoneNumber = ph,
                studyProgramId = sp,
                year = yr
            )

            // Call repository; do not add any registration token by default
            val result = repository.register(req, null)
            registerState = result
        }
    }

    fun resetState() { registerState = null }
}
