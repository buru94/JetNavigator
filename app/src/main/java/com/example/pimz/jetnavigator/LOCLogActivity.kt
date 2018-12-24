package com.example.pimz.jetnavigator

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.PointMobile.PMSyncService.BluetoothChatService
import com.PointMobile.PMSyncService.SendCommand
import kotlinx.android.synthetic.main.activity_cs_info.*
import kr.co.ezapps.ezsmarty.Data_array
import kr.co.ezapps.ezsmarty.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.HashMap

class  LOCLogActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_cs_info)
        SendCommand.SendCommandInit(mChatService, mHandler)

        item = ArrayList()


        var mProductId = intent.extras.get("mProductId").toString()
        doPostLOCLog(mProductId)


        val recyclerView = findViewById<View>(R.id.recyclerview) as RecyclerView
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
    }

    class LOCLogAdapter(internal var context: Context, internal var items: ArrayList<ArrayList<Any>>?, internal var item_layout: Int) :
        RecyclerView.Adapter<LOCLogAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_layout, null)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            var mCrdate = items!![position][0].toString()
            var mContent= items!![position][1].toString()

            holder.crdate.setText(mCrdate)
            holder.content.setText(mContent)

        }

        override fun getItemCount(): Int {
            return this.items!!.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal var crdate: TextView = itemView.findViewById(R.id.CARD_VIEW_CRDATE_TEXTVIEW)
            internal var content: TextView = itemView.findViewById(R.id.CARD_VIEW_CONTENT_TEXTVIEW)
            internal var cardview: CardView = itemView.findViewById(R.id.cardview)
        }
    }

    private fun doPostLOCLog(mProductId: String) {
        try {
            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
            val mUrlProductId= URLEncoder.encode(mProductId, "UTF-8")
            val mWorker = Session.getInstance().userName.toString()
            val mUuid = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
            val mURL = "api/function.php"
            val map = HashMap<String, String>()

            val mToken = "PRODUCTID=$mUrlProductId|WORKER =$mWorker|UUID=$mUuid"

            map["ACTION"] = URLFactory.GetLocLog
            map["AUTHCODE"] = mUrlAuthcode
            map["VERSION"] = "v1"
            map["TOKEN"] = mToken

            progressON(this, null)
            val retrofitService = retrofit.create(Service::class.java)
            val call = retrofitService.postData_array(map, mURL)
            call.enqueue(object : Callback<Data_array> {
                override fun onResponse(call: Call<Data_array>, response: Response<Data_array>) {
                    var mData = response.body()!!.data
                    var LOCLog_array:ArrayList<Any> = ArrayList()

                    for(i in 0 until mData!!.size()){
                        var mObj = mData[i].asJsonObject

                        var mCrdate = mObj.get("crdate").toString().replace("\"","")
                        var mLocation = mObj.get("location").toString().replace("\"","")

                        LOCLog_array = arrayListOf(mCrdate, mLocation)
                        item!!.add(LOCLog_array)
                    }
                    recyclerview.adapter = LOCLogAdapter(applicationContext, item, R.layout.activity_main)
                    progressOFF()
                }

                override fun onFailure(call: Call<Data_array>, t: Throwable) {
                    Log.e(TAG,t.message.toString())
                    progressOFF()
                }
            })


        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
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
                        //   textview.setText(Barcode);
                    }
                }
            }
        }
    }
    companion object {
        var item:ArrayList<ArrayList<Any>>? = null
        val D = true
    }
}