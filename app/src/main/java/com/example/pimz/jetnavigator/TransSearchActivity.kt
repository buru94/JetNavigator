package com.example.pimz.jetnavigator

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.Log
import com.PointMobile.PMSyncService.BluetoothChatService
import com.PointMobile.PMSyncService.SendCommand
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_trans.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kr.co.ezapps.ezsmarty.Data
import kr.co.ezapps.ezsmarty.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


class TransSearchActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trans)

        var value = intent.extras.get("title")
        title = "배송처리 > " + value

        SendCommand.SendCommandInit(mChatService, mHandler)





        TRANS_ACTIVITY_INVOICE_SEARCH_BTN.setOnClickListener {
            var mTransNo = TRANS_ACTIVITY_INVOICE_SEARCH_EDIT_TEXT.text.toString()
            doTransNoPost(mTransNo)
        }

        TRANS_ACTIVITY_RESET_BTN.setOnClickListener {
            doPlayAudio("msg_clear")
            TRANS_ACTIVITY_INVOICE_SEARCH_EDIT_TEXT.setText("")
            TRANS_ACTIVITY_RESULT_EDIT_TEXT.setHint("")
            item = ArrayList()
            TRANS_ACTIVITY_LIST_VIEW.adapter = null
        }

        TRANS_ACTIVITY_CS_LIST_BTN.setOnClickListener {
            var mTransNo = TRANS_ACTIVITY_INVOICE_SEARCH_EDIT_TEXT.text.toString()
            if (mTransNo == "") {
                doPlayAudio("error_trans_no_error")
                TRANS_ACTIVITY_RESULT_EDIT_TEXT.setHint("송장번호오류")
            } else
                doPostCsInfo(mTransNo)

        }

        TRANS_ACTIVITY_RESERVE_CANCEL_BTN.setOnClickListener {
            var mTransNo = TRANS_ACTIVITY_INVOICE_SEARCH_EDIT_TEXT.text.toString()
            if (mTransNo == "") {
                doPlayAudio("error_trans_no_error")
                TRANS_ACTIVITY_RESULT_EDIT_TEXT.setHint("송장번호오류")
            } else
                doHoldReleasePost(mTransNo)
        }


    }

    private fun doTransNoPost(mTransNo: String) {
        try {

            val mUUID = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
            val mUrlType = 0
            val mUrlUserName = URLEncoder.encode(Session.getInstance().userName, "UTF-8")
            val mUrlTransNo = URLEncoder.encode(mTransNo, "UTF-8")
            val mURL = "api/function.php"
            val map = HashMap<String, String>()


            val mToken = "TYPE=$mUrlType|TRANSNO=$mUrlTransNo|WORKER=$mUrlUserName|UUID=$mUUID"


            map["ACTION"] = URLFactory.GetTransNoInfo
            map["AUTHCODE"] = mUrlAuthcode
            map["VERSION"] = "v1"
            map["TOKEN"] = mToken
            doGetTransNo(map, mURL)

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    private fun doHoldReleasePost(mTransNo: String) {
        try {
            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
            val mUrlTransNo = URLEncoder.encode(mTransNo, "UTF-8")
            val mURL = "api/function.php"
            val map = HashMap<String, String>()



            map["ACTION"] = URLFactory.SetHoldRelease
            map["AUTHCODE"] = mUrlAuthcode
            map["VERSION"] = "v1"
            map["TRANSNO"] = mUrlTransNo
            doSetHoldRelease(map, mURL)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    fun doGetTransNo(input: HashMap<String, String>, Url: String) {


        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, Url)
        progressON(this, null)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                // 성공

                val TransProducts = response.body()!!.products
                var TransMessage = response.body()!!.message
                var TransError = response.body()!!.errorcode
                TRANS_ACTIVITY_RESULT_EDIT_TEXT.hint = TransMessage

                if (response.body()!!.errorcode == 3008) {
                    doPlayAudio("error_trans_no_error")
                } else if (TransProducts != null && TransProducts.size() != 0) {
                    when {
                        TransError == 3002 -> doPlayAudio("result_order_address_change")
                        TransError == 3003 -> doPlayAudio("result_order_hold")
                        TransError == 3006 -> doPlayAudio("result_order_hold")
                        TransError == 3007 -> doPlayAudio("result_already_trans_short")
                        TransError == 0 -> doPlayAudio("result_normal")
                        TransError == 3009 -> doPlayAudio("result_order_all_cancel")
                    }
                    item = ArrayList()
                    for (i in 0 until TransProducts!!.size()) {
                        var mTransObj: JsonObject = TransProducts[i].asJsonObject

                        var array: ArrayList<Any> = arrayListOf(
                            mTransObj.get("barcode").toString().replace("\"", "")
                            , mTransObj.get("enable_stock").toString().replace("\"", "")
                            , mTransObj.get("is_cancel").toString().replace("\"", "")
                            , mTransObj.get("is_change").toString().replace("\"", "")
                            , mTransObj.get("location").toString().replace("\"", "")
                            , mTransObj.get("options").toString().replace("\"", "")
                            , mTransObj.get("product_id").toString().replace("\"", "")
                            , mTransObj.get("product_name").toString().replace("\"", "")
                            , mTransObj.get("qty").toString().replace("\"", "")

                        )

                        item.add(array)

                    }
                    TRANS_ACTIVITY_LIST_VIEW.adapter = TransListViewAdapter(applicationContext, item)

                    progressOFF()
                } else if (response.body()!!.errorcode == 3007) {
                    doPlayAudio("result_already_trans_short")
                }

            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                // 실패
                Log.getStackTraceString(t)
            }
        })
    }

    fun doSetHoldRelease(input: HashMap<String, String>, Url: String) {
        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, Url)
        progressON(this,null)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                val HoldReleaseError = response.body()!!.errorcode
                val HoldReleaseMessage = response.body()!!.message
                when (HoldReleaseError) {
                    0 -> {
                        doPlayAudio("msg_hold_release_success")
                        TRANS_ACTIVITY_RESULT_EDIT_TEXT.setHint(HoldReleaseMessage)
                    }
                    6005 -> {
                        doPlayAudio("result_stock_modify_fail")
                        TRANS_ACTIVITY_RESULT_EDIT_TEXT.setHint(HoldReleaseMessage)
                    }
                }
            progressOFF()
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                // 실패
                Log.getStackTraceString(t)
            }
        })
    }


    fun doPostCsInfo(mTransNo: String) {
        try {
            val mUUID = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
            val mUrlTransNo = URLEncoder.encode(mTransNo, "UTF-8")
            val mURL = "api/function.php"
            val map = HashMap<String, String>()



            map["ACTION"] = URLFactory.GetCsInfo
            map["AUTHCODE"] = mUrlAuthcode
            map["VERSION"] = "v1"
            map["TRANSNO"] = mUrlTransNo
            doGetCsInfo(map, mURL)

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    fun doGetCsInfo(input: HashMap<String, String>, Url: String) {
        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, Url)
        progressON(this, null)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                // 성공
                val CsInfoError = response.body()!!.errorcode
                val CsInfoMessage = response.body()!!.message
                if (CsInfoError == 3008) {
                    doPlayAudio("error_trans_no_error")
                    TRANS_ACTIVITY_RESULT_EDIT_TEXT.setHint(CsInfoMessage)
                } else if (CsInfoError == 0) {
                    var mTransNo = TRANS_ACTIVITY_INVOICE_SEARCH_EDIT_TEXT.text.toString()
                    val intent = Intent(applicationContext, CsInfoActivity::class.java)
                    intent.putExtra("mTransNo", mTransNo)
                    startActivity(intent)
                }
            progressOFF()
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                // 실패
                Log.getStackTraceString(t)
            }
        })
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

                StockManageFragment.MESSAGE_BARCODE -> {

                    val BarcodeBuff = msg.obj as ByteArray

                    var Barcode = ""

                    Barcode = String(BarcodeBuff, 0, msg.arg1)
                    if (Barcode.length != 0) {
                        TRANS_ACTIVITY_INVOICE_SEARCH_EDIT_TEXT.setText(Barcode)
                        doTransNoPost(Barcode)
                    }
                }
            }

        }
    }


    companion object {
        private val D = true
        var item: ArrayList<ArrayList<Any>?> = ArrayList()
    }
}