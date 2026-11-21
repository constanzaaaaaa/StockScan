package com.example.stockscan.ui.screens.inventory

// Historial de auditoría: muestra movimientos guardados y filtra por tienda, tipo, producto o usuario.

import com.example.stockscan.ui.components.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stockscan.model.Perfil
import com.example.stockscan.model.TipoMovimiento
import com.example.stockscan.viewmodel.StockScanViewModel

/** Lee los movimientos del ViewModel y presenta los resultados con componentes compartidos. */
@Composable
fun HistorialView(navController: NavController, viewModel: StockScanViewModel) {
    val e by viewModel.uiState.collectAsStateWithLifecycle()
    var tipo by remember { mutableStateOf("Todos") }
    Pantalla(navController, viewModel, "Historial") {
        Tarjeta("Registro de auditoría", "Los movimientos de la demo no se editan ni eliminan. Una corrección requiere otro ajuste.")
        OutlinedTextField(value = e.filtroHistorial, onValueChange = viewModel::onFiltroHistorial,
            label = { Text("Producto, usuario o tipo") }, modifier = Modifier.fillMaxWidth())
        RowFiltros(listOf("Todos") + TipoMovimiento.entries.map { it.titulo }, tipo) { tipo = it }
        /** Aplica permisos del perfil, tipo seleccionado y texto de búsqueda. */
        val lista = e.movimientos.filter {
            (e.perfil == Perfil.GERENCIAL || it.tiendaId == e.tiendaId) &&
                (tipo == "Todos" || it.tipo.titulo == tipo) &&
                (it.productoId.contains(e.filtroHistorial, ignoreCase = true) ||
                    it.usuario.contains(e.filtroHistorial, ignoreCase = true) ||
                    it.tipo.titulo.contains(e.filtroHistorial, ignoreCase = true))
        }
        Seccion("${lista.size} movimientos encontrados")
        if (lista.isEmpty()) Text("Sin movimientos para estos filtros.")
        lista.forEach { m ->
            val producto = e.productos.find { it.codigoInterno == m.productoId }
            Tarjeta("${producto?.nombre ?: m.productoId} · ${if(m.cantidad > 0) "+" else ""}${m.cantidad} u.",
                "${m.tipo.titulo} · ${m.anterior} → ${m.nuevo} u.\n${m.tiendaId} · ${m.usuario}\n${m.fechaUtc} UTC\n${m.motivo}")
        }
    }
}
