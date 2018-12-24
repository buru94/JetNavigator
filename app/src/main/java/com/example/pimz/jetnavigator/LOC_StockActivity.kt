package com.example.pimz.jetnavigator

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import com.PointMobile.PMSyncService.BluetoothChatService
import com.PointMobile.PMSyncService.SendCommand
import kotlinx.android.synthetic.main.activity_loc_stock_in.*
import kr.co.ezapps.ezsmarty.Data
import kr.co.ezapps.ezsmarty.Data_array
import kr.co.ezapps.ezsmarty.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException


class LOC_StockActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loc_stock_in)

        SendCommand.SendCommandInit(mChatService, mHandler)

        mTitle = intent.extras.get("title").toString()

        if(mTitle == "LOC_STOCK_IN_ORDER" ||mTitle == "LOC_STOCK_OUT_ORDER" ) {
            mSeq = intent.extras.get("seq").toString()
            doPostGetLocationWorkSheetDetail(mSeq!!)
        }

        STOCK_LOC_STOCK_IN_SEARCH_BTN.setOnClickListener {
            var mBarcode = STOCK_LOC_STOCK_IN_BARCODE_EDITTEXT.text.toString()
            doPostGetPeriodProduct(mBarcode)
        }


    }

    private fun doPostGetPeriodProduct(mBarcode: String) {
        try {
            val mUrlAuthcode = Session.getInstance().authCode
            val mURL = "api/function.php"
            val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
            val mDomain = pref.getString("domain_save", "")
            val map = HashMap<String, String>()

            map["authcode"] = mUrlAuthcode
            map["barcode"] = mBarcode
            map["ACTION"] = URLFactory.GetPeriodProduct
            map["VERSION"] = "v1"
            map["DOMAIN"] = mDomain


            doGetPeriodProduct(map, mURL)

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
                progressOFF()
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                progressOFF()
                Log.e("에러에러",t.message)

            }
        })
    }

    private fun doPostGetLocationWorkSheetDetail(mSeq: String) {
        try {
            val mUrlAuthcode = Session.getInstance().authCode
            val mURL = "api/function.php"
            val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
            val mDomain = pref.getString("domain_save", "")
            val map = HashMap<String, String>()

            map["ACTION"] = URLFactory.GetLocWorkSheetDetail
            map["AUTHCODE"] = mUrlAuthcode
            map["VERSION"] = "v1"
            map["SHEET"] = mSeq
            map["DOMAIN"] = mDomain


            doGetLocationWorkSheetDetail(map, mURL)

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    private fun doGetLocationWorkSheetDetail(input: HashMap<String, String>, mUrl: String) {
        progressON(this, null)
        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, mUrl)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {

                var mData = response.body()!!.data
                var mList = mData!!.get("list").asJsonArray

                for(i in 0 until mList.size()){
                    var mObj = mList[i].asJsonObject

                    var mName = mObj.get("name")
                    var mOptions = mObj.get("options")
                    var mLocation = mObj.get("location")
                }

                progressOFF()
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                progressOFF()
                Log.e("에러", t.message)
            }
        })
    }

    override fun onResume() {
        // TODO Auto-generated method stub
        super.onResume()
        if (D) Log.e("TAG", "+++ ON RESUME +++")
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
        if (D) Log.e("TAG", "--- ON RESUME ---")
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
        private val D = true

        var mSeq:String? = null
        var mTitle:String? = null
    }

}