package com.example.stockscan.viewmodel

// Centro de estado y reglas: conecta pantallas Compose, InventarioReglas y StockScanRepository.

import com.example.stockscan.model.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockscan.repository.InventarioPersistido
import com.example.stockscan.repository.StockScanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update

/** Estado de interfaz de StockScan; los datos de inventario se respaldan mediante Repository + Room. */
class StockScanViewModel(private val repository: StockScanRepository) : ViewModel() {
    /** Flujo mutable interno; las pantallas reciben solo la versión pública de lectura. */
    private val _uiState = MutableStateFlow(
        StockScanModel(tiendas = emptyList(), productos = emptyList(), existencias = emptyList())
    )
    /** Estado observable que las pantallas consumen con collectAsStateWithLifecycle(). */
    val uiState: StateFlow<StockScanModel> = _uiState.asStateFlow()

    /** Carga Room al iniciar y persiste cambios del inventario mediante el contrato Repository. */
    init {
        viewModelScope.launch {
            try {
                val datos = repository.cargarInventario()

                _uiState.update { estado ->
                    estado.copy(
                        tiendas = datos.tiendas,
                        productos = datos.productos,
                        existencias = datos.existencias,
                        movimientos = datos.movimientos,
                        alertas = datos.alertas
                    )
                }

                // Persiste únicamente los datos de negocio. Los filtros, sesión,
                // mensajes y conteos temporales no forman parte de la base de datos.
                uiState
                    .map { estado ->
                        InventarioPersistido(
                            tiendas = estado.tiendas,
                            productos = estado.productos,
                            existencias = estado.existencias,
                            movimientos = estado.movimientos,
                            alertas = estado.alertas
                        )
                    }
                    .distinctUntilChanged()
                    .collect { inventario ->
                        try {
                            repository.guardarInventario(inventario)
                        } catch (error: Exception) {
                            Log.e("StockScan", "No se pudo guardar el inventario", error)
                            aviso("No se pudieron guardar los cambios del inventario")
                        }
                    }
            } catch (error: Exception) {
                Log.e("StockScan", "No se pudo cargar el inventario", error)
                aviso("No se pudo cargar el inventario local")
            }
        }
    }

