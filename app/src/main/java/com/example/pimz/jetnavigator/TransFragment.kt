package com.example.pimz.jetnavigator



import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.PointMobile.PMSyncService.BluetoothChatService
import com.PointMobile.PMSyncService.SendCommand
import kotlinx.android.synthetic.main.fragment_trans.*


class TransFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trans, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        TRANS_FRAGMENT_NORMAL_TRANS_BTN.setOnClickListener{
            doPlayAudio("menu_trans_normal")
            val intent = Intent(context, TransActivity::class.java )
            intent.putExtra("title", "일반배송")
            startActivity(intent)


        }
        TRANS_FRAGMENT_SEARCH_BTN.setOnClickListener{
            doPlayAudio("menu_trans_search_only")
            val intent = Intent(context, TransSearchActivity::class.java )
            intent.putExtra("title", "조회전용")
            startActivity(intent)

        }
        TRANS_FRAGMENT_DIRECT_TRANS_BTN.setOnClickListener{
            doPlayAudio("menu_trans_force")
            val intent = Intent(context, TransForceActivity::class.java )
            intent.putExtra("title", "강제배송")
            startActivity(intent)
        }
        TRANS_FRAGMENT_PRODUCT_SCAN_BTN.setOnClickListener{
            doPlayAudio("menu_trans_product_each")
            val intent = Intent(context, TransProductScanActivity::class.java )
            intent.putExtra("title", "상품별스캔")
            startActivity(intent)
        }

    }

    val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {

                MESSAGE_BARCODE -> {

                    val BarcodeBuff = msg.obj as ByteArray

                    var Barcode: String?= null


                    Barcode = String(BarcodeBuff, 0, msg.arg1)
                    if (Barcode.length != 0) {
                        //detail_text.text = Barcode
                    }
                }
            }


        }
    }

    companion object {

        val MESSAGE_BARCODE = 2

        @JvmStatic
        fun newInstance() = TransFragment()
    }
}