package com.feng.insurance.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.feng.insurance.Routes
import com.feng.insurance.model.LoginViewModel
import com.feng.insurance.service.UserRegisterDto

@Composable
fun SignUp(navController: NavHostController, viewModel: LoginViewModel) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val firstName = remember { mutableStateOf(TextFieldValue()) }
        val lastName = remember { mutableStateOf(TextFieldValue()) }
        val email = remember { mutableStateOf(TextFieldValue()) }
        val phone = remember { mutableStateOf(TextFieldValue()) }
        val password = remember { mutableStateOf(TextFieldValue()) }


        Text(text = "Sign up", style = TextStyle(fontSize = 24.sp))

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)

        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "First Name") },
                value = firstName.value,
                onValueChange = { firstName.value = it })

            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Last Name") },
                value = lastName.value,
                onValueChange = { lastName.value = it })

            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Email") },
                value = email.value,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                onValueChange = { email.value = it })

            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Phone") },
                value = phone.value,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                onValueChange = { phone.value = it })

            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Password") },
                value = password.value,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { password.value = it })

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.register(
                        UserRegisterDto(
                            email = email.value.text,
                            name = firstName.value.text,
                            surname = lastName.value.text,
                            password = password.value.text,
                            phone = phone.value.text
                        )
                    )},
            ) {
                Text(text = "Sign Up")
            }

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { navController.navigate(Routes.Login.route) },
            ) {
                Text(text = "Login")
            }
        }
    }
}