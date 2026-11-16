# StockScan — importación inicial

Repositorio privado de **StockScan**, proyecto Android Studio con Kotlin y Jetpack Compose.

## Importar el proyecto desde el ZIP

1. En la pestaña **Code**, selecciona **Add file → Upload files**.
2. Sube el archivo original `StockScan_sin_DAO_ni_Excel_CSV (2).zip` **sin descomprimirlo y sin cambiarle el nombre**, en la raíz de la rama `main`.
3. Presiona **Commit changes**.
4. En **Actions**, comprueba que terminó correctamente el flujo **Importar proyecto StockScan**.

El flujo verifica el SHA-256 del ZIP entregado, extrae los 62 archivos a la raíz del repositorio, elimina el ZIP de la versión activa y crea un commit con el proyecto Android. No ejecuta el código del ZIP.

Una vez importado, el propio `README.md` de StockScan reemplazará este aviso.

## Alcance del código suministrado

El paquete que se importará es un prototipo frontend con inventario en memoria, sin DAO, sin persistencia en base de datos y sin exportación Excel/CSV.
