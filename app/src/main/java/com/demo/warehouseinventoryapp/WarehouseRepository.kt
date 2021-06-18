package com.demo.warehouseinventoryapp

import android.app.Application
import androidx.lifecycle.LiveData

class WarehouseRepository(app: Application) {
    private val db: WarehouseDatabase = WarehouseDatabase.getDatabase(app)
    private val warehouseDao: WarehouseDao = db.itemDao()
    val allItems: LiveData<List<WarehouseItem>> = warehouseDao.allItems

    fun add(item: WarehouseItem?) {
        WarehouseDatabase.Companion.dataWriteExecutor.execute(Runnable { warehouseDao.addItem(item) })
    }

    fun delete(index: Int) {
        WarehouseDatabase.Companion.dataWriteExecutor.execute(Runnable { warehouseDao.delItem(index) })
    }

    fun deleteAll() {
        WarehouseDatabase.Companion.dataWriteExecutor.execute(Runnable { warehouseDao.deleteAll() })
    }
}
