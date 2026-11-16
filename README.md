# StockScan · Grupo 6

**Proyecto Android Studio (Kotlin / Jetpack Compose), basado en los ejemplos de clases del profesor y en la documentación del caso DSY1105.**

> **ESTADO: PROTOTIPO FRONTEND DEMO.** El inventario se administra en memoria durante la sesión. No incluye DAO, Room, base de datos, exportación CSV ni archivos Excel.

## Abrir y ejecutar

1. Descomprime el ZIP y selecciona la carpeta **`StockScan`** en Android Studio > **Open**.
2. Deja que Gradle sincronice. La máquina debe tener instalado el SDK requerido por el proyecto y acceso para resolver las dependencias de AndroidX, CameraX y ML Kit.
3. Ejecuta el módulo **app** en un teléfono Android con cámara o en un emulador; la cámara requiere un dispositivo con entrada disponible.
4. Perfiles de prueba: `OP-007` (operador), `EB-001` (bodega), `GE-001` (gerencial); contraseña **`Demo-2026`** para los tres.
5. Elige una tienda (T-01 Norte, T-02 Centro o T-03 Sur). T-04 Reserva empieza deshabilitada.

## Flujos de prueba recomendados

**Operador:**
- Accede con `OP-007` / `Demo-2026`, elige T-01 y consulta `2015`.
- Stock inicial P-001 en T-01: 8 u. Ejecuta **Entrada 12**; el resultado local es 20 u. y genera un movimiento.
- Inicia un conteo. Fija P-001=18, P-002=24, P-003=42. Revisa las dos diferencias (−2 y +2), confirma y observa historial/alertas.
- El botón **Leer con la cámara** usa CameraX y ML Kit. Alternativa: introducir uno de los códigos de barras de prueba.

**Encargado de bodega:**
- Accede con `EB-001`; consulta stock y alertas de la tienda, declara reposición con motivo; esa acción no suma existencias. Puede resolver alertas manuales.

**Gerencial:**
- Accede con `GE-001`; revisa resumen por tiendas, administra estado de tienda, crea/edita catálogo y consulta historial.
- La pantalla **Reportes** presenta indicadores dentro de la aplicación; no crea ni descarga archivos.

## Organización basada en las clases del profesor

- `MainActivity.kt`: `NavHost`, `composable` y `rememberNavController`.
- `modelo/Destinos.kt`: rutas de navegación.
- `model/StockScanModel.kt`: `data class` y estado de pantalla.
- `viewmodel/StockScanViewModel.kt`: `ViewModel`, `MutableStateFlow`, `StateFlow`, `asStateFlow()` y `_uiState.update { it.copy(...) }`.
- `ui/view/*.kt`: `@Composable`, formularios, botones y `collectAsStateWithLifecycle`.
- `model/InventarioReglas.kt`: validaciones simples y separadas para poder probarlas sin Android.
- `ui/view/CamaraCodigoBarras.kt`: excepción justificada por el requisito de lectura de códigos de barras; está aislada del resto.

## Alcance y limitaciones

- No hay DAO, Room, base de datos local ni persistencia. Tiendas, productos, existencias, movimientos y alertas se mantienen en memoria.
- Al cerrar la aplicación, los cambios de la sesión se restablecen a los datos de demostración.
- No hay backend, API ni sincronización entre dispositivos.
- No hay exportación Excel/XLSX ni CSV. La pantalla Reportes solo muestra indicadores en la aplicación.
- Los datos son ficticios y no representan inventario de una organización real.
- La cámara requiere prueba en un dispositivo Android; un emulador sin cámara puede no permitir validar ese flujo.

## Datos de demostración

- T-01: 72 u. inicialmente, T-02: 126 u., T-03: 90 u. ⇒ 288 u. en total.
- T-04 no tiene stock y comienza deshabilitada.
- P-001 2015 · P-002 2016 · P-003 2017.
- Registros y alertas se conservan solo mientras la aplicación permanezca abierta.

## Antes de la entrega

Revisar la pauta oficial para confirmar si la persistencia de datos o la exportación de reportes son requisitos obligatorios. En esta versión ambas funciones están excluidas por decisión de alcance.
