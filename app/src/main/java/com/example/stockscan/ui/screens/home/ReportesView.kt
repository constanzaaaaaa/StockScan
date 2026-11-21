package com.example.stockscan.ui.screens.home

// Indicadores para GERENCIAL; agrega existencias, tiendas, productos, movimientos y alertas del ViewModel.

import com.example.stockscan.ui.components.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stockscan.model.Perfil
import com.example.stockscan.navigation.Destinos
import com.example.stockscan.viewmodel.StockScanViewModel

/** Muestra un resumen agregado; no exporta archivos ni consulta un servidor. */
@Composable
fun ReportesView(navController: NavController, viewModel: StockScanViewModel) {
    val e by viewModel.uiState.collectAsStateWithLifecycle()

    Pantalla(navController, viewModel, "Reportes") {
        if (e.perfil != Perfil.GERENCIAL) {
            Text("Acceso exclusivo de gerencia")
            return@Pantalla
        }

        /** Calcula indicadores solo para las tiendas habilitadas. */
        val tiendasActivas = e.tiendas.filter { it.habilitada }
        val totalUnidades = tiendasActivas.sumOf { e.totalUnidades(it.id) }
        val alertasActivas = e.alertasActivas().size

        Seccion("Resumen general")
        Tarjeta(
            "$totalUnidades unidades disponibles",
            "${tiendasActivas.size} tiendas habilitadas · ${e.productos.size} productos registrados"
        )
        Tarjeta(
            "${e.movimientos.size} movimientos registrados",
            "Entradas, salidas, ajustes y diferencias de conteo de esta sesión"
        )
        Tarjeta(
            "$alertasActivas alertas activas",
            "${e.alertasMinimo().size} alertas automáticas por stock mínimo"
        )

        Seccion("Stock por tienda")
        tiendasActivas.forEach { tienda ->
            val bajoMinimo = e.productos.count { producto ->
                e.stockDe(producto, tienda.id) <= producto.stockMinimo
            }
            Tarjeta(
                tienda.nombre,
                "${e.totalUnidades(tienda.id)} unidades · $bajoMinimo productos en mínimo o bajo"
            )
        }

        Accion("Consultar historial de movimientos", {
            navController.irA(Destinos.Historial.ruta)
        })
        Text("Este reporte permite consultar los indicadores generales de inventario.")
        Text("Los datos son ficticios y permanecen en memoria durante esta sesión.")
    }
}
