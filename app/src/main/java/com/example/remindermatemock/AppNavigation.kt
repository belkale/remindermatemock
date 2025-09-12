package com.example.remindermatemock

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.remindermatemock.screen.HomeScreen // Assuming HomeScreen exists
import com.example.remindermatemock.screen.OverdueScreen
// Import other screens as you create them

object AppDestinations {
    const val HOME_ROUTE = "home"
    const val OVERDUE_ROUTE = "overdue"
}

/**
 * Main Navigation Host for the application.
 */
@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestinations.HOME_ROUTE
    ) {
        composable(AppDestinations.HOME_ROUTE) {
            // Pass the navController to HomeScreen so it can navigate
            HomeScreen(navController = navController)
        }

        composable(AppDestinations.OVERDUE_ROUTE) {
            // Pass the navController to OverdueScreen for its back navigation
            OverdueScreen(navController = navController)
        }

        // Add other composable routes here:
        // composable(AppDestinations.SETTINGS_ROUTE) {
        //     SettingsScreen(navController = navController)
        // }
        //
        // Example with argument:
        // composable(
        //     route = AppDestinations.REMINDER_DETAIL_ROUTE,
        //     arguments = listOf(navArgument("reminderId") { type = NavType.IntType })
        // ) { backStackEntry ->
        //     val reminderId = backStackEntry.arguments?.getInt("reminderId")
        //     ReminderDetailScreen(navController = navController, reminderId = reminderId)
        // }
    }
}
