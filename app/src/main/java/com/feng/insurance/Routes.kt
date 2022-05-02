package com.feng.insurance

sealed class Routes(val route: String) {
    object Login : Routes("Login")
    object SignUp : Routes("SignUp")
    object Insurances : Routes("Insurances")
    object InsuranceDetails : Routes("insurance/{insuranceID}")
}
