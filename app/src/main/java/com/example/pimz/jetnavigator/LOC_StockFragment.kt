package com.example.pimz.jetnavigator

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_loc_stock_manage.*

class LOC_StockFragment : BaseFragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loc_stock_manage, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
     LOC_STOCK_FRAGMENT_LOC_IN_DIRECTON_BTN.setOnClickListener{
         doPlayAudio("menu_stock_in")
         val intent = Intent(context, SheetListActivity::class.java)
         intent.putExtra("title","LOC_STOCK_IN_ORDER")
         startActivity(intent)
     }
     LOC_STOCK_FRAGMENT_LOC_OUT_DIRECTION_BTN.setOnClickListener{
         doPlayAudio("menu_stock_out")
         val intent = Intent(context, SheetListActivity::class.java)
         intent.putExtra("title","LOC_STOCK_OUT_ORDER")
         startActivity(intent)
     }
     LOC_STOCK_FRAGMENT_LOC_STOCK_IN_BTN.setOnClickListener {
         doPlayAudio("menu_stock_in")
         val intent = Intent(context, LOC_StockActivity::class.java)
         intent.putExtra("title","LOC_STOCK_IN")
         startActivity(intent)
     }
     LOC_STOCK_FRAGMENT_LOC_STOCK_OUT_BTN.setOnClickListener{
         doPlayAudio("menu_stock_out")
         val intent = Intent(context, LOC_StockActivity::class.java)
         intent.putExtra("title","LOC_STOCK_OUT")
         startActivity(intent)
     }
     LOC_STOCK_FRAGMENT_LOC_STOCK_CHECK_BTN.setOnClickListener {
         doPlayAudio("menu_stock_conduct")
  //       val intent = Intent(context, LOC_StockCheckActivity::class.java)
  //     intent.putExtra("title","LOC_STOCK_CHECK")
  //         startActivity(intent)
     }

    }
}