package com.example.stockscan.ui.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stockscan.model.FormularioProductoModel
import com.example.stockscan.model.Perfil
import com.example.stockscan.modelo.Destinos
import com.example.stockscan.viewmodel.StockScanViewModel

/** Formulario guiado por el FormularioModel del profesor: errores por campo y copy(). */
@Composable
fun GestionProductoView(navController: NavController, viewModel: StockScanViewModel) {
    val e by viewModel.uiState.collectAsStateWithLifecycle()
    val p = e.productoActual()
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
        Tarjeta("Catálogo independiente", "Editar nombre, categoría o mínimo no modifica las existencias.")
        Accion("Validar y guardar producto", {
            val validado = formulario.validar(e.productos, p?.codigoInterno.orEmpty())
            formulario = validado
            if (validado.esValido() && viewModel.guardarProducto(
                    p?.codigoInterno.orEmpty(), validado.nombre, validado.barras,
                    validado.categoria, validado.descripcion, validado.minimo
                )) navController.irA(Destinos.Gestion.ruta)
        })
    }
}
