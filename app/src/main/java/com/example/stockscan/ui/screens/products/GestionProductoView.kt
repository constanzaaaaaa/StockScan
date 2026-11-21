package com.example.stockscan.ui.screens.products

// Formulario de alta y edición: FormularioProductoModel valida los campos y StockScanViewModel actualiza el catálogo.

import com.example.stockscan.ui.components.*
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stockscan.model.FormularioProductoModel
import com.example.stockscan.model.Perfil
import com.example.stockscan.navigation.Destinos
import com.example.stockscan.repository.FotoProductoRepository
import kotlinx.coroutines.launch
import com.example.stockscan.viewmodel.StockScanViewModel

/** Formulario guiado por el FormularioModel del profesor: errores por campo y copy(). */
@Composable
fun GestionProductoView(navController: NavController, viewModel: StockScanViewModel) {
    val e by viewModel.uiState.collectAsStateWithLifecycle()
    val p = e.productoActual()

    // El selector de Android entrega solo la foto elegida; no requiere permisos de galería.
    val contexto = LocalContext.current
    val fotosRepository = remember(contexto) { FotoProductoRepository(contexto) }
    val scope = rememberCoroutineScope()
    var fotoPendiente by remember(e.productoId) { mutableStateOf<Uri?>(null) }
    var fotoPrevia by remember(e.productoId) { mutableStateOf<Bitmap?>(null) }
    var fotoGuardada by remember(e.productoId) { mutableStateOf<Bitmap?>(null) }
    var mensajeFoto by remember(e.productoId) { mutableStateOf<String?>(null) }
    var guardandoFoto by remember(e.productoId) { mutableStateOf(false) }

    // Recarga la fotografía ya asociada cuando se abre un producto existente.
    LaunchedEffect(p?.codigoInterno) {
        fotoGuardada = p?.let { fotosRepository.cargarFoto(it.codigoInterno) }
    }
    // Muestra una vista previa de la nueva elección sin modificar todavía el producto.
    LaunchedEffect(fotoPendiente) {
        fotoPendiente?.let { uri ->
            fotoPrevia = fotosRepository.previsualizarFoto(uri)
            if (fotoPrevia == null) mensajeFoto = "No se pudo leer la foto; elige otra."
        }
    }

    val selectorFotos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            fotoPendiente = uri
            fotoPrevia = null
            mensajeFoto = null
        }
    }
    /** Carga los valores del producto seleccionado o inicia un formulario vacío. */
    var formulario by remember(e.productoId) {
        mutableStateOf(FormularioProductoModel(
            nombre = p?.nombre.orEmpty(), barras = p?.codigoBarras.orEmpty(),
            categoria = p?.categoria.orEmpty(), descripcion = p?.descripcion.orEmpty(),
            minimo = p?.stockMinimo?.toString() ?: "0"
        ))
    }
    Pantalla(navController, viewModel, if (p == null) "Crear producto" else "Editar · ${p.codigoInterno}") {
        if (e.perfil != Perfil.GERENCIAL) { Text("Acceso exclusivo de gerencia"); return@Pantalla }
        Tarjeta("Formulario de catálogo", "Completa los datos. Los mensajes de error aparecen bajo cada campo.")
        OutlinedTextField(
            value = formulario.nombre,
            onValueChange = { formulario = formulario.copy(nombre = it, errorNombre = null) },
            label = { Text("Nombre del producto") },
            isError = formulario.errorNombre != null,
            supportingText = { formulario.errorNombre?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = formulario.barras,
            onValueChange = { formulario = formulario.copy(barras = it.filter(Char::isDigit), errorBarras = null) },
            label = { Text("Código de barras") },
            isError = formulario.errorBarras != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            supportingText = { formulario.errorBarras?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = formulario.categoria,
            onValueChange = { formulario = formulario.copy(categoria = it, errorCategoria = null) },
            label = { Text("Categoría") },
            isError = formulario.errorCategoria != null,
            supportingText = { formulario.errorCategoria?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(value = formulario.descripcion,
            onValueChange = { formulario = formulario.copy(descripcion = it) },
            label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = formulario.minimo,
            onValueChange = { formulario = formulario.copy(minimo = it.filter(Char::isDigit), errorMinimo = null) },
            label = { Text("Stock mínimo (unidades)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = formulario.errorMinimo != null,
            supportingText = { formulario.errorMinimo?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        if (p != null) e.tiendas.forEach { t ->
            Text("${t.nombre}: ${e.stockDe(p, t.id)} u. (solo lectura)")
        }
        Seccion("Fotografía del producto")
        // La fotografía es opcional y se vincula al código interno al guardar.
        val fotoMostrada = if (fotoPendiente != null) fotoPrevia else fotoGuardada
        fotoMostrada?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Fotografía del producto",
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentScale = ContentScale.Fit
            )
        }
        OutlinedButton(
            onClick = {
                selectorFotos.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Seleccionar foto de la galería") }
        Text("La foto es opcional; se guarda solo en este dispositivo.")
        mensajeFoto?.let { Text(it) }

        Tarjeta("Catálogo independiente", "Editar nombre, categoría o mínimo no modifica las existencias.")
        /** Valida los datos y conserva la foto elegida si se guardó el producto. */
        Accion("Validar y guardar producto", {
            val validado = formulario.validar(e.productos, p?.codigoInterno.orEmpty())
            formulario = validado
            if (validado.esValido()) {
                if (fotoPendiente != null && fotoPrevia == null) {
                    mensajeFoto = "No se pudo leer la foto o todavía se está cargando."
                } else if (viewModel.guardarProducto(
                        p?.codigoInterno.orEmpty(), validado.nombre, validado.barras,
                        validado.categoria, validado.descripcion, validado.minimo
                    )) {
                    val uri = fotoPendiente
                    if (uri == null) {
                        navController.irA(Destinos.Gestion.ruta)
                    } else {
                        // El ViewModel asigna el código definitivo al crear el producto.
                        val codigoGuardado = viewModel.uiState.value.productos
                            .firstOrNull { it.codigoBarras == validado.barras }?.codigoInterno
                        if (codigoGuardado == null) {
                            mensajeFoto = "Producto guardado, pero no se identificó su código para la foto."
                        } else {
                            guardandoFoto = true
                            scope.launch {
                                val exito = fotosRepository.guardarFoto(codigoGuardado, uri)
                                guardandoFoto = false
                                if (exito) {
                                    navController.irA(Destinos.Gestion.ruta)
                                } else {
                                    mensajeFoto = "Producto guardado, pero no se pudo guardar la foto."
                                }
                            }
                        }
                    }
                }
            }
        }, habilitado = !guardandoFoto)
    }
}
