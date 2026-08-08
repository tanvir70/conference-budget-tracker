package com.tanvir.conferencebudget.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tanvir.conferencebudget.ui.auth.LoginScreen
import com.tanvir.conferencebudget.ui.conferences.ConferenceListScreen
import com.tanvir.conferencebudget.ui.conferences.CreateConferenceScreen
import com.tanvir.conferencebudget.ui.dashboard.DashboardScreen
import com.tanvir.conferencebudget.ui.items.AddCategoryScreen
import com.tanvir.conferencebudget.ui.items.AddEditItemScreen
import com.tanvir.conferencebudget.ui.items.AddSpendingEntryScreen
import com.tanvir.conferencebudget.ui.items.AddSubCategoryScreen
import com.tanvir.conferencebudget.ui.person.PersonDetailScreen
import com.tanvir.conferencebudget.ui.person.RecordTransactionScreen
import com.tanvir.conferencebudget.viewmodel.AuthViewModel
import com.tanvir.conferencebudget.viewmodel.BudgetViewModel
import com.tanvir.conferencebudget.viewmodel.BudgetViewModelFactory
import com.tanvir.conferencebudget.viewmodel.ConferenceViewModel
import com.tanvir.conferencebudget.viewmodel.PersonViewModel
import com.tanvir.conferencebudget.viewmodel.PersonViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val conferenceViewModel: ConferenceViewModel = viewModel()
    val currentUser by authViewModel.currentUser.collectAsState()

    val startDestination = if (currentUser != null) "conferences" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("conferences") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        composable("conferences") {
            ConferenceListScreen(
                viewModel = conferenceViewModel,
                authViewModel = authViewModel,
                onNavigateToCreate = { navController.navigate("create_conference") },
                onConferenceSelected = { confId -> navController.navigate("dashboard/$confId") },
                onNavigateToBulkOnboard = { navController.navigate("bulk_onboard") },
                onSignOut = {
                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable("create_conference") {
            CreateConferenceScreen(
                viewModel = conferenceViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            "dashboard/{conferenceId}",
            arguments = listOf(navArgument("conferenceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val conferenceId = backStackEntry.arguments?.getString("conferenceId") ?: return@composable
            val budgetViewModel: BudgetViewModel = viewModel(factory = BudgetViewModelFactory(conferenceId))
            val conferences by conferenceViewModel.conferences.collectAsState()
            val confName = conferences.find { it.id == conferenceId }?.name ?: "Dashboard"

            DashboardScreen(
                conferenceId = conferenceId,
                conferenceName = confName,
                budgetViewModel = budgetViewModel,
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddCategory = { navController.navigate("add_category/$conferenceId") },
                onNavigateToAddSubCategory = { catId -> 
                    val route = if (catId != null) "add_sub_category/$conferenceId?catId=$catId" else "add_sub_category/$conferenceId"
                    navController.navigate(route)
                },
                onNavigateToAddSpending = { subCatId -> navController.navigate("add_spending/$conferenceId/$subCatId") },
                onNavigateToEditItem = { itemId -> navController.navigate("edit_item/$conferenceId/$itemId") },
                onNavigateToPersonDetail = { personId -> navController.navigate("person_detail/$conferenceId/$personId") },
                onNavigateToBulkOnboard = { navController.navigate("bulk_onboard") },
                onSignOut = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("bulk_onboard") {
            com.tanvir.conferencebudget.ui.auth.BulkOnboardScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            "add_category/{conferenceId}",
            arguments = listOf(navArgument("conferenceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val conferenceId = backStackEntry.arguments?.getString("conferenceId") ?: return@composable
            val budgetViewModel: BudgetViewModel = viewModel(factory = BudgetViewModelFactory(conferenceId))

            AddCategoryScreen(
                conferenceId = conferenceId,
                budgetViewModel = budgetViewModel,
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            "add_sub_category/{conferenceId}?catId={catId}",
            arguments = listOf(
                navArgument("conferenceId") { type = NavType.StringType },
                navArgument("catId") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val conferenceId = backStackEntry.arguments?.getString("conferenceId") ?: return@composable
            val catId = backStackEntry.arguments?.getString("catId")
            val budgetViewModel: BudgetViewModel = viewModel(factory = BudgetViewModelFactory(conferenceId))

            AddSubCategoryScreen(
                conferenceId = conferenceId,
                categoryId = catId,
                budgetViewModel = budgetViewModel,
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            "add_spending/{conferenceId}/{subCategoryId}",
            arguments = listOf(
                navArgument("conferenceId") { type = NavType.StringType },
                navArgument("subCategoryId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val conferenceId = backStackEntry.arguments?.getString("conferenceId") ?: return@composable
            val subCategoryId = backStackEntry.arguments?.getString("subCategoryId") ?: return@composable
            val budgetViewModel: BudgetViewModel = viewModel(factory = BudgetViewModelFactory(conferenceId))

            AddSpendingEntryScreen(
                conferenceId = conferenceId,
                subCategoryId = subCategoryId,
                budgetViewModel = budgetViewModel,
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            "edit_item/{conferenceId}/{itemId}",
            arguments = listOf(
                navArgument("conferenceId") { type = NavType.StringType },
                navArgument("itemId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val conferenceId = backStackEntry.arguments?.getString("conferenceId") ?: return@composable
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            val budgetViewModel: BudgetViewModel = viewModel(factory = BudgetViewModelFactory(conferenceId))
            
            AddEditItemScreen(
                conferenceId = conferenceId,
                itemId = itemId,
                budgetViewModel = budgetViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            "person_detail/{conferenceId}/{personId}",
            arguments = listOf(
                navArgument("conferenceId") { type = NavType.StringType },
                navArgument("personId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val conferenceId = backStackEntry.arguments?.getString("conferenceId") ?: return@composable
            val personId = backStackEntry.arguments?.getString("personId") ?: return@composable
            val personViewModel: PersonViewModel = viewModel(factory = PersonViewModelFactory(conferenceId, personId))
            
            PersonDetailScreen(
                personViewModel = personViewModel,
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onRecordCash = { navController.navigate("give_cash/$conferenceId/$personId") },
                onRecordExpense = { navController.navigate("record_expense/$conferenceId/$personId") },
                onNavigateToBulkOnboard = { navController.navigate("bulk_onboard") },
                onSignOut = {
                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            "give_cash/{conferenceId}/{personId}",
            arguments = listOf(
                navArgument("conferenceId") { type = NavType.StringType },
                navArgument("personId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val conferenceId = backStackEntry.arguments?.getString("conferenceId") ?: return@composable
            val personId = backStackEntry.arguments?.getString("personId") ?: return@composable
            val personViewModel: PersonViewModel = viewModel(factory = PersonViewModelFactory(conferenceId, personId))
            
            RecordTransactionScreen(
                type = "cash",
                personViewModel = personViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            "record_expense/{conferenceId}/{personId}",
            arguments = listOf(
                navArgument("conferenceId") { type = NavType.StringType },
                navArgument("personId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val conferenceId = backStackEntry.arguments?.getString("conferenceId") ?: return@composable
            val personId = backStackEntry.arguments?.getString("personId") ?: return@composable
            val personViewModel: PersonViewModel = viewModel(factory = PersonViewModelFactory(conferenceId, personId))
            
            RecordTransactionScreen(
                type = "expense",
                personViewModel = personViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
