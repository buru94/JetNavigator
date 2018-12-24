package com.example.pimz.jetnavigator

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_product_manage.*

class ProductManageFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_manage, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        PRODUCT_MANAGE_FRAGMENT_LOC_APPOINT_BTN.setOnClickListener {
            doPlayAudio("menu_loc_set")
            val intent =  Intent (context, Product_LOC_Activity::class.java)
            intent.putExtra("title","LOC_SET")
            startActivity(intent)
        }
        PRODUCT_MANAGE_FRAGMENT_LOC_MOVE_BTN.setOnClickListener {
            doPlayAudio("menu_loc_move")
            val intent =  Intent (context, Product_LOC_Activity::class.java)
            intent.putExtra("title","LOC_MOVE")
            startActivity(intent)
        }
        PRODUCT_MANAGE_FRAGMENT_LOC_CONFIRM_BTN.setOnClickListener {
            doPlayAudio("menu_loc_check")
            val intent =  Intent (context, Product_LOC_CheckActivity::class.java)
            intent.putExtra("title","LOC_CHECK")
            startActivity(intent)
        }
        PRODUCT_MANAGE_FRAGMENT_PRODUCT_LIST_BTN.setOnClickListener {
            doPlayAudio("menu_product_search")
            val intent =  Intent (context, ProductListActivity::class.java)
            intent.putExtra("title","PRODUCT_LIST")
            startActivity(intent)
        }

        PRODUCT_MANAGE_FRAGMENT_PRODUCT_SEARCH_BTN.setOnClickListener {
            doPlayAudio("menu_product_search")
            val intent =  Intent (context, StockArrangeActivity::class.java)
            intent.putExtra("title","PRODUCT_SEARCH")
            startActivity(intent)
        }
        }
    }
