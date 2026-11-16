package com.example.stockscan

import com.example.stockscan.model.DatosDemo
import com.example.stockscan.model.FormularioProductoModel
import org.junit.Assert.*
import org.junit.Test

class FormularioProductoModelTest {
    @Test fun validaFormularioCorrecto() {
        val f = FormularioProductoModel(nombre = "Lápiz azul", barras = "1234567890123",
            categoria = "Papelería", minimo = "0")
        assertTrue(f.validar(DatosDemo.productos, "").esValido())
    }
    @Test fun rechazaCodigoDuplicado() {
        val f = FormularioProductoModel(nombre = "Lápiz azul", barras = "2015",
            categoria = "Papelería", minimo = "10")
        assertNotNull(f.validar(DatosDemo.productos, "").errorBarras)
    }
    @Test fun permiteEditarElMismoCodigo() {
        val f = FormularioProductoModel(nombre = "Botella nueva", barras = "2015",
            categoria = "Accesorios", minimo = "10")
        assertTrue(f.validar(DatosDemo.productos, "P-001").esValido())
    }
    @Test fun rechazaStockMinimoInvalido() {
        val f = FormularioProductoModel(nombre = "Lápiz azul", barras = "1234567890123",
            categoria = "Papelería", minimo = "-2")
        assertNotNull(f.validar(DatosDemo.productos, "").errorMinimo)
    }
}
