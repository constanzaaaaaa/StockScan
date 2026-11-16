package com.example.stockscan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stockscan.repository.StockScanRepository

class StockScanViewModelFactory(
    private val repository: StockScanRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockScanViewModel::class.java)) {
            return StockScanViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
