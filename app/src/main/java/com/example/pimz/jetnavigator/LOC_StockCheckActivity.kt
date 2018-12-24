package com.example.pimz.jetnavigator

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.PointMobile.PMSyncService.BluetoothChatService
import com.PointMobile.PMSyncService.SendCommand
import kotlinx.android.synthetic.main.sheet_listview.*

class LOC_StockCheckActivity : BaseActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sheet_listview)

        var value = intent.extras.get("title")
        title = "LOC재고관리 > " + value

        val item = Array(20,{ i -> "$i + list" })


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

    val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_BARCODE -> {
                    Log.d("asda", "asdasdasdasd")
                    val BarcodeBuff = msg.obj as ByteArray
                    var Barcode = ""
                    Barcode = String(BarcodeBuff, 0, msg.arg1)
                    if (Barcode.length != 0) {
                        //   textview.setText(Barcode);
                    }
                }
            }
        }
    }

    companion object {
        val D = true
    }

}