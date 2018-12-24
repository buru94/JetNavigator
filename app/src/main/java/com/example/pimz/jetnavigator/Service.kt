package kr.co.ezapps.ezsmarty

import retrofit2.Call
import retrofit2.http.*
import java.util.HashMap

interface Service {

    @FormUrlEncoded
    @POST
    fun postData(@FieldMap param: HashMap<String, String>, @Url path : String):Call<Data>

    @FormUrlEncoded
    @POST
    fun postSheet(@FieldMap param: HashMap<String?, String?>, @Url path : String):Call<Data>

    @FormUrlEncoded
    @POST
    fun postData_array(@FieldMap param: HashMap<String, String>, @Url path : String):Call<Data_array>





    @FormUrlEncoded
    @POST("kyj.php")
    fun postField(@Field("key") param: String): Call<Data>

}
