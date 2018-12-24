package com.example.pimz.jetnavigator

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso

class TransListViewAdapter(context: Context, item: ArrayList<ArrayList<Any>?>) : BaseAdapter() {
    private val mContext = context
    private val mItem = item

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        lateinit var viewHolder: ViewHolder
        var view = convertView
        if (view == null) {
            viewHolder = ViewHolder()
            view = LayoutInflater.from(mContext).inflate(R.layout.search_listview_item, parent, false)
            viewHolder.mImage = view.findViewById(R.id.SEARCH_LIST_IMAGEVIEW)
            viewHolder.mPrname = view.findViewById(R.id.SEARCH_LIST_PRNAME_TEXTVIEW)
            viewHolder.mBarcode = view.findViewById(R.id.SEARCH_LIST_BARCODE_TEXTVIEW)
            viewHolder.mOption = view.findViewById(R.id.SEARCH_LIST_OPTION_TEXTVIEW)
            viewHolder.mBack = view.findViewById(R.id.SEARCH_LIST_VIEW_ITEM)
            view.tag = viewHolder

            Picasso.get().load(R.drawable.ezadmin_title_logo).resize(50, 50).into(viewHolder.mImage)

            if (mItem[position]!![2].toString() == "1")
                viewHolder.mBack.setBackgroundResource(R.color.is_cancel)
            else if (mItem[position]!![3].toString() == "1")
                viewHolder.mBack.setBackgroundResource(R.color.is_change)




            viewHolder.mPrname.text = mItem[position]!![7].toString()
            viewHolder.mBarcode.text = mItem[position]!![0].toString()
            viewHolder.mOption.text = mItem[position]!![5].toString()
            return view
        } else {
            viewHolder = view.tag as ViewHolder
        }

        Picasso.get().load(R.drawable.ezadmin_title_logo).resize(50, 50).into(viewHolder.mImage)

        if (mItem[position]!![2].toString() == "1")
            viewHolder.mBack.setBackgroundResource(R.color.is_cancel)
        else if (mItem[position]!![3].toString() == "1")
            viewHolder.mBack.setBackgroundResource(R.color.is_change)



        viewHolder.mPrname.text = mItem[position]!![7].toString()
        viewHolder.mBarcode.text = mItem[position]!![0].toString()
        viewHolder.mOption.text = mItem[position]!![5].toString()
        return view
    }

    override fun getItem(position: Int) = mItem[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = mItem.size

    inner class ViewHolder {
        lateinit var mImage: ImageView
        lateinit var mPrname: TextView
        lateinit var mBarcode: TextView
        lateinit var mOption: TextView
        lateinit var button: Button
        lateinit var mBack: RelativeLayout
    }
}
