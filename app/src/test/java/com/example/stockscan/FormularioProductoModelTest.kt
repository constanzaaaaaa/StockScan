package com.example.stockscan

// Pruebas unitarias del formulario: comprueban campos válidos, duplicados y stock mínimo.

import com.example.stockscan.model.DatosDemo
import com.example.stockscan.model.FormularioProductoModel
import org.junit.Assert.*
import org.junit.Test

/** Agrupa casos de prueba para FormularioProductoModel.validar(). */
class FormularioProductoModelTest {
    /** Comprueba que un formulario completo y válido no genere errores. */
    @Test fun validaFormularioCorrecto() {
        val f = FormularioProductoModel(nombre = "Lápiz azul", barras = "1234567890123",
            categoria = "Papelería", minimo = "0")
        assertTrue(f.validar(DatosDemo.productos, "").esValido())
    }
    /** Comprueba que no se pueda repetir el código de barras de otro producto. */
    @Test fun rechazaCodigoDuplicado() {
        val f = FormularioProductoModel(nombre = "Lápiz azul", barras = "2015",
            categoria = "Papelería", minimo = "10")
        assertNotNull(f.validar(DatosDemo.productos, "").errorBarras)
    }
    /** Comprueba que editar un producto conserve su propio código de barras. */
    @Test fun permiteEditarElMismoCodigo() {
        val f = FormularioProductoModel(nombre = "Botella nueva", barras = "2015",
            categoria = "Accesorios", minimo = "10")
        assertTrue(f.validar(DatosDemo.productos, "P-001").esValido())
    }
    /** Comprueba que el stock mínimo no acepte números negativos. */
    @Test fun rechazaStockMinimoInvalido() {
        val f = FormularioProductoModel(nombre = "Lápiz azul", barras = "1234567890123",
            categoria = "Papelería", minimo = "-2")
        assertNotNull(f.validar(DatosDemo.productos, "").errorMinimo)
    }
}
