package com.example.stockscan.ui.screens.inventory

// Pantalla de escaneo: solicita permiso de cámara y conecta el lector CameraX/ML Kit con el ViewModel.

import com.example.stockscan.ui.components.*
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stockscan.model.Perfil
import com.example.stockscan.navigation.Destinos
import com.example.stockscan.viewmodel.StockScanViewModel

/** Permite leer un código con la cámara o escribirlo manualmente. */
@Composable
fun EscanearView(navController: NavController, viewModel: StockScanViewModel) {
    val e by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var permiso by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }
    var habilitarCamara by remember { mutableStateOf(false) }
    /** Solicita CAMERA al sistema Android y actualiza el estado del permiso. */
    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permiso = it
        habilitarCamara = it
    }
    Pantalla(navController, viewModel, "Escanear producto") {
        Tarjeta("Lectura de códigos", "Cámara o ingreso manual. No se guardan fotos ni videos.")
        if (permiso && habilitarCamara) {
            /** Recibe el texto detectado por ML Kit y lo dirige al conteo activo o a la búsqueda del producto. */
            CamaraCodigoBarras(onCodigo = { codigo ->
                habilitarCamara = false
                val conteoActual = e.conteo
                if (e.perfil == Perfil.OPERADOR && conteoActual != null && !conteoActual.enRevision) {
                    viewModel.leerEnConteo(codigo)
                    navController.irA(Destinos.Conteo.ruta)
                } else if (viewModel.codigoEscaneado(codigo)) {
                    navController.irA(Destinos.Detalle.ruta)
                }
            })
            OutlinedButton(onClick = { habilitarCamara = false }, modifier = Modifier.fillMaxWidth()) { Text("Cerrar cámara") }
        } else {
            Accion("Abrir cámara", {
                if (permiso) habilitarCamara = true else pedirPermiso.launch(Manifest.permission.CAMERA)
            })
            if (!permiso) Text("Si denegaste el permiso, habilítalo en Ajustes o usa el ingreso manual.")
        }
        Seccion("O ingresa el código manualmente")
        OutlinedTextField(value = e.codigoManual, onValueChange = viewModel::onCodigoManualChange,
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            label = { Text("Código de barras") },
            supportingText = { Text("Código de prueba: 2015") })
        Accion("Consultar código", {
            val conteoActual = e.conteo
            if (e.perfil == Perfil.OPERADOR && conteoActual != null && !conteoActual.enRevision) {
                if (viewModel.leerEnConteo(e.codigoManual)) navController.irA(Destinos.Conteo.ruta)
            } else if (viewModel.buscarCodigo()) navController.irA(Destinos.Detalle.ruta)
        }, habilitado = e.codigoManual.isNotBlank())
        Text("Información ficticia. La cámara procesa códigos, no fotografías.")
    }
}
