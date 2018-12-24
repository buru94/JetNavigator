package com.example.pimz.jetnavigator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso

class DetailProductListAdapter(context: Context, item: ArrayList<ArrayList<Any>?>) : BaseAdapter() {
    private val mContext = context
    private val mItem = item
    override fun getCount(): Int {
            return mItem.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any? {
        return mItem[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        lateinit var viewHolder : ViewHolder
        var view = convertView

        if (view == null){
            viewHolder = ViewHolder()
            view = LayoutInflater.from(mContext).inflate(R.layout.activity_detail_product_list_item,parent,false)
            viewHolder.mPrId = view.findViewById(R.id.DETAIL_PRODUCT_ACTIVITY_LIST_ITEM_PRODUCT_ID)
            viewHolder.mPrSoldOut = view.findViewById(R.id.DETAIL_PRODUCT_ACTIVITY_LIST_ITEM_PRODUCT_SOLDOUT)
            viewHolder.mTempSoldOut = view.findViewById(R.id.DETAIL_PRODUCT_ACTIVITY_LIST_ITEM_PRODUCT_TEMPORARILY_SOLDOUT)
            viewHolder.mBarcode = view.findViewById(R.id.DETAIL_PRODUCT_ACTIVITY_LIST_ITEM_PRODUCT_BARCODE)
            viewHolder.mOption = view.findViewById(R.id.DETAIL_PRODUCT_ACTIVITY_LIST_ITEM_PRODUCT_OPTION)
            viewHolder.mStock = view.findViewById(R.id.DETAIL_PRODUCT_ACTIVITY_LIST_ITEM_PRODUCT_CURRENT_STOCK)

            view.tag = viewHolder

            viewHolder.mPrId.text = mItem[position]!![4].toString()
            viewHolder.mBarcode.text = mItem[position]!![0].toString()
            viewHolder.mOption.text = mItem[position]!![3].toString()
            viewHolder.mPrSoldOut.setImageResource(R.color.background)
            viewHolder.mTempSoldOut.setImageResource(R.color.background)
            viewHolder.mTempSoldOut.visibility = View.VISIBLE
            viewHolder.mPrSoldOut.visibility = View.VISIBLE
            if(mItem[position]!![1].toString() != "1" && mItem[position]!![7].toString() != "1") {
                viewHolder.mTempSoldOut.visibility = View.GONE
                Picasso.get().load(R.drawable.soldout).into(viewHolder.mPrSoldOut)
            }
            else if(mItem[position]!![7].toString() == "1" && mItem[position]!![1].toString() == "1") {
                viewHolder.mPrSoldOut.visibility = View.GONE
                Picasso.get().load(R.drawable.temp_soldout).into(viewHolder.mTempSoldOut)
            }
            else if(mItem[position]!![1].toString() != "1" && mItem[position]!![7].toString() == "1"){
                viewHolder.mPrSoldOut.layoutParams.height = 60
                viewHolder.mTempSoldOut.layoutParams.height = 60
                Picasso.get().load(R.drawable.soldout).into(viewHolder.mPrSoldOut)
                Picasso.get().load(R.drawable.temp_soldout).into(viewHolder.mTempSoldOut)
            }
            viewHolder.mStock.text = mItem[position]!![6].toString()

            return view
        }else{
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.mPrId.text = mItem[position]!![4].toString()
        viewHolder.mBarcode.text = mItem[position]!![0].toString()
        viewHolder.mOption.text = mItem[position]!![3].toString()
        viewHolder.mPrSoldOut.setImageResource(R.color.background)
        viewHolder.mTempSoldOut.setImageResource(R.color.background)
        viewHolder.mTempSoldOut.visibility = View.VISIBLE
        viewHolder.mPrSoldOut.visibility = View.VISIBLE
        if(mItem[position]!![1].toString() != "1" && mItem[position]!![7].toString() != "1") {
            viewHolder.mTempSoldOut.visibility = View.GONE
            Picasso.get().load(R.drawable.soldout).into(viewHolder.mPrSoldOut)
        }
        else if(mItem[position]!![7].toString() == "1" && mItem[position]!![1].toString() == "1") {
            viewHolder.mPrSoldOut.visibility = View.GONE
            Picasso.get().load(R.drawable.temp_soldout).into(viewHolder.mTempSoldOut)
        }
        else if(mItem[position]!![1].toString() != "1" && mItem[position]!![7].toString() == "1"){
            viewHolder.mPrSoldOut.layoutParams.height = 60
            viewHolder.mTempSoldOut.layoutParams.height = 60
            Picasso.get().load(R.drawable.soldout).into(viewHolder.mPrSoldOut)
            Picasso.get().load(R.drawable.temp_soldout).into(viewHolder.mTempSoldOut)
        }
        viewHolder.mStock.text = mItem[position]!![6].toString()
        return  view
    }
    inner class ViewHolder{
        lateinit var mPrId : TextView
        lateinit var mPrSoldOut : ImageView
        lateinit var mTempSoldOut : ImageView
        lateinit var mBarcode: TextView
        lateinit var mOption : TextView
        lateinit var mStock : TextView
        lateinit var button : Button
    }

}
