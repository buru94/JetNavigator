package com.example.pimz.jetnavigator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.PointMobile.PMSyncService.BluetoothChatService
import com.PointMobile.PMSyncService.SendCommand
import kotlinx.android.synthetic.main.activity_product_loc.*
import kr.co.ezapps.ezsmarty.Data
import kr.co.ezapps.ezsmarty.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.HashMap


class Product_LOC_Activity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_product_loc)

        SendCommand.SendCommandInit(mChatService, mHandler)

        GetTitle = intent.extras.get("title").toString()

        when (GetTitle) {
            "LOC_SET" ->{
                PRODUCT_LOC_ACTIVITY_LOC_ARRANGE_BTN.setText("지정")
                PRODUCT_LOC_ACTIVITY_LOC_SUB_TEXTVIEW.setText("*로케이션 없는 상품만 지정 가능")
            }
            "LOC_MOVE" -> {
                PRODUCT_LOC_ACTIVITY_LOC_ARRANGE_BTN.setText("이동")
                PRODUCT_LOC_ACTIVITY_LOC_SUB_TEXTVIEW.setText("*로케이션 있는 상품만 지정 가능")
            }
        }

        var value = intent.extras.get("title")
        title = "상품관리 > " + value

        PRODUCT_LOC_ACTIVITY_SEARCH_BTN.setOnClickListener {
            var mBarcode = PRODUCT_LOC_ACTIVITY_BARCODE_EDITTEXT.text.toString()
            doPostProductInfo(mBarcode)
        }

        PRODUCT_LOC_ACTIVITY_LOC_LOG_BTN.setOnClickListener {
            var Log_intent = Intent(this, LOCLogActivity::class.java)
            if (mProduct_id == null) {
                Toast.makeText(applicationContext, "상품정보가 없습니다", Toast.LENGTH_SHORT).show()
            } else {
                Log_intent.putExtra("mProductId", mProduct_id)
                startActivity(Log_intent)
            }
        }

        PRODUCT_LOC_ACTIVITY_LOC_ARRANGE_BTN.setOnClickListener {
            var mLocation = PRODUCT_LOC_ACTIVITY_LOC_ARRANGE_EDITTEXT.text.toString()
            if (mProduct_id == null)
                Toast.makeText(applicationContext, "지정된 상품이 없습니다", Toast.LENGTH_SHORT).show()
            else {
                when (GetTitle) {
                    "LOC_SET" -> doPostProductLocSet(mProduct_id.toString(), mLocation)
                    "LOC_MOVE" -> PRODUCT_LOC_ACTIVITY_LOC_ARRANGE_BTN.setText("이동")
                }

            }
        }

    }

    private fun doPostProductInfo(mBarcode: String) {
        try {

            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
            val mUrlBarcode = URLEncoder.encode(mBarcode, "UTF-8")
            val mURL = "api/function.php"
            val map = HashMap<String, String>()

            map["ACTION"] = URLFactory.GetProductInfo
            map["AUTHCODE"] = mUrlAuthcode
            map["VERSION"] = "v1"
            map["BARCODE"] = mUrlBarcode

            doGetProductInfo(map, mURL)


        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    private fun doGetProductInfo(input: HashMap<String, String>, mURL: String) {
        progressON(this, null)
        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, mURL)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                var data = response.body()!!.data
                var mErrorCode = response.body()!!.errorcode
                if (mErrorCode == 5001)
                    doPlayAudio("error_barcode_error")
                else if (mErrorCode == 0) {
                    var ImagePath = data!!.get("img_500").toString().replace("\"", "")
                    var name = data!!.get("name").toString().replace("\"", "")
                    var options = data!!.get("options").toString().replace("\"", "")
                    var barcode = data!!.get("barcode").toString().replace("\"", "")
                    var product_id = data!!.get("product_id").toString().replace("\"", "")
                    var location = data!!.get("location").toString().replace("\"", "")
                    var CurrentStock = data!!.get("stock").toString().replace("\"", "")
                    var SupplyName = data!!.get("supply_name").toString().replace("\"", "")
                    var Origin = data!!.get("origin").toString().replace("\"", "")
                    var Category = data!!.get("str_category").toString().replace("\"", "")
                    /*
                    data!!.get("img_desc4").toString().replace("\"","")
                    data!!.get("supply_code").toString().replace("\"","")
                    */

                    if(GetTitle == "LOC_MOVE") {
                        PRODUCT_LOC_ACTIVITY_LOC_ARRANGE_BTN.isEnabled = location != null
                    }else if(GetTitle == "LOC_SET"){
                        PRODUCT_LOC_ACTIVITY_LOC_ARRANGE_BTN.isEnabled = location == null
                    }

                    mProduct_id = product_id


                    PRODUCT_LOC_ACTIVITY_PRODUCT_NAME_TEXTVIEW.setText(name)
                    PRODUCT_LOC_ACTIVITY_PRODUCT_NAME_TEXTVIEW.isSelected = true

                    PRODUCT_LOC_ACTIVITY_PRODUCT_ID_TEXTVIEW.setText(product_id)

                    PRODUCT_LOC_ACTIVITY_PRODUCT_BARCODE_TEXTVIEW.setText(barcode)

                    PRODUCT_LOC_ACTIVITY_LOC_ARRANGE_EDITTEXT.setText(location)
                    PRODUCT_LOC_ACTIVITY_PRODUCT_LOC_TEXTVIEW.setText(location)

                    PRODUCT_LOC_ACTIVITY_PRODUCT_OPTION_TEXTVIEW.setText(options)
                    PRODUCT_LOC_ACTIVITY_PRODUCT_OPTION_TEXTVIEW.isSelected = true


                    PRODUCT_LOC_ACTIVITY_PRODUCT_SUPPLY_TEXTVIEW.setText(SupplyName)

                    PRODUCT_LOC_ACTIVITY_PRODUCT_ORIGIN_TEXTVIEW.setText(Origin)

                    PRODUCT_LOC_ACTIVITY_PRODUCT_CATEGORY_TEXTVIEW.setText(Category)
                }
                progressOFF()
            }
            override fun onFailure(call: Call<Data>, t: Throwable) {}
        })
    }

    private fun doPostProductLocSet(mProductId: String, mLocation: String) {
        try {
            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
            val mUrlProductId = URLEncoder.encode(mProductId, "UTF-8")
            val mUrlWorker = Session.getInstance().userName
            val mUrlLocation = URLEncoder.encode(mLocation, "UTF-8")
            val mUUID = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)

            val mToken = "LOCATION=$mUrlLocation|PRODUCTID=$mUrlProductId|WORKER=$mUrlWorker|UUID=$mUUID"

            val mURL = "api/function.php"
            val map = HashMap<String, String>()

            map["ACTION"] = URLFactory.SetLocation
            map["AUTHCODE"] = mUrlAuthcode
            map["VERSION"] = "v1"
            map["TOKEN"] = mToken

            progressON(this, null)
            val retrofitService = retrofit.create(Service::class.java)
            val call = retrofitService.postData(map, mURL)
            call.enqueue(object : Callback<Data> {
                override fun onResponse(call: Call<Data>, response: Response<Data>) {
                    Toast.makeText(applicationContext,"이동이 완료되었습니다",Toast.LENGTH_SHORT)
                    progressOFF()
                }

                override fun onFailure(call: Call<Data>, t: Throwable) {
                    Log.e(TAG, t.message.toString())
                    Toast.makeText(applicationContext,"이동실패",Toast.LENGTH_SHORT)
                    progressOFF()
                }

            })


        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
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
        var mProduct_id: String? = null
        var GetTitle:String? = null
    }


}