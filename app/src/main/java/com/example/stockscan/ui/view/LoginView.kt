package com.example.stockscan.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stockscan.model.Perfil
import com.example.stockscan.modelo.Destinos
import com.example.stockscan.viewmodel.StockScanViewModel

@Composable
fun LoginView(navController: NavController, viewModel: StockScanViewModel) {
    val e by viewModel.uiState.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Spacer(Modifier.height(12.dp))
        Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.fillMaxWidth().padding(22.dp)) {
                Text("StockScan", color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text("Multi-Tienda", color = MaterialTheme.colorScheme.onPrimary)
                Spacer(Modifier.height(18.dp))
                Text("Tu inventario, siempre al día", color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 23.sp, fontWeight = FontWeight.SemiBold)
                Text("Escanea, cuenta y consulta el stock de cada tienda.",
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        }
        Text("Acceso de prueba", fontWeight = FontWeight.Bold, fontSize = 19.sp)
        Perfil.entries.forEach { perfil ->
            FilterChip(selected = e.perfil == perfil,
                onClick = { viewModel.seleccionarPerfil(perfil) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("${perfil.descripcion} · ${perfil.codigo}") })
        }
        OutlinedTextField(value = e.usuario, onValueChange = viewModel::onUsuarioChange,
            label = { Text("Usuario ficticio") }, singleLine = true,
            isError = e.mensaje?.contains("Usuario o clave") == true,
            supportingText = { if (e.usuario.isBlank()) Text("Ingresa el usuario de prueba") },
            modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = e.clave, onValueChange = viewModel::onClaveChange,
            label = { Text("Clave ficticia") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isError = e.mensaje?.contains("Usuario o clave") == true,
            supportingText = { if (e.clave.isBlank()) Text("Clave demo: Demo-2026") },
            modifier = Modifier.fillMaxWidth())
        Text("Clave de demostración: Demo-2026", style = MaterialTheme.typography.bodySmall)
        if (e.mensaje != null) Text(e.mensaje ?: "", color = MaterialTheme.colorScheme.error)
        Accion("Acceder como ${e.perfil.codigo}", {
            if (viewModel.ingresar()) navController.navigate(Destinos.Tiendas.ruta)
        })
        Text("DSY1105 · Grupo 6 · Datos de prueba", style = MaterialTheme.typography.labelSmall)
    }
}
