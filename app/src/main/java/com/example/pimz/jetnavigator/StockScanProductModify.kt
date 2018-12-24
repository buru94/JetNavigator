package com.example.pimz.jetnavigator

import android.app.Activity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_stock_in_out.*
import kotlinx.android.synthetic.main.dialog_stock_scan_product_modify.*
import android.content.Intent
import java.io.Serializable


class StockScanProductModify : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_stock_scan_product_modify)
        var item = intent.extras.get("product") as ArrayList<Products>
        var position = intent.extras.get("position") as Int
        var cnt = item[position].getCnt().toString()
        var name = item[position].getName().toString()
        var barcode= item[position].getBarcode().toString()
        var product_id= item[position].getProduct_id().toString()



        STOCK_SCAN_PRODUCT_MODIFY_DIALOG_SCAN_COUNT_EDITTEXT.setText(cnt)
        STOCK_SCAN_PRODUCT_MODIFY_DIALOG_BARCODE_TEXTVIEW.setText(barcode)
        STOCK_SCAN_PRODUCT_MODIFY_DIALOG_PRODUCT_NAME_TEXTVIEW.setText(name)
        STOCK_SCAN_PRODUCT_MODIFY_DIALOG_BARCODE_TEXTVIEW.setText(barcode)
        STOCK_SCAN_PRODUCT_MODIFY_DIALOG_PRODUCT_ID_TEXTVIEW.setText(product_id)

        STOCK_SCAN_PRODUCT_MODIFY_DIALOG_CANCEL_BTN.setOnClickListener {
            finish()
        }
        STOCK_SCAN_PRODUCT_MODIFY_DIALOG_DELETE_BTN.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("position", position)
            resultIntent.putExtra("result", "Delete")
            setResult(Activity.RESULT_OK, resultIntent)

            finish()

        }
        STOCK_SCAN_PRODUCT_MODIFY_DIALOG_MODIFY_BTN.setOnClickListener {

            var mScan_cnt = STOCK_SCAN_PRODUCT_MODIFY_DIALOG_SCAN_COUNT_EDITTEXT.text.toString()
            item[position].setCnt(Integer.parseInt(mScan_cnt))

            val resultIntent = Intent()
            resultIntent.putExtra("cnt",mScan_cnt)
            resultIntent.putExtra("position", position)
            resultIntent.putExtra("result", "Modify")
            setResult(Activity.RESULT_OK, resultIntent)

            finish()
        }


    }
}