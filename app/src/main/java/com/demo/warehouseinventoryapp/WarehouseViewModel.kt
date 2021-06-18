package com.demo.warehouseinventoryapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class WarehouseViewModel(app: Application) : AndroidViewModel(app) {
    private val repository: WarehouseRepository = WarehouseRepository(app)
    val allItems: LiveData<List<WarehouseItem>> = repository.allItems

    fun add(item: WarehouseItem?) {
        repository.add(item)
    }

    fun delete(index: Int) {
        repository.delete(index)
    }

    fun deleteAll() {
        repository.deleteAll()
    }
}
