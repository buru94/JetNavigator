package com.example.pimz.jetnavigator;

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
import com.PointMobile.PMSyncService.SendCommand
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_stock_check.*
import kotlinx.android.synthetic.main.activity_stock_in_out.*
import kr.co.ezapps.ezsmarty.Data
import kr.co.ezapps.ezsmarty.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList


class StockInOutActivity : BaseActivity() {

    internal var retrofit_ecn = Retrofit.Builder()
        .baseUrl(URLFactory.ecnUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SendCommand.SendCommandInit(mChatService, mHandler)
        value = intent.extras.get("title").toString()
        if (value == "STOCK_CHECK" || value == "EZCHAIN_TRANS") {
            setContentView(R.layout.activity_stock_check)


            item = ArrayList()
            Barcode_array = ArrayList()
            mSeq = intent.extras.get("seq").toString()

            var mTitle = intent.extras.get("sheet_title").toString()
            title = mTitle

            if (value == "STOCK_CHECK") {
                StockCheck_adapter = StockCheckListViewAdapter(this, item)
                doPostConductDetail(mSeq!!)
            } else if (value == "EZCHAIN_TRANS") {
                TRANS_EZCHAIN_ACTIVITY_SHOP_NAME_LINEARLAYOUT.visibility = View.VISIBLE
                EzChain_adapter = EzChainListViewAdapter(this, item)
                doPostGetInspectSheetItem()
            }


            STOCK_CHECK_ACTIVITY_BARCODE_SEARCH_BTN.setOnClickListener {
                var mBarcode = STOCK_CHECK_ACTIVITY_BARCODE_EDITTEXT.text.toString().toLowerCase()
                if(mBarcode == "") {
                    doPlayAudio("error_barcode_error")
                    Toast.makeText(this, "바코드 오류", Toast.LENGTH_SHORT).show()
                } else if (value == "EZCHAIN_TRANS") {
                    doPostCheckInspectBarcode(mBarcode)
                } else if (mBarcode.substring(0, 1) == "#") {
                    var i = 0
                    var index = 0
                    var check = 0
                    while (i < Barcode_array.size) {
                        if (item[i][6] == true) {
                            index = i
                            check = 1
                            break
                        }
                        i++
                    }

                    if (check == 1)
                        doCountNumber(mBarcode, index)
                    else {
                        Toast.makeText(applicationContext, "바코드 오류", Toast.LENGTH_SHORT).show()
                        doPlayAudio("error_barcode_error")
                    }
                } else if (item.size > 0) {
                    Num_cnt = 0
                    for (i in 0 until item.size) {
                        item[i]!![6] = false
                    }
                    doCountStock(mBarcode)
                } else {
                    Num_cnt = 0
                    doPostGetProductInfo(mBarcode)
                }
            }

            STOCK_CHECK_ACTIVITY_RESET_BTN.setOnClickListener {
                doReset()
            }

            STOCK_CHECK_ACTIVITY_COMPLETE_BTN.setOnClickListener {
                if (value == "STOCK_CHECK")
                    doPostSetConduct(mSeq!!)
                else if (value == "EZCHAIN_TRANS") {
                    val customDialog = CustomDialogActivity(this)
                    customDialog.callFunction("EZCHAIN_TRANS_COMLETE_ERROR")
                }

            }


        } else {
            setContentView(R.layout.activity_stock_in_out)
            Product = ArrayList()
            Barcode_array = ArrayList()
            StockInOut_adapter = StockInOutListViewAdapter(this, Product)

            var Sheet_title = intent.extras.get("sheet_title").toString()
            title = Sheet_title

            STOCK_IN_OUT_ACTIVITY_SHEET_TEXTVIEW.setText(Sheet_title)

            STOCK_IN_OUT_ACTIVITY_SEARCH_BTN.setOnClickListener {

                var mBarcode = STOCK_IN_OUT_ACTIVITY_RESULT_EDIT_TEXT.text.toString()
                doCountProduct(mBarcode)
            }


            STOCK_IN_OUT_ACTIVITY_COMPLETE_BTN.setOnClickListener {
                Log.d("CLICKCLICK", value.toString())
                when (value) {
                    "STOCK_IN" -> {
                        doPostRunStockWork("in")
                    }
                    "STOCK_OUT" -> {
                        doPostRunStockWork("out")
                    }
                }
                doReset()
            }
            STOCK_IN_OUT_ACTIVITY_RESET_BTN.setOnClickListener {
                doReset()
            }

            STOCK_IN_OUT_ACTIVITY_LIST_VIEW.setOnItemClickListener { parent, view, position, id ->
                var listview_intent = Intent(this, StockScanProductModify::class.java)
                listview_intent.putExtra("product", Product)
                listview_intent.putExtra("position", position)
                startActivityForResult(listview_intent, 3000)

            }

        }


    }

