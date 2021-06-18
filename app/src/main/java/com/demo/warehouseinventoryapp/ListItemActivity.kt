package com.demo.warehouseinventoryapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.warehouseinventoryapp.RecyclerAdapter.OnCardDelete

class ListItemActivity : AppCompatActivity() {
    /* Recycler View */
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RecyclerAdapter

    /* Warehouse Database */
    private lateinit var warehouseViewModelProvider: WarehouseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_item)

        /* Get Data Source */
        warehouseViewModelProvider = ViewModelProvider(this).get(WarehouseViewModel::class.java)

        /* Recycler View */
        recyclerView = findViewById(R.id.activity_list_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = RecyclerAdapter(object : OnCardDelete {
            override fun onDelete(index: Int) {
                warehouseViewModelProvider.delete(index)
            }
        })
        recyclerView.adapter = recyclerAdapter

        /* Set Data Source */
        warehouseViewModelProvider.allItems.observe(this, Observer { newData: List<WarehouseItem> ->
            recyclerAdapter.setDataSource(newData)
            recyclerAdapter.notifyDataSetChanged()
        })
    }
}
