package com.example.unitrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.unitrack.data.model.UserProfile
import com.example.unitrack.ui.admin.AdminHomeScreen
import com.example.unitrack.ui.admin.AdminRequestsScreen
import com.example.unitrack.ui.admin.ManageCategoriesScreen
import com.example.unitrack.ui.auth.LoginScreen
import com.example.unitrack.ui.auth.RegisterScreen
import com.example.unitrack.ui.profile.ProfileScreen
import com.example.unitrack.ui.user.CreateRequestScreen
import com.example.unitrack.ui.user.MyRequestsScreen
import com.example.unitrack.ui.user.RequestHistoryScreen
import com.example.unitrack.ui.user.UserHomeScreen
import com.example.unitrack.viewmodel.AuthViewModel
import com.example.unitrack.viewmodel.CategoryViewModel
import com.example.unitrack.viewmodel.ProfileViewModel
import com.example.unitrack.viewmodel.RequestViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState.user) {
        val user = authState.user

        if (user != null) {
            val destination = if (user.profileType == "ADMIN") {
                Routes.ADMIN_HOME
            } else {
                Routes.USER_HOME
            }

            navController.navigate(destination) {
                popUpTo(Routes.LOGIN) {
                    inclusive = true
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                authState = authState,
                onLoginClick = { email, password ->
                    authViewModel.login(email, password)
                },
                onRegisterClick = {
                    authViewModel.clearError()
                    navController.navigate(Routes.REGISTER)
                },
                onClearError = {
                    authViewModel.clearError()
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                authState = authState,
                onRegisterClick = { name, email, password, profileType ->
                    authViewModel.register(name, email, password, profileType)
                },
                onBackToLogin = {
                    authViewModel.clearError()
                    navController.popBackStack()
                },
                onClearError = {
                    authViewModel.clearError()
                }
            )
        }

        composable(Routes.USER_HOME) {
            RequireRole(
                user = authState.user,
                requiredProfileType = "USER",
                navController = navController
            ) {
                UserHomeScreen(
                    onCreateRequest = {
                        navController.navigate(Routes.CREATE_REQUEST)
                    },
                    onMyRequests = {
                        navController.navigate(Routes.MY_REQUESTS)
                    },
                    onHistory = {
                        navController.navigate(Routes.REQUEST_HISTORY)
                    },
                    onProfile = {
                        navController.navigate(Routes.PROFILE)
                    },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0)
                        }
                    }
                )
            }
        }

        composable(Routes.ADMIN_HOME) {
            RequireRole(
                user = authState.user,
                requiredProfileType = "ADMIN",
                navController = navController
            ) {
                val requestViewModel: RequestViewModel = viewModel()
                val adminStatsState by requestViewModel.adminStatsState.collectAsState()

                LaunchedEffect(Unit) {
                    requestViewModel.loadAdminStats()
                }

                AdminHomeScreen(
                    adminStatsState = adminStatsState,
                    onRefreshStats = {
                        requestViewModel.loadAdminStats()
                    },
                    onRequests = {
                        navController.navigate(Routes.ADMIN_REQUESTS)
                    },
                    onCategories = {
                        navController.navigate(Routes.MANAGE_CATEGORIES)
                    },
                    onProfile = {
                        navController.navigate(Routes.PROFILE)
                    },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0)
                        }
                    }
                )
            }
        }

        composable(Routes.CREATE_REQUEST) {
            RequireRole(
                user = authState.user,
                requiredProfileType = "USER",
                navController = navController
            ) { user ->
                val requestViewModel: RequestViewModel = viewModel()
                val createRequestState by requestViewModel.createRequestState.collectAsState()

                LaunchedEffect(Unit) {
                    requestViewModel.loadCategories()
                }

                CreateRequestScreen(
                    state = createRequestState,
                    onCreateRequest = { categoryId, location, description, photoBytes ->
                        requestViewModel.createRequest(
                            userId = user.id,
                            categoryId = categoryId,
                            location = location,
                            description = description,
                            photoBytes = photoBytes
                        )
                    },
                    onClearMessages = {
                        requestViewModel.clearMessages()
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Routes.MY_REQUESTS) {
            RequireRole(
                user = authState.user,
                requiredProfileType = "USER",
                navController = navController
            ) { user ->
                val requestViewModel: RequestViewModel = viewModel()
                val userRequestsState by requestViewModel.userRequestsState.collectAsState()

                LaunchedEffect(user.id) {
                    requestViewModel.loadUserRequests(user.id)
                }

                MyRequestsScreen(
                    state = userRequestsState,
                    onRefresh = {
                        requestViewModel.loadUserRequests(user.id)
                    },
                    onCancelRequest = { requestId ->
                        requestViewModel.cancelRequest(requestId, user.id)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Routes.REQUEST_HISTORY) {
            RequireRole(
                user = authState.user,
                requiredProfileType = "USER",
                navController = navController
            ) { user ->
                val requestViewModel: RequestViewModel = viewModel()
                val historyRequestsState by requestViewModel.historyRequestsState.collectAsState()

                LaunchedEffect(user.id) {
                    requestViewModel.loadHistoryRequests(user.id)
                }

                RequestHistoryScreen(
                    state = historyRequestsState,
                    onRefresh = {
                        requestViewModel.loadHistoryRequests(user.id)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Routes.ADMIN_REQUESTS) {
            RequireRole(
                user = authState.user,
                requiredProfileType = "ADMIN",
                navController = navController
            ) {
                val requestViewModel: RequestViewModel = viewModel()
                val adminRequestsState by requestViewModel.adminRequestsState.collectAsState()

                LaunchedEffect(Unit) {
                    requestViewModel.loadAllRequestsForAdmin()
                }

                AdminRequestsScreen(
                    state = adminRequestsState,
                    onRefresh = {
                        requestViewModel.loadAllRequestsForAdmin()
                    },
                    onUpdateStatus = { requestId, newStatus ->
                        requestViewModel.updateRequestStatusAsAdmin(
                            requestId = requestId,
                            newStatus = newStatus
                        )
                    },
                    onDeleteRequest = { requestId ->
                        requestViewModel.deleteRequestAsAdmin(requestId)
                    },
                    onClearMessages = {
                        requestViewModel.clearAdminMessages()
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Routes.MANAGE_CATEGORIES) {
            RequireRole(
                user = authState.user,
                requiredProfileType = "ADMIN",
                navController = navController
            ) {
                val categoryViewModel: CategoryViewModel = viewModel()
                val categoryState by categoryViewModel.categoryState.collectAsState()

                LaunchedEffect(Unit) {
                    categoryViewModel.loadCategories()
                }

                ManageCategoriesScreen(
                    state = categoryState,
                    onRefresh = {
                        categoryViewModel.loadCategories()
                    },
                    onCreateCategory = { name, description ->
                        categoryViewModel.createCategory(name, description)
                    },
                    onUpdateCategory = { categoryId, name, description ->
                        categoryViewModel.updateCategory(categoryId, name, description)
                    },
                    onRemoveCategory = { categoryId ->
                        categoryViewModel.removeCategory(categoryId)
                    },
                    onClearMessages = {
                        categoryViewModel.clearMessages()
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Routes.PROFILE) {
            RequireAuthenticated(
                user = authState.user,
                navController = navController
            ) { user ->
                val profileViewModel: ProfileViewModel = viewModel()
                val profileState by profileViewModel.profileState.collectAsState()

                LaunchedEffect(user.id) {
                    profileViewModel.loadProfile(user.id)
                }

                ProfileScreen(
                    state = profileState,
                    onSaveName = { name ->
                        profileViewModel.updateName(
                            userId = user.id,
                            name = name
                        )
                    },
                    onUpdatePassword = { newPassword, confirmPassword ->
                        profileViewModel.updatePassword(
                            newPassword = newPassword,
                            confirmPassword = confirmPassword
                        )
                    },
                    onClearMessages = {
                        profileViewModel.clearMessages()
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
private fun RequireAuthenticated(
    user: UserProfile?,
    navController: NavHostController,
    content: @Composable (UserProfile) -> Unit
) {
    LaunchedEffect(user?.id) {
        if (user == null) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0)
            }
        }
    }

    if (user != null) {
        content(user)
    }
}

@Composable
private fun RequireRole(
    user: UserProfile?,
    requiredProfileType: String,
    navController: NavHostController,
    content: @Composable (UserProfile) -> Unit
) {
    LaunchedEffect(user?.id, user?.profileType) {
        when {
            user == null -> {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(0)
                }
            }

            user.profileType != requiredProfileType -> {
                val correctDestination = if (user.profileType == "ADMIN") {
                    Routes.ADMIN_HOME
                } else {
                    Routes.USER_HOME
                }

                navController.navigate(correctDestination) {
                    popUpTo(0)
                }
            }
        }
    }

    if (user != null && user.profileType == requiredProfileType) {
        content(user)
    }
}