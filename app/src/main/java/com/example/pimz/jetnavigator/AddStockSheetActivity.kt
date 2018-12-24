package com.example.pimz.jetnavigator

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewParent
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.gson.JsonArray
import kotlinx.android.synthetic.main.activity_sheet_list.*
import kotlinx.android.synthetic.main.dialog_add_stock_in_sheet.*
import kotlinx.android.synthetic.main.dialog_add_stock_out_sheet.*
import kr.co.ezapps.ezsmarty.Data
import kr.co.ezapps.ezsmarty.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class AddStockSheetActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var TYPE = intent.extras.get("TYPE").toString()
        if (TYPE == "STOCK_IN"){
            setContentView(R.layout.dialog_add_stock_in_sheet)

            var In_intent = Intent(this, SheetListActivity::class.java)
            In_intent.putExtra("title", "STOCK_IN")
            In_intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP)

        val item: ArrayList<Any> = ArrayList()
        for (i in 0 until WareHouse.getArray()!!.size) {
            item.add(WareHouse.getArray()!![i][1])
            Spinner_map[WareHouse.getArray()!![i][1].toString()] = WareHouse.getArray()!![i][0].toString()
        }

        val Listadapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, item)
        ADD_SHEET_STOCK_IN_DIALOG_ECN_SPINNER.adapter = Listadapter

        ADD_SHEET_STOCK_IN_DIALOG_CANCEL_BTN.setOnClickListener {
            finish()
        }
        ADD_SHEET_STOCK_IN_DIALOG_ADD_BTN.setOnClickListener {
            var mTitle = ADD_SHEET_STOCK_IN_DIALOG_SHEET_NAME_EDITTEXT.text.toString()
            var mType = "0"
            var mRet = if (ADD_SHEET_STOCK_IN_DIALOG_REFUND_CHECKBOX.isChecked) "1" else "0"
            var mWH = Spinner_map.get(ADD_SHEET_STOCK_IN_DIALOG_ECN_SPINNER.selectedItem.toString())
            var mWorker = Session.getInstance().userName.toString()
            var mWork = ""
            var mMove_wh = ""

            doPostAddStockSheet(mType,mTitle,mRet,mWH,mWork, mMove_wh,mWorker)
            finish()
            startActivity(In_intent)



        }
    }else if(TYPE =="STOCK_OUT") {
            setContentView(R.layout.dialog_add_stock_out_sheet)
            var item: ArrayList<Any> = ArrayList()

            var Out_intent = Intent(this, SheetListActivity::class.java)
            Out_intent.putExtra("title", "STOCK_OUT")
            Out_intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP)

            var ECN_spinner = ADD_SHEET_STOCK_OUT_DIALOG_ECN_SPINNER
            var MOVE_spinner = ADD_SHEET_STOCK_OUT_DIALOG_MOVE_SPINNER
            var WORK_spinner = ADD_SHEET_STOCK_OUT_DIALOG_WORK_SPINNER

            for(i in 0 until WareHouse.getArray()!!.size) {
                item.add(WareHouse.getArray()!![i][1])
                Spinner_map!!.put(WareHouse.getArray()!![i][1].toString(), WareHouse.getArray()!![i][0].toString())
            }

            var mWorkItem:ArrayList<Any> = arrayListOf("출고", "이동")

            val mEcnListadapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, item)
            val mWorkListadapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, mWorkItem)

            ECN_spinner.adapter = mEcnListadapter
            MOVE_spinner.adapter = mEcnListadapter
            WORK_spinner.adapter = mWorkListadapter



            WORK_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if(WORK_spinner.selectedItem == "이동"){
                        ADD_SHEET_STOCK_OUT_DIALOG_MOVE_SPINNER.isEnabled = true
                        ADD_SHEET_STOCK_OUT_DIALOG_MOVE_SPINNER.isClickable = true
                    }else if (WORK_spinner.selectedItem == "출고"){
                        ADD_SHEET_STOCK_OUT_DIALOG_MOVE_SPINNER.isEnabled = false
                        ADD_SHEET_STOCK_OUT_DIALOG_MOVE_SPINNER.isClickable = false
                    }
                }

            }




            ADD_SHEET_STOCK_OUT_DIALOG_CANCEL_BTN.setOnClickListener {
                finish()
            }

            ADD_SHEET_STOCK_OUT_DIALOG_ADD_BTN.setOnClickListener {
                var mTitle = ADD_SHEET_STOCK_OUT_DIALOG_SHEET_NAME_EDITTEXT.text.toString()
                var mType = "1"
                var mRet = if (ADD_SHEET_STOCK_OUT_DIALOG_REFUND_CHECKBOX.isChecked) "1" else "0"
                var mWH = Spinner_map!!.get(ADD_SHEET_STOCK_OUT_DIALOG_ECN_SPINNER.selectedItem.toString())
                var mWork = if(ADD_SHEET_STOCK_OUT_DIALOG_WORK_SPINNER.selectedItem == "이동") "1" else "0"
                var mMove_wh =  Spinner_map!!.get(ADD_SHEET_STOCK_OUT_DIALOG_MOVE_SPINNER.selectedItem.toString())
                var mWorker = Session.getInstance().userName.toString()
                doPostAddStockSheet(mType,mTitle,mRet,mWH,mWork, mMove_wh,mWorker)
                finish()
                startActivity(Out_intent)
            }
        }








    }

    fun doPostAddStockSheet(type: String, title: String, ret: String, wh: String?, work: String, move_wh: String?, worker:String) {
        try {

            val mUrlAuthcode = URLEncoder.encode(Session.getInstance().authCode, "UTF-8")
            val mUrlType = URLEncoder.encode(type, "UTF-8")
            val mUrlTitle = URLEncoder.encode(title, "UTF-8")
            val mUrlRet = URLEncoder.encode(ret, "UTF-8")
            val mUrlWH = URLEncoder.encode(wh, "UTF-8")
            val mUrlWork = URLEncoder.encode(work, "UTF-8")
            val mUrlMove_WH = URLEncoder.encode(move_wh, "UTF-8")
            val mUrlWorker = URLEncoder.encode(worker, "UTF-8")

            val mURL = "api/function.php"
            val map = HashMap<String?, String?>()
            var mToken: String? = null
            if(type == "0") {
                mToken = "TYPE=$mUrlType|TITLE=$mUrlTitle|WH=$mUrlWH|RET=$mUrlRet|WORKER=$mUrlWorker"
            }
            else if(type == "1") {
                mToken =
                    "TYPE=$mUrlType|TITLE=$mUrlTitle|WH=$mUrlWH|RET=$mUrlRet|WORK=$mUrlWork|MOVE_WH=$mUrlMove_WH|WORKER=$mUrlWorker"
            }

            map["ACTION"] = URLFactory.AddStockSheet
            map["AUTHCODE"] = mUrlAuthcode
            map["VERSION"] = "v1"
            map["TOKEN"] = mToken

            val retrofitService = retrofit.create(Service::class.java)
            val call = retrofitService.postSheet(map, mURL)
            call.enqueue(object : Callback<Data> {
                override fun onResponse(call: Call<Data>, response: Response<Data>) {
                    var errorcode = response.body()!!.errorcode

                    if(errorcode == 4003) {
                        Toast.makeText(applicationContext, "이미 저장된 전표입니다", Toast.LENGTH_SHORT).show()
                        doPlayAudio("result_already_set")
                    }
                }
                override fun onFailure(call: Call<Data>, t: Throwable) {}
            })

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    companion object {
        var Spinner_map:HashMap<String, String> = HashMap()


    }
}