package com.example.stockscan.data

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

class StockScanConverters {
    @TypeConverter
    fun perfilAString(valor: Perfil): String = valor.name

    @TypeConverter
    fun stringAPerfil(valor: String): Perfil = Perfil.valueOf(valor)

    @TypeConverter
    fun movimientoAString(valor: TipoMovimiento): String = valor.name

    @TypeConverter
    fun stringAMovimiento(valor: String): TipoMovimiento = TipoMovimiento.valueOf(valor)

    @TypeConverter
    fun alertaAString(valor: TipoAlerta): String = valor.name

    @TypeConverter
    fun stringAAlerta(valor: String): TipoAlerta = TipoAlerta.valueOf(valor)
}

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
abstract class AppDatabase : RoomDatabase() {

    internal abstract fun stockScanDAO(): StockScanDAO

    companion object {
        @Volatile
        private var INSTANCIA: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stockscan_database"
                ).build().also { INSTANCIA = it }
            }
        }
    }
}
