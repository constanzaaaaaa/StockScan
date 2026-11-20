package com.example.stockscan.data

// Consultas SQLite generadas por Room; solo RoomStockScanRepository utiliza esta interfaz.

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stockscan.model.Alerta
import com.example.stockscan.model.Existencia
import com.example.stockscan.model.Movimiento
import com.example.stockscan.model.Producto
import com.example.stockscan.model.Tienda

/**
 * Adaptador interno de Room. La interfaz de acceso que utiliza la aplicación
 * es StockScanRepository; el ViewModel nunca depende de este DAO.
 */
@Dao
internal interface StockScanDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /** Inserta o reemplaza las tiendas del inventario. */
    suspend fun guardarTiendas(lista: List<Tienda>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /** Inserta o reemplaza el catálogo de productos. */
    suspend fun guardarProductos(lista: List<Producto>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /** Persiste unidades por tienda y producto. */
    suspend fun guardarExistencias(lista: List<Existencia>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /** Guarda el historial de entradas, salidas, ajustes y conteos. */
    suspend fun guardarMovimientos(lista: List<Movimiento>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /** Guarda alertas manuales y de auditoría. */
    suspend fun guardarAlertas(lista: List<Alerta>)

    @Query("SELECT * FROM tiendas ORDER BY id")
    /** Lee las tiendas ordenadas por identificador. */
    suspend fun obtenerTiendas(): List<Tienda>

    @Query("SELECT * FROM productos ORDER BY codigoInterno")
    /** Lee el catálogo ordenado por código interno. */
    suspend fun obtenerProductos(): List<Producto>

    @Query("SELECT * FROM existencias ORDER BY tiendaId, codigoInterno")
    /** Lee el stock separado por tienda y producto. */
    suspend fun obtenerExistencias(): List<Existencia>

    @Query("SELECT * FROM movimientos ORDER BY id DESC")
    /** Lee los movimientos más recientes primero. */
    suspend fun obtenerMovimientos(): List<Movimiento>

    @Query("SELECT * FROM alertas ORDER BY id DESC")
    /** Lee las alertas más recientes primero. */
    suspend fun obtenerAlertas(): List<Alerta>
}
