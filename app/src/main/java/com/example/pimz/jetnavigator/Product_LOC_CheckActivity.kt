package com.example.pimz.jetnavigator

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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product_loc_check.*
import kr.co.ezapps.ezsmarty.Data_array
import kr.co.ezapps.ezsmarty.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.HashMap

class Product_LOC_CheckActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_loc_check)

        var value = intent.extras.get("title")
        title = "상품관리 > " + value



        SendCommand.SendCommandInit(mChatService, mHandler)

        PRODUCT_LOC_CHECK_ACTIVITY_LOC_SEARCH_BTN.setOnClickListener {
            var mBarcode = PRODUCT_LOC_CHECK_ACTIVITY_LOC_EDIT_TEXT.text.toString()
            doPostProductLocationCheck(mBarcode)
        }
        PRODUCT_LOC_CHECK_ACTIVITY_LISTVIEW.setOnItemClickListener { parent, view, position, id ->
            var Detail_intent = Intent(this, DetailProductActivity::class.java)
            Detail_intent.putExtra("mImageUrl", item!![position][0].toString())
            Detail_intent.putExtra("mProduct_id", item!![position][4].toString())
            startActivity(Detail_intent)
        }

    }

    class ProductLocationCheckListViewAdapter(context: Context, item: ArrayList<ArrayList<Any>>?) : BaseAdapter() {
        private val mContext = context
        private val mItem = item

        val mHandler: Handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {

                    StockManageFragment.MESSAGE_BARCODE -> {

                        val BarcodeBuff = msg.obj as ByteArray

                        var Barcode = ""

                        Barcode = String(BarcodeBuff, 0, msg.arg1)
                        if (Barcode.length != 0) {

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
            if (view == null) {
                viewHolder = ViewHolder()
                view = LayoutInflater.from(mContext).inflate(R.layout.search_listview_item, parent, false)
                viewHolder.mImage = view.findViewById(R.id.SEARCH_LIST_IMAGEVIEW)
                viewHolder.mPrname = view.findViewById(R.id.SEARCH_LIST_PRNAME_TEXTVIEW)
                viewHolder.mOption = view.findViewById(R.id.SEARCH_LIST_OPTION_TEXTVIEW)
                viewHolder.mStock = view.findViewById(R.id.SEARCH_LIST_BARCODE_TEXTVIEW)
                viewHolder.mBack = view.findViewById(R.id.SEARCH_LIST_VIEW_ITEM)
                view.tag = viewHolder



            if(position % 2 == 1)
                viewHolder.mBack.setBackgroundResource(R.color.gray)
                else if(position % 2 == 0)
                viewHolder.mBack.setBackgroundResource(R.color.white)

                Picasso.get().load(item!![position][0].toString()).error(R.drawable.ezadmin_title_logo).resize(50, 50).into(viewHolder.mImage)

                viewHolder.mStock.text = mItem!![position]!![3].toString()
                viewHolder.mPrname.text = mItem[position]!![2].toString()
                viewHolder.mOption.text = mItem[position]!![1].toString()



                return view
            }else {
                viewHolder = view.tag as ViewHolder
            }



            if(position % 2 == 1)
                viewHolder.mBack.setBackgroundResource(R.color.gray)
            else if(position % 2 == 0)
                viewHolder.mBack.setBackgroundResource(R.color.white)

            Picasso.get().load(mItem!![position][0].toString()).error(R.drawable.ezadmin_title_logo).resize(50, 50).into(viewHolder.mImage)

            viewHolder.mStock.text = mItem!![position]!![3].toString()
            viewHolder.mPrname.text = mItem[position]!![2].toString()
            viewHolder.mOption.text = mItem[position]!![1].toString()


            return view
        }

        override fun getItem(position: Int) = mItem!![position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = mItem!!.size


        inner class ViewHolder {
            lateinit var mImage: ImageView
            lateinit var mPrname: TextView
            lateinit var mBarcode: TextView
            lateinit var mOption: TextView
            lateinit var mStock: TextView
            lateinit var button: Button
            lateinit var mBack: RelativeLayout
        }

    }

    private fun doPostProductLocationCheck(mLocation: String) {
        try {
            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
            val mUrlWorker = Session.getInstance().userName
            val mUrlLocation = URLEncoder.encode(mLocation, "UTF-8")
            val mUUID = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)

            val mToken = "LOCATION=$mUrlLocation|WORKER=$mUrlWorker|UUID=$mUUID"

            val mURL = "api/function.php"
            val map = HashMap<String, String>()

            map["ACTION"] = URLFactory.GetProductInfoFromLoc
            map["AUTHCODE"] = mUrlAuthcode
            map["VERSION"] = "v1"
            map["TOKEN"] = mToken

            doGetProductLocationCheck(map, mURL)

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

    }

    private fun doGetProductLocationCheck(input: HashMap<String, String>, mUrl: String) {
        progressON(this, null)
        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData_array(input, mUrl)
        call.enqueue(object : Callback<Data_array> {
            override fun onResponse(call: Call<Data_array>, response: Response<Data_array>) {
                var mData = response.body()!!.data
                var mMessage = response.body()!!.message
                item = ArrayList()
                var LOC_array: ArrayList<Any> = ArrayList()
                for (i in 0 until mData!!.size()) {
                    var mObj = mData[i].asJsonObject

                    var mImagePath = mObj.get("img_500").toString().replace("\"", "")
                    var mName = mObj.get("name").toString().replace("\"", "")
                    var mOptions = mObj.get("options").toString().replace("\"", "")
                    var mStock = mObj.get("stock").toString().replace("\"", "")
                    var mProductId = mObj.get("product_id").toString().replace("\"","")

                    LOC_array = arrayListOf(mImagePath, mName, mOptions, mStock,mProductId)
                    item!!.add(LOC_array)
                }

                PRODUCT_LOC_CHECK_ACTIVITY_MESSAGE_EDITTEXT.setHint(mMessage)
                PRODUCT_LOC_CHECK_ACTIVITY_LISTVIEW.adapter = ProductLocationCheckListViewAdapter(applicationContext, item)

                progressOFF()



            }

            override fun onFailure(call: Call<Data_array>, t: Throwable) {
                Log.e(TAG, t.message.toString())
                progressOFF()
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
        var item: ArrayList<ArrayList<Any>>? = ArrayList()
        var adapter: ProductLocationCheckListViewAdapter? = null
    }

}