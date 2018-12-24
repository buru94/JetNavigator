package com.example.pimz.jetnavigator

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.UnsupportedEncodingException
import java.util.*
import kotlin.collections.ArrayList

class StockEzChainActivity : BaseActivity() {

    internal var retrofit_ecn = Retrofit.Builder()
        .baseUrl(URLFactory.ecnUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_check)  //재고실사 레이아웃


        value = intent.extras.get("title").toString()
        seq = intent.extras.get("seq").toString()
        Log.d("SEQ", seq.toString())


        adapter = EzChainListViewAdapter(this, item)
        doPostGetInspectSheetItem()

        var mBarcode = STOCK_CHECK_ACTIVITY_BARCODE_EDITTEXT.text.toString()

        TRANS_EZCHAIN_ACTIVITY_SHOP_NAME_LINEARLAYOUT.visibility = View.VISIBLE

    }

    private fun doPostGetInspectSheetItem() {
        try {
            val mURL = "get_inspect_sheet_item.php"
            val map = HashMap<String, String>()

            map["is_mobile"] = "1"
            map["sheet_type"] = "5"
            map["id"] = "pimz"
            map["sheet_seq"] = seq.toString()
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
                var array: ArrayList<Any>
                var mGroup = response.body()!!.group.toString().replace("\"", "")
                var mShop = response.body()!!.shop.toString().replace("\"","")
                var mTotal_scan = response.body()!!.total_new.toString().replace("\"", "")
                var mTotal_qty = response.body()!!.total_old.toString().replace("\"", "")
                for (i in 0 until mList!!.size()) {
                    var mObj = mList[i].asJsonObject

                    var mBarcode = mObj.get("barcode").toString().replace("\"", "")
                    var mName = mObj.get("name").toString().replace("\"", "")
                    var mNew_qty = mObj.get("new_qty").toString().replace("\"", "")
                    var mOld_qty = mObj.get("old_qty").toString().replace("\"", "")
                    var mOptions = mObj.get("options").toString().replace("\"", "")
                    var mProduct_id = mObj.get("product_id").toString().replace("\"", "")

                    array = arrayListOf(mBarcode, mName, mNew_qty, mOld_qty, mOptions, mProduct_id)
                    item.add(array)

                }

                TRANS_EZCHAIN_ACTIVITY_GROUP_NAME_TEXTVIEW.setText(mGroup)
                TRANS_EZCHAIN_ACTIVITY_SHOP_NAME_TEXTVIEW.setText(mShop)
                STOCK_CHECK_ACTIVITY_TOTAL_SCAN_TEXTVIEW.setText(mTotal_scan)
                STOCK_CHECK_ACTIVITY_TOTAL_QTY_TEXTVIEW.setText(mTotal_qty)

                STOCK_CHECK_ACTIVITY_LISTVIEW.adapter = adapter
                progressOFF()
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                progressOFF()
            }
        })

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
            var mNew_qty = item[position]!![2].toString()
            var mOld_qty = item[position]!![3].toString()
            var mOptions = item[position]!![4].toString()
            var mProduct_id = item[position]!![5].toString()

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


                Picasso.get().load(R.drawable.ezadmin_title_logo).resize(50, 50)
                    .into(viewHolder.mImage)
                if (position % 2 == 1)
                    viewHolder.mBack.setBackgroundResource(R.color.gray)
                else
                    viewHolder.mBack.setBackgroundResource(R.color.white)
                /*
                if (mSelected)
                    viewHolder.mBack.setBackgroundResource(R.color.scanning)
                    */


                viewHolder.mPrname.text = mName
                viewHolder.mOption.text = mOptions
                viewHolder.mQty.text = mOld_qty
                viewHolder.mScan.text = mNew_qty



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
            /*
                if (mSelected)
                    viewHolder.mBack.setBackgroundResource(R.color.scanning)
                    */

            viewHolder.mPrname.text = mName
            viewHolder.mOption.text = mOptions
            viewHolder.mQty.text = mOld_qty
            viewHolder.mScan.text = mNew_qty




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

    private fun doCountNumber(mBarcode: String, mIndex: Int) {
        var Num = Integer.parseInt(mBarcode.substring(1, 4))

        when (Num) {
            in 100..999 -> Num_cnt = Num
            in 10..99 -> Num_cnt += Num
            in 1..9 -> Num_cnt += Num
        }
        item[mIndex][4] = Num_cnt
        adapter!!.notifyDataSetChanged()

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
        var value: String? = null
        var seq: String? = null

        var item: ArrayList<ArrayList<Any>> = ArrayList()

        var CODE: String? = null

        var Num_cnt = 0

        var adapter:EzChainListViewAdapter? = null
    }
}