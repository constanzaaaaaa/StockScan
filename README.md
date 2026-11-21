# StockScan · Grupo 6

**Proyecto Android Studio en Kotlin y Jetpack Compose**, basado en los ejemplos de clases y en la documentación del caso DSY1105.

> **Estado:** prototipo Android con persistencia local Room mediante Repository. Tiendas, productos, existencias, movimientos y alertas se guardan en el dispositivo. El conteo que está en curso sigue siendo temporal.

## Abrir y ejecutar

1. Clona el repositorio y abre la carpeta **StockScan** en Android Studio.
2. Deja que Gradle sincronice. Se requiere el SDK configurado por el proyecto y conexión para resolver dependencias AndroidX, Room, CameraX y ML Kit.
3. Ejecuta el módulo **app** en un teléfono Android con cámara o en un emulador.
4. Perfiles de prueba: `OP-007` (operador), `EB-001` (bodega), `GE-001` (gerencial); contraseña **`Demo-2026`** para los tres.
5. Elige una tienda (T-01 Norte, T-02 Centro o T-03 Sur). T-04 Reserva empieza deshabilitada.

## Estructura de carpetas y flujo

La carpeta principal del código está organizada por responsabilidad:

```text
com.example.stockscan/
├── MainActivity.kt
├── data/
│   ├── AppDatabase.kt
│   └── StockScanDAO.kt
├── model/
│   ├── StockScanModel.kt
│   ├── FormularioProductoModel.kt
│   └── InventarioReglas.kt
├── navigation/
│   ├── Destinos.kt
│   └── StockScanNavHost.kt
├── repository/
│   ├── StockScanRepository.kt
│   ├── RoomStockScanRepository.kt
│   └── FotoProductoRepository.kt
├── ui/
│   ├── components/
│   │   ├── ComponentesComunes.kt
│   │   └── CamaraCodigoBarras.kt
│   ├── screens/
│   │   ├── auth/
│   │   ├── home/
│   │   ├── inventory/
│   │   ├── products/
│   │   └── splash/
│   └── theme/
│       └── Theme.kt
└── viewmodel/
    ├── StockScanViewModel.kt
    └── StockScanViewModelFactory.kt
```

**Flujo principal:** `MainActivity` crea `AppDatabase`, `RoomStockScanRepository` y `StockScanViewModel`. Después llama a `StockScanNavHost`, que conecta `Destinos` con las pantallas. Las pantallas envían acciones al ViewModel; este aplica las reglas y utiliza `StockScanRepository` para persistir inventario con Room. El DAO queda interno a la implementación de Room.

**Fotografías:** `GestionProductoView` y `DetalleView` usan `FotoProductoRepository` para guardar y recuperar imágenes en el almacenamiento privado del dispositivo; esas imágenes no se guardan dentro de Room.

## Persistencia local

La aplicación utiliza **MVVM + Repository + Room**:

- `model/StockScanModel.kt`: estado de interfaz y modelos de inventario; las entidades Room están declaradas aquí y el conteo en curso es temporal.
- `data/AppDatabase.kt`: configura Room y los conversores. El nombre local de la base es `stockscan_database`.
- `data/StockScanDAO.kt`: adaptador interno requerido por Room; no se utiliza directamente desde ViewModel ni desde las pantallas.
- `repository/StockScanRepository.kt`: contrato de acceso a inventario.
- `repository/RoomStockScanRepository.kt`: implementación local que encapsula consultas y escrituras Room.
- `navigation/StockScanNavHost.kt` y `navigation/Destinos.kt`: registran las rutas y conectan cada pantalla.
- `ui/components/`: contiene componentes reutilizables y el lector de códigos.
- `ui/screens/`: agrupa pantallas por acceso, inicio, inventario, productos y splash.
- `viewmodel/StockScanViewModel.kt`: conserva el estado de interfaz y depende de Repository, no de Room ni del DAO.
- `viewmodel/StockScanViewModelFactory.kt`: inyección del repositorio al ViewModel.

Los datos de demostración se insertan una sola vez cuando la base de datos está vacía. Al cerrar y volver a abrir la aplicación, se recuperan tiendas, productos, existencias, movimientos y alertas. El conteo en curso no se restaura después de cerrar la app.

## Flujos de prueba recomendados

**Operador:**
- Accede con `OP-007` / `Demo-2026`, elige T-01 y consulta `2015`.
- Stock inicial P-001 en T-01: 8 u. Ejecuta **Entrada 12**; el resultado local es 20 u. y genera un movimiento.
- Inicia un conteo. Fija P-001=18, P-002=24, P-003=42. Revisa las diferencias, confirma y observa historial/alertas.
- El botón **Leer con la cámara** usa CameraX y ML Kit. Alternativa: introducir uno de los códigos de barras de prueba.

**Encargado de bodega:**
- Accede con `EB-001`; consulta stock y alertas de la tienda, declara reposición con motivo; esa acción no suma existencias. Puede resolver alertas manuales.

**Gerencial:**
- Accede con `GE-001`; revisa resumen por tiendas, administra estado de tienda, crea/edita catálogo y consulta historial.
- La pantalla **Reportes** presenta indicadores dentro de la aplicación; no crea ni descarga archivos.

## Alcance y limitaciones

- Room ofrece persistencia **local en el dispositivo**; no hay backend, API REST ni sincronización entre dispositivos.
- Los perfiles y contraseñas son ficticios para demostración; no representan autenticación de producción.
- No hay exportación Excel/XLSX ni CSV en esta versión. La pantalla Reportes muestra indicadores dentro de la aplicación.
- Los datos son ficticios y no representan inventario de una organización real.
- La cámara requiere prueba en un dispositivo Android; un emulador sin cámara puede no permitir validar ese flujo.
- El conteo que está en curso es temporal; al cerrar la aplicación se descarta.

## Datos de demostración

- T-01: 72 u. inicialmente, T-02: 126 u., T-03: 90 u. ⇒ 288 u. en total.
- T-04 no tiene stock y comienza deshabilitada.
- P-001 2015 · P-002 2016 · P-003 2017.
- Los cambios de inventario se conservan localmente mediante Room.

## Antes de la entrega

Verificar la compilación y probar los flujos en Android Studio y en un dispositivo real. Si el caso académico exige backend, sincronización o exportación, esas funciones deben desarrollarse por separado.
