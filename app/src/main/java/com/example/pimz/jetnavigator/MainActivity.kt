package com.example.pimz.jetnavigator

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.PointMobile.PMSyncService.BluetoothChatService
import com.PointMobile.PMSyncService.SendCommand
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // Handle navigation view item clicks here.
        val id = item.itemId
        var fragment: Fragment? = null

        if (id == R.id.nav_home) {
            fragment = StartFragment()
        }
        else if (id == R.id.nav_trans) {
            doPlayAudio("menu_trans")
            fragment = TransFragment()
            // Handle the camera action
        } else if (id == R.id.nav_stock) {
            doPlayAudio("menu_stock_manage")
            fragment = StockManageFragment()
        } else if (id == R.id.nav_loc_stock) {
            doPlayAudio("menu_loc_check")
            fragment = LOC_StockFragment()
        } else if (id == R.id.nav_product_manage) {
            doPlayAudio("menu_product_manage")
            fragment = ProductManageFragment()
        } else if (id == R.id.nav_quality_manage) {
            doPlayAudio("menu_setting")
            val intent = Intent(applicationContext, SettingsActivity::class.java);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            val String = "LOG_OUT"
            Dialog(String)
        }
        if (fragment != null) {
            val fragmentManager = supportFragmentManager
            val ft = fragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, fragment)
            ft.commit()
        }

        if (supportActionBar != null) {
            supportActionBar!!.title = title
        }


        val drawer: DrawerLayout = drawer_layout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(toolbar)



        var Domain = intent.extras.get("Domain")
        var Id = intent.extras.get("Id")

        nav_view.setNavigationItemSelectedListener(this)

        val nav_header = nav_view.getHeaderView(0)
        val nav_domain = nav_header.NAV_HEADER_MAIN_DOMAIN_TEXTVIEW
        val nav_id = nav_header.NAV_HEADER_MAIN_ADD_INFO_TEXTVIEW

        nav_id.text = "[" + Id.toString() + "]"
        nav_domain.text = Domain.toString()


        APP_BAR_MAIN_LABLE_TEXTVIEW.text = "EzSmarty"

        APP_BAR_MAIN_DOMAIN_TEXTVIEW.text = Domain.toString()
        APP_BAR_MAIN_ID_TEXTVIEW.text = "[" + Id.toString() + "]"


        val fab = fab as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }



        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            Toast.makeText(
                this, "BlueTooth is not available",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        val fragmentManager = supportFragmentManager
        val ft = fragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, StartFragment())
        ft.commit()

        FirstConnectWindowClear = true

        MAIN_ACTIVITY_SEARCH_PM3_BTN.setOnClickListener {
            doPlayAudio("msg_pm3_search")
            val serverIntent = Intent(this, DeviceListActivity::class.java)
            startActivityForResult(serverIntent, BT_REQUEST_CONNECT_DEVICE)
        }


        val drawer: DrawerLayout = drawer_layout
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView: NavigationView = nav_view
        navigationView.setNavigationItemSelectedListener(this)


    }

    fun Dialog(ID: String) {
        val customDialog = CustomDialogActivity(this)
        customDialog.callFunction(ID)
    }

    public override fun onStart() {
        // TODO Auto-generated method stub
        super.onStart()

        Log.e("MainActivity", "+++ ON START +++")
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter!!.isEnabled) {
            if(D)
            Log.e("MainActivity", "+++ mBluetoothAdapter.isEnabled +++")
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, BT_REQUEST_ENABLE)
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null)
                setupChat()
        }
    }


    private fun setupChat() {
        if (D)
            Log.e("MainActivity", "+++ ON SETUP CHAT +++")

        mChatService = BluetoothChatService(this, mHandler)

        SendCommand.SendCommandInit(mChatService, mHandler)

        LoadSelections()
Log.e("MainActivity", StrMACAdress.toString())
        if (StrMACAdress != null) {
            val pairedDevices = mBluetoothAdapter!!.bondedDevices
            if (pairedDevices.size != 0) {
               // AutoConnect()

            }
        }

    }


    val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {

                StockManageFragment.MESSAGE_BARCODE -> {

                    val BarcodeBuff = msg.obj as ByteArray

                    var Barcode= ""

                    Barcode = String(BarcodeBuff, 0, msg.arg1)
                    if (Barcode.length != 0) {
                        MAIN_ACTIVITY_BARCODE_SCAN_TEXT_VIEW.setText(Barcode)
                    }
                }
            }

        }
    }

    override fun onResume() {
        // TODO Auto-generated method stub
        super.onResume()
        if (D) Log.e(TAG, "+++ ON RESUME +++")
        SendCommand.SendCommandInit(mChatService, mHandler)
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {

            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.state == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start()
            }
        }
        if (D) Log.e(TAG, "--- ON RESUME ---")
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO Auto-generated method stub
        if (D)
            Log.e("MainActivity", "+++ onActivityResult +++$resultCode")

        when (requestCode) {
            BT_REQUEST_CONNECT_DEVICE ->
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    val address = data!!.extras!!.getString(
                        DeviceListActivity.EXTRA_DEVICE_ADDRESS
                    )
                    try {
                        val device = mBluetoothAdapter!!.getRemoteDevice(address)
                        // Attempt to connect to the device
                        mChatService!!.connect(device)
                    } catch (e: Exception) {
                    }

                    StrMACAdress = address
                }

        }
    }

    override fun onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy()
        Log.d("MainActivity", StrMACAdress.toString())
        if (mChatService != null) {
            if (StrMACAdress != null) {
                SaveSelections()
            }
            mChatService!!.stop()
        }

        if (D)
            Log.e("MainActivity", "--- ON DESTROY ---")
    }

    override fun onPause() {
        // TODO Auto-generated method stub
        super.onPause()
        if (D)
            Log.e("MainActivity", "--- ON PAUSE ---")
    }


    private fun LoadSelections() {
        Log.i("Test", "LoadSelections ")

        // if the selections were previously saved load them
        val settingsActivity = getSharedPreferences("MACAddress", Activity.MODE_PRIVATE)

        if (settingsActivity.contains("MACAddresssave")) {
            val savedItems = settingsActivity.getString("MACAddresssave", "")
            val splitData = savedItems!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            StrMACAdress = splitData[2]
        }
    }


    private fun SaveSelections() {
        // save the selections in the shared preference in private mode for the user
        val settingsActivity = getSharedPreferences("MACAddress", Activity.MODE_PRIVATE)
        val prefEditor = settingsActivity.edit()

        val savedItems = getSavedItems()

        prefEditor.putString("MACAddresssave", savedItems)

        prefEditor.commit()
    }

    private fun getSavedItems(): String {
        var savedItems = ""

        savedItems += "," + "MACAdress" + "," + StrMACAdress.toString()

        return savedItems
    }


    private fun AutoConnect() {
        showDialog(PROGRESS_DIALOG)
        progressAutoConnectThread = ProgressAutoConnectThread(mProgAutoConnHandler)
        progressAutoConnectThread!!.start()
    }

    internal var mProgAutoConnHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == PROGRESS_STOP) {
                progressDlg!!.dismiss()
                if (AutoConnect == false) {
                    AutoConnect = true
                    Toast.makeText(applicationContext, "Auto Connect Fail, Please Reconnect PM3!", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }


    public override fun onCreateDialog(id: Int): Dialog? {
        when (id) {
            PROGRESS_DIALOG -> {
                progressDlg = ProgressDialog(this)
                progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                progressDlg!!.setMessage("Auto Connect... Please wait!")
                return progressDlg
            }
        }

        return null
    }

    private inner class ProgressAutoConnectThread internal constructor(internal var mProgAutoConnHandler: Handler) :
        Thread() {

        @Synchronized
        override fun run() {
            Log.d("MainActivity", "FirstConnect")
            var i: Int
            val address = StrMACAdress
            val device = mBluetoothAdapter!!.getRemoteDevice(address)

            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
            }

            mChatService!!.connect(device)

            i = 0
            while (i < 6) {
                Log.d("MainActivity", "AutoConnect")
                try {
                    Thread.sleep(2000)
                } catch (e: InterruptedException) {
                }

                val a = mChatService!!.getState()
                if (a == 3) {
                    mProgAutoConnHandler.sendEmptyMessage(PROGRESS_STOP)
                    break
                } else {
                    if (i > 2) {
                        AutoConnect = false
                        mProgAutoConnHandler.sendEmptyMessage(PROGRESS_STOP)
                        break
                    }
                }
                i++
            }
        }
    }

    override fun onBackPressed() {
        val drawer: DrawerLayout = drawer_layout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            if (System.currentTimeMillis() - time >= 2000) run {
                Dialog("FINISH")

            } else if (System.currentTimeMillis() - time < 2000) {
                ActivityCompat.finishAffinity(this)
                System.runFinalization()
                System.exit(0)
            }

        }


    }


    companion object {
        // for Debugging
        val D = true

        var time = 0

        // Intent request codes, onActivityResult
        private val BT_REQUEST_CONNECT_DEVICE = 1
        private val BT_REQUEST_ENABLE = 2

        // Message types sent from the BluetoothChatService Handler

        val MESSAGE_BARCODE = 2

        val context = this
        // Local Bluetooth adapter
        //internal var mBluetoothAdapter: BluetoothAdapter? = null



        var FirstConnectWindowClear: Boolean? = null

        var StrMACAdress: String? = null

        var AutoConnect: Boolean = true


        private lateinit var progressDlg: ProgressDialog
        private lateinit var progressAutoConnectThread: ProgressAutoConnectThread

        private val PROGRESS_DIALOG = 1001
        private val PROGRESS_STOP = 600

    }
}





