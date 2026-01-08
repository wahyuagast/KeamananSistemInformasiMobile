package com.wahyuagast.keamanansisteminformasimobile

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.LoginScreen
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.RegisterScreen
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.admin.AdminDashboardScreen
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.admin.AdminMahasiswaScreen
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.admin.AdminProfileScreen
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.admin.AdminRegistrationListScreen
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.admin.AdminRegistrationScreen
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.admin.AdminSuratScreen
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.student.MahasiswaDashboardScreen
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.student.MonevScreen
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.student.PelaksanaanScreen
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.student.PendaftaranScreen
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.student.ProfileScreen
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.student.SuratScreen
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.student.UjianScreen
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.KeamananSistemInformasiMobileTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.wahyuagast.keamanansisteminformasimobile.data.remote.RetrofitClient.initialize(
            applicationContext
        )

        // Security: Clean up old temporary files on app start
        com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel.MahasiswaProfileViewModel.cleanupOldTempFiles(
            applicationContext
        )

        setContent {
            val navController = rememberNavController()

            KeamananSistemInformasiMobileTheme {
                val mainViewModel: com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel.MainViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel()
                val sessionState by mainViewModel.sessionState.collectAsState()


                NavHost(navController = navController, startDestination = "splash") {

                    composable("splash") {

                        LaunchedEffect(sessionState) {
                            when (val state = sessionState) {
                                is com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel.SessionState.Authenticated -> {
                                    if (state.roleId == 3) {
                                        navController.navigate("dashboard_admin") {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate("dashboard_mahasiswa") {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    }
                                }

                                is com.wahyuagast.keamanansisteminformasimobile.ui.viewmodel.SessionState.Unauthenticated -> {
                                    navController.navigate("login") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }

                                else -> { /* Loading */
                                }
                            }
                        }

                        // Simple centered loading screen
                        androidx.compose.foundation.layout.Box(
                            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            androidx.compose.material3.CircularProgressIndicator(
                                color = com.wahyuagast.keamanansisteminformasimobile.ui.theme.CustomPrimary
                            )
                        }
                    }

                    // --- LOGIN ---
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { user ->
                                // Role ID 3 = Admin, Role ID 2 = Mahasiswa (Student)
                                if (user.roleId == 3) {
                                    navController.navigate("dashboard_admin") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("dashboard_mahasiswa") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            },
                            onNavigateToRegister = {
                                navController.navigate("register")
                            }
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            onRegisterSuccess = {
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            },
                            onNavigateToLogin = {
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }
                        )
                    }

                    // --- STUDENT ROUTES ---
                    composable("dashboard_mahasiswa") {
                        MahasiswaDashboardScreen(
                            onNavigate = { route -> navController.navigate(route) },
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("dashboard_mahasiswa") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("profile") {
                        ProfileScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("surat") {
                        SuratScreen(onBack = { navController.popBackStack() })
                    }

                    composable("pendaftaran") {
                        PendaftaranScreen(onBack = { navController.popBackStack() })
                    }

                    composable("pelaksanaan") {
                        PelaksanaanScreen(onBack = { navController.popBackStack() })
                    }

                    composable("monev") {
                        MonevScreen(onBack = { navController.popBackStack() })
                    }

                    composable("ujian") {
                        UjianScreen(onBack = { navController.popBackStack() })
                    }

                    // --- ADMIN ROUTES ---
                    composable("dashboard_admin") {
                        AdminDashboardScreen(
                            onNavigate = { route -> navController.navigate(route) },
                            _onLogout = {
                                navController.navigate("login") {
                                    popUpTo("dashboard_admin") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("admin-surat") {
                        AdminSuratScreen(onBack = { navController.popBackStack() })
                    }

                    composable("admin-mahasiswa") {
                        AdminMahasiswaScreen(onBack = { navController.popBackStack() })
                    }

                    composable("admin-registration-list") {
                        AdminRegistrationListScreen(
                            onBack = { navController.popBackStack() },
                            onNavigateToDetail = { id ->
                                navController.navigate("admin-registration/$id")
                            }
                        )
                    }

                    composable("admin-registration/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                        AdminRegistrationScreen(
                            registrationId = id,
                            onBack = { navController.popBackStack() })
                    }

                    composable("admin-profile") {
                        AdminProfileScreen(
                            onBack = { navController.popBackStack() },
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("dashboard_admin") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}