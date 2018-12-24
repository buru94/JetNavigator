package com.example.pimz.jetnavigator

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.PointMobile.PMSyncService.BluetoothChatService
import com.PointMobile.PMSyncService.SendCommand
import kotlinx.android.synthetic.main.activity_stock_log.*
import kr.co.ezapps.ezsmarty.Data_array
import kr.co.ezapps.ezsmarty.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
import java.util.HashMap

class StockLogActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_log)

        var value = intent.extras.get("title")
        title = "재고로그조회"

        adapter = StockLogListViewAdapter(this, mSheet_array)

        STOCK_LOG_ACTIVITY_SEARCH_BTN.setOnClickListener {
            var mBarcode = STOCK_LOG_ACTIVITY_RESULT_EDIT_TEXT.text.toString()
            doPostStockLog(mBarcode)
        }







    }

    class StockLogListViewAdapter(context: Context, item: ArrayList<ArrayList<Any>>) : BaseAdapter() {
        private val mContext = context
        private val mItem = item

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            lateinit var viewHolder: ViewHolder
            var view = convertView
            if (view == null) {
                viewHolder = ViewHolder()
                view = LayoutInflater.from(mContext).inflate(R.layout.stock_log_list_item, parent, false)
                viewHolder.mCrdate = view.findViewById(R.id.STOCK_LOG_LIST_ITEM_CRDATE)
                viewHolder.mWork = view.findViewById(R.id.STOCK_LOG_LIST_ITEM_WORK)
                viewHolder.mStock = view.findViewById(R.id.STOCK_LOG_LIST_ITEM_STOCK)
                viewHolder.mQty = view.findViewById(R.id.STOCK_LOG_LIST_ITEM_QTY)
                viewHolder.mMemo = view.findViewById(R.id.STOCK_LOG_LIST_ITEM_MEMO)
                view.tag = viewHolder
                Log.d("만들어짐", mItem[position][4].toString())
                viewHolder.mMemo.text = mItem[position][4].toString()
                viewHolder.mCrdate.text = mItem[position][0].toString()
                viewHolder.mWork.text = mItem[position][1].toString()
                viewHolder.mQty.text = mItem[position][2].toString()
                viewHolder.mStock.text = mItem[position][3].toString()
                return view
            } else {
                viewHolder = view.tag as ViewHolder
            }
            viewHolder.mMemo.text = mItem[position][4].toString()
            viewHolder.mCrdate.text = mItem[position][0].toString()
            viewHolder.mWork.text = mItem[position][1].toString()
            viewHolder.mQty.text = mItem[position][2].toString()
            viewHolder.mStock.text = mItem[position][3].toString()
            return view
        }

        override fun getItem(position: Int) = mItem[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = mItem.size

        inner class ViewHolder {
            lateinit var mMemo: TextView
            lateinit var mCrdate: TextView
            lateinit var mWork: TextView
            lateinit var mStock: TextView
            lateinit var mQty: TextView

            lateinit var button: Button
        }
    }

    private fun doPostStockLog(mBarcode: String) {
        val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
        val mUUID = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
        val mUrlUserName = Session.getInstance().userName
        val mBarcode = URLEncoder.encode(mBarcode, "UTF-8")

        var mToken = "BARCODE=$mBarcode|WORKER=$mUrlUserName|UUID=$mUUID"
        val mURL = "api/function.php"
        val map = HashMap<String, String>()

        map["ACTION"] = URLFactory.GetStockLog
        map["AUTHCODE"] = mUrlAuthcode
        map["VERSION"] = "v1"
        map["TOKEN"] = mToken

        doGetStockLog(map, mURL)
    }

    private fun doGetStockLog(input: HashMap<String, String>, mUrl: String) {
        progressON(this, null)
        val retrofitService = retrofit.create(Service::class.java)
        val call: Call<Data_array> = retrofitService.postData_array(input, mUrl)
        call.enqueue(object : Callback<Data_array> {
            override fun onResponse(call: Call<Data_array>, response: Response<Data_array>) {
                var mErrorCode = response.body()!!.errorcode
                if (mErrorCode == 0) {

                    var mData = response.body()!!.data!!

                    var mArray :ArrayList<Any> = ArrayList()
                    for (i in 0 until mData.size()) {
                        var mObj = mData[i].asJsonObject

                        var mCrdate = mObj.get("crdate").toString().replace("\"", "")
                        var mWork = mObj.get("job").toString().replace("\"","")
                        mWork = when(mWork){
                                "arrange" -> "조정"
                                "in" -> "입고"
                                "out" -> "출고"
                                "retin" -> "반품입고"
                                "retout" -> "반품출고"
                                else -> mWork
                                }

                        var mQty = mObj.get("qty").toString().replace("\"","")
                        var mStock = mObj.get("stock").toString().replace("\"","")
                        var mMemo = mObj.get("memo").toString().replace("\"","")


                        mArray = arrayListOf(mCrdate, mWork,mQty,mStock,mMemo)
                        mSheet_array.add(mArray)


                    }

                    STOCK_LOG_ACTIVITY_LISTVIEW.adapter = adapter
                    progressOFF()

                } else {
                    progressOFF()
                    Toast.makeText(applicationContext, "error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Data_array>, t: Throwable) {
                Log.e(TAG, t.message)
                progressOFF()
                Toast.makeText(applicationContext, "실패", Toast.LENGTH_SHORT).show()

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
        var mSheet_array: ArrayList<ArrayList<Any>> = ArrayList()
        var adapter: StockLogListViewAdapter? = null
    }

}