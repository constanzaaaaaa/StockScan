# StockScan — Actualización con nuevas clases del profesor

**Alcance acordado:** frontend Android (Kotlin + Jetpack Compose), demo con datos ficticios en memoria, sin backend. No se hizo ningún commit ni push.

## Qué se integró efectivamente

| Clase del profesor | Adaptación al frontend StockScan | Responsable |
|---|---|---|
| `ROOM_v3` / `EjemploROOM_V2`: `FormularioModel`, `copy()`, errores y validaciones de campos | `model/FormularioProductoModel.kt` + `GestionProductoView.kt` muestran errores por campo antes de guardar | Coni |
| `ConexionAPIRetrofit V3`: `PersonaScreenFiltro` con búsqueda y selección | Búsquedas y chips de categoría/stock en `ProductosView`, `TiendasView`, `GestionView`, `HistorialView` y `AlertasView` | Andrés, Vale, Coni |
| `ConexionAPIRetrofit V3`: edición en modal `AlertDialog` | Confirmación explícita antes de cambiar el estado de una tienda en `GestionView`; confirmación de movimientos conservada | Coni, Vale |
| `ROOM_v3`: fichas y formulario en Compose | Tarjetas de inventario y `OutlinedTextField` con `supportingText` | Todos |
| Propuesta Grupo 6 y mockup original | Perfiles Operador/Bodega/Gerencia, bloqueo de T-04, movimientos, inventario y alertas | Todos |

## Qué NO se incorporó (deliberadamente)

- `@Entity`, `@Dao`, `AppDatabase`, Room/KSP: se excluyen del proyecto porque no se requieren para el frontend solicitado. Se conserva el patrón de estado y validaciones del ejemplo, no su persistencia.
- `ApiService`, `RetrofitInstance`, repositorios HTTP y llamadas REST: no hay backend solicitado ni URL real de pruebas.
- `GeoMapbox` y `GeolocalizacionOpenStreet`: el ERS/mockup de StockScan no requiere GPS, un mapa de sucursales ni ubicación del usuario. Evitamos pedir permisos y añadir servicios de mapas sin necesidad.
- El `Propuesta.txt` nuevo menciona otros perfiles como cliente y reponedor; se mantienen los **tres perfiles del ERS Grupo 6**, para no modificar el alcance silenciosamente.
- `Manual_Portal_Azure_Evidencias.pdf` es material de entrega; no requiere cambios en el frontend.
- No se cambiaron dependencias Gradle, porque los componentes nuevos usan bibliotecas ya incluidas.

## Código por persona (sin archivos duplicados entre paquetes)

**Andrés**: LoginView, TiendasView, InicioView, EscanearView, DetalleView. Filtros de sucursal/bodega, campo de escaneo manual y presentación de stock.

**Vale**: ProductosView, MovimientoView, ConteoView, ResumenConteoView, HistorialView, ReportesView. Búsquedas, chips, validación en movimiento y filtros de auditoría. `ConteoView` y `ReportesView` se mantienen de la versión anterior porque ya seguían el flujo del mockup.

**Coni**: FormularioProductoModel (nuevo), Theme, ComunView, GestionProductoView, GestionView, AlertasView y ResumenView. Formularios validados, modal de confirmación, filtros de alertas y tablero de gerencia. Se incluye un test JUnit para el nuevo modelo.

## Cómo copiar a las ramas

1. En GitHub Desktop seleccionar **la rama correcta** antes de copiar: `rama_coni`, `rama-andres` o la rama de Vale que acuerden.
2. Descomprimir **solo el ZIP personal** y copiar la carpeta `app` a la raíz del repositorio `StockScan`, de forma que quede `StockScan/app/src/main/java/com/example/stockscan/...`. Reemplazar solo los archivos incluidos. **Nunca borrar `.git`.**
3. Abrir la carpeta raíz de StockScan en Android Studio; realizar sincronización Gradle, compilar y revisar pantallas.
4. Subir los cambios únicamente cuando el equipo los revise. **No se generaron commits ni pull requests desde ChatGPT.**

## Comprobaciones

- Compilación Kotlin/JVM de `StockScanModel.kt`, `InventarioReglas.kt` y `FormularioProductoModel.kt`: **correcta**.
- Pruebas manuales locales de validación de campos, código duplicado y salida superior al stock: **correctas**.
- Se verificó que los ZIP contienen rutas `app/...` con archivos Kotlin; al integrarlos, no se pisan archivos entre personas.
- **Pendiente:** compilación Android/Gradle, prueba de cámara y navegación en emulador o dispositivo; este entorno no tiene Android SDK.

> El frontend simula el inventario en memoria: se restablece al cerrar la aplicación. No se presenta como stock sincronizado en tiempo real.
