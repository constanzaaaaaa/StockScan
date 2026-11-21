package com.example.stockscan.ui.components

// Componentes visuales compartidos: Pantalla, Accion, Tarjeta y Seccion mantienen diseño y navegación por perfil.

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.stockscan.model.Perfil
import com.example.stockscan.navigation.Destinos
import com.example.stockscan.viewmodel.StockScanViewModel

/** Navega a una ruta sin duplicar la misma pantalla en la pila. */
fun NavController.irA(ruta: String) { navigate(ruta) { launchSingleTop = true } }

// Contenedor compartido para mantener todas las pantallas con el mismo estilo.
/** Construye la estructura común, pestañas por perfil y mensajes del ViewModel. */
@Composable
fun Pantalla(
    navController: NavController, viewModel: StockScanViewModel, titulo: String,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val e by viewModel.uiState.collectAsStateWithLifecycle()
    val ruta = navController.currentBackStackEntryAsState().value?.destination?.route
    /** Define la navegación inferior según los permisos del perfil activo. */
    val pestanas = when (e.perfil) {
        Perfil.OPERADOR -> listOf(Triple("Inicio", "⌂", Destinos.Inicio.ruta),
            Triple("Escanear", "▣", Destinos.Escanear.ruta),
            Triple("Productos", "▤", Destinos.Productos.ruta),
            Triple("Historial", "◷", Destinos.Historial.ruta))
        Perfil.BODEGA -> listOf(Triple("Inicio", "⌂", Destinos.Inicio.ruta),
            Triple("Consultar", "▣", Destinos.Escanear.ruta),
            Triple("Alertas", "!", Destinos.Alertas.ruta))
        Perfil.GERENCIAL -> listOf(Triple("Resumen", "▥", Destinos.Resumen.ruta),
            Triple("Gestión", "⚙", Destinos.Gestion.ruta),
            Triple("Historial", "◷", Destinos.Historial.ruta),
            Triple("Reportes", "⇩", Destinos.Reportes.ruta))
    }
    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                pestanas.forEach { (nombre, icono, destino) ->
                    NavigationBarItem(
                        selected = ruta == destino,
                        onClick = { navController.irA(destino) },
                        icon = { Text(icono, fontSize = 19.sp) },
                        label = { Text(nombre, fontSize = 10.sp) }
                    )
                }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Surface(color = MaterialTheme.colorScheme.primary) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(titulo, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${e.perfil.descripcion} · ${e.usuario}", fontSize = 12.sp, color = Color.White)
                    }
                    TextButton(onClick = { navController.irA(Destinos.Tiendas.ruta) }) {
                        Text(e.tiendaId ?: "Tienda", color = Color.White)
                    }
                }
            }
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("DEMO ACADÉMICA · inventario ficticio, sin servidor",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (e.mensaje != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(e.mensaje ?: "", modifier = Modifier.weight(1f))
                            TextButton(onClick = viewModel::limpiarMensaje) { Text("Cerrar") }
                        }
                    }
                }
                contenido()
            }
        }
    }
}

/** Botón principal reutilizable para ejecutar acciones de cada pantalla. */
@Composable
fun Accion(texto: String, onClick: () -> Unit, habilitado: Boolean = true) {
    Button(
        onClick = onClick, enabled = habilitado,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
    ) { Text(texto, fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }
}

/** Tarjeta reutilizable para mostrar información y, opcionalmente, navegar al tocarla. */
@Composable
fun Tarjeta(titulo: String, detalle: String, onClick: (() -> Unit)? = null) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick ?: {},
        enabled = onClick != null,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(titulo, fontWeight = FontWeight.Bold, fontSize = 17.sp,
                color = MaterialTheme.colorScheme.primary)
            Text(detalle, fontSize = 14.sp)
        }
    }
}

/** Título reutilizable para separar bloques dentro de una pantalla. */
@Composable
fun Seccion(texto: String) {
    Text(texto, fontSize = 19.sp, fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground)
}

/** Chips horizontales reutilizables para filtrar listas en distintas pantallas. */
@Composable
fun RowFiltros(opciones: List<String>, seleccionado: String, cambio: (String) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        opciones.forEach { opcion ->
            FilterChip(
                selected = seleccionado == opcion,
                onClick = { cambio(opcion) },
                label = { Text(opcion) }
            )
        }
    }
}
