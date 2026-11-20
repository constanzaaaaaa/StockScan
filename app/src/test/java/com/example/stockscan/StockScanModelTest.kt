package com.example.stockscan

// Pruebas unitarias del modelo y reglas: verifican stock inicial, búsquedas, movimientos, alertas y conteo.

import com.example.stockscan.model.*
import org.junit.Assert.*
import org.junit.Test

/** Agrupa pruebas del modelo de inventario y de InventarioReglas. */
class StockScanModelTest {
    /** Verifica las cantidades ficticias cargadas para cada tienda. */
    @Test fun inventarioInicial() {
        val e = StockScanModel()
        assertEquals(72, e.totalUnidades("T-01"))
        assertEquals(126, e.totalUnidades("T-02"))
        assertEquals(90, e.totalUnidades("T-03"))
        assertEquals(288, e.tiendas.filter { it.habilitada }.sumOf { e.totalUnidades(it.id) })
        assertFalse(e.tiendas.single { it.id == "T-04" }.habilitada)
    }
    /** Comprueba que la búsqueda encuentre un producto por código de barras. */
    @Test fun busqueda() { assertEquals("P-001", StockScanModel(busqueda = "2015").productosFiltrados().single().codigoInterno) }
    /** Comprueba que una salida mayor al stock se rechace. */
    @Test fun salidaSinStock() {
        assertEquals("Stock insuficiente", InventarioReglas.validarMovimiento(8, TipoMovimiento.SALIDA, 9, ""))
        assertNull(InventarioReglas.calcularNuevoStock(8, TipoMovimiento.SALIDA, 9))
    }
    /** Verifica los cálculos de entrada, salida, conteo y ajuste. */
    @Test fun movimientos() {
        assertEquals(20, InventarioReglas.calcularNuevoStock(8, TipoMovimiento.ENTRADA, 12))
        assertEquals(3, InventarioReglas.calcularNuevoStock(8, TipoMovimiento.SALIDA, 5))
        assertEquals(18, InventarioReglas.calcularNuevoStock(20, TipoMovimiento.CONTEO, 18))
        assertEquals("El ajuste necesita un motivo", InventarioReglas.validarMovimiento(8, TipoMovimiento.AJUSTE, -2, ""))
        assertEquals(6, InventarioReglas.calcularNuevoStock(8, TipoMovimiento.AJUSTE, -2))
    }
    /** Comprueba que el stock mínimo genere alertas en las tiendas correspondientes. */
    @Test fun alertasAutomaticas() {
        val alertas = StockScanModel().alertasMinimo()
        assertEquals(2, alertas.size)
        assertTrue(alertas.any { it.tiendaId == "T-01" && it.productoId == "P-001" })
        assertTrue(alertas.any { it.tiendaId == "T-03" && it.productoId == "P-002" })
    }
    /** Comprueba el cálculo de diferencia entre stock inicial y cantidad contada. */
    @Test fun diferenciasSinCambios() {
        val e = StockScanModel(conteo = Conteo(14, "T-01", "UTC", stockInicial = mapOf("P-001" to 20), contados = mapOf("P-001" to 18)))
        assertEquals(-2, e.diferenciasConteo()["P-001"])
    }
}
