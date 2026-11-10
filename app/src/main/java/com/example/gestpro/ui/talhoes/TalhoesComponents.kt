package com.example.gestpro.ui.talhoes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestpro.R
import com.example.gestpro.ui.theme.GestBlue
import com.example.gestpro.ui.theme.GestOverlay

private val DimOverlay = Color(0x99000000)

/**
 * Fundo das telas de talhão: a mesma imagem e o mesmo filtro verde da Home,
 * para as rotas parecerem sobreposições, como no protótipo.
 * [dim] escurece mais (usado atrás dos formulários cinza).
 */
@Composable
internal fun TalhoesBackground(dim: Boolean = false, content: @Composable BoxScope.() -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_home),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(Modifier.fillMaxSize().background(GestOverlay))
        if (dim) Box(Modifier.fillMaxSize().background(DimOverlay))
        content()
    }
}

/** Botão azul com "+" ("Novo talhão", "Novo Lançamento"). */
@Composable
internal fun BlueButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GestBlue),
        contentPadding = PaddingValues(horizontal = 14.dp)
    ) {
        Text(text, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}
