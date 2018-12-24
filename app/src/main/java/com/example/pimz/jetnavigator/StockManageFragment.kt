package com.example.pimz.jetnavigator

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.view.*
import com.PointMobile.PMSyncService.BluetoothChatService
import com.PointMobile.PMSyncService.SendCommand
import kotlinx.android.synthetic.main.fragment_stock_manage.*

class StockManageFragment : BaseFragment() {


    val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {

                MESSAGE_BARCODE -> {

                    val BarcodeBuff = msg.obj as ByteArray

                    var Barcode = ""


                    Barcode = String(BarcodeBuff, 0, msg.arg1)
                    if (Barcode.length != 0) {
                        //  LIST_TEXT_VIEW.text = Barcode
                    }
                }
            }


        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_stock_manage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        STOCK_FRAGMENT_STOCK_IN_BTN.setOnClickListener {
            doPlayAudio("menu_stock_in")
            val intent = Intent(context, SheetListActivity::class.java)
            intent.putExtra("title", "STOCK_IN")
            startActivity(intent)
        }
        STOCK_FRAGMENT_STOCK_OUT_BTN.setOnClickListener {
            doPlayAudio("menu_stock_out")
            val intent = Intent(context, SheetListActivity::class.java)
            intent.putExtra("title", "STOCK_OUT")
            startActivity(intent)
        }
        STOCK_FRAGMENT_STOCK_CHECK_BTN.setOnClickListener {
            doPlayAudio("menu_stock_conduct")
            val intent = Intent(context, SheetListActivity::class.java)
            intent.putExtra("title", "STOCK_CHECK")
            startActivity(intent)
        }
        STOCK_FRAGMENT_STOCK_ARRANGE_BTN.setOnClickListener {
            doPlayAudio("menu_stock_arrage")
            val intent = Intent(context, StockArrangeActivity::class.java)
            intent.putExtra("title", "STOCK_ARRANGE")
            startActivity(intent)

        }
        STOCK_FRAGMENT_STOCK_LOG_BTN.setOnClickListener {
            doPlayAudio("menu_stock_log_seach")
            val intent = Intent(context, StockLogActivity::class.java)
            intent.putExtra("title", "STOCK_LOG")
            startActivity(intent)
        }
        if (Session.getInstance().is_ecn_use == 0) {
            STOCK_FRAGMENT_EZCHAIN_TRANS_BTN.visibility = View.GONE
            STOCK_FRAGMENT_EZCHAIN_TRANS_BTN.isEnabled = false
        } else {
            STOCK_FRAGMENT_EZCHAIN_TRANS_BTN.visibility = View.VISIBLE
            STOCK_FRAGMENT_EZCHAIN_TRANS_BTN.setOnClickListener {
                doPlayAudio("menu_ecn_trans")
                val intent = Intent(context, SheetListActivity::class.java)
                intent.putExtra("title", "EZCHAIN_TRANS")
                startActivity(intent)
            }
        }
    }


    companion object {
        val MESSAGE_BARCODE = 2
    }


}



