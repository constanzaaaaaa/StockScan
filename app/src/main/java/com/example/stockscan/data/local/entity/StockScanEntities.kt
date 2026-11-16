package com.example.stockscan.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tiendas")
data class TiendaEntity(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val bodega: String,
    val habilitada: Boolean
)

@Entity(
    tableName = "productos",
    indices = [Index(value = ["codigoBarras"], unique = true)]
)
data class ProductoEntity(
    @PrimaryKey
    val codigoInterno: String,
    val codigoBarras: String,
    val nombre: String,
    val categoria: String,
    val descripcion: String,
    val stockMinimo: Int
)

@Entity(
    tableName = "existencias",
    primaryKeys = ["tiendaId", "productoId"],
    foreignKeys = [
        ForeignKey(
            entity = TiendaEntity::class,
            parentColumns = ["id"],
            childColumns = ["tiendaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductoEntity::class,
            parentColumns = ["codigoInterno"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["productoId"])]
)
data class ExistenciaEntity(
    val tiendaId: String,
    val productoId: String,
    val cantidad: Int
)

@Entity(
    tableName = "movimientos",
    foreignKeys = [
        ForeignKey(
            entity = TiendaEntity::class,
            parentColumns = ["id"],
            childColumns = ["tiendaId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = ProductoEntity::class,
            parentColumns = ["codigoInterno"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["tiendaId"]),
        Index(value = ["productoId"]),
        Index(value = ["fechaUtc"])
    ]
)
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tiendaId: String,
    val productoId: String,
    val tipo: String,
    val cantidad: Int,
    val anterior: Int,
    val nuevo: Int,
    val usuario: String,
    val fechaUtc: String,
    val motivo: String
)

@Entity(
    tableName = "alertas",
    foreignKeys = [
        ForeignKey(
            entity = TiendaEntity::class,
            parentColumns = ["id"],
            childColumns = ["tiendaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductoEntity::class,
            parentColumns = ["codigoInterno"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["tiendaId"]),
        Index(value = ["productoId"]),
        Index(value = ["resuelta"])
    ]
)
data class AlertaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tiendaId: String,
    val productoId: String,
    val tipo: String,
    val descripcion: String,
    val usuario: String,
    val fechaUtc: String,
    val resuelta: Boolean = false
)

@Entity(
    tableName = "conteos",
    foreignKeys = [
        ForeignKey(
            entity = TiendaEntity::class,
            parentColumns = ["id"],
            childColumns = ["tiendaId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["tiendaId"])]
)
data class ConteoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tiendaId: String,
    val inicioUtc: String,
    val inicioMillis: Long,
    val enRevision: Boolean = false
)

/**
 * Detalle normalizado de un conteo. Guarda la fotografía inicial del stock
 * y deja cantidadContada nula hasta que el operador registre ese producto.
 */
@Entity(
    tableName = "conteo_detalles",
    primaryKeys = ["conteoId", "productoId"],
    foreignKeys = [
        ForeignKey(
            entity = ConteoEntity::class,
            parentColumns = ["id"],
            childColumns = ["conteoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductoEntity::class,
            parentColumns = ["codigoInterno"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["productoId"])]
)
data class ConteoDetalleEntity(
    val conteoId: Long,
    val productoId: String,
    val stockInicial: Int,
    val cantidadContada: Int? = null
)
