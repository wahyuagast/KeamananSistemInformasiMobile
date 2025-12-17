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
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.LoginScreen
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.student.*
import com.wahyuagast.keamanansisteminformasimobile.ui.screens.admin.*
import com.wahyuagast.keamanansisteminformasimobile.ui.theme.KeamananSistemInformasiMobileTheme
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.wahyuagast.keamanansisteminformasimobile.data.remote.RetrofitClient.initialize(applicationContext)

        setContent {
            val navController = rememberNavController()
            
            // Mock Data for Dashboard (In real app, fetch from ViewModel)
            val userData = mapOf(
                "nama" to "Budi Santoso",
                "nim" to "2021001",
                "email" to "budi@example.com",
                "prodi" to "Teknik Informatika",
                "phone" to "081234567890"
            )

            KeamananSistemInformasiMobileTheme {
                NavHost(navController = navController, startDestination = "login") {
                    
                    // --- LOGIN ---
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { user ->
                                // Assuming role_id 3 is Admin based on shared artifact. 
                                // Adjust this logic if other roles have specific IDs.
                                if (user.roleId == 3) {
                                    navController.navigate("dashboard_admin") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("dashboard_mahasiswa") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                                // Ideally, store the token/user session here (DataStore)
                            }
                        )
                    }

                    // --- STUDENT ROUTES ---
                    composable("dashboard_mahasiswa") {
                        MahasiswaDashboardScreen(
                            userData = userData,
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
                            userData = userData,
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
                            onLogout = {
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