package com.example.stockscan.ui.components

// Lector reutilizable: CameraX entrega fotogramas, ML Kit decodifica y onCodigo devuelve el texto a EscanearView.

import android.util.Size
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/** Cámara optimizada con CameraX y ML Kit. Solo procesa códigos; nunca guarda imágenes. */
@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun CamaraCodigoBarras(onCodigo: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val onCodigoActualizado by rememberUpdatedState(onCodigo)

    /** Evita devolver más de un código durante la misma apertura de cámara. */
    val capturado = remember(lifecycleOwner) { AtomicBoolean(false) }
    /** Impide analizar varios fotogramas simultáneamente. */
    val procesando = remember(lifecycleOwner) { AtomicBoolean(false) }
    val executorAnalisis = remember(lifecycleOwner) { Executors.newSingleThreadExecutor() }

    /** Limita ML Kit a los formatos de códigos usados por StockScan. */
    val opcionesScanner = remember(lifecycleOwner) {
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_ITF
            )
            .build()
    }
    val scanner = remember(lifecycleOwner) {
        BarcodeScanning.getClient(opcionesScanner)
    }

    /** Vista Android que muestra en Compose la imagen en directo de CameraX. */
    val previewView = remember(context) {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        }
    }

    var camaraActiva by remember(lifecycleOwner) { mutableStateOf<Camera?>(null) }
    var tieneLinterna by remember(lifecycleOwner) { mutableStateOf(false) }
    var linternaEncendida by remember(lifecycleOwner) { mutableStateOf(false) }
    var mensajeEstado by remember(lifecycleOwner) {
        mutableStateOf("Centra el QR o código de barras dentro del marco.")
    }

    /** Vincula cámara y análisis al ciclo de vida; al salir libera cámara, scanner y executor. */
    DisposableEffect(lifecycleOwner) {
        val descartado = AtomicBoolean(false)
        val futuroProveedor = ProcessCameraProvider.getInstance(context)

        var proveedor: ProcessCameraProvider? = null
        var preview: Preview? = null
        var analisis: ImageAnalysis? = null
        var camara: Camera? = null

        futuroProveedor.addListener({
            if (descartado.get()) return@addListener

            try {
                proveedor = futuroProveedor.get()

                val previewLocal = Preview.Builder()
                    .build()
                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                /** Configura el análisis y conserva el fotograma más reciente para reducir retrasos. */
                val analisisLocal = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                /** Envía cada fotograma a ML Kit y devuelve el código detectado mediante onCodigo. */
                analisisLocal.setAnalyzer(executorAnalisis) { frame ->
                    val imagen = frame.image

                    // Evita acumular fotogramas mientras ML Kit procesa el anterior.
                    if (
                        imagen == null ||
                        capturado.get() ||
                        !procesando.compareAndSet(false, true)
                    ) {
                        frame.close()
                        return@setAnalyzer
                    }

                    try {
                        scanner.process(
                            InputImage.fromMediaImage(
                                imagen,
                                frame.imageInfo.rotationDegrees
                            )
                        )
                            .addOnSuccessListener { codigos ->
                                val valor = codigos
                                    .firstOrNull { !it.rawValue.isNullOrBlank() }
                                    ?.rawValue
                                    ?.trim()

                                if (
                                    !valor.isNullOrEmpty() &&
                                    capturado.compareAndSet(false, true)
                                ) {
                                    onCodigoActualizado(valor)
                                }
                            }
                            .addOnCompleteListener {
                                frame.close()
                                procesando.set(false)
                            }
                    } catch (_: Exception) {
                        frame.close()
                        procesando.set(false)
                    }
                }

                preview = previewLocal
                analisis = analisisLocal

                /** Conecta vista previa y análisis con la cámara trasera del dispositivo. */
                camara = proveedor?.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    previewLocal,
                    analisisLocal
                )

                camaraActiva = camara
                tieneLinterna = camara?.cameraInfo?.hasFlashUnit() == true
                linternaEncendida = false

                // Enfoque automático inicial en el centro del visor.
                val camaraEnfocar = camara
                previewView.post {
                    if (
                        !descartado.get() &&
                        camaraEnfocar != null &&
                        previewView.width > 0 &&
                        previewView.height > 0
                    ) {
                        val punto = previewView.meteringPointFactory.createPoint(
                            previewView.width / 2f,
                            previewView.height / 2f
                        )
                        val accion = FocusMeteringAction.Builder(punto)
                            .setAutoCancelDuration(3, TimeUnit.SECONDS)
                            .build()
                        camaraEnfocar.cameraControl.startFocusAndMetering(accion)
                    }
                }
            } catch (_: Exception) {
                mensajeEstado = "No se pudo iniciar la cámara. Puedes ingresar el código manualmente."
            }
        }, ContextCompat.getMainExecutor(context))

        /** Detiene el análisis y libera los recursos al cerrar el lector. */
        onDispose {
            descartado.set(true)
            analisis?.clearAnalyzer()

            val previewAlCerrar = preview
            val analisisAlCerrar = analisis
            if (previewAlCerrar != null && analisisAlCerrar != null) {
                proveedor?.unbind(previewAlCerrar, analisisAlCerrar)
            }

            camara?.cameraControl?.enableTorch(false)
            camaraActiva = null
            scanner.close()
            executorAnalisis.shutdown()
        }
    }

    /** Interfaz del visor, marco de lectura, estado del escáner y control de linterna. */
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(camaraActiva) {
                        detectTapGestures { posicion ->
                            val camara = camaraActiva
                                ?: return@detectTapGestures

                            val punto = previewView.meteringPointFactory.createPoint(
                                posicion.x,
                                posicion.y
                            )
                            val accion = FocusMeteringAction.Builder(punto)
                                .setAutoCancelDuration(3, TimeUnit.SECONDS)
                                .build()

                            camara.cameraControl.startFocusAndMetering(accion)
                        }
                    }
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(12.dp),
                color = Color.Black.copy(alpha = 0.55f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Alinea el código dentro del marco",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(width = 232.dp, height = 154.dp)
                    .border(
                        width = 2.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(14.dp)
                    )
            )

            if (tieneLinterna) {
                Button(
                    onClick = {
                        val nuevoEstado = !linternaEncendida
                        camaraActiva?.cameraControl?.enableTorch(nuevoEstado)
                        linternaEncendida = nuevoEstado
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Text(if (linternaEncendida) "Apagar linterna" else "Linterna")
                }
            }
        }

        Text(
            text = mensajeEstado,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
