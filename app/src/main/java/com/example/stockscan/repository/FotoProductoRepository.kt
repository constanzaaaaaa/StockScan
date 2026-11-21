package com.example.stockscan.repository

// Guarda fotos de productos en el almacenamiento privado de la app, sin modificar Room ni el DAO.

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Copia la foto elegida con el selector nativo a un archivo interno asociado
 * al código del producto. Así permanece disponible después de reiniciar la app.
 */
class FotoProductoRepository(context: Context) {
    private val appContext = context.applicationContext
    private val carpeta = File(appContext.filesDir, "fotos_productos")

    /** Usa el código interno del producto como nombre seguro de archivo. */
    private fun archivo(codigoInterno: String): File {
        val nombreSeguro = codigoInterno.replace(Regex("[^A-Za-z0-9_-]"), "_")
        return File(carpeta, "${nombreSeguro}.jpg")
    }

    /** Lee una imagen del selector sin cargarla a resolución completa en memoria. */
    private fun decodificar(uri: Uri): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        appContext.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, bounds)
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var muestra = 1
        while (bounds.outWidth / muestra > 1200 || bounds.outHeight / muestra > 1200) {
            muestra *= 2
        }
        val opciones = BitmapFactory.Options().apply { inSampleSize = muestra }
        return appContext.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, opciones)
        }
    }

    /** Permite previsualizar la foto seleccionada antes de guardar el producto. */
    suspend fun previsualizarFoto(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        try {
            decodificar(uri)
        } catch (_: Exception) {
            null
        }
    }

    /** Copia la foto a un archivo JPEG local que no necesita permisos de galería. */
    suspend fun guardarFoto(codigoInterno: String, uri: Uri): Boolean = withContext(Dispatchers.IO) {
        var temporal: File? = null
        try {
            val imagen = decodificar(uri) ?: return@withContext false
            if (!carpeta.exists() && !carpeta.mkdirs()) return@withContext false

            val archivoTemporal = File.createTempFile("foto-", ".tmp", carpeta)
            temporal = archivoTemporal
            val escrita = FileOutputStream(archivoTemporal).use { salida ->
                imagen.compress(Bitmap.CompressFormat.JPEG, 85, salida)
            }
            if (!escrita) return@withContext false

            val destino = archivo(codigoInterno)
            if (!archivoTemporal.renameTo(destino)) {
                archivoTemporal.copyTo(destino, overwrite = true)
            }
            true
        } catch (_: Exception) {
            false
        } finally {
            temporal?.delete()
        }
    }

    /** Recupera la foto local asociada al producto para mostrarla en DetalleView. */
    suspend fun cargarFoto(codigoInterno: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val foto = archivo(codigoInterno)
            if (foto.isFile) BitmapFactory.decodeFile(foto.absolutePath) else null
        } catch (_: Exception) {
            null
        }
    }
}
