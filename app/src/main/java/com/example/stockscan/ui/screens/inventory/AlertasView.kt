package com.example.stockscan.ui.screens.inventory

// Pantalla del perfil BODEGA: filtra alertas y declara reposiciones mediante StockScanViewModel.

import com.example.stockscan.ui.components.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stockscan.model.Perfil
import com.example.stockscan.model.TipoAlerta
import com.example.stockscan.viewmodel.StockScanViewModel

/** Lee el estado del inventario y envía resolución o reposición al ViewModel. */
@Composable
fun AlertasView(navController: NavController, viewModel: StockScanViewModel) {
    val e by viewModel.uiState.collectAsStateWithLifecycle()
    var productoId by remember { mutableStateOf("P-001") }
    var motivo by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf("Activas") }
    var tipo by remember { mutableStateOf("Todos") }
    Pantalla(navController, viewModel, "Alertas de bodega") {
        if (e.perfil != Perfil.BODEGA) { Text("Solo el encargado de bodega puede gestionar alertas."); return@Pantalla }
        /** Combina alertas guardadas y automáticas, limitadas a la tienda seleccionada. */
        val lista = (e.alertas + e.alertasMinimo()).filter { it.tiendaId == e.tiendaId }
        Tarjeta("${lista.count { !it.resuelta }} alertas activas", "${lista.size} avisos en ${e.tiendaActual()?.nombre}")
        RowFiltros(listOf("Activas", "Resueltas", "Todas"), filtro) { filtro = it }
        RowFiltros(listOf("Todos", "Mínimo", "Descalce", "Reposición"), tipo) { tipo = it }
        val filtradas = lista.filter { alerta ->
            val estadoValido = when (filtro) {
                "Activas" -> !alerta.resuelta
                "Resueltas" -> alerta.resuelta
                else -> true
            }
            val tipoValido = when (tipo) {
                "Mínimo" -> alerta.tipo == TipoAlerta.MINIMO
                "Descalce" -> alerta.tipo == TipoAlerta.DESCALCE
                "Reposición" -> alerta.tipo == TipoAlerta.REPOSICION
                else -> true
            }
            estadoValido && tipoValido
        }
        if (filtradas.isEmpty()) Text("No hay alertas para estos filtros.")
        filtradas.forEach { a ->
            val producto = e.productos.find { it.codigoInterno == a.productoId }
            Tarjeta("${a.tipo.titulo} · ${producto?.nombre ?: a.productoId}",
                "${a.descripcion}\n${if(a.resuelta) "Resuelta" else "Activa"} · ${a.usuario} · ${a.fechaUtc}")
            if (!a.resuelta && a.tipo != TipoAlerta.MINIMO) {
                TextButton(onClick = { viewModel.resolverAlerta(a.id) }) { Text("Resolver alerta") }
            }
        }
        Seccion("Declarar reposición")
        e.productos.forEach { p ->
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                RadioButton(selected = productoId == p.codigoInterno, onClick = { productoId = p.codigoInterno })
                Text(p.nombre)
            }
        }
        OutlinedTextField(value = motivo, onValueChange = { motivo = it },
            label = { Text("Motivo obligatorio") }, isError = motivo.isBlank(),
            supportingText = { if (motivo.isBlank()) Text("Explica qué se necesita reponer") },
            modifier = Modifier.fillMaxWidth())
        Accion("Declarar reposición", { if (viewModel.declararReposicion(productoId, motivo)) motivo = "" },
            habilitado = motivo.isNotBlank())
        Tarjeta("Aviso", "Declarar una reposición no aumenta las existencias.")
    }
}
