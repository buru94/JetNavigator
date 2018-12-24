package com.example.pimz.jetnavigator

import android.support.v7.widget.CardView
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class CsInfoAdapter(internal var context: Context, internal var items: ArrayList<Any>?, internal var item_layout: Int) :
    RecyclerView.Adapter<CsInfoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_layout, null)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items!![position].toString()

        holder.content.setText(item)
        holder.crdate.visibility = View.GONE

    }

    override fun getItemCount(): Int {
        return this.items!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var content: TextView = itemView.findViewById(R.id.CARD_VIEW_CONTENT_TEXTVIEW)
        internal var crdate: TextView = itemView.findViewById(R.id.CARD_VIEW_CRDATE_TEXTVIEW)
        internal var cardview: CardView = itemView.findViewById(R.id.cardview)

    }
}
