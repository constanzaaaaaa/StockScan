package com.example.stockscan.ui.screens.products

// Panel gerencial: permite habilitar tiendas y abrir el formulario de productos.

import com.example.stockscan.ui.components.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stockscan.model.Perfil
import com.example.stockscan.model.Tienda
import com.example.stockscan.navigation.Destinos
import com.example.stockscan.viewmodel.StockScanViewModel

/** Solo GERENCIAL puede ejecutar estas acciones; los cambios se delegan al ViewModel. */
@Composable
fun GestionView(navController: NavController, viewModel: StockScanViewModel) {
    val e by viewModel.uiState.collectAsStateWithLifecycle()
    var busqueda by remember { mutableStateOf("") }
    var tiendaPorCambiar by remember { mutableStateOf<Tienda?>(null) }
    Pantalla(navController, viewModel, "Administrar tiendas") {
        if (e.perfil != Perfil.GERENCIAL) { Text("Acceso exclusivo de gerencia"); return@Pantalla }
        Tarjeta("${e.tiendas.size} tiendas de prueba", "Activas: ${e.tiendas.count { it.habilitada }} · Bloqueadas: ${e.tiendas.count { !it.habilitada }}")
        e.tiendas.forEach { t ->
            Tarjeta(t.nombre, "${t.id} · Bodega ${t.bodega} · ${if (t.habilitada) "Habilitada" else "Deshabilitada"}")
            OutlinedButton(onClick = { tiendaPorCambiar = t }, modifier = Modifier.fillMaxWidth()) {
                Text(if (t.habilitada) "Deshabilitar ${t.id}" else "Habilitar ${t.id}")
            }
        }
        Text("Las tiendas deshabilitadas no pueden recibir movimientos ni conteos. Conservan el historial.")
        Seccion("Administrar productos")
        OutlinedTextField(value = busqueda, onValueChange = { busqueda = it },
            label = { Text("Buscar por nombre o código") }, modifier = Modifier.fillMaxWidth())
        val productosFiltrados = e.productos.filter {
            it.nombre.contains(busqueda, ignoreCase = true) ||
                it.codigoInterno.contains(busqueda, ignoreCase = true) ||
                it.codigoBarras.contains(busqueda)
        }
        Text("${productosFiltrados.size} productos encontrados")
        productosFiltrados.forEach { p ->
            Tarjeta(p.nombre, "${p.codigoInterno} · ${p.codigoBarras} · mínimo ${p.stockMinimo} u.") {
                viewModel.seleccionarProducto(p.codigoInterno)
                navController.irA(Destinos.GestionProducto.ruta)
            }
        }
        Accion("Crear producto", {
            viewModel.seleccionarProducto("")
            navController.irA(Destinos.GestionProducto.ruta)
        })
    }
    /** Muestra confirmación antes de cambiar el estado de una tienda. */
    val t = tiendaPorCambiar
    if (t != null) AlertDialog(
        onDismissRequest = { tiendaPorCambiar = null },
        title = { Text("Confirmar cambio de tienda") },
        text = { Text("¿${if (t.habilitada) "Deshabilitar" else "Habilitar"} ${t.nombre}? Los movimientos solo están disponibles en tiendas habilitadas.") },
        confirmButton = { TextButton(onClick = {
            viewModel.cambiarEstadoTienda(t.id)
            tiendaPorCambiar = null
        }) { Text("Confirmar") } },
        dismissButton = { TextButton(onClick = { tiendaPorCambiar = null }) { Text("Cancelar") } }
    )
}
