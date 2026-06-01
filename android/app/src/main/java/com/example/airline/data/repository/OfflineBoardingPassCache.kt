package com.example.airline.data.repository

import com.example.airline.data.model.BoardingPassModel

object OfflineBoardingPassCache {
    private val _passes = mutableListOf<BoardingPassModel>()

    fun save(pass: BoardingPassModel) {
        if (_passes.none { it.barcode == pass.barcode }) {
            _passes.add(0, pass)
        }
    }

    fun getAll(): List<BoardingPassModel> = _passes.toList()
}
