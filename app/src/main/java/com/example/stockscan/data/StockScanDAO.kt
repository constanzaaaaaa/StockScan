package com.example.stockscan.data

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
    suspend fun guardarTiendas(lista: List<Tienda>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarProductos(lista: List<Producto>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarExistencias(lista: List<Existencia>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarMovimientos(lista: List<Movimiento>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarAlertas(lista: List<Alerta>)

    @Query("SELECT * FROM tiendas ORDER BY id")
    suspend fun obtenerTiendas(): List<Tienda>

    @Query("SELECT * FROM productos ORDER BY codigoInterno")
    suspend fun obtenerProductos(): List<Producto>

    @Query("SELECT * FROM existencias ORDER BY tiendaId, codigoInterno")
    suspend fun obtenerExistencias(): List<Existencia>

    @Query("SELECT * FROM movimientos ORDER BY id DESC")
    suspend fun obtenerMovimientos(): List<Movimiento>

    @Query("SELECT * FROM alertas ORDER BY id DESC")
    suspend fun obtenerAlertas(): List<Alerta>
}
