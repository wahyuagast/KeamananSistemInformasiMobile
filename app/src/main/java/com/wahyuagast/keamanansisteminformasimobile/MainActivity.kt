package com.wahyuagast.keamanansisteminformasimobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.data.TokenManager
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.network.RetrofitProvider
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.repository.AuthRepository
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.AdminHomeScreen
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.AuthViewModel
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.LoginScreen
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.RegisterScreen
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.UserHomeScreen
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.ui.HomeViewModel
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.ProfileDto

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init infra
        val tokenManager = TokenManager(applicationContext)
        val authApi = RetrofitProvider.provideAuthApi()
        val postgrestApi = RetrofitProvider.providePostgrestApi(applicationContext, tokenManager)
        val repo = AuthRepository(authApi, postgrestApi, tokenManager)
        val authVm = AuthViewModel(repo)
        val homeVm = HomeViewModel(repo)

        setContent {
            val navController = rememberNavController()

            // Holds the profile returned after login; used to pass to Home screens
            var currentProfile by remember { mutableStateOf<ProfileDto?>(null) }

            NavHost(navController = navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(
                        vm = authVm,
                        onRegister = { navController.navigate("register") },
                        onLoginSuccess = { profile ->
                            // Save profile to state so Home screens can read it
                            currentProfile = profile

                            // safety: if profile is null (login failed/needs confirm) keep at login
                            if (profile == null) {
                                // optional: show error handled by ViewModel; remain on login
                                return@LoginScreen
                            }

                            if (profile.role == "admin") {
                                navController.navigate("admin_home") {
                                    // clear backstack so user doesn't go back to login with back button
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
                    // pass the profile object saved earlier
                    UserHomeScreen(
                        profile = currentProfile,
                        onLogout = {
                            // call logout which clears server/local token, then navigate to login
                            authVm.logout {
                                currentProfile = null
                                navController.navigate("login") {
                                    // clear entire backstack and land on login
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                            }
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
                        }
                    )
                }
            }
        }
    }
}