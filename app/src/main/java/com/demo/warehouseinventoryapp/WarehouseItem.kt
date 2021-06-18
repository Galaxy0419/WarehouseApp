package com.demo.warehouseinventoryapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val TABLE_NAME = "warehouse_items"

@Entity(tableName = TABLE_NAME)
class WarehouseItem(
        @field:ColumnInfo(name = "name") var name: String,
        @field:ColumnInfo(name = "quantity") var quantity: Int,
        @field:ColumnInfo(name = "cost") var cost: Float,
        @field:ColumnInfo(name = "description") var description: String,
        @field:ColumnInfo(name = "is_frozen") var isFrozen: Boolean) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "item_id")
    var itemId = 0
}
