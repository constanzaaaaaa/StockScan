package com.example.stockscan.ui.view

// Resumen global para GERENCIAL; agrega existencias y alertas de todas las tiendas habilitadas.

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stockscan.model.Perfil
import com.example.stockscan.modelo.Destinos
import com.example.stockscan.viewmodel.StockScanViewModel

/** Presenta indicadores globales a partir del estado compartido del ViewModel. */
@Composable
fun ResumenView(navController: NavController, viewModel: StockScanViewModel) {
    val e by viewModel.uiState.collectAsStateWithLifecycle()
    Pantalla(navController, viewModel, "Resumen global") {
        if (e.perfil != Perfil.GERENCIAL) { Text("Acceso exclusivo de gerencia"); return@Pantalla }
        /** Excluye tiendas bloqueadas para que no participen en los totales. */
        val habilitadas = e.tiendas.filter { it.habilitada }
        val total = habilitadas.sumOf { e.totalUnidades(it.id) }
        Seccion("Indicadores · tiendas habilitadas")
        Tarjeta("$total unidades consolidadas", "${e.productos.size} productos únicos · ${habilitadas.size} tiendas habilitadas")
        Tarjeta("${e.alertasActivas().size} alertas activas", "${e.alertasMinimo().size} alertas automáticas por stock mínimo")
        Seccion("Stock por tienda")
        habilitadas.forEach { tienda ->
            val quiebres = e.productos.count { e.stockDe(it, tienda.id) <= it.stockMinimo }
            Tarjeta(tienda.nombre, "${e.totalUnidades(tienda.id)} unidades · ${tienda.id} · $quiebres productos en mínimo o bajo")
        }
        if (e.tiendas.any { !it.habilitada }) {
            Text("Tiendas excluidas por estar deshabilitadas: ${e.tiendas.filterNot { it.habilitada }.joinToString { it.nombre }}")
        }
        Text("Datos de prueba en memoria. No corresponden a stock en tiempo real.")
        Accion("Administrar tiendas y productos", { navController.irA(Destinos.Gestion.ruta) })
        Accion("Historial de movimientos", { navController.irA(Destinos.Historial.ruta) })
        Accion("Ver reportes", { navController.irA(Destinos.Reportes.ruta) })
    }
}
