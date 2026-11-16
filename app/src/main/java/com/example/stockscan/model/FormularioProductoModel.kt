package com.example.stockscan.model

/**
 * Estado del formulario como FormularioModel de las clases del profesor.
 * La demostración es exclusivamente frontend y el catálogo vive en memoria.
 */
data class FormularioProductoModel(
    val nombre: String = "",
    val barras: String = "",
    val categoria: String = "",
    val descripcion: String = "",
    val minimo: String = "0",
    val errorNombre: String? = null,
    val errorBarras: String? = null,
    val errorCategoria: String? = null,
    val errorMinimo: String? = null
) {
    fun validar(productos: List<Producto>, idActual: String): FormularioProductoModel = copy(
        errorNombre = if (nombre.trim().length < 3) "Ingresa al menos 3 caracteres" else null,
        errorBarras = when {
            barras.isBlank() -> "Ingresa el código de barras"
            !barras.all(Char::isDigit) -> "Solo se permiten números"
            productos.any { it.codigoBarras == barras && it.codigoInterno != idActual } -> "Ya existe un producto con este código"
            else -> null
        },
        errorCategoria = if (categoria.isBlank()) "Ingresa una categoría" else null,
        errorMinimo = if (minimo.toIntOrNull() == null || (minimo.toIntOrNull() ?: -1) < 0) {
            "El stock mínimo debe ser un entero positivo o cero"
        } else null
    )

    fun esValido(): Boolean = errorNombre == null && errorBarras == null &&
        errorCategoria == null && errorMinimo == null
}
