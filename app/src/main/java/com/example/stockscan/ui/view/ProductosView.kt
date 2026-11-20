package com.example.stockscan.ui.view

// Listado y filtros del catálogo; al seleccionar un producto navega a DetalleView.

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stockscan.modelo.Destinos
import com.example.stockscan.viewmodel.StockScanViewModel

/** Búsqueda y filtros inspirados en PersonaScreenFiltro del profesor. */
@Composable
fun ProductosView(navController: NavController, viewModel: StockScanViewModel) {
    val e by viewModel.uiState.collectAsStateWithLifecycle()
    var categoria by remember { mutableStateOf("Todas") }
    var stockFiltro by remember { mutableStateOf("Todos") }
    Pantalla(navController, viewModel, "Productos y stock") {
        Seccion("Buscar en ${e.tiendaActual()?.nombre ?: "la tienda"}")
        OutlinedTextField(value = e.busqueda, onValueChange = viewModel::onBusquedaChange,
            label = { Text("Nombre, código de barras o interno") },
            modifier = Modifier.fillMaxWidth(), singleLine = true)
        Seccion("Categoría")
        RowFiltros(listOf("Todas") + e.productos.map { it.categoria }.distinct(), categoria) { categoria = it }
        Seccion("Nivel de stock")
        RowFiltros(listOf("Todos", "Sobre mínimo", "Bajo mínimo"), stockFiltro) { stockFiltro = it }
        /** Combina la búsqueda del ViewModel con los filtros locales de categoría y nivel de stock. */
        val lista = e.productosFiltrados().filter { p ->
            val cantidad = e.stockDe(p)
            (categoria == "Todas" || p.categoria == categoria) &&
                when (stockFiltro) {
                    "Sobre mínimo" -> cantidad > p.stockMinimo
                    "Bajo mínimo" -> cantidad <= p.stockMinimo
                    else -> true
                }
        }
        Text("${lista.size} referencias encontradas")
        lista.forEach { p ->
            val cantidad = e.stockDe(p)
            val estado = when {
                cantidad == 0 -> "En quiebre"
                cantidad <= p.stockMinimo -> "Stock bajo"
                else -> "Stock normal"
            }
            Tarjeta(p.nombre, "${p.codigoInterno} · ${p.categoria}\n${p.codigoBarras}\n" +
                "$cantidad u. · mínimo ${p.stockMinimo} · $estado") {
                viewModel.seleccionarProducto(p.codigoInterno)
                navController.irA(Destinos.Detalle.ruta)
            }
        }
        if (lista.isEmpty()) Text("No se encontraron productos con esos filtros.")
    }
}
