package com.example.stockscan.viewmodel

// Fábrica de ViewModel: inyecta StockScanRepository para que ViewModelProvider cree StockScanViewModel.

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stockscan.repository.StockScanRepository

/** Entrega al ViewModel la dependencia Repository creada en MainActivity. */
class StockScanViewModelFactory(
    private val repository: StockScanRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    /** Crea el ViewModel solicitado o informa si el tipo no está soportado. */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockScanViewModel::class.java)) {
            return StockScanViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
