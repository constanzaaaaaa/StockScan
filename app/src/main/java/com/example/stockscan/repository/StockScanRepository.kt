package com.example.stockscan.repository

// Contrato de persistencia: StockScanViewModel depende de esta interfaz y RoomStockScanRepository la implementa.

import com.example.stockscan.model.Alerta
import com.example.stockscan.model.Existencia
import com.example.stockscan.model.Movimiento
import com.example.stockscan.model.Producto
import com.example.stockscan.model.Tienda

/**
 * Instantánea de los datos de inventario que deben sobrevivir al cierre de la app.
 * El conteo en curso se mantiene temporal
 */
data class InventarioPersistido(
    val tiendas: List<Tienda>,
    val productos: List<Producto>,
    val existencias: List<Existencia>,
    val movimientos: List<Movimiento>,
    val alertas: List<Alerta>
)

/** Abstrae el almacenamiento para poder cambiar Room sin modificar pantallas ni ViewModel. */
interface StockScanRepository {
    /** Entrega al ViewModel las entidades guardadas en el dispositivo. */
    suspend fun cargarInventario(): InventarioPersistido
    /** Recibe los datos de negocio que deben persistir entre sesiones. */
    suspend fun guardarInventario(datos: InventarioPersistido)
}
