package com.example.stockscan.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stockscan.data.local.entity.AlertaEntity
import com.example.stockscan.data.local.entity.ConteoDetalleEntity
import com.example.stockscan.data.local.entity.ConteoEntity
import com.example.stockscan.data.local.entity.ExistenciaEntity
import com.example.stockscan.data.local.entity.MovimientoEntity
import com.example.stockscan.data.local.entity.ProductoEntity
import com.example.stockscan.data.local.entity.TiendaEntity

/**
 * Esquema local de StockScan.
 *
 * Se declaran las tablas y sus relaciones, pero no se exponen DAO porque
 * esta implementación se limita a dejar Room configurado y preparado.
 */
@Database(
    entities = [
        TiendaEntity::class,
        ProductoEntity::class,
        ExistenciaEntity::class,
        MovimientoEntity::class,
        AlertaEntity::class,
        ConteoEntity::class,
        ConteoDetalleEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class StockScanDatabase : RoomDatabase() {

    companion object {
        private const val NOMBRE_BASE_DATOS = "stockscan.db"

        @Volatile
        private var INSTANCIA: StockScanDatabase? = null

        fun obtenerInstancia(context: Context): StockScanDatabase {
            return INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    StockScanDatabase::class.java,
                    NOMBRE_BASE_DATOS
                ).build().also { INSTANCIA = it }
            }
        }
    }
}
