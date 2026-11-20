package com.example.stockscan.ui.view

// Formulario de entradas, salidas y ajustes; usa InventarioReglas para validar y ViewModel para registrar.

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stockscan.model.InventarioReglas
import com.example.stockscan.model.Perfil
import com.example.stockscan.model.TipoMovimiento
import com.example.stockscan.modelo.Destinos
import com.example.stockscan.viewmodel.StockScanViewModel

/** Validaciones y diálogo de confirmación con la metodología de formularios del profesor. */
@Composable
fun MovimientoView(navController: NavController, viewModel: StockScanViewModel) {
    val e by viewModel.uiState.collectAsStateWithLifecycle()
    var confirmar by remember { mutableStateOf(false) }
    val p = e.productoActual()
    /** Calcula el mensaje de validación antes de habilitar la confirmación. */
    val error = if (p == null || e.movimientoCantidad.isBlank()) null else {
        InventarioReglas.validarMovimiento(e.stockDe(p), e.movimientoTipo,
            e.movimientoCantidad.toIntOrNull(), e.movimientoMotivo)
    }
    Pantalla(navController, viewModel, "Registrar movimiento") {
        if (p == null || e.perfil != Perfil.OPERADOR) { Text("No tienes permiso o falta producto"); return@Pantalla }
        Seccion(p.nombre)
        Tarjeta("Stock actual: ${e.stockDe(p)} u.", "Producto ${p.codigoInterno} · ${e.tiendaActual()?.nombre}")
        Seccion("Tipo de movimiento")
        RowFiltros(TipoMovimiento.entries.filter { it != TipoMovimiento.CONTEO }.map { it.titulo },
            e.movimientoTipo.titulo) { opcion ->
            TipoMovimiento.entries.firstOrNull { it.titulo == opcion }?.let(viewModel::onTipoMovimiento)
        }
        OutlinedTextField(value = e.movimientoCantidad, onValueChange = viewModel::onCantidadMovimiento,
            label = { Text(if (e.movimientoTipo == TipoMovimiento.AJUSTE) "Cambio de unidades (+/-)" else "Cantidad de unidades") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = error != null,
            supportingText = { if (error != null) Text(error) else Text("Solo cantidades enteras; salida no puede superar el stock") },
            modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = e.movimientoMotivo, onValueChange = viewModel::onMotivoMovimiento,
            label = { Text(if (e.movimientoTipo == TipoMovimiento.AJUSTE) "Motivo obligatorio" else "Referencia opcional") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (e.movimientoTipo == TipoMovimiento.AJUSTE && e.movimientoMotivo.isBlank()) Text("Debes justificar el ajuste")
            })
        Text("Actual: ${e.stockDe(p)} u. → Resultante: ${viewModel.resultadoMovimiento()?.let { "$it u." } ?: "Cantidad inválida"}")
        Accion("Revisar y confirmar movimiento", { confirmar = true },
            e.movimientoCantidad.isNotBlank() && error == null && viewModel.resultadoMovimiento() != null)
    }
    /** Resume la operación y solicita confirmación antes de modificar existencias. */
    if (confirmar && p != null) AlertDialog(
        onDismissRequest = { confirmar = false },
        title = { Text("Confirmar movimiento") },
        text = { Text("${e.movimientoTipo.titulo}: ${e.movimientoCantidad} u.\nStock final: ${viewModel.resultadoMovimiento() ?: "inválido"} u.\nEsta es una demo local.") },
        confirmButton = { TextButton(onClick = {
            confirmar = false
            if (viewModel.registrarMovimiento()) navController.irA(Destinos.Detalle.ruta)
        }) { Text("Confirmar") } },
        dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar") } }
    )
}
