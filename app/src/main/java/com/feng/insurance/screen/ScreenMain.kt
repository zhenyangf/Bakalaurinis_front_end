package com.feng.insurance.screen

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.feng.insurance.Routes
import com.feng.insurance.model.LoginViewModel
@Composable
fun ScreenMain(viewModel: LoginViewModel){
    val navController = rememberNavController()

    LaunchedEffect(viewModel.user.value) {
        if (viewModel.user.value !== null) {
            viewModel.getInsuranceList()
            navController.navigate(Routes.Insurances.route)
        } else {
            navController.navigate(Routes.Login.route)
        }
    }

    NavHost(navController = navController, startDestination = Routes.Login.route) {
        composable(Routes.Login.route) {
            LoginPage(navController = navController, viewModel)
        }

        composable(Routes.SignUp.route) {
            SignUp(navController = navController, viewModel)
        }

        composable(Routes.Insurances.route) {
            Insurances(navController = navController, viewModel)
        }

        composable(
            "insurance/{insuranceID}",
            arguments = listOf(navArgument("insuranceID") { type = NavType.IntType })
        ) { backStackEntry ->
            InsuranceDetails(navController = navController, viewModel, backStackEntry.arguments?.getInt("insuranceID"))
        }
    }
}