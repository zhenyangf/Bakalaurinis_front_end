package com.feng.insurance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Chip(title: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.height(32.dp).clip(CircleShape).background(Color.LightGray).padding(12.dp, 0.dp)) {
        Text(text = title, color = Color.Gray, fontSize = 12.sp)
    }
}