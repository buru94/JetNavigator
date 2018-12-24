package com.example.pimz.jetnavigator

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.google.gson.JsonArray
import kotlinx.android.synthetic.main.activity_sheet_list.*
import kr.co.ezapps.ezsmarty.Data
import kr.co.ezapps.ezsmarty.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.PointMobile.PMSyncService.BluetoothChatService
import com.PointMobile.PMSyncService.SendCommand
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class SheetListActivity : BaseActivity() {
    internal var retrofit_ecn = Retrofit.Builder()
        .baseUrl(URLFactory.ecnUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sheet_list)

        SendCommand.SendCommandInit(mChatService, mHandler)

        getTitle = intent.extras.get("title").toString()
        item = ArrayList()

        val recyclerView = findViewById<View>(R.id.SHEET_LIST_RECYCLE_VIEW) as RecyclerView
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        if (WH_FLAG) {
            doPostWareHouse()
            WH_FLAG = false
        }

        val fab = SHEET_LIST_ACTIVITY_ADD_BTN as FloatingActionButton
        when (getTitle) {
            "STOCK_IN" -> doPostStockInSheet("in")
            "STOCK_OUT" -> doPostStockInSheet("out")
            "STOCK_CHECK" -> {
                doPostConductSheet()
                fab.isEnabled = false
            }
            "LOC_STOCK_IN_ORDER" -> {
                doPostGetLocationWorkSheetList("IN")
                fab.isEnabled = false
            }
            "LOC_STOCK_OUT_ORDER" -> {
                doPostGetLocationWorkSheetList("OUT")
                fab.isEnabled = false
            }
            "EZCHAIN_TRANS" -> {
                doPostGetInspectSheetList()
                fab.isEnabled = false
            }
        }


        fab.isFocusable = false
        var Addintent: Intent? = null
        Addintent = Intent(applicationContext, AddStockSheetActivity::class.java)
        fab.setOnClickListener { view ->
            if (getTitle == "STOCK_IN") {
                Addintent!!.putExtra("TYPE", "STOCK_IN")
            }
            if (getTitle == "STOCK_OUT") {
                Addintent!!.putExtra("TYPE", "STOCK_OUT")
            }

            startActivity(Addintent)
        }
    }

    class SheetListCardAdapter(internal var context: Context, internal var items: ArrayList<ArrayList<Any>?>) :
        RecyclerView.Adapter<SheetListCardAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val v = LayoutInflater.from(parent.context).inflate(R.layout.sheet_listview_item, null)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            var title: String? = null
            var seq: String? = null
            var warehouse: String? = null
            var warehouse_seq: String? = null
            var crdate: String? = null
            var crtime: String? = null
            var bad: String? = null
            var cruser: String? = null
            var in_wh: String? = null
            var is_move: String? = null
            var out_wh: String? = null
            var ret: String? = null
            var wh: String? = null
            var type: String? = null
            var shop: String? = null
            var shop_name: String? = null
            var status: String? = null
            var worker: String? = null

            if (getTitle == "STOCK_CHECK") {
                title = items[position]!![0].toString()
                seq = items[position]!![1].toString()
                warehouse = items[position]!![2].toString()
                warehouse_seq = items[position]!![3].toString()
                crdate = items[position]!![4].toString()
                crtime = items[position]!![5].toString()

                viewHolder.mTitle.text = title
                viewHolder.mSeq.text = seq
                viewHolder.mWh.text = warehouse
                viewHolder.mRet.visibility = View.GONE
                viewHolder.mCrdate.text = crdate

            } else if (getTitle == "EZCHAIN_TRANS") {
                title = items[position]!![1].toString()
                seq = items[position]!![2].toString()
                type = items[position]!![3].toString()
                shop = items[position]!![4].toString()
                shop_name = items[position]!![5].toString()
                status = items[position]!![6].toString()
                cruser = items[position]!![7].toString()

                viewHolder.mTitle.text = title
                viewHolder.mSeq.text = seq
                viewHolder.mWh.text = shop + "->" + shop_name
                viewHolder.mOwner.text = cruser
            } else if (getTitle == "LOC_STOCK_IN_ORDER" || getTitle == "LOC_STOCK_OUT_ORDER") {
                seq = items[position]!![0].toString()
                title = items[position]!![1].toString()
                cruser = items[position]!![2].toString()
                crdate = items[position]!![3].toString()
                viewHolder.mSeq.text = seq
                viewHolder.mTitle.text = title
                viewHolder.mOwner.text = cruser
                viewHolder.mCrdate.text = crdate


            } else {
                bad = items[position]!![0].toString()
                cruser = items[position]!![1].toString()
                crdate = items[position]!![2].toString()
                in_wh = items[position]!![3].toString()
                is_move = items[position]!![4].toString()
                out_wh = items[position]!![5].toString()
                ret = items[position]!![6].toString()
                seq = items[position]!![7].toString()
                title = items[position]!![8].toString()
                wh = items[position]!![9].toString()


                var index_out: Int = 0
                var index_in: Int = 0

                Log.d("item", items[position].toString())
                viewHolder.mTitle.text = title.toString()
                viewHolder.mTitle.isSelected = true

                if (ret == "1")
                    viewHolder.mRet.text = "반품"
                else
                    viewHolder.mRet.visibility = View.GONE

                viewHolder.mCrdate.text = crdate

                viewHolder.mOwner.text = cruser

                viewHolder.mSeq.text = seq


                if (getTitle == "STOCK_IN") {

                    for (i in 0 until WareHouse.getArray()!!.size) {
                        if (in_wh == WareHouse.getArray()!![i][0].toString())
                            index_in = i
                    }

                    var WH_Name = WareHouse.getArray()!![index_in][1].toString()

                    viewHolder.mWh.text = WH_Name

                } else if (getTitle == "STOCK_OUT" && is_move == "0") {

                    for (i in 0 until WareHouse.getArray()!!.size) {
                        if (out_wh == WareHouse.getArray()!![i][0].toString())
                            index_out = i
                    }
                    var WH_Name = WareHouse.getArray()!![index_out][1].toString()

                    viewHolder.mWh.text = WH_Name
                } else if (getTitle == "STOCK_OUT" && is_move == "1") {
                    for (i in 0 until WareHouse.getArray()!!.size) {
                        if (out_wh == WareHouse.getArray()!![i][0].toString())
                            index_out = i

                        if (in_wh == WareHouse.getArray()!![i][0].toString())
                            index_in = i
                    }
                    var WH_Name_Out = WareHouse.getArray()!![index_out][1].toString()
                    var WH_Name_In = WareHouse.getArray()!![index_in][1].toString()
                    viewHolder.mWh.textSize = 10F
                    viewHolder.mWh.text = WH_Name_Out + "-->" + WH_Name_In

                }
            }

            viewHolder.mCardView.setOnClickListener {
                var LOC: String? = null
                if (getTitle == "LOC_STOCK_IN_ORDER")
                    LOC = "LOC_STOCK_IN_ORDER"
                else if (getTitle == "LOC_STOCK_OUT_ORDER")
                    LOC = "LOC_STOCK_OUT_ORDER"

                var mSheetIntent = Intent(context, StockInOutActivity::class.java)
                var mLOCIntent = Intent(context, LOC_StockActivity::class.java)
                mLOCIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mSheetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                when (getTitle) {
                    "STOCK_IN" -> {
                        mSheetIntent.putExtra("title", "STOCK_IN")
                        mSheetIntent.putExtra("seq", seq)
                        mSheetIntent.putExtra("sheet_title", title)

                    }

                    "STOCK_OUT" -> {
                        mSheetIntent.putExtra("title", "STOCK_OUT")
                        mSheetIntent.putExtra("sheet_title", title)
                        mSheetIntent.putExtra("seq", seq)
                        context.startActivity(mSheetIntent)
                    }
                    "STOCK_CHECK" -> {
                        mSheetIntent.putExtra("title", "STOCK_CHECK")
                        mSheetIntent.putExtra("sheet_title", title)
                        mSheetIntent.putExtra("seq", seq)
                        context.startActivity(mSheetIntent)
                    }
                    "EZCHAIN_TRANS" -> {
                        mSheetIntent.putExtra("title", "EZCHAIN_TRANS")
                        mSheetIntent.putExtra("sheet_title", title)
                        mSheetIntent.putExtra("seq", seq)
                        context.startActivity(mSheetIntent)
                    }
                    LOC -> {
                        mLOCIntent.putExtra("title", LOC)
                        mLOCIntent.putExtra("sheet_title", title)
                        mLOCIntent.putExtra("seq", seq)
                        context.startActivity(mLOCIntent)
                    }
                }

            }

        }

        override fun getItemCount(): Int {
            return this.items.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var mTitle: TextView = itemView.findViewById(R.id.INVOICE_LISTVIEW_ITEM_CONTENT)
            var mRet: TextView = itemView.findViewById(R.id.INVOICE_LISTVIEW_ITEM_STATUS)
            var mOwner: TextView = itemView.findViewById(R.id.INVOICE_LISTVIEW_OWNER)
            var mSeq: TextView = itemView.findViewById(R.id.INVOICE_LISTVIEW_ITEM_SEQ)
            var mWh: TextView = itemView.findViewById(R.id.INVOICE_LISTVIEW_ITEM_WH_TEXTVIEW)
            var mCrdate: TextView = itemView.findViewById(R.id.INVOICE_LISTVIEW_ITEM_CRDATE)
            var mBack: RelativeLayout = itemView.findViewById(R.id.INVOICE_LISTVIEW_RELATIVELAYOUT)
            var mCardView: CardView = itemView.findViewById(R.id.SHEET_LIST_ITEM_CARDVIEW)


        }
    }

    private fun doPostConductSheet() {
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
            doGetConductSheet(map, mURL)

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    private fun doGetConductSheet(input: HashMap<String, String>, mUrl: String) {
        progressON(this, null)
        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, mUrl)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                var mData = response.body()!!.data
                var mSheet = mData!!.get("sheet").asJsonArray

                var mSheet_array: ArrayList<Any> = ArrayList()
                for (i in 0 until mSheet!!.size()) {
                    var mObj = mSheet[i].asJsonObject

                    var mTitle = mObj.get("title").toString().replace("\"", "")
                    var mSeq = mObj.get("seq").toString().replace("\"", "")
                    var mWarehouse = mObj.get("warehouse").toString().replace("\"", "")
                    var mWarehouse_seq = mObj.get("warehouse_seq").toString().replace("\"", "")
                    var mCrdate = mObj.get("crdate").toString().replace("\"", "")
                    var mCrtime = mObj.get("crtime").toString().replace("\"", "")

                    mSheet_array = arrayListOf(mTitle, mSeq, mWarehouse, mWarehouse_seq, mCrdate, mCrtime)
                    item.add(mSheet_array)
                }
                SHEET_LIST_RECYCLE_VIEW.adapter = SheetListCardAdapter(applicationContext, item)
                progressOFF()
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                progressOFF()
                Log.e(TAG, t.message.toString())
            }
        })
    }

    private fun doPostStockInSheet(mType: String) {
        try {

            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
            val mUrlType = URLEncoder.encode(mType, "UTF-8")
            val mURL = "api/function.php"
            val map = HashMap<String, String>()




            map["ACTION"] = URLFactory.GetSheetList
            map["AUTHCODE"] = mUrlAuthcode
            map["VERSION"] = "v1"
            map["TYPE"] = mUrlType
            doGetStockInSheet(map, mURL)

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    private fun doGetStockInSheet(input: HashMap<String, String>, Url: String) {
        progressON(this, null)
        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, Url)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                var data = response.body()!!.data
                var errorcode = response.body()!!.errorcode
                item = ArrayList()
                if (errorcode == 4002)
                    Toast.makeText(applicationContext, "등록된 전표가 없습니다", Toast.LENGTH_LONG)
                else {
                    var SheetArray = data!!.get("sheet") as JsonArray
                    Log.d("Sheet data", SheetArray.toString())

                    item = ArrayList()
                    for (i in 0 until SheetArray!!.size()) {

                        var SheetObj = SheetArray[i].asJsonObject

                        var array: ArrayList<Any> = arrayListOf(
                            SheetObj.get("bad")
                            , SheetObj.get("cruser").toString().replace("\"", "")
                            , SheetObj.get("dd").toString().replace("\"", "")
                            , SheetObj.get("in_wh").toString().replace("\"", "")
                            , SheetObj.get("is_move").toString().replace("\"", "")
                            , SheetObj.get("out_wh").toString().replace("\"", "")
                            , SheetObj.get("ret").toString().replace("\"", "")
                            , SheetObj.get("seq").toString().replace("\"", "")
                            , SheetObj.get("title").toString().replace("\"", "")
                            , SheetObj.get("wh").toString().replace("\"", "")
                        )

                        item!!.add(array)

                        Log.d("전표 데이터", item.toString())

                    }

                }
                SHEET_LIST_RECYCLE_VIEW.adapter = SheetListCardAdapter(applicationContext, item)
                progressOFF()
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
            }
        })

    }

    private fun doPostWareHouse() {

        try {
            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
            val mURL = "api/function.php"
            val map = HashMap<String, String>()

            map["ACTION"] = URLFactory.GetWareHouseList
            map["AUTHCODE"] = mUrlAuthcode
            map["VERSION"] = "v1"

            doGetWareHouse(map, mURL)

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

    }

    private fun doGetWareHouse(input: HashMap<String, String>, Url: String) {

        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, Url)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                var data = response.body()!!.data
                var wh_Arr = data!!.get("wh") as JsonArray
                var name: String
                var seq: String
                var code: String

                for (i in 0 until wh_Arr.size()) {
                    var wh_OBJ = wh_Arr[i].asJsonObject

                    name = wh_OBJ.get("name").toString().replace("\"", "")
                    seq = wh_OBJ.get("seq").toString().replace("\"", "")
                    if (wh_OBJ.get("code") == null)
                        WareHouse.setArray(name, "null", seq)
                    else {
                        code = wh_OBJ.get("code").toString().replace("\"", "")
                        WareHouse.setArray(name, code, seq)
                    }
                }
                Log.d("aaaaaaaa", WareHouse.getArray().toString())


            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
            }
        })

    }

    private fun doPostGetLocationWorkSheetList(mType: String) {
        try {
            val mUrlAuthcode = Session.getInstance().authCode
            val mURL = "api/function.php"
            val pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
            val mDomain = pref.getString("domain_save", "")
            val map = HashMap<String, String>()

            map["AUTHCODE"] = mUrlAuthcode
            map["STOCK_MODE"] = mType
            map["VERSION"] = "v1"
            map["ACTION"] = URLFactory.GetLocWorkSheetList
            map["DOMAIN"] = mDomain


            doGetLocationWorkSheetList(map, mURL)

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    private fun doGetLocationWorkSheetList(input: HashMap<String, String>, mUrl: String) {
        progressON(this, null)
        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, mUrl)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                var mData = response.body()!!.data
                var mSheet = mData!!.get("sheet").asJsonArray

                for (i in 0 until mSheet!!.size()) {
                    var mObj = mSheet[i].asJsonObject
                    var mArray: ArrayList<Any> = ArrayList()

                    var mSeq = mObj.get("seq").toString().replace("\"", "")
                    var mTitle = mObj.get("title").toString().replace("\"", "")
                    var mOwner = mObj.get("owner").toString().replace("\"", "")
                    var mCrdate = mObj.get("crdate").toString().replace("\"", "")

                    mArray = arrayListOf(mSeq, mTitle, mOwner, mCrdate)
                    item.add(mArray)
                }
                SHEET_LIST_RECYCLE_VIEW.adapter = SheetListCardAdapter(applicationContext, item)
                progressOFF()
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                progressOFF()
                Log.e("에러에러", t.message)

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
                progressOFF()
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                progressOFF()
                Log.e("에러", t.message)

            }
        })
    }

    private fun doPostGetInspectSheetList() {
        try {

            val mURL = "get_inspect_sheet_list.php"
            val map = HashMap<String, String>()


            var cal: Calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val getTime = sdf.format(cal.time)
            cal.add(Calendar.MONTH, -1).toString()
            val fromTime = sdf.format(cal.time)


            map["to"] = getTime
            map["ex_complete"] = "1"
            map["from"] = fromTime
            map["is_mobile"] = "1"
            map["sheet_type"] = "6"
            map["id"] = "pimz"
            map["user_type"] = "1"
            doGetInspectSheetList(map, mURL)


        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    private fun doGetInspectSheetList(input: HashMap<String, String>, mUrl: String) {
        progressON(this, null)
        val retrofitService = retrofit_ecn.create(Service::class.java)
        val call = retrofitService.postData(input, mUrl)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                var mList = response.body()!!.list

                var array: ArrayList<Any> = ArrayList()
                for (i in 0 until mList!!.size()) {
                    var mObj = mList[i].asJsonObject

                    var mCrdate = mObj.get("crdate").toString().replace("\"", "")
                    var mName = mObj.get("name").toString().replace("\"", "")
                    var mSeq = mObj.get("seq").toString().replace("\"", "")
                    var mSheet = mObj.get("sheet_type").toString().replace("\"", "")
                    var mShop = mObj.get("shop").toString().replace("\"", "")
                    var mShop_name = mObj.get("shop_name").toString().replace("\"", "")
                    var mStatus = mObj.get("status").toString().replace("\"", "")
                    var mWorker = mObj.get("worker").toString().replace("\"", "")
                    array = arrayListOf(mCrdate, mName, mSeq, mSheet, mShop, mShop_name, mStatus, mWorker)
                    item.add(array)
                }

                SHEET_LIST_RECYCLE_VIEW.adapter = SheetListCardAdapter(applicationContext, item)
                progressOFF()
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {

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
                    }
                }
            }
        }
    }

    companion object {
        var item: ArrayList<ArrayList<Any>?> = ArrayList()
        var getTitle: String? = null
        var D = true

    }
}