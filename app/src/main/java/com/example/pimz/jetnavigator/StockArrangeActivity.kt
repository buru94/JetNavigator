package com.example.pimz.jetnavigator

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import com.PointMobile.PMSyncService.BluetoothChatService
import com.PointMobile.PMSyncService.SendCommand
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_stock_arrange.*
import kr.co.ezapps.ezsmarty.Data
import kr.co.ezapps.ezsmarty.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.net.URLEncoder
import java.util.*

class StockArrangeActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_arrange)
        var value = intent.extras.get("title").toString()
        when (value) {
            "STOCK_ARRANGE" -> title = "재고조정"
            "PRODUCT_SEARCH" -> title = "상품조회"
        }

        if (value == "PRODUCT_SEARCH") {
            STOCK_ARRANGE_ACTIVITY_STOCK_TEXTVIEW.setText("LOC")
            STOCK_ARRANGE_ACTIVITY_ARRANGE_BTN.setText("이력")
        }

        SendCommand.SendCommandInit(mChatService, mHandler)

        STOCK_ARRANGE_ACTIVITY_SEARCH_BTN.setOnClickListener {
            var mBarcode = STOCK_ARRANGE_ACTIVITY_BARCODE_EDITTEXT.text.toString()
            if (mBarcode == "") {
                doPlayAudio("error_barcode_error")
                Toast.makeText(this, "바코드 오류", Toast.LENGTH_SHORT).show()
            } else
                doPostGetProductInfo(mBarcode)
        }
        STOCK_ARRANGE_ACTIVITY_ARRANGE_BTN.setOnClickListener {
            if (value == "STOCK_ARRANGE") {
                var mStock = STOCK_ARRANGE_ACTIVITY_STOCK_EDITTEXT.text.toString()
                if (mStock == "") {
                    doPlayAudio("error_fail")
                    Toast.makeText(this, "오류", Toast.LENGTH_SHORT).show()
                } else
                doPostArrangeStock(mStock)
                STOCK_ARRANGE_ACTIVITY_STOCK_EDITTEXT.isFocusable = true
            } else {
                var LOC_intent = Intent(this, LOCLogActivity::class.java)
                LOC_intent.putExtra("mProductId", mProduct_id)
                startActivity(LOC_intent)
            }


        }


    }

    private fun doPostGetProductInfo(mBarcode: String) {
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


    private fun doPostArrangeStock(mStock: String) {
        try {
            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")

            val mURL = "api/function.php"
            val map = java.util.HashMap<String, String>()
            val mUsername = Session.getInstance().userName
            val mUuid = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
            val mToken = "PRODUCTID=$mProduct_id|STOCK=$mStock|WORKER=$mUsername|UUID=$mUuid"

            map["ACTION"] = URLFactory.ArrangeStock
            map["AUTHCODE"] = mUrlAuthcode
            map["VERSION"] = "v1"
            map["TOKEN"] = mToken

            progressON(this, null)
            val retrofitService = retrofit.create(Service::class.java)
            val call = retrofitService.postData(map, mURL)
            call.enqueue(object : Callback<Data> {
                override fun onResponse(call: Call<Data>, response: Response<Data>) {
                    progressOFF()
                    Toast.makeText(applicationContext, "완료되었습니다", Toast.LENGTH_SHORT).show()

                }

                override fun onFailure(call: Call<Data>, t: Throwable) {
                }
            })

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
                    //STOCK_ARRANGE_CURRENT_STOCK_EDITTEXT.setText(CurrentStock)

                    mProduct_id = product_id

                    STOCK_ARRANGE_ACTIVITY_PRODUCT_NAME_TEXTVIEW.setText(name)
                    STOCK_ARRANGE_ACTIVITY_PRODUCT_NAME_TEXTVIEW.isSelected = true

                    STOCK_ARRANGE_ACTIVITY_PRODUCT_ID_TEXTVIEW.setText(product_id)

                    STOCK_ARRANGE_ACTIVITY_PRODUCT_BARCODE_TEXTVIEW.setText(barcode)
                    STOCK_ARRANGE_ACTIVITY_PRODUCT_LOC_TEXTVIEW.setText(location)
                    STOCK_ARRANGE_ACTIVITY_PRODUCT_OPTION_TEXTVIEW.setText(options)
                    STOCK_ARRANGE_ACTIVITY_PRODUCT_OPTION_TEXTVIEW.isSelected = true


                    STOCK_ARRANGE_ACTIVITY_PRODUCT_SUPPLY_TEXTVIEW.setText(SupplyName)
                    STOCK_ARRANGE_ACTIVITY_STOCK_EDITTEXT.setText(CurrentStock)
                    STOCK_ARRANGE_ACTIVITY_PRODUCT_ORIGIN_TEXTVIEW.setText(Origin)
                    STOCK_ARRANGE_ACTIVITY_PRODUCT_CATEGORY_TEXTVIEW.setText(Category)
                    Picasso.get().load(ImagePath).fit()
                        .into(STOCK_ARRANGE_ACTIVITY_PRODUCT_IMAGEVIEW, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                STOCK_ARRANGE_ACTIVITY_PRODUCT_NO_IMAGE.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {
                                STOCK_ARRANGE_ACTIVITY_PRODUCT_NO_IMAGE.visibility = View.VISIBLE
                            }
                        })

                }
                progressOFF()
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {}
        })
    }

    private fun doReset() {
        val RE_intent = getIntent()
        finish()
        startActivity(RE_intent)
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
                        STOCK_ARRANGE_ACTIVITY_BARCODE_EDITTEXT.setText(Barcode)
                        doPostArrangeStock(Barcode)
                        STOCK_ARRANGE_ACTIVITY_STOCK_EDITTEXT.isFocusable = true
                        if (STOCK_ARRANGE_ACTIVITY_STOCK_EDITTEXT.isFocusable) {
                            var mStock = STOCK_ARRANGE_ACTIVITY_STOCK_EDITTEXT.text.toString()
                            doPostArrangeStock(mStock)
                        }
                    }
                }
            }
        }
    }

    companion object {
        val D = true
        var mProduct_id: String? = null
    }

}