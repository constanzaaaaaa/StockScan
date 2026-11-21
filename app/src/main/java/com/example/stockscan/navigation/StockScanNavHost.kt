package com.example.stockscan.navigation

// Grafo de navegación: conecta cada ruta de Destinos con la pantalla que corresponde.

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stockscan.ui.screens.auth.LoginView
import com.example.stockscan.ui.screens.home.InicioView
import com.example.stockscan.ui.screens.home.ReportesView
import com.example.stockscan.ui.screens.home.ResumenView
import com.example.stockscan.ui.screens.inventory.AlertasView
import com.example.stockscan.ui.screens.inventory.ConteoView
import com.example.stockscan.ui.screens.inventory.EscanearView
import com.example.stockscan.ui.screens.inventory.HistorialView
import com.example.stockscan.ui.screens.inventory.MovimientoView
import com.example.stockscan.ui.screens.inventory.ResumenConteoView
import com.example.stockscan.ui.screens.inventory.TiendasView
import com.example.stockscan.ui.screens.products.DetalleView
import com.example.stockscan.ui.screens.products.GestionProductoView
import com.example.stockscan.ui.screens.products.GestionView
import com.example.stockscan.ui.screens.products.ProductosView
import com.example.stockscan.viewmodel.StockScanViewModel

/** Registra las rutas y comparte el mismo ViewModel con todas las pantallas. */
@Composable
fun StockScanNavHost(viewModel: StockScanViewModel) {
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
