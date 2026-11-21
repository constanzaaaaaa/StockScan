package com.example.stockscan

// Entrada de la app: conecta Room, Repository, ViewModel y navegación Compose.

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.example.stockscan.data.AppDatabase
import com.example.stockscan.repository.RoomStockScanRepository
import com.example.stockscan.navigation.StockScanNavHost
import com.example.stockscan.ui.screens.splash.SplashStockScanView
import com.example.stockscan.ui.theme.StockScanTheme
import com.example.stockscan.viewmodel.StockScanViewModel
import com.example.stockscan.viewmodel.StockScanViewModelFactory
import kotlinx.coroutines.delay

/** Inicializa la base local y comparte el ViewModel con todas las pantallas. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Debe instalarse antes de super.onCreate para mostrar el splash nativo.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        /** Construye la cadena de dependencias: Room → Repository → Factory → ViewModel. */
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = RoomStockScanRepository(database)
        val factory = StockScanViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[StockScanViewModel::class.java]

        setContent {
            StockScanTheme {
                var mostrarSplash by rememberSaveable { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(1400L)
                    mostrarSplash = false
                }

                if (mostrarSplash) {
                    SplashStockScanView()
                } else {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        StockScanNavHost(viewModel)
                    }
                }
            }
        }
    }
}
