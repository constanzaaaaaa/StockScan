package com.example.stockscan.repository

// Implementación Room del contrato StockScanRepository; encapsula el DAO para que el ViewModel no conozca SQLite.

import androidx.room.withTransaction
import com.example.stockscan.data.AppDatabase
import com.example.stockscan.model.StockScanModel

/**
 * Implementación local del repositorio. Toda interacción con Room queda aquí;
 * las pantallas y el ViewModel solo conocen la interfaz StockScanRepository.
 */
class RoomStockScanRepository(
    private val database: AppDatabase
) : StockScanRepository {

    /** DAO privado que ejecuta las consultas definidas en StockScanDAO. */
    private val storage = database.stockScanDAO()

    /** Lee el inventario persistido y carga datos demo solo cuando la base está vacía. */
    override suspend fun cargarInventario(): InventarioPersistido {
        return database.withTransaction {
            var tiendas = storage.obtenerTiendas()
            var productos = storage.obtenerProductos()
            var existencias = storage.obtenerExistencias()
            val movimientos = storage.obtenerMovimientos()
            val alertas = storage.obtenerAlertas()

            // La información ficticia inicial se inserta una sola vez.
            if (tiendas.isEmpty() && productos.isEmpty()) {
                val demo = StockScanModel()
                storage.guardarTiendas(demo.tiendas)
                storage.guardarProductos(demo.productos)
                storage.guardarExistencias(demo.existencias)

                tiendas = demo.tiendas
                productos = demo.productos
                existencias = demo.existencias
            }

            InventarioPersistido(
                tiendas = tiendas,
                productos = productos,
                existencias = existencias,
                movimientos = movimientos,
                alertas = alertas
            )
        }
    }

    /** Guarda el snapshot de negocio dentro de una transacción Room. */
    override suspend fun guardarInventario(datos: InventarioPersistido) {
        database.withTransaction {
            // REPLACE actualiza registros existentes y agrega los nuevos.
            // No se vacían las tablas completas en cada cambio de estado.
            if (datos.tiendas.isNotEmpty()) storage.guardarTiendas(datos.tiendas)
            if (datos.productos.isNotEmpty()) storage.guardarProductos(datos.productos)
            if (datos.existencias.isNotEmpty()) storage.guardarExistencias(datos.existencias)
            if (datos.movimientos.isNotEmpty()) storage.guardarMovimientos(datos.movimientos)
            if (datos.alertas.isNotEmpty()) storage.guardarAlertas(datos.alertas)
        }
    }
}
