package com.example.pimz.jetnavigator

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.AppBarLayout
import android.view.View
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_product.*
import java.lang.Exception
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import com.PointMobile.PMSyncService.BluetoothChatService
import com.PointMobile.PMSyncService.SendCommand
import kotlinx.android.synthetic.main.activity_trans.*
import kr.co.ezapps.ezsmarty.Data
import kr.co.ezapps.ezsmarty.Service
import retrofit2.Call
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


class DetailProductActivity : BaseActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)



        var mImageUrl = intent.extras.get("mImageUrl") as String
        var mProduct_id = intent.extras.get("mProductId") as String

        doGetDeatilPost(mProduct_id)
        doGetImage(mImageUrl, mEnable_sale.toString())




        DETAIL_PRODUCT_ACTIVITY_SWIFE_REFRESH_LAYOUT.setOnRefreshListener(object :
            SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                val intent = getIntent()
                finish()
                startActivity(intent)
                DETAIL_PRODUCT_ACTIVITY_SWIFE_REFRESH_LAYOUT.isRefreshing = false
            }
        })

        DETAIL_PRODUCT_ACTIVITY_APP_BAR_LAYOUT.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { p0, p1 ->
            DETAIL_PRODUCT_ACTIVITY_SWIFE_REFRESH_LAYOUT.isEnabled = p1 == 0
        })

        DETAIL_PRODUCT_ACTIVITY_LISTVIEW.setOnTouchListener { v, event ->
            DETAIL_PRODUCT_ACTIVITY_SCROLLVIEW.requestDisallowInterceptTouchEvent(true)
            false
        }
    }

    fun doGetImage(mImageUrl: String, mEnable_sale: String) {
        Picasso.get().load(mImageUrl).fit().into(DETAIL_PRODUCT_MAIN_IMAGEVIEW, object : Callback {
            override fun onSuccess() {
                Picasso.get().load(mImageUrl).fit().into(DETAIL_PRODUCT_MAIN_IMAGEVIEW)
                DETAIL_PRODUCT_ACTIVITY_SOLDOUT_TEXTVIEW.visibility = View.GONE
                DETAIL_PRODUCT_MAIN_IMAGEVIEW.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.MULTIPLY);

                if (mEnable_sale == "0") {
                    DETAIL_PRODUCT_ACTIVITY_SOLDOUT_TEXTVIEW.visibility = View.VISIBLE
                    DETAIL_PRODUCT_MAIN_IMAGEVIEW.setColorFilter(Color.parseColor("#6E6E6E"), PorterDuff.Mode.MULTIPLY);
                }

            }

            override fun onError(e: Exception?) {
                Picasso.get().load(R.drawable.ezadmin_title).resize(100, 100).into(DETAIL_PRODUCT_MAIN_IMAGEVIEW)
                DETAIL_PRODUCT_ACTIVITY_SOLDOUT_TEXTVIEW.visibility = View.GONE
                DETAIL_PRODUCT_MAIN_IMAGEVIEW.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.MULTIPLY);

                if (mEnable_sale == "0") {
                    DETAIL_PRODUCT_ACTIVITY_SOLDOUT_TEXTVIEW.visibility = View.VISIBLE
                    DETAIL_PRODUCT_MAIN_IMAGEVIEW.setColorFilter(Color.parseColor("#6E6E6E"), PorterDuff.Mode.MULTIPLY);
                }

            }

        })
        //Picasso.get().load(mImageUrl).error(R.drawable.ezadmin_title_logo).fit().into(DETAIL_PRODUCT_MAIN_IMAGEVIEW)
    }

    private fun doGetDeatilPost(PrId: String) {
        progressON(this, "Loading...")
        try {
            val mProductId = PrId
            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
            val mUrlProductId = URLEncoder.encode(mProductId, "UTF-8")
            val mURL = "api/function.php"
            val map = HashMap<String, String>()




            map["ACTION"] = URLFactory.GetProductDetail
            map["AUTHCODE"] = mUrlAuthcode
            map["VERSION"] = "v1"
            map["PRODUCT_ID"] = mUrlProductId

            doGetProductDetail(map, mURL)

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

    }

    fun doGetProductDetail(input: HashMap<String, String>, Url: String) {

        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, Url)
        call.enqueue(object : retrofit2.Callback<Data> {

            override fun onResponse(call: Call<Data>, response: Response<Data>) {

                val mProduct = response.body()!!.productInfo.toString()

                val parser = JsonParser()
                val mProductInfo: JsonObject? = parser.parse(mProduct) as JsonObject?
                val mOptionsArray: JsonArray = mProductInfo!!.get("options") as JsonArray
                var enable_sale = mProductInfo.get("enable_sale").toString().replace("\"", "")
                var product_id =mProductInfo.get("product_id").toString().replace("\"", "")
                var product_name = mProductInfo.get("name").toString().replace("\"", "")
                var link_id = mProductInfo.get("link_id").toString().replace("\"", "")
                var origin = mProductInfo.get("origin").toString().replace("\"", "")
                var supply_name = mProductInfo.get("supply_name").toString().replace("\"", "")
                var location =mProductInfo.get("location").toString().replace("\"", "")
                var maker = mProductInfo.get("maker").toString().replace("\"", "")
                var tags = mProductInfo.get("tags").toString().replace("\"", "")
                var category = mProductInfo.get("str_category").toString().replace("\"", "")
                var shop_price = toNumFormat(mProductInfo.get("shop_price").toString().replace("\"", ""))
                var reg_date = mProductInfo.get("reg_date").toString().replace("\"", "")

                mEnable_sale = enable_sale

                DETAIL_PRODUCT_ACTIVITY_PRODUCT_ID_TEXTVIEW.text = product_id
                DETAIL_PRODUCT_ACTIVITY_PRODUCT_NAME_TEXTVIEW.text =product_name
                DETAIL_PRODUCT_ACTIVITY_PRODUCT_NAME_TEXTVIEW.isSelected = true
                DETAIL_PRODUCT_ACTIVITY_PRODUCT_NAME.text = product_name
                DETAIL_PRODUCT_ACTIVITY_PRODUCT_LINK_TEXTVIEW.text =link_id
                DETAIL_PRODUCT_ACTIVITY_PRODUCT_ORIGIN_TEXTVIEW.text =origin
                DETAIL_PRODUCT_ACTIVITY_PRODUCT_SUPPLY_TEXTVIEW.text = supply_name
                DETAIL_PRODUCT_ACTIVITY_PRODUCT_LOC_TEXTVIEW.text = location
                DETAIL_PRODUCT_ACTIVITY_PRODUCT_MADE_TEXTVIEW.text = maker
                DETAIL_PRODUCT_ACTIVITY_PRODUCT_TAG_TEXTVIEW.text = tags
                DETAIL_PRODUCT_ACTIVITY_PRODUCT_CATEGORY_TEXTVIEW.text = category
                DETAIL_PRODUCT_ACTIVITY_PRODUCT_NAME.text = product_name
                DETAIL_PRODUCT_ACTIVITY_PRODUCT_PRICE.text =  shop_price + "원"
                DETAIL_PRODUCT_ACTIVITY_PRODUCT_DATE.text = reg_date


                var item: ArrayList<ArrayList<Any>?> = ArrayList()
                for (i in 0 until mOptionsArray.size()) {
                    var mOptionsObj: JsonObject = mOptionsArray.get(i).asJsonObject

                    var array: ArrayList<Any> = arrayListOf(
                        mOptionsObj.get("barcode").toString().replace("\"", "")
                        , mOptionsObj.get("enable_sale").toString().replace("\"", "")
                        , mOptionsObj.get("link_id").toString().replace("\"", "")
                        , mOptionsObj.get("options").toString().replace("\"", "")
                        , mOptionsObj.get("product_id").toString().replace("\"", "")
                        , mOptionsObj.get("reg_date").toString().replace("\"", "")
                        , mOptionsObj.get("stock").toString().replace("\"", "")
                        , mOptionsObj.get("use_temp_soldout").toString().replace("\"", "")
                    )

                    item.add(array)

                }
                DETAIL_PRODUCT_ACTIVITY_LISTVIEW.adapter = DetailProductListAdapter(applicationContext, item)

                progressOFF()
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                // 실패
                progressOFF()
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
        var mEnable_sale :String? = null

    }


}