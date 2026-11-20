package com.example.stockscan.ui.theme

// Tema Material 3 compartido por MainActivity y todas las pantallas Compose.

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// La paleta mantiene los verdes y los contrastes del mockup académico.
val VerdeStock = Color(0xFF006F60)
val MentaStock = Color(0xFF008B7C)
val AzulTexto = Color(0xFF0D3B66)
val FondoStock = Color(0xFFF4F8F7)
val AlertaSuave = Color(0xFFFFF1D7)
val ErrorSuave = Color(0xFFFFE7E3)

/** Relaciona la paleta StockScan con los colores semánticos de Material 3. */
private val ColoresStockScan = lightColorScheme(
    primary = VerdeStock,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD9F1EA),
    onPrimaryContainer = AzulTexto,
    secondary = MentaStock,
    onSecondary = Color.White,
    background = FondoStock,
    onBackground = Color(0xFF1A252C),
    surface = Color.White,
    onSurface = Color(0xFF1A252C),
    error = Color(0xFFB3261E)
)

/** Aplica la paleta común a todo el contenido de la aplicación. */
@Composable
fun StockScanTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = ColoresStockScan, content = content)
}
