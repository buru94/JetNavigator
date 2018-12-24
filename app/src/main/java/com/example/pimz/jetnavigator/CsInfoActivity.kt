package com.example.pimz.jetnavigator

import android.os.Bundle
import android.provider.Settings
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.Window
import com.google.gson.JsonArray
import kotlinx.android.synthetic.main.activity_cs_info.*
import kotlinx.android.synthetic.main.activity_trans.*
import kr.co.ezapps.ezsmarty.Data
import kr.co.ezapps.ezsmarty.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


class CsInfoActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_cs_info)

        val recyclerView = findViewById<View>(R.id.recyclerview) as RecyclerView
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager

        var TransNo = intent.extras.get("mTransNo").toString()
        doPostCsInfo(mTransNo = TransNo)


    }

    fun doPostCsInfo(mTransNo: String) {
        try {
            val mUUID = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
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

    fun doGetCsInfo(input: HashMap<String, String>, Url: String) {
        val retrofitService = retrofit.create(Service::class.java)
        val call = retrofitService.postData(input, Url)
        call.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                // 성공
                val CsInfoData = response.body()!!.data
                val CsInfoError = response.body()!!.errorcode
                val CsInfoMessage = response.body()!!.message
                if(CsInfoError == 3008){
                    doPlayAudio("error_trans_no_error")
                    TRANS_ACTIVITY_RESULT_EDIT_TEXT.setHint(CsInfoMessage)
                }
                else if(CsInfoError == 0) {
                    val CsArray =CsInfoData!!.get("cs") as JsonArray
                    Log.d("CsArray", CsArray.toString())
                    Log.d("CsArray", CsArray.size().toString())

                    item = ArrayList()
                    for (i in 0 until CsArray!!.size()) {
                        item.add(CsArray[i])
                    }
                    Log.d("item", item.toString())

                    recyclerview.adapter = CsInfoAdapter(applicationContext, item, R.layout.activity_main)
                }
            }
            override fun onFailure(call: Call<Data>, t: Throwable) {
                // 실패
                Log.getStackTraceString(t)
            }
        })
    }

    companion object {
        var item: ArrayList<Any> = ArrayList()
    }

}
