package com.example.pimz.jetnavigator

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.PointMobile.PMSyncService.BluetoothChatService
import com.PointMobile.PMSyncService.SendCommand
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_stock_check.*
import kr.co.ezapps.ezsmarty.Data
import kr.co.ezapps.ezsmarty.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*


class StockCheckActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_check)
        item = ArrayList()
        Barcode_array = ArrayList()
        adapter = StockCheckListViewAdapter(this, item)

        SendCommand.SendCommandInit(mChatService, mHandler)

        mSeq = intent.extras.get("seq").toString()
        var value = intent.extras.get("title")

        when (value) {
            "STOCK_CHECK" -> value = "재고실사"
        }

        doPostConductDetail(mSeq!!)

        STOCK_CHECK_ACTIVITY_BARCODE_SEARCH_BTN.setOnClickListener {
            var mBarcode = STOCK_CHECK_ACTIVITY_BARCODE_EDITTEXT.text.toString().toLowerCase()
            var cnt = 0
                Log.d("COCNCOCNO",mBarcode.substring(0,1))
            if (mBarcode.substring(0, 1) == "#") {
                cnt = Integer.parseInt(mBarcode.substring(1, 4))
                Log.d("CNT", cnt.toString())
            }

        }

        STOCK_CHECK_ACTIVITY_RESET_BTN.setOnClickListener {
            doReset()
            adapter!!.notifyDataSetChanged()
        }
        STOCK_CHECK_ACTIVITY_COMPLETE_BTN.setOnClickListener {
            doPostSetConduct(mSeq!!)
        }


        title = "재고관리 > $value"
    }

    private fun doPostConductDetail(Seq: String) {
        try {
            val mUuid = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
            val mWorker = URLEncoder.encode(Session.getInstance().userName, "UTF-8")
            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
            val mSeq = URLEncoder.encode(Seq, "UTF-8")

            val mToken = "SEQ=$mSeq|WORKER=$mWorker|UUID=$mUuid"
            var map: HashMap<String, String> = HashMap()
            val mUrl = "api/function.php"

            map["ACTION"] = URLFactory.GetConductDetail
            map["AUTHCODE"] = mUrlAuthcode
            map["VERSION"] = "v1"
            map["TOKEN"] = mToken

            doGetConductDetail(map, mUrl)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    private fun doGetConductDetail(input: HashMap<String, String>, mUrl: String) {
        progressON(this, null)
        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, mUrl)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                var mErrorCode = response.body()!!.errorcode
                if (mErrorCode != 0) {
                    Toast.makeText(applicationContext, "전표 오류", Toast.LENGTH_SHORT).show()
                    progressOFF()
                } else if (mErrorCode == 0) {
                    var mData = response.body()!!.data
                    var mList = mData!!.get("list").asJsonArray
                    var mTotal = mData.get("total").asJsonObject

                    var mTotal_scan = mTotal.get("total_scan").toString().replace("\"", "")
                    var mTotal_stock = mTotal.get("total_stock").toString().replace("\"", "")

                    STOCK_CHECK_ACTIVITY_TOTAL_QTY_TEXTVIEW.setText(mTotal_stock)
                    STOCK_CHECK_ACTIVITY_TOTAL_SCAN_TEXTVIEW.setText(mTotal_scan)
                    var mList_array: ArrayList<Any>


                    for (i in 0 until mList.size()) {
                        var mObj = mList[i].asJsonObject

                        var mBarcode = mObj.get("barcode").toString().replace("\"", "")
                        var mImageUrl = mObj.get("img_500").toString().replace("\"", "")
                        var mName = mObj.get("name").toString().replace("\"", "")
                        var mOptions = mObj.get("options").toString().replace("\"", "")
                        var mProduct_id = mObj.get("product_id").toString().replace("\"", "")
                        var mScan = mObj.get("scan").toString().replace("\"", "")
                        var mStock = mObj.get("stock").toString().replace("\"", "")
                        var mMultiBarcode = mObj.get("multi_barcode").asJsonArray
                        var mSelected: Boolean = false


                        var mMulti_array: ArrayList<Any> = ArrayList()
                        for (i in 0 until mMultiBarcode.size()) {
                            var mMulti = mMultiBarcode[i].toString().replace("\"", "").toLowerCase()

                            mMulti_array.add(mMulti)
                        }


                        Barcode_array.add(mMulti_array)


                        mList_array =
                                arrayListOf(mBarcode, mName, mOptions, mProduct_id, mScan, mStock, mSelected, mImageUrl)

                        item.add(mList_array)
                    }

                    STOCK_CHECK_ACTIVITY_LISTVIEW.adapter = adapter
                    progressOFF()
                }
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                Log.e(TAG, t.message.toString())
                progressOFF()

            }
        })

    }

    private fun doPostGetProductInfo(mBarcode: String) {
        try {

            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
            val mUrlBarcode = URLEncoder.encode(mBarcode, "UTF-8")
            val mURL = "api/function.php"
            val map = java.util.HashMap<String, String>()

            map["ACTION"] = URLFactory.GetProductInfo
            map["AUTHCODE"] = mUrlAuthcode
            map["VERSION"] = "v1"
            map["BARCODE"] = mUrlBarcode

            doGetProductInfo(map, mURL)


        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    private fun doGetProductInfo(input: HashMap<String, String>, mUrl: String) {
        progressON(this, null)
        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, mUrl)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                var mData = response.body()!!.data
                var mErrorCode = response.body()!!.errorcode

                if (mErrorCode != 0) {
                    Toast.makeText(applicationContext, "전표 오류", Toast.LENGTH_SHORT).show()
                    progressOFF()
                } else if(mErrorCode == 0){
                    var mProduct_array: ArrayList<Any>
                    var mBarcode = mData!!.get("barcode").toString().replace("\"", "")
                    var mImage_500 = mData!!.get("img_500").toString().replace("\"", "")
                    var mOptions = mData!!.get("options").toString().replace("\"", "")
                    var mName = mData!!.get("name").toString().replace("\"", "")
                    var mProduct_id = mData!!.get("product_id").toString().replace("\"", "")
                    var mStock = mData!!.get("stock").toString().replace("\"", "")
                    var mMultiBarcode = mData!!.get("multi_barcode").asJsonArray
                    var mSelected: Boolean = true
                    var mMulti_array: ArrayList<Any> = ArrayList()

                    var mSize = Barcode_array.size
                    for (i in 0 until mMultiBarcode.size()) {
                        var mMulti = mMultiBarcode[i].toString().replace("\"", "").toLowerCase()
                        mMulti_array.add(mMulti)
                    }
                    Barcode_array.add(mMulti_array)




                    mProduct_array =
                            arrayListOf(mBarcode, mName, mOptions, mProduct_id, 1, mStock, mSelected, mImage_500)

                    item.add(0, mProduct_array)
                    Collections.swap(Barcode_array, 0, mSize)
                    Log.d("Barcode_array : ", Barcode_array.toString())
                    adapter!!.notifyDataSetChanged()
                    STOCK_CHECK_ACTIVITY_LISTVIEW.setSelection(0)
                    progressOFF()

                }
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                progressOFF()
            }

        })
    }

    private fun doPostSetConduct(mSeq: String) {

        try {
            var mProductsJson = "["
            for (i in 0 until item.size) {

                mProductsJson += "{"
                mProductsJson += "\"qty\":" + item[i]!![4]  //item[i]!![4] -> scan
                mProductsJson += ","
                mProductsJson += "\"product_id\":\"" + item[i]!![3] + "\""    //item[i]!![3] -> product_id
                mProductsJson += "}"

                if (item.size - 1 > i)
                    mProductsJson += ","
            }
            mProductsJson += "]"


            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
            val mUUID = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
            val mUrlUserName = Session.getInstance().userName
            val mSheetSeq = URLEncoder.encode(mSeq, "UTF-8")
            val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
            var mUrlDomain = pref.getString("domain_save", "")

            var mToken =
                "PRODUCTSJSON=$mProductsJson|SHEETSEQ=$mSheetSeq|DOMAIN=$mUrlDomain|WORKER=$mUrlUserName|UUID=$mUUID"
            val mURL = "api/function.php"
            val map = java.util.HashMap<String, String>()

            map["ACTION"] = URLFactory.SetConductDetail
            map["AUTHCODE"] = mUrlAuthcode
            map["VERSION"] = "v1"
            map["TOKEN"] = mToken

            progressON(this, null)
            val retrofitService = retrofit.create(Service::class.java)
            val call = retrofitService.postData(map, mURL)
            call.enqueue(object : Callback<Data> {
                override fun onResponse(call: Call<Data>, response: Response<Data>) {
                    var mErrorCode = response.body()!!.errorcode
                    if (mErrorCode == 0) {
                        progressOFF()
                        doReset()
                        doPlayAudio("result_stock_in_success")
                        Toast.makeText(applicationContext, "성공", Toast.LENGTH_SHORT).show()
                    } else {
                        progressOFF()
                        doPlayAudio("error_fail")
                        Toast.makeText(applicationContext, "실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Data>, t: Throwable) {
                    progressOFF()
                    Toast.makeText(applicationContext, "실패", Toast.LENGTH_SHORT).show()

                }

            })

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

    }

    private fun doCountStock(mBarcode: String) {

        var mTotal_stock = STOCK_CHECK_ACTIVITY_TOTAL_QTY_TEXTVIEW.text.toString()
        var index = 0
        var i = 0
        var Check = true
        var cnt = 0

        while (i < Barcode_array.size) {
            if (Barcode_array[i].contains(mBarcode)) {
                index = i
                Check = false
                break
            }
            i++
        }
        Log.d("인덱스", index.toString())
        Log.d("바코드", Barcode_array.toString())
        if (!Check) {
            var mScan: Int = Integer.parseInt(item[index]!![4].toString())
            item[index]!![4] = mScan + 1
            item[index]!![6] = true
            Collections.swap(item, 0, index)
            Collections.swap(Barcode_array, 0, index)
            STOCK_CHECK_ACTIVITY_TOTAL_QTY_TEXTVIEW.setText((Integer.parseInt(mTotal_stock) + 1).toString())
            adapter!!.notifyDataSetChanged()
            STOCK_CHECK_ACTIVITY_LISTVIEW.setSelection(0)
        } else if (Check)
            doPostGetProductInfo(mBarcode)

    }


    private fun doReset() {
        item.clear()
        Barcode_array.clear()
        doPostConductDetail(mSeq!!)
        adapter!!.notifyDataSetChanged()
    }

    class StockCheckListViewAdapter(context: Context, item: ArrayList<ArrayList<Any>?>) : BaseAdapter() {
        private val mContext = context
        private val mItem = item

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var i = 0
            lateinit var viewHolder: ViewHolder
            var view = convertView

            var mBarcode = item[position]!![0].toString()
            var mName = item[position]!![1].toString()
            var mOptions = item[position]!![2].toString()
            var mProduct_id = item[position]!![3].toString()
            var mScan = item[position]!![4].toString()
            var mStock = item[position]!![5].toString()
            var mSelected: Boolean = item[position]!![6] as Boolean
            var mImage_500 = item[position]!![7].toString()

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




                if (CODE == "MASTERALL") {

                    if (position % 1 == 0) {

                        if (mItem[position]!![2].toString() == "1")
                            viewHolder.mBack.setBackgroundResource(R.color.is_cancel)
                        else if (mItem[position]!![3].toString() == "1")
                            viewHolder.mBack.setBackgroundResource(R.color.is_change)
                        else
                            viewHolder.mBack.setBackgroundResource(R.color.scan_complete)

                        viewHolder.mPrname.text = mItem[position]!![7].toString()
                        viewHolder.mOption.text = mItem[position]!![5].toString()
                        viewHolder.mQty.text = mItem[position]!![8].toString()
                        viewHolder.mScan.text = mItem[position]!![8].toString()

                    }


                } else if (CODE == "MASTERCODE") {
                    if (mItem[position]!![1] == 0) {
                        viewHolder.mBack.setBackgroundResource(R.color.scan_complete)
                    }
                }


                Picasso.get().load(mImage_500).error(R.drawable.ezadmin_title_logo).resize(50, 50)
                    .into(viewHolder.mImage)
                if (position % 2 == 1)
                    viewHolder.mBack.setBackgroundResource(R.color.gray)
                else
                    viewHolder.mBack.setBackgroundResource(R.color.white)

                if (mSelected)
                    viewHolder.mBack.setBackgroundResource(R.color.scanning)


                viewHolder.mPrname.text = mName
                viewHolder.mOption.text = mOptions
                viewHolder.mQty.text = mStock
                viewHolder.mScan.text = mScan



                return view
            } else {
                viewHolder = view.tag as ViewHolder
            }





            if (CODE == "MASTERALL") {

                if (position % 1 == 0) {

                    if (mItem[position]!![2].toString() == "1")
                        viewHolder.mBack.setBackgroundResource(R.color.is_cancel)
                    else if (mItem[position]!![3].toString() == "1")
                        viewHolder.mBack.setBackgroundResource(R.color.is_change)
                    else
                        viewHolder.mBack.setBackgroundResource(R.color.scan_complete)

                    viewHolder.mPrname.text = mItem[position]!![7].toString()
                    viewHolder.mOption.text = mItem[position]!![5].toString()
                    viewHolder.mQty.text = mItem[position]!![8].toString()
                    viewHolder.mScan.text = mItem[position]!![8].toString()

                }
            } else if (CODE == "MASTERCODE") {
                if (mItem[position]!![1] == 0) {
                    viewHolder.mBack.setBackgroundResource(R.color.scan_complete)
                }
            }

            Picasso.get().load(mImage_500).error(R.drawable.ezadmin_title_logo).resize(50, 50).into(viewHolder.mImage)
            if (position % 2 == 1)
                viewHolder.mBack.setBackgroundResource(R.color.gray)
            else
                viewHolder.mBack.setBackgroundResource(R.color.white)

            if (mSelected)
                viewHolder.mBack.setBackgroundResource(R.color.scanning)

            viewHolder.mPrname.text = mName
            viewHolder.mOption.text = mOptions
            viewHolder.mQty.text = mStock
            viewHolder.mScan.text = mScan




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
                    }
                }
            }
        }
    }

    companion object {
        var item: ArrayList<ArrayList<Any>?> = ArrayList()
        var Barcode_array: ArrayList<ArrayList<Any>> = ArrayList()
        var getTitle: String? = null
        var D = true
        var adapter: StockCheckListViewAdapter? = null
        var CODE: String? = null
        var mSeq: String? = null

    }
}