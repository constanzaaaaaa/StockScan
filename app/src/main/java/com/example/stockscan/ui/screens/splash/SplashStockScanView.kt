package com.example.stockscan.ui.screens.splash

// Splash animado que MainActivity muestra durante la apertura antes de iniciar la navegación.

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockscan.R
import com.example.stockscan.ui.theme.VerdeStock

/** Muestra la marca StockScan y una animación breve de escala del logotipo. */
@Composable
fun SplashStockScanView() {
    /** Crea la transición infinita que anima el tamaño del logotipo. */
    val transicion = rememberInfiniteTransition(label = "animacion-logo-stockscan")
    val escalaLogo by transicion.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 950),
            repeatMode = RepeatMode.Reverse
        ),
        label = "escala-logo"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdeStock),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_stockscan_mark),
                contentDescription = null,
                modifier = Modifier
                    .size(116.dp)
                    .scale(escalaLogo)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "StockScan",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "CONTROL INTELIGENTE DE INVENTARIO",
                color = Color.White.copy(alpha = 0.86f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.8.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(44.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        }
    }
}
