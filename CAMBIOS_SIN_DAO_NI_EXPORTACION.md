# Cambios solicitados — StockScan

Se preparó esta versión quitando las funciones que no necesitas:

## Eliminado
- `data/StockScanDAO.kt` y `data/AppDatabase.kt`.
- Todas las anotaciones e imports de Room (`@Entity`, `@PrimaryKey` y dependencias).
- Configuración de Room y KSP en Gradle y en el catálogo de versiones.
- `model/ExportarCsv.kt`.
- La exportación CSV y la simulación de Excel en `ReportesView`.
- La prueba unitaria que verificaba la generación del CSV.
- El documento antiguo `ROOM_INTEGRACION.md`, que ya no corresponde a esta versión.

## Conservado
- Los modelos `Tienda`, `Producto`, `Existencia`, `Movimiento` y `Alerta` como `data class` normales.
- El `StockScanViewModel` con `ViewModel`, `MutableStateFlow`, `StateFlow` y `copy()`.
- La navegación, pantallas, formularios, cámara, inventario, conteos, alertas e historial.
- La pantalla **Reportes**, ahora como resumen de indicadores dentro de la app, sin crear archivos.

## Importante
- El inventario y los movimientos quedan solo en memoria: al cerrar la aplicación se restablecen los datos demo.
- RF10 de la matriz de pauta (exportación Excel/CSV) queda marcado como no implementado. Si el profesor lo exige, habrá que volver a incluirlo.
- No se modificó GitHub ni se hicieron commits. Este ZIP es una copia limpia del proyecto para abrir en Android Studio.
