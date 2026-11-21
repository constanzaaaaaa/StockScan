# StockScan: arquitectura Room + Repository

La aplicación utiliza **MVVM + Repository + Room**. Esta guía describe el flujo y dónde encontrar cada parte.

## Carpetas principales

- `MainActivity.kt`: punto de entrada; crea las dependencias de la aplicación.
- `data/AppDatabase.kt`: configura Room, los conversores y la instancia local. El nombre de la base de datos es **`stockscan_database`**.
- `data/StockScanDAO.kt`: consultas de Room. Solo lo utiliza `RoomStockScanRepository`.
- `model/StockScanModel.kt`: entidades de inventario, estado de pantalla y datos derivados.
- `model/InventarioReglas.kt`: reglas de entradas, salidas y ajustes.
- `model/FormularioProductoModel.kt`: estado y validaciones del formulario de producto.
- `repository/StockScanRepository.kt`: contrato de persistencia que conoce el ViewModel.
- `repository/RoomStockScanRepository.kt`: implementación que realiza las operaciones con Room y agrupa escrituras en transacciones.
- `repository/FotoProductoRepository.kt`: guarda y recupera fotografías en archivos privados del dispositivo; no usa Room.
- `viewmodel/StockScanViewModel.kt`: mantiene el estado y coordina las acciones de la aplicación.
- `viewmodel/StockScanViewModelFactory.kt`: entrega el Repository al ViewModel.
- `navigation/Destinos.kt`: nombres de las rutas.
- `navigation/StockScanNavHost.kt`: conecta las rutas con sus pantallas.
- `ui/components/`: componentes compartidos y lector de códigos.
- `ui/screens/`: pantallas agrupadas por función.
- `ui/theme/Theme.kt`: tema Material 3 y colores de StockScan.

## Flujo principal

1. `MainActivity` obtiene la instancia de `AppDatabase`.
2. Crea `RoomStockScanRepository` y lo entrega a `StockScanViewModelFactory`.
3. La fábrica crea `StockScanViewModel`, que depende de la interfaz `StockScanRepository`.
4. `StockScanNavHost` registra las rutas de `Destinos` y muestra las pantallas.
5. Las pantallas consultan el estado y envían acciones al ViewModel.
6. El ViewModel aplica las reglas del modelo y solicita guardar los cambios al Repository.
7. `RoomStockScanRepository` utiliza `StockScanDAO` y `AppDatabase` para persistir los datos.

**Importante:** las pantallas y el ViewModel no utilizan directamente el DAO. El DAO es un componente interno necesario para que Room ejecute sus consultas.

## Datos persistidos

Se guardan tiendas, productos, existencias, movimientos y alertas. El conteo que está en curso permanece temporal y no se recupera después de cerrar la aplicación.

Los datos de demostración se insertan una sola vez cuando las tablas de tiendas y productos están vacías.

## Fotografías de productos

`GestionProductoView` selecciona una imagen con el selector nativo de Android y usa `FotoProductoRepository` para copiarla a `filesDir/fotos_productos`. `DetalleView` recupera la fotografía por el código interno del producto. Las fotos se almacenan como archivos privados, no como registros de Room.

## Alcance

Room proporciona persistencia local en el dispositivo. Esta integración no incorpora sincronización remota ni autenticación real. Las operaciones de inventario siguen sujetas a las validaciones existentes en `InventarioReglas` y `StockScanViewModel`.
