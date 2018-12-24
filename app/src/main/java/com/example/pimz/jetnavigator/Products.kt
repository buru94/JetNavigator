package com.example.pimz.jetnavigator

import android.media.audiofx.AudioEffect
import android.support.v7.app.AppCompatActivity
import com.google.gson.JsonArray
import org.json.JSONArray
import java.io.Serializable


class Products : BaseActivity(), Serializable {


    private var Name: String? = null
    private var Product_id: String? = null
    private var Options: String? = null
    private var Enable_sale: String? = null
    private var Imageurl: String? = null
    private var Barcode: String? = null
    private var Enable_stock: String? = null
    private var Multi_barcode: String? = null
    private var ProductInfo_array: ArrayList<Any>? = ArrayList()
    private var Selected:Boolean = false
    private var cnt = 1

    fun setCnt(cnt : Int){
        this.cnt = cnt
    }
    fun getCnt() : Int {
        return cnt
    }
    fun setSelected(Selected: Boolean){
        this.Selected = Selected
    }
    fun getSelected(): Boolean?{
        return Selected
    }
    fun setArray(img_500: String, name: String, option: String, count: Int, barcode: String, product_id: String, selected: Boolean) {
        this.ProductInfo_array = arrayListOf(img_500, name, option, count, barcode, product_id, selected)
    }

    fun getArray(): ArrayList<Any>? {
        return ProductInfo_array
    }

    fun getName(): String? {
        return Name
    }

    fun setName(Name: String) {
        this.Name = Name
    }

    fun getProduct_id(): String? {
        return Product_id
    }

    fun setProduct_id(Product_id: String) {
        this.Product_id = Product_id
    }

    fun getOptions(): String? {
        return Options
    }

    fun setOptions(Options: String) {
        this.Options = Options
    }

    fun getEnable_sale(): String? {
        return Enable_sale
    }

    fun setEnable_sale(Enable_sale: String) {
        this.Enable_sale = Enable_sale
    }

    fun getMulti_barcode(): String? {
        return Multi_barcode
    }

    fun setMulti_barcode(Multi_barcode: String) {
        this.Multi_barcode = Multi_barcode
    }

    fun getImageurl(): String? {
        return Imageurl
    }

    fun setImageurl(Imageurl: String) {
        this.Imageurl = Imageurl
    }

    fun getBarcode(): String? {
        return Barcode
    }

    fun setBarcode(barcode: String) {
        this.Barcode = barcode
    }

    fun getEnable_stock(): String? {
        return Enable_stock
    }

    fun setEnable_stock(Enable_stock: String?) {
        this.Enable_stock = Enable_stock
    }
}
