# Matriz de cumplimiento — StockScan Grupo 6

Fuentes revisadas: **002_Formato_Caso_MultiTienda (3 págs.)**, **Propuesta Solucion StockScan Grupo 6 (3 págs., apartado 5)** y **mockup StockScan (1 pág.)**. Los siguientes son los requisitos que esos archivos documentan; esta matriz no reemplaza la pauta oficial de evaluación.

| Requisito del ERS | Implementación en esta versión | Evaluación verificable |
|---|---|---|
| RF01 — Acceso por roles de prueba | `LoginView`, `StockScanViewModel`, datos ficticios | Demo implementada |
| RF02 — Selección y cambio de tienda | `TiendasView`, bloqueo T-04 por defecto | Demo implementada |
| RF03 — Cámara + código manual | `EscanearView`, `CamaraCodigoBarras` con CameraX/ML Kit y alternativa manual | Código incorporado; falta prueba física de cámara |
| RF04 — Detalle producto y stock por tienda | `ProductosView`, `DetalleView` | Demo implementada |
| RF05 — Entradas, salidas y ajustes, confirmación y validaciones | `MovimientoView`, `InventarioReglas`; impide negativos; ajuste requiere motivo | Demo implementada y persistida localmente con Room/Repository; sin API |
| RF06 — Conteo con reloj, repetición y comparador | `ConteoView`, tiempo transcurrido, entrada por código y acumulador | Conteo en curso temporal; las diferencias confirmadas se guardan como stock, movimientos y alertas en Room |
| RF07 — Confirmar diferencias fija nuevo stock | `ResumenConteoView`, `confirmarConteo` crea movimientos solo para diferencias | Confirmación y resultados persistidos localmente mediante Repository + Room; sin API |
| RF08 — Alertas de mínimo, descalce y reposición | `AlertasView`, reglas y resolución manual | Alertas manuales persistidas en Room; alertas de mínimo calculadas desde existencias |
| RF09 — Gerencial: tiendas, productos e historial inmutable | `GestionView`, `GestionProductoView`, `HistorialView` | Crear/editar productos, habilitar/deshabilitar tiendas e historial persistidos localmente; sin backend |
| RF10 — Exportación Excel/CSV local | No se incluye exportación. `ReportesView` muestra indicadores dentro de la app | **No implementado en esta versión**, por decisión del alcance solicitado. Confirmar con el profesor si RF10 es obligatorio. |
| RNF01 — Kotlin / Compose / MVVM y backend REST Spring Boot | Android estructurado en ViewModel/Model/View + Repository + Room local | Android y persistencia local implementadas; **Spring Boot/Retrofit y persistencia REST pendientes** |
| RNF02 — Sin conexión bloqueada, sin modo offline | La demo funciona sin servidor como prototipo anterior a integración | **Pendiente**: la versión demo no cumple conexión obligatoria |
| RNF03 — Legibilidad, botones altos y navegación según rol | Tema contrastado, botones primarios ≥56 dp, barra inferior por rol | Implementación inicial; requiere prueba de accesibilidad |
| RNF04 — Privacidad y datos sintéticos | Códigos anónimos y productos ficticios; no fotos guardadas ni usuarios reales | Incorporado como criterio de desarrollo; revisión legal formal fuera de alcance |

## Restricciones adicionales del caso

- **Android Studio, Kotlin, MVVM, Jetpack Compose y GitHub:** proyecto organizado para Android Studio; GitHub aún no actualizado desde esta sesión por error 403 de integración.
- **Backend Spring Boot como fuente de verdad:** aún NO implementado. Room conserva datos localmente en el dispositivo, pero todavía no existe servidor ni sincronización remota.
- **Internet permanente / sincronización inmediata:** NO implementado. Debe bloquearse inventario y movimientos si falla la API.
- **Reporte Excel/CSV:** no se implementa en esta versión. La pantalla Reportes solo presenta indicadores en la aplicación.
- **Sin credenciales de producción ni ERPs:** cumple en esta versión (exclusivamente ficticio).
- **Historial UTC:** se registra con cadena UTC y se conserva localmente mediante Room; todavía no se sincroniza con un servidor.

## Qué falta antes de considerar cumplido el caso académico completo

1. Crear backend Spring Boot REST para tiendas, productos, stock, movimientos, conteos y alertas con persistencia real de los datos de prueba.
2. Conectar Android a ese backend vía Retrofit2; mostrar y bloquear al perder conexión, rechazar duplicados desde servidor, operar únicamente tras respuesta exitosa.
3. Probar cámara y permisos en Android real; probar sincronización y manejo de respuestas API.
4. Confirmar si se exige **generar `.xlsx` real** o basta la simulación definida en mockup.
5. Confirmar con el docente si RF10 (exportación Excel/CSV) es obligatorio; si lo es, deberá reincorporarse. Ejecutar Gradle/build y pruebas Android instrumentadas en Android Studio.
