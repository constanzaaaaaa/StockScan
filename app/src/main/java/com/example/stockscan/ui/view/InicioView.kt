package com.example.stockscan.ui.view

// Inicio por perfil: resume la tienda activa y ofrece accesos a los flujos disponibles para cada rol.

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stockscan.model.Perfil
import com.example.stockscan.modelo.Destinos
import com.example.stockscan.viewmodel.StockScanViewModel

/** Construye el panel principal usando el estado del ViewModel y las rutas de Destinos. */
@Composable
fun InicioView(navController: NavController, viewModel: StockScanViewModel) {
    val e by viewModel.uiState.collectAsStateWithLifecycle()
    Pantalla(navController, viewModel, "StockScan") {
        Seccion("Tu inventario · ${e.tiendaActual()?.nombre ?: "Tienda pendiente"}")
        Text("Bodega ${e.tiendaActual()?.bodega ?: "-"} · Estado de prueba")
        Tarjeta("${e.productos.size} productos únicos", "${e.totalUnidades()} unidades actuales en memoria")
        Tarjeta("${e.alertasActivas().count { it.tiendaId == e.tiendaId }} alertas", "Stock mínimo, descalces y reposiciones")
        /** Muestra acciones distintas para OPERADOR, BODEGA y GERENCIAL. */
        when (e.perfil) {
            Perfil.OPERADOR -> {
                Accion("Escanear producto", { navController.irA(Destinos.Escanear.ruta) })
                Accion("Consultar y registrar movimiento", { navController.irA(Destinos.Productos.ruta) })
                Accion("Conteo físico", { if (viewModel.iniciarConteo()) navController.irA(Destinos.Conteo.ruta) })
                Accion("Historial de tienda", { navController.irA(Destinos.Historial.ruta) })
            }
            Perfil.BODEGA -> {
                Accion("Consultar productos", { navController.irA(Destinos.Productos.ruta) })
                Accion("Alertas y reposiciones", { navController.irA(Destinos.Alertas.ruta) })
            }
            Perfil.GERENCIAL -> {
                Accion("Ver resumen global", { navController.irA(Destinos.Resumen.ruta) })
                Accion("Administrar tiendas y productos", { navController.irA(Destinos.Gestion.ruta) })
                Accion("Reportes", { navController.irA(Destinos.Reportes.ruta) })
            }
        }
        val ultimos = e.movimientos.filter { it.tiendaId == e.tiendaId }.take(3)
        if (ultimos.isNotEmpty()) {
            Seccion("Movimientos recientes")
            ultimos.forEach { m -> Tarjeta("${m.tipo.titulo} · ${m.productoId}",
                "${m.anterior} → ${m.nuevo} u. · ${m.usuario} · ${m.fechaUtc} UTC") }
        }
        Accion("Cambiar tienda", { navController.irA(Destinos.Tiendas.ruta) })
        Accion("Cerrar sesión", {
            viewModel.cerrarSesion()
            navController.navigate(Destinos.Login.ruta) { popUpTo(0) }
        })
    }
}
