package com.wahyuagast.keamanansisteminformasimobile

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.data.TokenManager
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.network.RetrofitProvider
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.repository.AuthRepository
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.repository.PklRepository
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.AdminHomeScreen
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.AuthViewModel
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.LoginScreen
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.RegisterScreen
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.UserHomeScreen
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.HomeViewModel
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.ProfileDto
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.pkl.AdminDashboard
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.pkl.PklViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MainActivity : ComponentActivity() {

    // helper: convert Uri to File in cache dir
    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            if (inputStream == null) return null
            val file = File(cacheDir, "upload_${System.currentTimeMillis()}")
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()
            file
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init infra - these are available outside compose
        val tokenManager = TokenManager(applicationContext)
        val authApi = RetrofitProvider.provideAuthApi()
        val postgrestApi = RetrofitProvider.providePostgrestApi(applicationContext, tokenManager)
        val authRepo = AuthRepository(authApi, postgrestApi, tokenManager)
        val authVm = AuthViewModel(authRepo)
        val homeVm = HomeViewModel(authRepo)

        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current

            // Holds the profile returned after login; used to pass to Home screens
            var currentProfile by remember { mutableStateOf<ProfileDto?>(null) }

            // NOTE: we instantiate pklApi/repo inside composables (so tokenManager and context accessible)
            NavHost(navController = navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(
                        vm = authVm,
                        onRegister = { navController.navigate("register") },
                        onLoginSuccess = { profile ->
                            currentProfile = profile
                            if (profile == null) return@LoginScreen

                            if (profile.role == "admin") {
                                navController.navigate("admin_home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                navController.navigate("user_home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable("register") {
                    RegisterScreen(vm = authVm, onBack = { navController.popBackStack() })
                }

                composable("user_home") {
                    // create PklRepository & ViewModel per currentProfile
                    val pklApi = RetrofitProvider.providePklApi(tokenManager, applicationContext)
                    val pklRepo = PklRepository(pklApi, tokenManager, applicationContext)
                    val currentUserId = currentProfile?.id ?: ""
                    val pklVm = remember { PklViewModel(pklRepo, currentUserId) }

                    UserHomeScreen(
                        profile = currentProfile,
                        onLogout = {
                            authVm.logout {
                                currentProfile = null
                                navController.navigate("login") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                            }
                        },
                        // additional navigation into PKL flow:
                        onOpenStudentDashboard = {
                            navController.navigate("student_dashboard")
                        }
                    )
                }

                composable("admin_home") {
                    AdminHomeScreen(
                        profile = currentProfile,
                        onLogout = {
                            authVm.logout {
                                currentProfile = null
                                navController.navigate("login") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                            }
                        },
                        onOpenAdminDashboard = {
                            navController.navigate("admin_dashboard")
                        }
                    )
                }

                // Student PKL screens
                composable("student_dashboard") {
                    val pklApi = RetrofitProvider.providePklApi(tokenManager, applicationContext)
                    val pklRepo = PklRepository(pklApi, tokenManager, applicationContext)
                    val currentUserId = currentProfile?.id ?: ""
                    val pklVm = remember { PklViewModel(pklRepo, currentUserId) }

                    // collect submissions state
                    val submissions by pklVm.submissions.collectAsState()

                    // import StudentDashboard composable from your UI package
                    com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.pkl.StudentDashboard(
                        submissions = submissions,
                        onCreateSubmission = { navController.navigate("submission_form") },
                        onOpenRegistration = { navController.navigate("registration_form") },
                        onOpenExecution = { navController.navigate("execution_form") },
                        onOpenMonev = { navController.navigate("monev_form") },
                        onOpenFinalReport = { navController.navigate("final_report") },
                        onOpenExamDocs = { navController.navigate("exam_docs") }
                    )
                }

                composable("submission_form") {
                    val pklApi = RetrofitProvider.providePklApi(tokenManager, applicationContext)
                    val pklRepo = PklRepository(pklApi, tokenManager, applicationContext)
                    val currentUserId = currentProfile?.id ?: ""
                    val pklVm = remember { PklViewModel(pklRepo, currentUserId) }

                    com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.pkl.SubmissionFormScreen(
                        onSubmit = { type, title, desc, file ->
                            pklVm.submitDocument(type, title, desc, file) { ok ->
                                if (ok) navController.popBackStack()
                            }
                        },
                        onBack = { navController.popBackStack() },
                        pickFileAsFile = { uri -> uriToFile(uri) ?: uriToFileFallback(context, uri) }
                    )
                }

                composable("registration_form") {
                    val pklApi = RetrofitProvider.providePklApi(tokenManager, applicationContext)
                    val pklRepo = PklRepository(pklApi, tokenManager, applicationContext)
                    val currentUserId = currentProfile?.id ?: ""
                    val pklVm = remember { PklViewModel(pklRepo, currentUserId) }

                    com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.pkl.RegistrationFormScreen(
                        onSubmitRegistration = { formType, fields, file ->
                            pklVm.registerPkl(formType, fields, file) { ok ->
                                if (ok) navController.popBackStack()
                            }
                        },
                        onBack = { navController.popBackStack() },
                        pickFileAsFile = { uri -> uriToFile(uri) ?: uriToFileFallback(context, uri) }
                    )
                }

                composable("execution_form") {
                    val pklApi = RetrofitProvider.providePklApi(tokenManager, applicationContext)
                    val pklRepo = PklRepository(pklApi, tokenManager, applicationContext)
                    val currentUserId = currentProfile?.id ?: ""
                    val pklVm = remember { PklViewModel(pklRepo, currentUserId) }

                    com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.pkl.ExecutionFormScreen(
                        onSubmitExecution = { start, end, activities, file ->
                            // treat as submission type "form_3a" or similar
                            pklVm.submitDocument("form_3a", "Pelaksanaan PKL", activities, file) { ok ->
                                if (ok) navController.popBackStack()
                            }
                        },
                        onBack = { navController.popBackStack() },
                        pickFileAsFile = { uri -> uriToFile(uri) ?: uriToFileFallback(context, uri) }
                    )
                }

                composable("monev_form") {
                    val pklApi = RetrofitProvider.providePklApi(tokenManager, applicationContext)
                    val pklRepo = PklRepository(pklApi, tokenManager, applicationContext)
                    val currentUserId = currentProfile?.id ?: ""
                    val pklVm = remember { PklViewModel(pklRepo, currentUserId) }

                    com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.pkl.MonevScreen(
                        onSubmitMonev = { notes, checklist, file ->
                            // store as submission type "monev"
                            pklVm.submitDocument("monev", "Monev", notes, file) { ok ->
                                if (ok) navController.popBackStack()
                            }
                        },
                        onBack = { navController.popBackStack() },
                        pickFileAsFile = { uri -> uriToFile(uri) ?: uriToFileFallback(context, uri) }
                    )
                }

                composable("final_report") {
                    val pklApi = RetrofitProvider.providePklApi(tokenManager, applicationContext)
                    val pklRepo = PklRepository(pklApi, tokenManager, applicationContext)
                    val currentUserId = currentProfile?.id ?: ""
                    val pklVm = remember { PklViewModel(pklRepo, currentUserId) }

                    com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.pkl.FinalReportScreen(
                        onUploadFinal = { logbook, surat, draft ->
                            // create multiple submissions for each file (logbook/surat/draft)
                            if (logbook != null) pklVm.submitDocument("logbook", "Logbook", "Upload logbook", logbook) {}
                            if (surat != null) pklVm.submitDocument("surat_selesai", "Surat Selesai", "Surat selesai", surat) {}
                            if (draft != null) pklVm.submitDocument("draft_jurnal", "Draft Jurnal", "Draft jurnal", draft) {}
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() },
                        pickFileAsFile = { uri -> uriToFile(uri) ?: uriToFileFallback(context, uri) }
                    )
                }

                composable("exam_docs") {
                    val pklApi = RetrofitProvider.providePklApi(tokenManager, applicationContext)
                    val pklRepo = PklRepository(pklApi, tokenManager, applicationContext)
                    val currentUserId = currentProfile?.id ?: ""
                    val pklVm = remember { PklViewModel(pklRepo, currentUserId) }

                    com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.pkl.ExamDocsScreen(
                        onUploadExamDocs = { files ->
                            files.forEach { f ->
                                pklVm.submitDocument("exam_doc", "Berkas Ujian", "Upload berkas ujian", f) {}
                            }
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() },
                        pickFileAsFile = { uri -> uriToFile(uri) ?: uriToFileFallback(context, uri) }
                    )
                }

                // Admin PKL dashboard (list + review)
                composable("admin_dashboard") {
                    val pklApi = RetrofitProvider.providePklApi(tokenManager, applicationContext)
                    val pklRepo = PklRepository(pklApi, tokenManager, applicationContext)
                    val currentUserId = currentProfile?.id ?: ""
                    val pklVm = remember { PklViewModel(pklRepo, currentUserId) }

                    val submissions by pklVm.submissions.collectAsState()

                    AdminDashboard(
                        submissions = submissions,
                        onRefresh = { pklVm.loadSubmissions() },
                        onAction = { id, action, comment ->
                            pklVm.adminAction(id, action, comment) { success ->
                                // optionally show toast/snackbar - keep simple: refresh
                                if (success) pklVm.loadSubmissions()
                            }
                        }
                    )
                }
            }
        }
    }

    // fallback read-copy method (attempt 2) if primary fails
    private fun uriToFileFallback(context: android.content.Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(cacheDir, "upload_fallback_${System.currentTimeMillis()}")
            FileOutputStream(file).use { output -> inputStream.copyTo(output) }
            inputStream.close()
            file
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }
}