    /** Genera la fecha UTC usada en movimientos, alertas y conteos. */
    private fun ahoraUtc(): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date())
    /** Publica un mensaje temporal que Pantalla muestra en la interfaz. */
    private fun aviso(mensaje: String) { _uiState.update { it.copy(mensaje = mensaje) } }
    /** Quita el mensaje temporal después de que el usuario lo cierre. */
    fun limpiarMensaje() { _uiState.update { it.copy(mensaje = null) } }
    /** Cambia el rol de demostración y prepara su usuario ficticio. */
    fun seleccionarPerfil(perfil: Perfil) { _uiState.update { it.copy(perfil = perfil, usuario = perfil.codigo, clave = "", mensaje = null) } }
    /** Actualiza el usuario escrito en LoginView. */
    fun onUsuarioChange(s: String) { _uiState.update { it.copy(usuario = s, mensaje = null) } }
    /** Actualiza la clave escrita en LoginView. */
    fun onClaveChange(s: String) { _uiState.update { it.copy(clave = s, mensaje = null) } }




    /** Valida las credenciales de prueba*/
    fun ingresar(): Boolean {
        val e = _uiState.value
        val valido = e.usuario.trim() == e.perfil.codigo && e.clave == "Demo-2026"
        if (!valido) aviso("Usuario o clave ficticia incorrectos")
        return valido
    }
    /** Restablece sesión, selección y formularios antes de volver al login. */
    fun cerrarSesion() {
        _uiState.update { it.copy(perfil = Perfil.OPERADOR, usuario = Perfil.OPERADOR.codigo,
            clave = "", tiendaId = null, productoId = null, busqueda = "", codigoManual = "",
            mensaje = null, conteo = null, movimientoTipo = TipoMovimiento.ENTRADA,
            movimientoCantidad = "", movimientoMotivo = "", filtroHistorial = "") }
    }
    /** Acepta solo tiendas habilitadas y reinicia selección de producto y conteo. */
    fun seleccionarTienda(id: String): Boolean {
        val t = _uiState.value.tiendas.find { it.id == id }
        if (t?.habilitada != true) { aviso("Esta tienda está deshabilitada"); return false }
        _uiState.update { it.copy(tiendaId = id, productoId = null, busqueda = "", conteo = null, mensaje = null) }
        return true
    }
    /** Actualiza el texto que StockScanModel usa para filtrar productos. */
    fun onBusquedaChange(s: String) { _uiState.update { it.copy(busqueda = s) } }
    /** Normaliza el código manual para buscar solo dígitos. */
    fun onCodigoManualChange(s: String) { _uiState.update { it.copy(codigoManual = s.filter(Char::isDigit), mensaje = null) } }
    /** Guarda el producto activo que consultan DetalleView y MovimientoView. */
    fun seleccionarProducto(id: String) { _uiState.update { it.copy(productoId = id, mensaje = null) } }
    /** Busca el código de barras en el catálogo y selecciona el producto encontrado. */
    fun buscarCodigo(): Boolean {
        val e = _uiState.value
        val p = e.productos.find { it.codigoBarras == e.codigoManual }
        if (p == null) { aviso("Producto no encontrado; revisa el código"); return false }
        seleccionarProducto(p.codigoInterno)
        return true
    }
    /** Recibe el resultado de CamaraCodigoBarras a través de EscanearView. */
    fun codigoEscaneado(codigo: String): Boolean {
        onCodigoManualChange(codigo)
        return buscarCodigo()
    }
    /** Cambia el tipo de operación y limpia la cantidad anterior. */
    fun onTipoMovimiento(tipo: TipoMovimiento) { _uiState.update { it.copy(movimientoTipo = tipo, movimientoCantidad = "", mensaje = null) } }
    /** Actualiza la cantidad ingresada en MovimientoView. */
    fun onCantidadMovimiento(s: String) { _uiState.update { it.copy(movimientoCantidad = s.filterIndexed { index, c -> c.isDigit() || (index == 0 && c == '-') }, mensaje = null) } }
    /** Actualiza el motivo que acompaña un ajuste o movimiento. */
    fun onMotivoMovimiento(s: String) { _uiState.update { it.copy(movimientoMotivo = s, mensaje = null) } }
    /** Calcula la cantidad final para la vista previa del formulario. */
    fun resultadoMovimiento(): Int? {
        val e = _uiState.value
        return InventarioReglas.calcularNuevoStock(e.productoActual()?.let { e.stockDe(it) } ?: 0,
            e.movimientoTipo, e.movimientoCantidad.toIntOrNull() ?: return null)
    }
    /** Valida permisos y reglas; actualiza stock y crea el registro de auditoría. */
    fun registrarMovimiento(): Boolean {
        val e = _uiState.value
        if (e.perfil != Perfil.OPERADOR || e.tiendaActual()?.habilitada != true) { aviso("Acción no permitida para este perfil o tienda"); return false }
        val p = e.productoActual() ?: run { aviso("Selecciona un producto"); return false }
        val actual = e.stockDe(p)
        val cantidad = e.movimientoCantidad.toIntOrNull()
        val error = InventarioReglas.validarMovimiento(actual, e.movimientoTipo, cantidad, e.movimientoMotivo)
        if (error != null) { aviso(error); return false }
        val nuevo = InventarioReglas.calcularNuevoStock(actual, e.movimientoTipo, cantidad!!) ?: return false
        val movimiento = Movimiento(e.movimientos.size + 1, e.tiendaId!!, p.codigoInterno, e.movimientoTipo,
            nuevo - actual, actual, nuevo, e.usuario, ahoraUtc(), e.movimientoMotivo.trim())
        _uiState.update { it.copy(existencias = cambiarStock(it.existencias, e.tiendaId, p.codigoInterno, nuevo),
            movimientos = listOf(movimiento) + it.movimientos, movimientoCantidad = "", movimientoMotivo = "",
            mensaje = "Movimiento registrado: ${actual} → ${nuevo} u.") }
        return true
    }
    /** Reemplaza la existencia de un producto en una tienda sin alterar las demás. */
    private fun cambiarStock(lista: List<Existencia>, tienda: String, codigo: String, nuevo: Int): List<Existencia> =
        lista.filterNot { it.tiendaId == tienda && it.codigoInterno == codigo } + Existencia(tienda, codigo, nuevo)

    /** Crea una sesión temporal con una copia del stock actual de la tienda. */
    fun iniciarConteo(): Boolean {
        val e = _uiState.value
        if (e.perfil != Perfil.OPERADOR || e.tiendaActual()?.habilitada != true) { aviso("No se puede iniciar el conteo"); return false }
        _uiState.update { it.copy(conteo = Conteo(it.siguienteConteoId, e.tiendaId!!, ahoraUtc(), System.currentTimeMillis(),
            e.productos.associate { p -> p.codigoInterno to e.stockDe(p) }), siguienteConteoId = it.siguienteConteoId + 1, mensaje = null) }
        return true
    }
    /** Suma una unidad al producto reconocido durante un conteo físico. */
    fun leerEnConteo(codigoBarras: String): Boolean {
        val e = _uiState.value
        val p = e.productos.find { it.codigoBarras == codigoBarras }
        if (p == null) { aviso("Producto no encontrado"); return false }
        val c = e.conteo ?: return false
        if (c.enRevision) return false
        _uiState.update { it.copy(conteo = c.copy(contados = c.contados + (p.codigoInterno to ((c.contados[p.codigoInterno] ?: 0) + 1))),
            mensaje = "${p.nombre}: +1 unidad contada") }
        return true
    }
    /** Guarda la cantidad contada manualmente para un producto. */
    fun fijarCantidad(id: String, valor: String) {
        val c = _uiState.value.conteo ?: return
        if (c.enRevision) return
        val numero = valor.toIntOrNull()
        if (numero == null || numero < 0) { aviso("El conteo debe ser un entero mayor o igual a 0"); return }
        _uiState.update { it.copy(conteo = c.copy(contados = c.contados + (id to numero)), mensaje = null) }
    }
    /** Elimina la cantidad manual mientras el conteo no esté en revisión. */
    fun limpiarCantidadConteo(id: String) {
        val c = _uiState.value.conteo ?: return
        if (!c.enRevision) _uiState.update { it.copy(conteo = c.copy(contados = c.contados - id)) }
    }
    /** Verifica que todos los productos estén contados y bloquea nuevas ediciones. */
    fun revisarConteo(): Boolean {
        val e = _uiState.value
        val c = e.conteo ?: return false
        if (c.contados.size != e.productos.size) { aviso("Debes contar los ${e.productos.size} productos antes de cerrar"); return false }
        _uiState.update { it.copy(conteo = c.copy(enRevision = true), mensaje = null) }
        return true
    }
    /** Descarta la sesión temporal sin modificar las existencias. */
    fun cancelarConteo() { _uiState.update { it.copy(conteo = null, mensaje = "Conteo descartado sin afectar el stock") } }
    /** Aplica diferencias al stock y crea movimientos y alertas de auditoría. */
    fun confirmarConteo(): Boolean {
        val e = _uiState.value
        val c = e.conteo ?: return false
        if (!c.enRevision || e.perfil != Perfil.OPERADOR || e.tiendaId != c.tiendaId || e.tiendaActual()?.habilitada != true) return false
        if (e.productos.any { p -> e.stockDe(p) != c.stockInicial[p.codigoInterno] }) {
            aviso("El stock cambió durante el conteo; cancela y vuelve a contar")
            return false
        }
        val diferencias = e.diferenciasConteo()
        var listaStock = e.existencias
        val nuevosMovimientos = mutableListOf<Movimiento>()
        val nuevasAlertas = mutableListOf<Alerta>()
        diferencias.forEach { (id, diferencia) ->
            val antes = c.stockInicial[id] ?: 0
            val despues = c.contados[id] ?: 0
            listaStock = cambiarStock(listaStock, c.tiendaId, id, despues)
            nuevosMovimientos.add(Movimiento(e.movimientos.size + nuevosMovimientos.size + 1, c.tiendaId, id,
                TipoMovimiento.CONTEO, diferencia, antes, despues, e.usuario, ahoraUtc(), "Conteo C-${c.id}"))
            nuevasAlertas.add(Alerta(e.alertas.size + nuevasAlertas.size + 1, c.tiendaId, id,
                TipoAlerta.DESCALCE, "Conteo C-${c.id}: $antes → $despues (${if (diferencia > 0) "+" else ""}$diferencia u.)",
                e.usuario, ahoraUtc()))
        }
        _uiState.update { it.copy(existencias = listaStock, movimientos = nuevosMovimientos.reversed() + it.movimientos,
            alertas = nuevasAlertas + it.alertas, conteo = null,
            mensaje = "Conteo confirmado: ${diferencias.size} diferencias registradas") }
        return true
    }
    /** Registra una alerta de reposición; no suma unidades al inventario. */
    fun declararReposicion(id: String, motivo: String): Boolean {
        val e = _uiState.value
        if (e.perfil != Perfil.BODEGA || e.tiendaActual()?.habilitada != true) { aviso("Solo el encargado puede declarar reposición"); return false }
        if (motivo.isBlank()) { aviso("Escribe el motivo de reposición"); return false }
        if (e.productos.none { it.codigoInterno == id }) { aviso("Producto no encontrado"); return false }
        val a = Alerta(e.alertas.size + 1, e.tiendaId!!, id, TipoAlerta.REPOSICION, motivo.trim(), e.usuario, ahoraUtc())
        _uiState.update { it.copy(alertas = listOf(a) + it.alertas, mensaje = "Reposición declarada (no suma stock)") }
        return true
    }
    /** Marca una alerta como resuelta desde AlertasView. */
    fun resolverAlerta(id: Int) {
        if (_uiState.value.perfil != Perfil.BODEGA) return
        _uiState.update { it.copy(alertas = it.alertas.map { a -> if (a.id == id) a.copy(resuelta = true) else a }, mensaje = "Alerta resuelta") }
    }
    /** Habilita o bloquea una tienda desde el panel gerencial. */
    fun cambiarEstadoTienda(id: String): Boolean {
        val e = _uiState.value
        if (e.perfil != Perfil.GERENCIAL) return false
        val tienda = e.tiendas.find { it.id == id } ?: return false
        _uiState.update { it.copy(tiendas = it.tiendas.map { t -> if (t.id == id) t.copy(habilitada = !tienda.habilitada) else t },
            mensaje = "Estado de ${tienda.nombre} actualizado") }
        return true
    }
    /** Crea o actualiza el catálogo y evita códigos de barras duplicados. */
    fun guardarProducto(id: String, nombre: String, barras: String, categoria: String, descripcion: String, minimo: String): Boolean {
        val e = _uiState.value
        if (e.perfil != Perfil.GERENCIAL) return false
        val min = minimo.toIntOrNull()
        // Misma regla que FormularioProductoModel: al menos 3 caracteres en el nombre, sin contar espacios extremos.
        if (nombre.trim().length < 3) { aviso("Ingresa al menos 3 caracteres para el nombre del producto"); return false }
        if (barras.isBlank() || categoria.isBlank() || min == null || min < 0) { aviso("Revisa barras, categoría y mínimo"); return false }
        if (!barras.all(Char::isDigit)) { aviso("El código de barras debe ser numérico"); return false }
        if (e.productos.any { it.codigoBarras == barras && it.codigoInterno != id }) { aviso("Código de barras duplicado"); return false }
        val codigo = id.ifBlank { "P-${(e.productos.size + 1).toString().padStart(3, '0')}" }
        val producto = Producto(codigo, barras, nombre, categoria, descripcion, min)
        _uiState.update { it.copy(productos = if (id.isBlank()) it.productos + producto else it.productos.map { p -> if (p.codigoInterno == id) producto else p },
            mensaje = "Producto guardado") }
        return true
    }
    /** Actualiza el texto de búsqueda usado por HistorialView. */
    fun onFiltroHistorial(s: String) { _uiState.update { it.copy(filtroHistorial = s) } }
}
