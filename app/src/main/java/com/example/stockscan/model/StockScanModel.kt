package com.example.stockscan.model

// Modelos y estado de StockScan: Room persiste entidades y el ViewModel expone StockScanModel a Compose.

import androidx.room.Entity
import androidx.room.PrimaryKey



/** Perfiles ficticios que controlan las opciones y acciones disponibles. */
enum class Perfil(val descripcion: String, val codigo: String) {
    OPERADOR("Operador", "OP-007"),
    BODEGA("Encargado de bodega", "EB-001"),
    GERENCIAL("Gerencial", "GE-001")
}

/** Tipos de operación que modifican o fijan las existencias. */
enum class TipoMovimiento(val titulo: String) {
    ENTRADA("Entrada"), SALIDA("Salida"), AJUSTE("Ajuste"), CONTEO("Conteo")
}

/** Clasifica avisos de mínimo, diferencias de conteo y reposición. */
enum class TipoAlerta(val titulo: String) {
    MINIMO("Stock mínimo"), DESCALCE("Descalce"), REPOSICION("Reposición")
}

/** Entidad Room: almacena las tiendas y si están habilitadas. */
@Entity(tableName = "tiendas")
data class Tienda(@PrimaryKey val id: String, val nombre: String, val bodega: String, val habilitada: Boolean)
/** Entidad Room: catálogo y código de barras de cada producto. */
@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey val codigoInterno: String,
    val codigoBarras: String,
    val nombre: String,
    val categoria: String,
    val descripcion: String,
    val stockMinimo: Int
)
/** Entidad Room: stock de un producto dentro de una tienda. */
@Entity(tableName = "existencias", primaryKeys = ["tiendaId", "codigoInterno"])
data class Existencia(val tiendaId: String, val codigoInterno: String, val cantidad: Int)
/** Entidad Room: auditoría de los cambios de inventario. */
@Entity(tableName = "movimientos")
data class Movimiento(
    @PrimaryKey val id: Int, val tiendaId: String, val productoId: String, val tipo: TipoMovimiento,
    val cantidad: Int, val anterior: Int, val nuevo: Int, val usuario: String,
    val fechaUtc: String, val motivo: String
)
/** Entidad Room: alertas que se gestionan desde AlertasView. */
@Entity(tableName = "alertas")
data class Alerta(
    @PrimaryKey val id: Int, val tiendaId: String, val productoId: String,
    val tipo: TipoAlerta, val descripcion: String, val usuario: String,
    val fechaUtc: String, val resuelta: Boolean = false
)
/** Sesión temporal compartida con ConteoView y ResumenConteoView; no se guarda en Room. */
data class Conteo(
    val id: Int,
    val tiendaId: String,
    val inicioUtc: String,
    val inicioMillis: Long = System.currentTimeMillis(),
    val stockInicial: Map<String, Int>,
    val contados: Map<String, Int> = emptyMap(),
    val enRevision: Boolean = false
)

/** Datos ficticios que RoomStockScanRepository inserta cuando la base está vacía. */
object DatosDemo {
    val tiendas = listOf(
        Tienda("T-01", "Tienda Norte", "BN-01", true),
        Tienda("T-02", "Tienda Centro", "BC-02", true),
        Tienda("T-03", "Tienda Sur", "BS-03", true),
        Tienda("T-04", "Tienda Reserva", "BR-04", false)
    )
    val productos = listOf(
        Producto("P-001", "2015", "Botella térmica 750 ml", "Accesorios", "Botella de acero reutilizable, capacidad 750 ml.", 10),
        Producto("P-002", "2016", "Taza cerámica 350 ml", "Hogar", "Taza cerámica de prueba, capacidad 350 ml.", 20),
        Producto("P-003", "2017", "Cuaderno A5", "Papelería", "Cuaderno de prueba para anotaciones.", 15)
    )
    val existencias = listOf(
        Existencia("T-01", "P-001", 8), Existencia("T-01", "P-002", 24), Existencia("T-01", "P-003", 40),
        Existencia("T-02", "P-001", 36), Existencia("T-02", "P-002", 42), Existencia("T-02", "P-003", 48),
        Existencia("T-03", "P-001", 24), Existencia("T-03", "P-002", 18), Existencia("T-03", "P-003", 48)
    )
}

/** Estado de interfaz que leen las pantallas y actualiza StockScanViewModel. */
data class StockScanModel(
    val perfil: Perfil = Perfil.OPERADOR,
    val usuario: String = Perfil.OPERADOR.codigo,
    val clave: String = "",
    val tiendaId: String? = null,
    val productoId: String? = null,
    val busqueda: String = "",
    val codigoManual: String = "",
    val mensaje: String? = null,
    val tiendas: List<Tienda> = DatosDemo.tiendas,
    val productos: List<Producto> = DatosDemo.productos,
    val existencias: List<Existencia> = DatosDemo.existencias,
    val movimientos: List<Movimiento> = emptyList(),
    val alertas: List<Alerta> = emptyList(),
    val conteo: Conteo? = null,
    val siguienteConteoId: Int = 14,
    val movimientoTipo: TipoMovimiento = TipoMovimiento.ENTRADA,
    val movimientoCantidad: String = "",
    val movimientoMotivo: String = "",
    val filtroHistorial: String = ""
) {
    /** Obtiene la tienda seleccionada a partir de tiendaId. */
    fun tiendaActual(): Tienda? = tiendas.find { it.id == tiendaId }
    /** Obtiene el producto seleccionado a partir de productoId. */
    fun productoActual(): Producto? = productos.find { it.codigoInterno == productoId }
    /** Busca las unidades de un producto en una tienda. */
    fun stockDe(producto: Producto, tienda: String? = tiendaId): Int =
        existencias.find { it.tiendaId == tienda && it.codigoInterno == producto.codigoInterno }?.cantidad ?: 0
    /** Suma las existencias de la tienda indicada. */
    fun totalUnidades(tienda: String? = tiendaId): Int =
        existencias.filter { it.tiendaId == tienda }.sumOf { it.cantidad }
    /** Filtra el catálogo por nombre, código de barras o código interno. */
    fun productosFiltrados(): List<Producto> = productos.filter {
        it.nombre.contains(busqueda, ignoreCase = true) || it.codigoBarras.contains(busqueda) ||
            it.codigoInterno.contains(busqueda, ignoreCase = true)
    }
    /** Genera alertas para productos cuyo stock está en el mínimo o por debajo. */
    fun alertasMinimo(): List<Alerta> = tiendas.filter { it.habilitada }.flatMap { tienda ->
        productos.filter { stockDe(it, tienda.id) <= it.stockMinimo }.map { producto ->
            Alerta(-1, tienda.id, producto.codigoInterno, TipoAlerta.MINIMO,
                "${stockDe(producto, tienda.id)} u. ≤ mínimo ${producto.stockMinimo} u.", "SISTEMA", "")
        }
    }
    /** Combina alertas pendientes con las automáticas por stock mínimo. */
    fun alertasActivas(): List<Alerta> = alertas.filter { !it.resuelta } + alertasMinimo()
    /** Compara cantidades contadas con la foto inicial y devuelve solo diferencias. */
    fun diferenciasConteo(): Map<String, Int> {
        val actual = conteo ?: return emptyMap()
        return actual.contados.filter { (producto, cantidad) -> actual.stockInicial[producto] != cantidad }
            .mapValues { (producto, cantidad) -> cantidad - (actual.stockInicial[producto] ?: 0) }
    }
}