    class StockInOutListViewAdapter(context: Context, mitem: ArrayList<Products>) : BaseAdapter() {
        private val mContext = context
        private val Item = mitem

        override fun getCount(): Int {
            return Item.size
        }

        override fun getItem(position: Int): Any {
            return Item!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var viewHolder: ViewHolder
            var view = convertView
            var a = 1

            if (view == null) {
                viewHolder = ViewHolder()
                view = LayoutInflater.from(mContext)
                    .inflate(R.layout.activity_stock_in_out_listview_item, parent, false)
                viewHolder.mImage = view.findViewById(R.id.STOCK_IN_OUT_LISTVIEW_ITEM_IMAGEVIEW)
                viewHolder.mName = view.findViewById(R.id.STOCK_IN_OUT_LISTVIEW_ITEM_PRNAME_TEXTVIEW)
                viewHolder.mOption = view.findViewById(R.id.STOCK_IN_OUT_LISTVIEW_ITEM_OPTION_TEXTVIEW)
                viewHolder.mCount = view.findViewById(R.id.STOCK_IN_OUT_LISTVIEW_ITEM_COUNT_TEXTVIEW)
                viewHolder.mBack = view.findViewById(R.id.STOCK_IN_OUT_LISTVIEW_ITEM)

                view.tag = viewHolder

                Picasso.get().load(Item[position].getImageurl().toString())
                    .into(viewHolder.mImage, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {}

                        override fun onError(e: Exception?) {
                            Picasso.get().load(R.drawable.ezadmin_title_logo).into(viewHolder.mImage)
                        }
                    })

                viewHolder.mName.text = Item[position].getName().toString()
                viewHolder.mName.isSelected = true
                viewHolder.mOption.text = Item[position].getOptions().toString()
                viewHolder.mOption.isSelected = true
                viewHolder.mCount.text = Item[position].getCnt().toString()

                if (Item[position].getSelected() == true)
                    viewHolder.mBack.setBackgroundResource(R.color.scan_complete)
                else if (Item[position].getSelected() == false && position % 2 == 1)
                    viewHolder.mBack.setBackgroundResource(R.color.white)
                else if (Item[position].getSelected() == false && position % 2 == 0)
                    viewHolder.mBack.setBackgroundResource(R.color.gray)


                return view
            } else {
                viewHolder = view.tag as ViewHolder
            }

            Picasso.get().load(Item[position].getImageurl().toString())
                .into(viewHolder.mImage, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {}

                    override fun onError(e: Exception?) {
                        Picasso.get().load(R.drawable.ezadmin_title_logo).into(viewHolder.mImage)
                    }
                })

            viewHolder.mName.text = Item[position].getName().toString()
            viewHolder.mName.isSelected = true
            viewHolder.mOption.text = Item[position].getOptions().toString()
            viewHolder.mOption.isSelected = true
            viewHolder.mCount.text = Item[position].getCnt().toString()

