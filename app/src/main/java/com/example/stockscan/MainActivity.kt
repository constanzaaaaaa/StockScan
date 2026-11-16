package com.example.stockscan

import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stockscan.data.local.StockScanDatabase
import com.example.stockscan.modelo.Destinos
import com.example.stockscan.ui.theme.StockScanTheme
import com.example.stockscan.ui.view.*
import com.example.stockscan.viewmodel.StockScanViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Debe instalarse antes de super.onCreate para mostrar el splash nativo.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Abre Room fuera del hilo principal y crea el esquema local.
        // No se ejecutan operaciones de inventario porque esta entrega no incluye DAO.
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                StockScanDatabase.obtenerInstancia(applicationContext)
                    .openHelper.writableDatabase
            } catch (error: Exception) {
                Log.e("StockScan", "No se pudo inicializar la base de datos Room", error)
            }
        }

        val viewModel = ViewModelProvider(this)[StockScanViewModel::class.java]
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
                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = Destinos.Login.ruta
                        ) {
                            composable(Destinos.Login.ruta) { LoginView(navController, viewModel) }
                            composable(Destinos.Tiendas.ruta) { TiendasView(navController, viewModel) }
                            composable(Destinos.Inicio.ruta) { InicioView(navController, viewModel) }
                            composable(Destinos.Escanear.ruta) { EscanearView(navController, viewModel) }
                            composable(Destinos.Productos.ruta) { ProductosView(navController, viewModel) }
                            composable(Destinos.Detalle.ruta) { DetalleView(navController, viewModel) }
                            composable(Destinos.Movimiento.ruta) { MovimientoView(navController, viewModel) }
                            composable(Destinos.Conteo.ruta) { ConteoView(navController, viewModel) }
                            composable(Destinos.ResumenConteo.ruta) { ResumenConteoView(navController, viewModel) }
                            composable(Destinos.Alertas.ruta) { AlertasView(navController, viewModel) }
                            composable(Destinos.Historial.ruta) { HistorialView(navController, viewModel) }
                            composable(Destinos.Resumen.ruta) { ResumenView(navController, viewModel) }
                            composable(Destinos.Gestion.ruta) { GestionView(navController, viewModel) }
                            composable(Destinos.GestionProducto.ruta) { GestionProductoView(navController, viewModel) }
                            composable(Destinos.Reportes.ruta) { ReportesView(navController, viewModel) }
                        }
                    }
                }
            }
        }
    }
}
