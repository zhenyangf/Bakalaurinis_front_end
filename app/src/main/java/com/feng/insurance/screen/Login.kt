package com.feng.insurance.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.feng.insurance.Routes
import com.feng.insurance.model.LoginViewModel

@Composable
fun LoginPage(navController: NavHostController, viewModel: LoginViewModel) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val email = remember { mutableStateOf(TextFieldValue()) }
        val password = remember { mutableStateOf(TextFieldValue()) }

        Text(text = "Login", style = TextStyle(fontSize = 24.sp))

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Email") },
                value = email.value,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                onValueChange = { email.value = it })

            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Password") },
                value = password.value,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { password.value = it })

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.login(email = email.value.text, password = password.value.text) },
            ) {
                Text(text = "Login")
            }

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { navController.navigate(Routes.SignUp.route) },
            ) {
                Text(text = "Sign Up")
            }
        }
    }
}