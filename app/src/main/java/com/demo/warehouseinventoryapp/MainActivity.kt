package com.demo.warehouseinventoryapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.demo.warehouseinventoryapp.SMSReceiver.ReceiverCallbacks
import com.demo.warehouseinventoryapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import java.util.*

/* Constants */
const val TOUCH_VERTICAL_TOLERANCE = 64.0f
private val PERMISSIONS = arrayOf(
    Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS)

class MainActivity : AppCompatActivity() {
    /* View Bindings */
    private lateinit var binding: ActivityMainBinding

    /* Broadcast Receiver */
    private var smsHandler = SMSHandler()
    private val smsReceiver = SMSReceiver(smsHandler)

    /* Shared Preference Editor */
    private lateinit var sharedPrefEditor: SharedPreferences.Editor

    /* Touch Controls */
    private var touchDownX = 0f
    private var touchDownY = 0f

    /* Warehouse Database */
    private lateinit var warehouseViewModelProvider: WarehouseViewModel

    /* Variables */
    private var isFrozen = false

    /* SMS Receiver Callback */
    inner class SMSHandler : ReceiverCallbacks {
        override fun onSMSReceived(msg: String) {
            val strToken = StringTokenizer(msg, ";")
            binding.etItemName.setText(strToken.nextToken())
            binding.etQuantity.setText(strToken.nextToken())
            binding.etCost.setText(strToken.nextToken())
            binding.etDescription.setText(strToken.nextToken())
            binding.tbFrozenItem.isChecked = (strToken.nextToken() == "true")
        }
    }

    /* Navigation Listener */
    inner class NavListener : NavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            binding.drawerLayoutMain.closeDrawer(GravityCompat.START)
            return onOptionsItemSelected(item)
        }
    }

    /* Permission Request Callback */
    override fun onRequestPermissionsResult(requestCode: Int,
            permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.contains(PackageManager.PERMISSION_DENIED))
            requestPermissions(PERMISSIONS, 0)
    }

    /* Lifecycle Callbacks */
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Initialize View Bindings */
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        /* Check and Request Permissions */
        for (perm in PERMISSIONS) {
            if (checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(PERMISSIONS, 0)
        }

        /* Set Toolbar */
        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(
                this, binding.drawerLayoutMain, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayoutMain.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(NavListener())
        
        /* Shared Preference */
        val sharedPerf = getSharedPreferences(getString(R.string.shared_preference_key), Context.MODE_PRIVATE)
        sharedPrefEditor = sharedPerf.edit()

        /* Restore Previous Shared Preference */
        binding.etItemName.setText(sharedPerf.getString(getString(R.string.item_name), null))
        binding.etQuantity.setText(sharedPerf.getString(getString(R.string.quantity), getString(R.string.quantity_default)))
        binding.etCost.setText(sharedPerf.getString(getString(R.string.cost), getString(R.string.cost_default)))
        binding.etDescription.setText(sharedPerf.getString(getString(R.string.description), null))
        binding.tbFrozenItem.isChecked = sharedPerf.getBoolean(getString(R.string.frozen_item), false).also { isFrozen = it }

        /* Create Database */
        warehouseViewModelProvider = ViewModelProvider(this).get(WarehouseViewModel::class.java)
        binding.activityMainConlayout.setOnTouchListener(OnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    touchDownX = event.x
                    touchDownY = event.y
                    true
                }
                MotionEvent.ACTION_UP -> {
                    /* Right to Left */if (event.x - touchDownX < 0
                            && event.y - touchDownY < TOUCH_VERTICAL_TOLERANCE) {
                        clearItems(v)
                        /* Left to Right */
                    } else if (event.x - touchDownX > 0
                            && event.y - touchDownY < TOUCH_VERTICAL_TOLERANCE) {
                        addItem(v)
                    }
                    true
                }
                else -> false
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(smsReceiver, intentFilter)
    }

    override fun onSaveInstanceState(inState: Bundle) {
        super.onSaveInstanceState(inState)
        inState.putBoolean(getString(R.string.frozen_item), isFrozen)
    }

    override fun onRestoreInstanceState(outState: Bundle) {
        super.onRestoreInstanceState(outState)
        isFrozen = outState.getBoolean(getString(R.string.frozen_item), isFrozen)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsReceiver)
    }

    fun toggleFrozen(@Suppress("UNUSED_PARAMETER")view: View?) {
        binding.tbFrozenItem.isChecked = !isFrozen.also { isFrozen = it }
    }


    /* Menu Overridden Methods */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (val itemId = item.itemId) {
            R.id.mi_add -> addItem(findViewById(itemId))
            R.id.mi_clear -> clearItems(findViewById(itemId))
            R.id.mi_list -> startActivity(Intent(this@MainActivity, ListItemActivity::class.java))
            else -> return true
        }
        return true
    }

    /* View Methods */
    fun addItem(@Suppress("UNUSED_PARAMETER")view: View?) {
        Snackbar.make(
                binding.coordLayoutMain, String.format(getString(R.string.item_added), binding.etItemName.text),
                Snackbar.LENGTH_LONG
        ).show()
        sharedPrefEditor.putString(getString(R.string.item_name), binding.etItemName.text.toString())
        sharedPrefEditor.putString(getString(R.string.quantity), binding.etQuantity.text.toString())
        sharedPrefEditor.putString(getString(R.string.cost), binding.etCost.text.toString())
        sharedPrefEditor.putString(getString(R.string.description), binding.etDescription.text.toString())
        sharedPrefEditor.putBoolean(getString(R.string.frozen_item), isFrozen)
        sharedPrefEditor.apply()
        warehouseViewModelProvider.add(
                WarehouseItem(
                        binding.etItemName.text.toString(), binding.etQuantity.text.toString().toInt(), binding.etCost.text.toString().toFloat(),
                        binding.etDescription.text.toString(),
                        isFrozen
                )
        )
    }

    fun clearItems(@Suppress("UNUSED_PARAMETER")view: View?) {
        binding.etItemName.text = null
        binding.etQuantity.setText(getString(R.string.quantity_default))
        binding.etCost.setText(getString(R.string.cost_default))
        binding.etDescription.text = null
        if (isFrozen) binding.tbFrozenItem.isChecked = false.also { isFrozen = it }
        sharedPrefEditor.clear()
        sharedPrefEditor.apply()
        warehouseViewModelProvider.deleteAll()
    }
}
