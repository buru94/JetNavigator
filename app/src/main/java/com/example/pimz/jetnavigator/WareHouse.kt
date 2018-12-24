package com.example.pimz.jetnavigator

object WareHouse{
    private  var name: String? = null
    private  var seq: String? = null
    private  var code: String? = null
    private  var WH_Array:ArrayList<ArrayList<Any>>? = ArrayList()
    private  var array:ArrayList<Any>? = ArrayList()
    fun setArray(name: String, code:String , seq:String) {
        array = arrayListOf(seq, name, code)
        this.WH_Array!!.add(array!!)
    }
    fun getArray() :ArrayList<ArrayList<Any>>? {
        return WH_Array
    }
    fun setName(name : String) {
        this.name = name
    }
    fun getName(): String? {
        return name
    }
    fun setSeq(seq : String) {
        this.seq = seq
    }
    fun getSeq(): String? {
        return seq
    }
    fun setCode(code : String) {
        this.code = code
    }
    fun getCode(): String? {
        return code
    }
}