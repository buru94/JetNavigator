package com.example.pimz.jetnavigator

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_trans_product_scan.*
import kr.co.ezapps.ezsmarty.Data
import kr.co.ezapps.ezsmarty.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


class TransProductScanActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trans_product_scan)

        var value = intent.extras.get("title")
        title = "배송처리 > " + value

        SendCommand.SendCommandInit(mChatService, mHandler)

        Trans_adapter = TransProductScanListViewAdapter(this,item)

        item = ArrayList()
        Product = ArrayList()



        TRANS_PRODUCT_SCAN_ACTIVITY_RESET_BTN.setOnClickListener {
            doPlayAudio("msg_clear")
            doReset()

        }
        TRANS_PRODUCT_SCAN_ACTIVITY_CS_LIST_BTN.setOnClickListener {
            var mTransNo = TRANS_PRODUCT_SCAN_ACTIVITY_SEARCH_EDIT_TEXT.text.toString()
            doPostCsInfo(mTransNo)
        }

        TRANS_PRODUCT_SCAN_ACTIVITY_RESERVE_CANCEL_BTN.setOnClickListener {
            var mTransNo = TRANS_PRODUCT_SCAN_ACTIVITY_SEARCH_EDIT_TEXT.text.toString()
            doHoldReleasePost(mTransNo)
        }

        TRANS_PRODUCT_SCAN_ACTIVITY_SEARCH_BTN.setOnClickListener {
            var mTransNo = TRANS_PRODUCT_SCAN_ACTIVITY_SEARCH_EDIT_TEXT.text.toString()
            if (mTransNo == "")
                TRANS_PRODUCT_SCAN_ACTIVITY_RESULT_EDIT_TEXT.setHint("송장번호오류")
            else {
                TextArray.add(TRANS_PRODUCT_SCAN_ACTIVITY_SEARCH_EDIT_TEXT.text.toString())
                if (item.size > 0) {

                    if (mTransNo.substring(0, 1) == "#") {
                        var i = 0
                        var index = 0
                        var check = 0
                        while (i < BarcodeArray.size) {
                            if (item[i]!![9] == true) {
                                index = i
                                check = 1
                                break
                            }
                            i++
                        }
                        if (check == 1)
                            doCountNumber(mTransNo, index)
                        else {
                            Toast.makeText(applicationContext, "바코드 오류", Toast.LENGTH_SHORT).show()
                            doPlayAudio("error_barcode_error")
                            TRANS_PRODUCT_SCAN_ACTIVITY_RESULT_EDIT_TEXT.setHint("바코드오류")
                        }
                    } else {
                        for (i in 0 until item.size) {
                            item[i]!![9] = false
                        }
                        doCountScan()
                    }
                } else
                    doTransNoPost(mTransNo, SEARCH)
            }
        }
    }

    class TransProductScanListViewAdapter(context: Context, mitem: ArrayList<ArrayList<Any>?>) : BaseAdapter() {
        private val mContext = context
        private val mItem = mitem

        val mHandler: Handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {

                    StockManageFragment.MESSAGE_BARCODE -> {

                        val BarcodeBuff = msg.obj as ByteArray

                        var Barcode = ""

                        Barcode = String(BarcodeBuff, 0, msg.arg1)
                        if (Barcode.length != 0) {
                            Log.d("BARCODE 확인", item[0].toString())
                            //if(TRANS_PRODUCT_SCAN_ACTIVITY_SEARCH_EDIT_TEXT.text == item[0])
                        }
                    }
                }

            }
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var i = 0
            lateinit var viewHolder: ViewHolder
            var view = convertView
            var mEnable_stock = mItem[position]!![0]
            var mIs_cancel  = mItem[position]!![1].toString()
            var mIs_change = mItem[position]!![2].toString()
            var mLocation = mItem[position]!![3].toString()
            var mOptions = mItem[position]!![4].toString()
            var mProduct_id= mItem[position]!![5].toString()
            var mProduct_name= mItem[position]!![6].toString()
            var mQty= mItem[position]!![7].toString()
            var mScan = mItem[position]!![8].toString()
            var mSelected = mItem[position]!![9]
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

                Picasso.get().load(R.drawable.ezadmin_title_logo).resize(50, 50).into(viewHolder.mImage)




                if (CODE == "MASTERALL") {

                    if (position % 1 == 0) {

                        if (mIs_cancel == "1")
                            viewHolder.mBack.setBackgroundResource(R.color.is_cancel)
                        else if (mIs_change == "1")
                            viewHolder.mBack.setBackgroundResource(R.color.is_change)
                        else
                            viewHolder.mBack.setBackgroundResource(R.color.scan_complete)

                        viewHolder.mPrname.text = mProduct_name
                        viewHolder.mOption.text = mOptions
                        viewHolder.mQty.text = mQty
                        viewHolder.mScan.text = mScan

                    }
                } else if(CODE == "MASTERCODE"){
                    if(mEnable_stock == 0){
                        viewHolder.mBack.setBackgroundResource(R.color.scan_complete)
                    }
                } else {
                    if (mIs_cancel == "1")
                        viewHolder.mBack.setBackgroundResource(R.color.is_cancel)
                    else if (mIs_change == "1")
                        viewHolder.mBack.setBackgroundResource(R.color.is_change)

                    if(position == 3)
                        viewHolder.mBack.setBackgroundResource(R.color.is_change)

                    if (Integer.parseInt(mScan) > 0 && mQty != mScan)
                        viewHolder.mBack.setBackgroundResource(R.color.scanning)
                    else if (mQty == mScan && Integer.parseInt(mScan) > 0)
                        viewHolder.mBack.setBackgroundResource(R.color.scan_complete)



                    viewHolder.mScan.text = mScan
                    viewHolder.mPrname.text = mProduct_name
                    viewHolder.mOption.text = mOptions
                    viewHolder.mQty.text = mQty
                }
                return view
            } else {
                viewHolder = view.tag as ViewHolder
            }
            Picasso.get().load(R.drawable.ezadmin_title_logo).resize(50, 50).into(viewHolder.mImage)

            if (CODE == "MASTERALL") {

                if (position % 1 == 0) {

                    if (mIs_cancel == "1")
                        viewHolder.mBack.setBackgroundResource(R.color.is_cancel)
                    else if (mIs_change == "1")
                        viewHolder.mBack.setBackgroundResource(R.color.is_change)
                    else
                        viewHolder.mBack.setBackgroundResource(R.color.scan_complete)

                    viewHolder.mPrname.text = mProduct_name
                    viewHolder.mOption.text = mOptions
                    viewHolder.mQty.text = mQty
                    viewHolder.mScan.text = mScan

                }
            } else if(CODE == "MASTERCODE"){
                if(mEnable_stock == 0){
                    viewHolder.mBack.setBackgroundResource(R.color.scan_complete)
                }
            } else {
                if (mIs_cancel == "1")
                    viewHolder.mBack.setBackgroundResource(R.color.is_cancel)
                else if (mIs_change == "1")
                    viewHolder.mBack.setBackgroundResource(R.color.is_change)

                if(position == 3)
                    viewHolder.mBack.setBackgroundResource(R.color.is_change)

                if (Integer.parseInt(mScan) > 0 && mQty != mScan)
                    viewHolder.mBack.setBackgroundResource(R.color.scanning)
                else if (mQty == mScan && Integer.parseInt(mScan) > 0)
                    viewHolder.mBack.setBackgroundResource(R.color.scan_complete)

                viewHolder.mScan.text = mScan
                viewHolder.mPrname.text = mProduct_name
                viewHolder.mOption.text = mOptions
                viewHolder.mQty.text = mQty
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


    private fun doCountScan() {
        var mTransNo = TRANS_PRODUCT_SCAN_ACTIVITY_SEARCH_EDIT_TEXT.text.toString().toLowerCase()
        var Check: Boolean = false
        var index = 0
        var i = 0
        var is_trans = if (flag == item.size) 1 else 0
        var CurrentTransNo = TextArray[0].toLowerCase()

        if (item.size > 0) {

            while (!Check) {
                var size = (BarcodeArray.size - 1)
                Check = BarcodeArray[i].contains(mTransNo)

                if (Check)
                    index = i


                if (i < size && !Check)
                    i++
                else if (i == size || Check)
                    break
            }

            if (Check) {
                var scan = item[index]!![8] as Int
                var qty = item[index]!![7] as Int
                var is_cancel = item[index]!![1].toString()

                if (is_cancel == "1") {
                    doPlayAudio("result_order_cancel")
                } else {
                    when {
                        (qty - 1 == scan) -> {
                            item[index]!![9] = true
                            item[index]!![8] = ++scan
                            doPlayAudioCount(Integer.parseInt(scan.toString()))

                            Trans_adapter!!.notifyDataSetChanged()

                            if (flag < item.size)
                                flag++
                        }

                        (qty > scan) -> {
                            item[index]!![9] = true
                            item[index]!![8] = ++scan
                            doPlayAudioCount(Integer.parseInt(scan.toString()))

                            TRANS_PRODUCT_SCAN_ACTIVITY_LIST_VIEW.adapter = Trans_adapter
                            Trans_adapter!!.notifyDataSetChanged()
                        }

                        (qty == scan) -> doPlayAudio("result_already_scan_product")
                    }
                }
            } else if (!Check && mTransNo == CurrentTransNo && flag != item.size)
                doPlayAudio("result_product_scan_not_finish")
            else if (!Check && mTransNo != CurrentTransNo) {
                doPlayAudio("error_barcode_error")
                TRANS_PRODUCT_SCAN_ACTIVITY_RESULT_EDIT_TEXT.setHint("바코드 오류")
            }

            if (is_trans == 1) {
                if (Session.getInstance().pos_product_trans == 1) {
                    doTransNoPost(CurrentTransNo, TRANS)
                    doReset()
                } else
                    if (mTransNo == CurrentTransNo)
                        doTransNoPost(CurrentTransNo, TRANS)
                doReset()
            }
        }

    }

    private fun doCountNumber(mBarcode: String, mIndex: Int) {
        var Num = Integer.parseInt(mBarcode.substring(1, 4))

        when (Num) {
            in 100..999 -> Num_cnt = Num
            in 10..99 -> Num_cnt += Num
            in 1..9 -> Num_cnt += Num
        }
        item[mIndex]!![8] = Num_cnt
        Trans_adapter!!.notifyDataSetChanged()
    }

    private fun doTransNoPost(mTransNo: String, mType: Int) {
        try {

            val mUUID = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
            val mUrlType = mType
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

    private fun doGetTransNo(input: HashMap<String, String>, Url: String) {
        progressON(this, null)
        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, Url)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                // 성공
                val TransProducts = response.body()!!.products
                var TransMessage = response.body()!!.message
                var TransError = response.body()!!.errorcode

                TRANS_PRODUCT_SCAN_ACTIVITY_RESULT_EDIT_TEXT.setHint(TransMessage)
                if (TransError == 3008) {
                    doPlayAudio("error_trans_no_error")
                    progressOFF()
                } else if (TransProducts == null) {
                    when (TransError) {
                        0 -> doPlayAudio("result_normal")
                        3002 -> doPlayAudio("result_order_address_change")
                        3003 -> doPlayAudio("result_order_hold")
                        3005 -> doPlayAudio("result_order_hold")
                        3006 -> doPlayAudio("result_order_hold")
                        3007 -> doPlayAudio("result_already_trans_short")
                        3009 -> doPlayAudio("result_order_all_cancel")
                    }
                    Log.d("여기여ㅣ여기여기여기여기여기","1번")
                    progressOFF()
                } else if (TransProducts!!.size() > 0) {
                    Log.d("여기여ㅣ여기여기여기여기여기","2번")
                    when (TransError) {
                        0 -> doPlayAudio("result_normal")
                        3002 -> doPlayAudio("result_order_address_change")
                        3003 -> doPlayAudio("result_order_hold")
                        3005 -> doPlayAudio("result_order_hold")
                        3006 -> doPlayAudio("result_order_hold")
                        3007 -> doPlayAudio("result_already_trans_short")
                        3009 -> doPlayAudio("result_order_all_cancel")
                    }
                    item = ArrayList()

                    for (i in 0 until TransProducts.size()) {
                        var mTransObj: JsonObject = TransProducts[i].asJsonObject
                        var mMultiBarcode = mTransObj.get("multi_barcode").asJsonArray
                        var array: ArrayList<Any> = arrayListOf(
                            mTransObj.get("enable_stock").toString().replace("\"", "")
                            , mTransObj.get("is_cancel").toString().replace("\"", "")
                            , mTransObj.get("is_change").toString().replace("\"", "")
                            , mTransObj.get("location").toString().replace("\"", "")
                            , mTransObj.get("options").toString().replace("\"", "")
                            , mTransObj.get("product_id").toString().replace("\"", "")
                            , mTransObj.get("product_name").toString().replace("\"", "")
                            , Integer.parseInt(mTransObj.get("qty").toString().replace("\"", ""))
                            , 0
                            , false
                        )
                        item.add(array)

                        var mArray: ArrayList<Any> = ArrayList()
                        for (i in 0 until mMultiBarcode.size()) {
                            var mMulti = mMultiBarcode[i].toString().replace("\"", "").toLowerCase()
                            mArray.add(mMulti)
                        }

                        BarcodeArray.add(mArray)

                        Product.add(Products())
                        Product[i].setEnable_stock(array[0].toString())
                        Product[i].setProduct_id(array[5].toString())

                        QtyArray.add(item[i]!![7])
                    }
                    Log.d("BARCODEARRAY", Product.toString())
                    Log.d("ITEMARRAY", item.toString())
                    Log.d("ADAPTER", Trans_adapter.toString())
                    Trans_adapter = TransProductScanListViewAdapter(applicationContext,item)
                    TRANS_PRODUCT_SCAN_ACTIVITY_LIST_VIEW.adapter = Trans_adapter

                    progressOFF()

                }

            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                // 실패
                Log.getStackTraceString(t)

                TRANS_PRODUCT_SCAN_ACTIVITY_RESULT_EDIT_TEXT.setHint("송장번호 오류")
                doPlayAudio("error_trans_no_error")
                progressOFF()

            }
        })
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

    private fun doSetHoldRelease(input: HashMap<String, String>, Url: String) {
        progressON(this, null)
        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, Url)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                val HoldReleaseError = response.body()!!.errorcode
                val HoldReleaseMessage = response.body()!!.message
                progressON(Activity(), "Loading")
                when (HoldReleaseError) {
                    0 -> {
                        doPlayAudio("msg_hold_release_success")
                        TRANS_PRODUCT_SCAN_ACTIVITY_RESULT_EDIT_TEXT.setHint(HoldReleaseMessage)
                    }
                    6005 -> {
                        doPlayAudio("result_stock_modify_fail")
                        TRANS_PRODUCT_SCAN_ACTIVITY_RESULT_EDIT_TEXT.setHint(HoldReleaseMessage)
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

    private fun doPostCsInfo(mTransNo: String) {
        try {

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

    private fun doGetCsInfo(input: HashMap<String, String>, Url: String) {
        progressON(this, null)
        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, Url)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                // 성공
                progressON(Activity(), "Loading")
                val CsInfoError = response.body()!!.errorcode
                val CsInfoMessage = response.body()!!.message
                if (CsInfoError == 3008) {
                    doPlayAudio("error_trans_no_error")
                    TRANS_PRODUCT_SCAN_ACTIVITY_RESULT_EDIT_TEXT.setHint(CsInfoMessage)
                } else if (CsInfoError == 0) {
                    var mTransNo = TRANS_PRODUCT_SCAN_ACTIVITY_SEARCH_EDIT_TEXT.text.toString()
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

    private fun doReset() {

        TRANS_PRODUCT_SCAN_ACTIVITY_SEARCH_EDIT_TEXT.setText("")
        TRANS_PRODUCT_SCAN_ACTIVITY_RESULT_EDIT_TEXT.setHint("")
        item.clear()
        Product.clear()
        TextArray.clear()
        Trans_adapter!!.notifyDataSetChanged()
    }

    private fun doMasterAll() { // 전체스캔 완료시키기
        CODE = "MASTERALL"
        Trans_adapter!!.notifyDataSetChanged()
    }

    private fun doMasterCode() {// 재고관리안함 상품 스캔완료시키기
        CODE == "MASTERCODE"
        Trans_adapter!!.notifyDataSetChanged()


    }

    private fun doCheckSpecialBarcode(mBarcode: String) {
        var mTransNo = TextArray[0]
        when (mBarcode) {
            "#INIT#" -> doReset()
            "#UNHOLD#" -> doHoldReleasePost(mTransNo)
            "#TRANS_FORCE#" -> doTransNoPost(mTransNo, FORCE)
            "#MASTERALL#" -> doMasterAll()
            "#MASTERCODE#" -> doMasterCode()
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

                StockManageFragment.MESSAGE_BARCODE -> {
                    val BarcodeBuff = msg.obj as ByteArray

                    var Barcode = ""

                    Barcode = String(BarcodeBuff, 0, msg.arg1)
                    if (item.size > 0) {
                        TRANS_PRODUCT_SCAN_ACTIVITY_SEARCH_EDIT_TEXT.setText(Barcode)
                        TextArray.add(TRANS_PRODUCT_SCAN_ACTIVITY_SEARCH_EDIT_TEXT.text.toString())
                        Log.d("TEXTARRAY", TextArray.toString())
                        doCountScan()

                    } else {
                        if (Barcode.isNotEmpty()) {
                            TRANS_PRODUCT_SCAN_ACTIVITY_SEARCH_EDIT_TEXT.setText(Barcode)
                            var mTransNo = TRANS_PRODUCT_SCAN_ACTIVITY_SEARCH_EDIT_TEXT.text.toString()
                            TextArray.add(TRANS_PRODUCT_SCAN_ACTIVITY_SEARCH_EDIT_TEXT.text.toString())
                            doCheckSpecialBarcode(mTransNo)
                            doTransNoPost(Barcode, SEARCH)
                        }
                    }
                }

            }
        }
    }


    companion object {
        private val D = true
        var item: ArrayList<ArrayList<Any>?> = ArrayList()

        val SEARCH = 0
        val TRANS = 1
        val FORCE = 2
        var QtyArray: ArrayList<Any> = ArrayList()
        var Product: ArrayList<Products> = ArrayList()
        var BarcodeArray: ArrayList<ArrayList<Any>> = ArrayList()
        var TextArray: ArrayList<String> = ArrayList()
        var Trans_adapter:TransProductScanListViewAdapter? = null
        var CODE: String? = null
        var Num_cnt = 0
        var flag = 0


    }
}