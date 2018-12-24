package com.example.pimz.jetnavigator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_start.*
import java.text.NumberFormat

class StartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var Price: Long?= 1234
        var tf = NumberFormat.getInstance()
        START_FRAGMENT_REV_TEXT_VIEW.text= tf.format(Price)
        START_FRAGMENT_INVOICE_TEXT_VIEW.text= tf.format(Price)
        START_FRAGMENT_TRANS_TEXT_VIEW.text= tf.format(Price)
        START_FRAGMENT_TODAY_TRANS_PER_TODAY_VALUE.text = tf.format(Price) + "건"
        START_FRAGMENT_TODAY_TRANS_PER_ALL_VALUE.text = tf.format(Price) + "건"

    }
}