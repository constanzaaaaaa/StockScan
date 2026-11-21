package com.example.stockscan.data

// Configuración de Room: define entidades, conversores y acceso al DAO usado por el Repository.

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.stockscan.model.Alerta
import com.example.stockscan.model.Perfil
import com.example.stockscan.model.Producto
import com.example.stockscan.model.Existencia
import com.example.stockscan.model.Movimiento
import com.example.stockscan.model.TipoAlerta
import com.example.stockscan.model.TipoMovimiento
import com.example.stockscan.model.Tienda

/** Room convierte los enums del modelo a texto y viceversa. */
class StockScanConverters {
    /** Guarda Perfil como texto en SQLite. */
    @TypeConverter
    fun perfilAString(valor: Perfil): String = valor.name

    /** Reconstruye Perfil desde el texto guardado por Room. */
    @TypeConverter
    fun stringAPerfil(valor: String): Perfil = Perfil.valueOf(valor)

    /** Guarda el tipo de movimiento como texto. */
    @TypeConverter
    fun movimientoAString(valor: TipoMovimiento): String = valor.name

    /** Reconstruye TipoMovimiento desde SQLite. */
    @TypeConverter
    fun stringAMovimiento(valor: String): TipoMovimiento = TipoMovimiento.valueOf(valor)

    /** Guarda el tipo de alerta como texto. */
    @TypeConverter
    fun alertaAString(valor: TipoAlerta): String = valor.name

    /** Reconstruye TipoAlerta desde SQLite. */
    @TypeConverter
    fun stringAAlerta(valor: String): TipoAlerta = TipoAlerta.valueOf(valor)
}

/** Registra las entidades persistidas; Conteo queda fuera porque es temporal. */
@Database(
    entities = [
        Tienda::class,
        Producto::class,
        Existencia::class,
        Movimiento::class,
        Alerta::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(StockScanConverters::class)
/** Room genera esta base y entrega el DAO a RoomStockScanRepository. */
abstract class AppDatabase : RoomDatabase() {

    /** Punto de acceso interno a las consultas de StockScanDAO. */
    internal abstract fun stockScanDAO(): StockScanDAO

    companion object {
        @Volatile
        private var INSTANCIA: AppDatabase? = null

        /** Entrega una única instancia compartida de la base local. */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    // Nombre del archivo de base de datos SQLite local de StockScan.
                    "stockscan_database"
                ).build().also { INSTANCIA = it }
            }
        }
    }
}
