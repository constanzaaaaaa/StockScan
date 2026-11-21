package com.example.stockscan.ui.screens.inventory

// Revisión previa al cierre del conteo; compara cantidades iniciales y contadas antes de confirmar.

import com.example.stockscan.ui.components.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stockscan.navigation.Destinos
import com.example.stockscan.viewmodel.StockScanViewModel

/** Muestra diferencias calculadas por StockScanModel y delega la confirmación al ViewModel. */
@Composable
fun ResumenConteoView(navController: NavController, viewModel: StockScanViewModel) {
    val e by viewModel.uiState.collectAsStateWithLifecycle()
    val c = e.conteo
    Pantalla(navController, viewModel, "Resumen del conteo") {
        if (c == null || !c.enRevision) { Text("No hay conteo pendiente de confirmar"); return@Pantalla }
        /** Obtiene únicamente los productos cuya cantidad contada difiere del stock inicial. */
        val diferencias = e.diferenciasConteo()
        Tarjeta("Conteo C-${c.id}", "${diferencias.size} de ${e.productos.size} productos con diferencias")
        Text("Tiempo transcurrido: ${(System.currentTimeMillis() - c.inicioMillis) / 1000} segundos")
        e.productos.forEach { p ->
            val anterior = c.stockInicial[p.codigoInterno] ?: 0
            val contado = c.contados[p.codigoInterno] ?: 0
            val diferencia = contado - anterior
            Tarjeta(p.nombre, "Registrado: $anterior u.\nContado: $contado u.\n" +
                if (diferencia == 0) "Sin diferencia" else "Diferencia: ${if (diferencia > 0) "+" else ""}$diferencia u.")
        }
        Tarjeta("Confirmación explícita", "Solo al confirmar se fijan las cantidades contadas; los productos sin diferencias no generan movimientos.")
        Accion("Confirmar ${diferencias.size} diferencias", {
            if (viewModel.confirmarConteo()) navController.irA(Destinos.Inicio.ruta)
        })
        Accion("Volver al conteo", { navController.irA(Destinos.Conteo.ruta) })
    }
}
