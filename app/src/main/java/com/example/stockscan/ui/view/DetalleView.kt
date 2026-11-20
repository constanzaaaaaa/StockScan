package com.example.stockscan.ui.view

// Detalle del producto seleccionado: consulta StockScanModel y deriva al flujo de movimientos si el perfil es operador.

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stockscan.model.Perfil
import com.example.stockscan.modelo.Destinos
import com.example.stockscan.viewmodel.StockScanViewModel

/** Presenta datos, stock y estado del producto seleccionado en el ViewModel. */
@Composable
fun DetalleView(navController: NavController, viewModel: StockScanViewModel) {
    val e by viewModel.uiState.collectAsStateWithLifecycle()
    val p = e.productoActual()
    Pantalla(navController, viewModel, "Detalle de producto") {
        if (p == null) { Text("Producto no encontrado"); return@Pantalla }
        val cantidad = e.stockDe(p)
        /** Clasifica el stock comparando las unidades disponibles con el mínimo configurado. */
        val estado = when {
            cantidad == 0 -> "En quiebre"
            cantidad <= p.stockMinimo -> "Stock bajo mínimo"
            else -> "Stock normal"
        }
        Seccion(p.nombre)
        Text("${p.categoria} · Código interno ${p.codigoInterno}")
        ElevatedCard {
            ListItem(headlineContent = { Text("$cantidad unidades disponibles") },
                supportingContent = { Text("Mínimo ${p.stockMinimo} u. · $estado") },
                overlineContent = { Text("${e.tiendaActual()?.nombre ?: "Tienda de prueba"} · Inventario simulado") })
        }
        Tarjeta("Código de barras", p.codigoBarras)
        Tarjeta("Descripción", p.descripcion)
        if (cantidad <= p.stockMinimo) {
            Text("Atención: el producto requiere revisión de reposición.",
                color = MaterialTheme.colorScheme.error)
        }
        Text("Información de prueba; no hay conexión con stock real.")
        if (e.perfil == Perfil.OPERADOR) {
            Accion("Registrar movimiento", { navController.irA(Destinos.Movimiento.ruta) })
        }
        Accion("Volver a productos", { navController.irA(Destinos.Productos.ruta) })
    }
}
