# StockScan: Room con Repository

La aplicación utiliza MVVM + Repository + Room.

- `model/StockScanModel.kt`: modelos de dominio y entidades Room para tiendas, productos, existencias, movimientos y alertas.
- `data/AppDatabase.kt`: instancia Room y TypeConverters para los enums.
- `data/StockScanDAO.kt`: adaptador interno requerido por Room. No se utiliza desde el ViewModel ni desde las pantallas.
- `repository/StockScanRepository.kt`: contrato de acceso a los datos.
- `repository/RoomStockScanRepository.kt`: implementación que encapsula las consultas y escrituras Room y agrupa operaciones en transacciones.
- `viewmodel/StockScanViewModel.kt`: mantiene el estado de la interfaz y depende únicamente de StockScanRepository.
- `viewmodel/StockScanViewModelFactory.kt`: entrega el repositorio al ViewModel.

## Datos persistidos

Se guardan tiendas, productos, existencias, movimientos y alertas. El conteo que está en curso permanece temporal y no se recupera después de cerrar la aplicación, igual que en el ZIP de referencia.

Los datos de demostración se insertan una sola vez cuando las tablas de tiendas y productos están vacías.

## Alcance

Room proporciona persistencia local en el dispositivo. Esta integración no incorpora sincronización remota ni autenticación real. Las operaciones de inventario siguen sujetas a las validaciones existentes en InventarioReglas y StockScanViewModel.

## Nota sobre Room

Room requiere una interfaz anotada con `@Dao` para ejecutar consultas. En esta arquitectura queda encapsulada en la capa `data`; el resto de la aplicación utiliza exclusivamente el Repository.
