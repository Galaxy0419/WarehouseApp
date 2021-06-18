package com.demo.warehouseinventoryapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WarehouseDao {
    @get:Query("select * from warehouse_items")
    val allItems: LiveData<List<WarehouseItem>>

    @Query("select * from warehouse_items where name=:name")
    fun getItem(name: String?): List<WarehouseItem?>?

    @Insert
    fun addItem(item: WarehouseItem?)

    @Query("delete from warehouse_items where item_id=:index")
    fun delItem(index: Int)

    @Query("delete from warehouse_items")
    fun deleteAll()
}
