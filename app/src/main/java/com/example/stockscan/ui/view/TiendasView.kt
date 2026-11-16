package com.example.stockscan.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stockscan.modelo.Destinos
import com.example.stockscan.viewmodel.StockScanViewModel

@Composable
fun TiendasView(navController: NavController, viewModel: StockScanViewModel) {
    val e by viewModel.uiState.collectAsStateWithLifecycle()
    var busqueda by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf("Todas") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Elige tu tienda", fontSize = 27.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary)
        Text("${e.perfil.descripcion} · ${e.usuario}")
        Seccion("¿Dónde vas a trabajar hoy?")
        Text("La tienda seleccionada define los productos y movimientos de la demo.")
        OutlinedTextField(value = busqueda, onValueChange = { busqueda = it },
            label = { Text("Buscar tienda o bodega") }, modifier = Modifier.fillMaxWidth())
        RowFiltros(listOf("Todas", "Habilitadas", "Deshabilitadas"), filtro) { filtro = it }
        val disponibles = e.tiendas.filter { t ->
            (t.nombre.contains(busqueda, ignoreCase = true) || t.bodega.contains(busqueda, ignoreCase = true) ||
                t.id.contains(busqueda, ignoreCase = true)) && when(filtro) {
                "Habilitadas" -> t.habilitada
                "Deshabilitadas" -> !t.habilitada
                else -> true
            }
        }
        Text("${disponibles.size} tiendas encontradas")
        disponibles.forEach { t ->
            Card(onClick = {
                if (viewModel.seleccionarTienda(t.id)) navController.navigate(Destinos.Inicio.ruta)
            }, enabled = t.habilitada, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(t.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("${t.id} · Bodega ${t.bodega}")
                    Text(if (t.habilitada) "Habilitada · seleccionable" else "Deshabilitada · bloqueada",
                        color = if (t.habilitada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                }
            }
        }
        if (disponibles.isEmpty()) Text("No se encontraron tiendas con estos filtros.")
        Text("Las tiendas deshabilitadas no permiten movimientos ni conteos.",
            style = MaterialTheme.typography.bodySmall)
        if (e.mensaje != null) Text(e.mensaje ?: "", color = MaterialTheme.colorScheme.error)
    }
}
