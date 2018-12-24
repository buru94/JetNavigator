package kr.co.ezapps.ezsmarty

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONObject
import java.util.*

//response가 object일 경우
class Data {
//로그인 정보
    var authcode: String? = null
    var errorcode: Int? = null
    var message: String? = null
    var username: String? = null
    var config: Any ?= null
    var svc: Any? = null
    var name: Any?= null
    //product 정보
    var products: JsonArray? = null
    var productInfo: JsonObject? = null
    var barcode: Any? = null
    var data: JsonObject? = null
    //sheet 정보
    var sheet: JsonArray? = null

    //ecn_sheet 정보
    var list : JsonArray? = null
    var group:JsonElement? = null
    var shop:JsonElement? = null
    var total_new:JsonElement? = null
    var total_old:JsonElement? = null
    var error:Int? = null
    var title:String? =null


}
// response가 array일 경우
class Data_array{
    var data:JsonArray? = JsonArray()
    var errorcode: Int? = null
    var sheet:JsonArray? = JsonArray()
    var list : JsonArray? = JsonArray()
    var message:String? = null

}
