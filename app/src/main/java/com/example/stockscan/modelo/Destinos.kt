package com.example.stockscan.modelo

// Catálogo de rutas de navegación; MainActivity registra estas rutas y las pantallas las usan para moverse.

// Siguiendo el archivo Destinos.kt entregado por el profesor.
/** Cada objeto representa una ruta única usada por NavHost y NavController. */
sealed class Destinos(val ruta: String) {
    object Login: Destinos("login")
    object Tiendas: Destinos("tiendas")
    object Inicio: Destinos("inicio")
    object Escanear: Destinos("escanear")
    object Productos: Destinos("productos")
    object Detalle: Destinos("detalle")
    object Movimiento: Destinos("movimiento")
    object Conteo: Destinos("conteo")
    object ResumenConteo: Destinos("resumen_conteo")
    object Alertas: Destinos("alertas")
    object Historial: Destinos("historial")
    object Resumen: Destinos("resumen")
    object Gestion: Destinos("gestion")
    object GestionProducto: Destinos("gestion_producto")
    object Reportes: Destinos("reportes")
}
