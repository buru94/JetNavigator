package com.example.pimz.jetnavigator

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.PointMobile.PMSyncService.BluetoothChatService
import com.PointMobile.PMSyncService.SendCommand
import kr.co.ezapps.ezsmarty.Data
import kr.co.ezapps.ezsmarty.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.ArrayList


class LOC_StockOrderActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loc_stock_in)

        var value = intent.extras.get("title")
        title = "LOC입고지시"

        val item = Array(20, { i -> "$i + list" })

        //INVOICE_LISTVIEW.adapter = ListViewAdapter(this,item)


    }

    class LocStockOrderListViewAdapter(context: Context, item: ArrayList<ArrayList<Any>?>) : BaseAdapter() {
        private val mContext = context
        private val mItem = item

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var i = 0
            lateinit var viewHolder: ViewHolder
            var view = convertView


            if (view == null) {
                viewHolder = ViewHolder()
                view = LayoutInflater.from(mContext).inflate(R.layout.product_scan_list_item, parent, false)
                viewHolder.mImage = view.findViewById(R.id.TRANS_PRODUCT_SCAN_ACTIVITY_LIST_VIEW_ITEM_IMAGEVIEW)
                viewHolder.mPrname = view.findViewById(R.id.TRANS_PRODUCT_SCAN_ACTIVITY_LIST_VIEW_ITEM_PRNAME_TEXTVIEW)
                viewHolder.mQty = view.findViewById(R.id.TRANS_PRODUCT_SCAN_ACTIVITY_LIST_VIEW_ITEM_QTY_TEXTVIEW)
                viewHolder.mScan = view.findViewById(R.id.TRANS_PRODUCT_SCAN_ACTIVITY_LIST_VIEW_ITEM_SCAN_TEXTVIEW)
                viewHolder.mOption = view.findViewById(R.id.TRANS_PRODUCT_SCAN_ACTIVITY_LIST_VIEW_ITEM_OPTION_TEXTVIEW)
                viewHolder.mBack = view.findViewById(R.id.TRANS_PRODUCT_SCAN_ACTIVITY_LIST_VIEW_ITEM)
                view.tag = viewHolder




                return view
            } else {
                viewHolder = view.tag as ViewHolder
            }

            return view
        }

        override fun getItem(position: Int) = mItem[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = mItem.size


        inner class ViewHolder {
            lateinit var mImage: ImageView
            lateinit var mPrname: TextView
            lateinit var mBarcode: TextView
            lateinit var mOption: TextView
            lateinit var mScan: TextView
            lateinit var mQty: TextView
            lateinit var button: Button
            lateinit var mBack: RelativeLayout
        }

    }

    private fun doPostGetPeriodProduct() {
        try {
            val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
            var mUrlDomain = pref.getString("domain_save", "")
            val mURL = "api/function.php"
            val map = HashMap<String, String>()


            map["ACTION"] = URLFactory.GetConductSheetList
            map["AUTHCODE"] = mUrlAuthcode
            map["VERSION"] = "v1"
            map["DOMAIN"] = mUrlDomain
//            doGetConductSheet(map, mURL)

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    private fun doGetPeriodProduct(input: HashMap<String, String>, mUrl: String) {
        progressON(this, null)
        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, mUrl)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {

            }

            override fun onFailure(call: Call<Data>, t: Throwable) {

            }
        })

    }

    private fun doPostGetLocWorkSheetDetail(){
        try {
            val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
            var mUrlDomain = pref.getString("domain_save", "")
            val mURL = "api/function.php"
            val map = HashMap<String, String>()


            map["ACTION"] = URLFactory.GetConductSheetList
            map["AUTHCODE"] = mUrlAuthcode
            map["VERSION"] = "v1"
            map["DOMAIN"] = mUrlDomain
//            doGetConductSheet(map, mURL)

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    private fun doGetLocWorkSheetDetail(input:HashMap<String, String>, mUrl:String){
        progressON(this, null)
        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, mUrl)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
            }
            override fun onFailure(call: Call<Data>, t: Throwable) {
            }
        })

    }

    override fun onResume() {
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