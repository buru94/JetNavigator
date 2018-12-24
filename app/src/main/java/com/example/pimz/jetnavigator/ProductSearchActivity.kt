package com.example.pimz.jetnavigator

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.PointMobile.PMSyncService.BluetoothChatService
import com.PointMobile.PMSyncService.SendCommand
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product_search.*
import kotlinx.android.synthetic.main.activity_stock_arrange.*
import kr.co.ezapps.ezsmarty.Data
import kr.co.ezapps.ezsmarty.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.net.URLEncoder
import java.util.HashMap

class ProductSearchActivity : BaseActivity() {


    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_search)

        var value = intent.extras.get("title")
        title = "상품관리 > " + value


        SendCommand.SendCommandInit(mChatService, mHandler)

        PRODUCT_SEARCH_ACTIVITY_SEARCH_BTN.setOnClickListener {
            var mBarcode = PRODUCT_SEARCH_ACTIVITY_BARCODE_EDITTEXT.text.toString()
            doPostGetProductInfo(mBarcode)
        }

        PRODUCT_SEARCH_ACTIVITY_LOC_LOG_BTN.setOnClickListener {
            var LOC_intent = Intent(this, LOCLogActivity::class.java)
            LOC_intent.putExtra("mProductId", mProduct_id)
            startActivity(LOC_intent)
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
                    var Origin= data!!.get("origin").toString().replace("\"", "")
                    var Category =data!!.get("str_category").toString().replace("\"", "")
                    /*
                    data!!.get("img_desc4").toString().replace("\"","")
                    data!!.get("supply_code").toString().replace("\"","")
                    */
                    //STOCK_ARRANGE_CURRENT_STOCK_EDITTEXT.setText(CurrentStock)

                    mProduct_id = product_id


                    PRODUCT_SEARCH_ACTIVITY_PRODUCT_NAME_TEXTVIEW.setText(name)
                    PRODUCT_SEARCH_ACTIVITY_PRODUCT_NAME_TEXTVIEW.isSelected = true

                    PRODUCT_SEARCH_ACTIVITY_PRODUCT_ID_TEXTVIEW.setText(product_id)

                    PRODUCT_SEARCH_ACTIVITY_PRODUCT_BARCODE_TEXTVIEW.setText(barcode)
                    PRODUCT_SEARCH_ACTIVITY_PRODUCT_LOC_TEXTVIEW.setText(location)
                    PRODUCT_SEARCH_ACTIVITY_LOC_LOG_EDITTEXT.setText(location)
                    PRODUCT_SEARCH_ACTIVITY_PRODUCT_OPTION_TEXTVIEW.setText(options)
                    PRODUCT_SEARCH_ACTIVITY_PRODUCT_OPTION_TEXTVIEW.isSelected = true


                    PRODUCT_SEARCH_ACTIVITY_PRODUCT_SUPPLY_TEXTVIEW.setText(SupplyName)
                    PRODUCT_SEARCH_ACTIVITY_PRODUCT_ORIGIN_TEXTVIEW.setText(Origin)
                    PRODUCT_SEARCH_ACTIVITY_PRODUCT_CATEGORY_TEXTVIEW.setText(Category)
                    Picasso.get().load(ImagePath).fit()
                        .into(PRODUCT_SEARCH_ACTIVITY_PRODUCT_IMAGEVIEW, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                PRODUCT_SEARCH_ACTIVITY_PRODUCT_NO_IMAGE.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {
                                PRODUCT_SEARCH_ACTIVITY_PRODUCT_NO_IMAGE.visibility = View.VISIBLE
                            }
                        })

                }
                progressOFF()
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {}
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
                MESSAGE_BARCODE -> {
                    Log.d("asda", "asdasdasdasd")
                    val BarcodeBuff = msg.obj as ByteArray
                    var Barcode = ""
                    Barcode = String(BarcodeBuff, 0, msg.arg1)
                    if (Barcode.length != 0) {
                        PRODUCT_SEARCH_ACTIVITY_BARCODE_EDITTEXT.setText(Barcode)
                        doPostGetProductInfo(Barcode)
                    }
                }
            }
        }
    }

    companion object {
        val D = true
        var mProduct_id:String? = null
    }

}