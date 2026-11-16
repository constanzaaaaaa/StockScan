package com.example.stockscan.repository

import com.example.stockscan.model.Alerta
import com.example.stockscan.model.Existencia
import com.example.stockscan.model.Movimiento
import com.example.stockscan.model.Producto
import com.example.stockscan.model.Tienda

/**
 * Instantánea de los datos de inventario que deben sobrevivir al cierre de la app.
 * El conteo en curso se mantiene temporal, igual que en el proyecto de referencia.
 */
data class InventarioPersistido(
    val tiendas: List<Tienda>,
    val productos: List<Producto>,
    val existencias: List<Existencia>,
    val movimientos: List<Movimiento>,
    val alertas: List<Alerta>
)

interface StockScanRepository {
    suspend fun cargarInventario(): InventarioPersistido
    suspend fun guardarInventario(datos: InventarioPersistido)
}
