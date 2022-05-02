package com.feng.insurance.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.feng.insurance.model.LoginViewModel
import com.feng.insurance.service.Insurance
import com.feng.insurance.service.InsuranceDto
import com.feng.insurance.service.InsuranceStatus

@Composable
fun Insurances(navController: NavHostController, viewModel: LoginViewModel) {
    val isCreateInsuranceOpen = remember { mutableStateOf(false) }

    if (isCreateInsuranceOpen.value) {
        CreateInsuranceDialog(viewModel, isCreateInsuranceOpen)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insure") },
                actions = {
                    TextButton(onClick = {
                        isCreateInsuranceOpen.value = true
                    }) {
                        Text(text = "Insure new object", color = Color.White)
                    }
                    TextButton(onClick = { viewModel.logout() }) {
                        Text(text = "Logout", color = Color.White)
                    }
                }
            )
        }
    ) {
        LazyColumn {
            items(viewModel.insuranceList) { insurance ->
                InsuranceListItem(insurance = insurance, onClick = {
                    navController.navigate("insurance/${insurance.id}")
                })
            }
        }
    }
}
//@Composable
//fun EventListItem(Event: event)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InsuranceListItem(insurance: Insurance, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    ListItem(
        modifier =  Modifier.clickable(onClick = onClick, interactionSource = interactionSource, indication = rememberRipple(true)),
        overlineText = {
            Text(text = insurance.status.toString(), fontSize = 10.sp)
        },
        text = {
            Text(text = insurance.title)
        },
        secondaryText = {
            Text(text = insurance.address)
        },
        trailing = {
            Text(text = insurance.category)
        }
    )
}

@Composable
fun CreateInsuranceDialog(viewModel: LoginViewModel, isOpen: MutableState<Boolean>) {
    val title = remember { mutableStateOf(TextFieldValue()) }
    val address = remember { mutableStateOf(TextFieldValue()) }

    val isAnApartment = remember { mutableStateOf(false) }

    val isCreateButtonDisabled = remember {
        derivedStateOf {
            title.value.text.isEmpty() || address.value.text.isEmpty()
        }
    }

    fun createNewInsurance() {
        isOpen.value = false
        viewModel.createNewInsurance(InsuranceDto(
            title = title.value.text,
            address = address.value.text,
            category = if (isAnApartment.value) "APARTMENT" else "HOUSE",
            userId = viewModel.user.value?.id ?: 0,
            status = InsuranceStatus.PENDING,
        ))
    }

    AlertDialog(
        onDismissRequest = { isOpen.value = false },
        title = {
            Text(text = "Create new insurance")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextField(
                    label = { Text(text = "Title") },
                    value = title.value,
                    onValueChange = { title.value = it }
                )
                TextField(
                    label = { Text(text = "Address") },
                    value = address.value,
                    onValueChange = { address.value = it }
                )
                Row {
                    Text(text = "Is an apartment?")
                    Checkbox(checked = isAnApartment.value, onCheckedChange = { isAnApartment.value = it })
                }
            }
        },
        buttons = {
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = { isOpen.value = false },
                ) {
                    Text("Cancel")
                }
                TextButton(
                    onClick = { createNewInsurance() },
                    enabled = !isCreateButtonDisabled.value
                ) {
                    Text("Create")
                }
            }
        }
    )
}