            if (Item[position].getSelected() == true)
                viewHolder.mBack.setBackgroundResource(R.color.scan_complete)
            else if (Item[position].getSelected() == false && position % 2 == 1)
                viewHolder.mBack.setBackgroundResource(R.color.white)
            else if (Item[position].getSelected() == false && position % 2 == 0)
                viewHolder.mBack.setBackgroundResource(R.color.gray)

            return view

        }

        inner class ViewHolder {
            lateinit var mName: TextView
            lateinit var mOption: TextView
            lateinit var mCount: TextView
            lateinit var mImage: ImageView
            lateinit var mBack: RelativeLayout

        }

    }

    class StockCheckListViewAdapter(context: Context, item: ArrayList<ArrayList<Any>>) : BaseAdapter() {
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

    class EzChainListViewAdapter(context: Context, item: ArrayList<ArrayList<Any>>) : BaseAdapter() {
        private val mContext = context
        private val mItem = item

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var i = 0
            lateinit var viewHolder: ViewHolder
            var view = convertView

            var mBarcode = item[position]!![0].toString()
            var mName = item[position]!![1].toString()
            var mOptions = item[position]!![2].toString()
            var mQty = item[position]!![3].toString()
            var mScan = item[position]!![4].toString()
            var mProduct_id = item[position]!![5].toString()
            var mSelected: Boolean = item[position]!![6] as Boolean


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

                        viewHolder.mPrname.text = mName
                        viewHolder.mOption.text = mOptions
                        viewHolder.mQty.text = mQty
                        viewHolder.mScan.text = mScan

                    }


                } else if (CODE == "MASTERCODE") {
                    if (mItem[position]!![1] == 0) {
                        viewHolder.mBack.setBackgroundResource(R.color.scan_complete)
                    }
                }


                Picasso.get().load(R.drawable.ezadmin_title_logo).resize(50, 50)
                    .into(viewHolder.mImage)
                if (position % 2 == 1)
                    viewHolder.mBack.setBackgroundResource(R.color.gray)
                else
                    viewHolder.mBack.setBackgroundResource(R.color.white)

                if (mSelected)
                    viewHolder.mBack.setBackgroundResource(R.color.scanning)



                viewHolder.mPrname.text = mName
                viewHolder.mOption.text = mOptions
                viewHolder.mQty.text = mQty
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

            Picasso.get().load(R.drawable.ezadmin_title_logo).resize(50, 50).into(viewHolder.mImage)
            if (position % 2 == 1)
                viewHolder.mBack.setBackgroundResource(R.color.gray)
            else
                viewHolder.mBack.setBackgroundResource(R.color.white)

            if (mSelected)
                viewHolder.mBack.setBackgroundResource(R.color.scanning)

            viewHolder.mPrname.text = mName
            viewHolder.mOption.text = mOptions
            viewHolder.mQty.text = mQty
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 3000) {
                var result = data!!.extras.get("result").toString()
                when (result) {
                    "Modify" -> {
                        var mCnt = Integer.parseInt(data!!.extras.get("cnt").toString())
                        var mPosition = data!!.extras.get("position") as Int
                        Product[mPosition].setCnt(mCnt)
                        StockInOut_adapter!!.notifyDataSetChanged()
                        doPlayAudioCount(Integer.parseInt(Product[mPosition].getCnt().toString()))
                    }

                    "Delete" -> {
                        var mPosition = data!!.extras.get("position") as Int
                        Product.remove(Product[mPosition])
                        StockInOut_adapter!!.notifyDataSetChanged()
                        doPlayAudio("result_success")
                    }

                }

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

    fun doGetProductInfo(input: HashMap<String, String>, Url: String) {
        progressON(this, null)
        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, Url)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                var data = response.body()!!.data
                var mErrorCode = response.body()!!.errorcode


                if (mErrorCode == 5001) {
                    doPlayAudio("error_barcode_error")
                    progressOFF()
                } else if (mErrorCode == 0) {

                    var mImagePath = data!!.get("img_500").toString().replace("\"", "")
                    var mName = data!!.get("name").toString().replace("\"", "")
                    var mOptions = data!!.get("options").toString().replace("\"", "")
                    var mBarcode = data!!.get("barcode").toString().replace("\"", "")
                    var mProduct_id = data!!.get("product_id").toString().replace("\"", "")
                    var mStock = data!!.get("stock").toString().replace("\"", "")
                    var mMulti_barcode = data!!.get("multi_barcode").asJsonArray
                    var mSelected: Boolean = true
                    var mMulti_array: ArrayList<Any> = ArrayList()


                    if (value == "STOCK_CHECK") {
                        var mProduct_array: ArrayList<Any>
                        var mSize = Barcode_array.size
                        for (i in 0 until mMulti_barcode.size()) {
                            var mMulti = mMulti_barcode[i].toString().replace("\"", "").toLowerCase()
                            mMulti_array.add(mMulti)
                        }
                        Barcode_array.add(mMulti_array)




                        mProduct_array =
                                arrayListOf(mBarcode, mName, mOptions, mProduct_id, 1, mStock, mSelected, mImagePath)

                        item.add(0, mProduct_array)
                        Collections.swap(Barcode_array, 0, mSize)
                        Log.d("Barcode_array : ", Barcode_array.toString())
                        StockCheck_adapter!!.notifyDataSetChanged()
                        STOCK_CHECK_ACTIVITY_LISTVIEW.setSelection(0)
                    } else {
                        var size = Product.size
                        var array: ArrayList<Any> = ArrayList()
                        for (i in 0 until mMulti_barcode.size()) {

                            array.add(mMulti_barcode[i].toString().replace("\"", "").toLowerCase())
                        }
                        Barcode_array.add(0, array)

                        Log.d(TAG, Barcode_array.toString())



                        Product.add(Products())
                        Product[size].setImageurl(mImagePath)
                        Product[size].setName(mName)
                        Product[size].setOptions(mOptions)
                        Product[size].setCnt(1)
                        Product[size].setBarcode(mBarcode)
                        Product[size].setProduct_id(mProduct_id)
                        Product[size].setSelected(true)
                        Collections.swap(Product, 0, size)
                        doPlayAudio("cnt_1")



                        if (Product.size > 1) {
                            StockInOut_adapter!!.notifyDataSetChanged()
                            STOCK_IN_OUT_ACTIVITY_LIST_VIEW.setSelection(0)
                        } else
                            STOCK_IN_OUT_ACTIVITY_LIST_VIEW.adapter = StockInOut_adapter
                    }
                    progressOFF()
                }


            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                progressOFF()
            }
        })
    }

    private fun doPostRunStockWork(Type: String) {
        try {
            var mProductsJson = "["
            for (i in 0 until Product.size) {

                mProductsJson += "{"
                mProductsJson += "\"qty\":" + Product[i].getCnt()
                mProductsJson += ","
                mProductsJson += "\"product_id\":\"" + Product[i].getProduct_id() + "\""
                mProductsJson += "}"

                if (Product.size - 1 > i)
                    mProductsJson += ","
            }
            mProductsJson += "]"


            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
            val mUUID = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
            val mUrlUserName = Session.getInstance().userName
            val mSheetSeq = intent.extras.get("seq").toString()
            var mToken = "PRODUCTSJSON=$mProductsJson|SHEETSEQ=$mSheetSeq|TYPE=$Type|WORKER=$mUrlUserName|UUID=$mUUID"
            val mURL = "api/function.php"
            val map = HashMap<String, String>()

            map["ACTION"] = URLFactory.RunStockWork
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

    private fun doCountProduct(Barcode: String) {

        var cnt = 1
        var i = 0
        var Check = true
        var mIndex = 0
        var mCurrentBarcode = Barcode.toLowerCase()


        if (Product.size > 0) {

            for (i in 0 until Product.size) {
                Product[i].setSelected(false)
            }
            while (i < Product.size) {
                Log.d(TAG, Barcode_array.toString())

                if (Barcode_array[i].contains(mCurrentBarcode)) {
                    mIndex = i
                    Check = false
                    break
                }
                i++
            }
            Log.d("인덱스", mIndex.toString())
            if (!Check) {
                Log.d("TAG", "증가")
                var mCnt = Product[mIndex].getCnt()

                Product[mIndex].setCnt((mCnt + 1))
                Product[mIndex].setSelected(true)
                Collections.swap(Product, 0, mIndex)
                doPlayAudioCount(mCnt)
                StockInOut_adapter!!.notifyDataSetChanged()
                STOCK_IN_OUT_ACTIVITY_LIST_VIEW.setSelection(0)
            } else if (Check)
                doPostGetProductInfo(mCurrentBarcode)

        } else {
            Log.d("TAG", "추가")
            doPostGetProductInfo(mCurrentBarcode)
        }
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

                    STOCK_CHECK_ACTIVITY_LISTVIEW.adapter = StockCheck_adapter
                    progressOFF()
                }
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                Log.e(TAG, t.message.toString())
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

            if (value == "STOCK_CHECK")
                StockCheck_adapter!!.notifyDataSetChanged()
            else if (value == "EZCHAIN_TRANS")
                EzChain_adapter!!.notifyDataSetChanged()

            STOCK_CHECK_ACTIVITY_LISTVIEW.setSelection(0)
        } else if (Check)
            doPostGetProductInfo(mBarcode)

    }

    private fun doCountNumber(mBarcode: String, mIndex: Int) {
        var Num = Integer.parseInt(mBarcode.substring(1, 4))

        when (Num) {
            in 100..999 -> Num_cnt = Num
            in 10..99 -> Num_cnt += Num
            in 1..9 -> Num_cnt += Num
        }
        item[mIndex][4] = Num_cnt
        StockCheck_adapter!!.notifyDataSetChanged()

    }

    private fun doPostGetInspectSheetItem() {
        try {
            val mURL = URLFactory.GetInspectSheetItem
            val map = HashMap<String, String>()
            val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
            val id = pref.getString("domain_save", "")

            map["is_mobile"] = "1"
            map["sheet_type"] = "5"
            map["id"] = id
            map["sheet_seq"] = mSeq.toString()
            doGetInspectSheetItem(map, mURL)

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    private fun doGetInspectSheetItem(input: HashMap<String, String>, mUrl: String) {
        progressON(this, null)
        val retrofitService = retrofit_ecn.create(Service::class.java)
        val call = retrofitService.postData(input, mUrl)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                var mList = response.body()!!.list
                var mTitle = response.body()!!.title
                title = mTitle
                var array: ArrayList<Any>
                var barray: ArrayList<Any>
                var mGroup = response.body()!!.group.toString().replace("\"", "")
                var mShop = response.body()!!.shop.toString().replace("\"", "")
                var mTotal_scan = response.body()!!.total_new.toString().replace("\"", "")
                var mTotal_qty = response.body()!!.total_old.toString().replace("\"", "")
                item.clear()
                for (i in 0 until mList!!.size()) {
                    var mObj = mList[i].asJsonObject
                    var mBarcode = mObj.get("barcode").toString().replace("\"", "")
                    var mName = mObj.get("name").toString().replace("\"", "")
                    var mNew_qty = mObj.get("new_qty").toString().replace("\"", "")
                    var mOld_qty = mObj.get("old_qty").toString().replace("\"", "")
                    var mOptions = mObj.get("options").toString().replace("\"", "")
                    var mProduct_id = mObj.get("product_id").toString().replace("\"", "")

                    array = arrayListOf(mBarcode, mName, mOptions, mOld_qty, mNew_qty, mProduct_id, false)
                    item.add(array)
                    barray = arrayListOf(mBarcode.toLowerCase())
                    Barcode_array.add(barray)
                }

                Log.d("Barcode", Barcode_array.toString())
                TRANS_EZCHAIN_ACTIVITY_GROUP_NAME_TEXTVIEW.setText(mGroup)
                TRANS_EZCHAIN_ACTIVITY_SHOP_NAME_TEXTVIEW.setText(mShop)
                STOCK_CHECK_ACTIVITY_TOTAL_SCAN_TEXTVIEW.setText(mTotal_scan)
                STOCK_CHECK_ACTIVITY_TOTAL_QTY_TEXTVIEW.setText(mTotal_qty)

                STOCK_CHECK_ACTIVITY_LISTVIEW.adapter = EzChain_adapter
                progressOFF()
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                progressOFF()
            }
        })
    }

    private fun doPostCheckInspectBarcode(mBarcode: String) {
        try {
            val mURL = URLFactory.CheckInspectbarcode
            val map = HashMap<String, String>()
            val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
            val id = pref.getString("domain_save", "")

            map["is_mobile"] = "1"
            map["sheet_type"] = "5"
            map["id"] = id
            map["barcode"] = mBarcode
            map["sheet"] = mSeq.toString()
            map["ecn_inspect_mode"] = "1"
            doCheckInspectBarcode(map, mURL)

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    private fun doCheckInspectBarcode(input: HashMap<String, String>, mUrl: String) {
        progressON(this, null)
        val retrofitService = retrofit_ecn.create(Service::class.java)
        val call = retrofitService.postData(input, mUrl)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                var mError = response.body()!!.error
                var mReal_qty: String? = null

                if (mError == 17) {
                    Toast.makeText(applicationContext, "이미 등록된 상품입니다", Toast.LENGTH_SHORT).show()
                    doPostGetInspectSheetItem()
                } else if (mError == 0) {
                    doPostGetInspectSheetItem()
                    EzChain_adapter!!.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {

            }
        })
    }

    fun doReset() {
        if (value == "STOCK_CHECK") {
            item.clear()
            Barcode_array.clear()
            doPostConductDetail(mSeq!!)
            StockCheck_adapter!!.notifyDataSetChanged()
        } else if (value == "STOCK_IN" || value == "STOCK_OUT") {
            Product.clear()
            Barcode_array.clear()
            STOCK_IN_OUT_ACTIVITY_RESULT_EDIT_TEXT.setText("")
            STOCK_IN_OUT_ACTIVITY_SHEET_TEXTVIEW.setText("")
            StockInOut_adapter!!.notifyDataSetChanged()
        } else if (value == "EZCHAIN_TRANS") {
            item.clear()
            Barcode_array.clear()
            doPostGetInspectSheetItem()

            STOCK_CHECK_ACTIVITY_BARCODE_EDITTEXT.setText("")
            STOCK_CHECK_ACTIVITY_TOTAL_SCAN_TEXTVIEW.setText("")
            STOCK_CHECK_ACTIVITY_TOTAL_QTY_TEXTVIEW.setText("")

            EzChain_adapter!!.notifyDataSetChanged()
        }
    }

    override fun onBackPressed() {
        this.finish()
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
                        STOCK_IN_OUT_ACTIVITY_RESULT_EDIT_TEXT.setText(Barcode)
                        doCountProduct(Barcode)
                    }
                }
            }
        }
    }

    companion object {
        val D = true
        var Product: ArrayList<Products> = ArrayList()
        var item: ArrayList<ArrayList<Any>> = ArrayList()
        var StockInOut_adapter: StockInOutListViewAdapter? = null
        var StockCheck_adapter: StockCheckListViewAdapter? = null
        var EzChain_adapter: EzChainListViewAdapter? = null
        var value: String? = null
        var Barcode_array: ArrayList<ArrayList<Any>> = ArrayList()
        var CODE: String? = null
        var mSeq: String? = null
        var Num_cnt = 0
    }
}
