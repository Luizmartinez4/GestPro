package com.example.gestpro.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestpro.ui.theme.GestError
import com.example.gestpro.ui.theme.GestSuccess
import com.example.gestpro.util.PasswordRule

@Composable
fun PasswordRequirements(rules: List<PasswordRule>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(start = 16.dp, top = 2.dp, bottom = 4.dp)) {
        rules.forEach { rule ->
            val color = if (rule.isMet) GestSuccess else GestError
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (rule.isMet) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(rule.label, color = color, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}