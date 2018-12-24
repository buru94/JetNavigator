package com.example.pimz.jetnavigator

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_custom_dialog.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_trans.*


class CustomDialogActivity(context: Context) : Dialog(context) {


    private val connect_error = "NOT_CONNECT_NETWORK"
    private val info_error = "WRONG_INFO"
    fun callFunction(ID: String) {
        var title:String? = null
        var body:String? = null
        val dlg = Dialog(context)

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dlg.setContentView(R.layout.activity_custom_dialog)


        val CUSTOM_DIALOG_ACTIVITY_TITLE_TEXT_VIEW =
            dlg.findViewById<View>(R.id.CUSTOM_DIALOG_ACTIVITY_TITLE_TEXT_VIEW) as TextView
        val CUSTOM_DIALOG_ACTIVITY_BODY_TEXT_VIEW =
            dlg.findViewById<View>(R.id.CUSTOM_DIALOG_ACTIVITY_BODY_TEXT_VIEW) as TextView
        val CUSTOM_DIALOG_ACTIVITY_SUMMIT_BUTTON =
            dlg.findViewById<View>(R.id.CUSTOM_DIALOG_ACTIVITY_SUMMIT_BUTTON) as Button
        val CUSTOM_DIALOG_ACTIVITY_CANCEL_BUTTON =
            dlg.findViewById<View>(R.id.CUSTOM_DIALOG_ACTIVITY_CANCEL_BUTTON) as Button

        if (ID === connect_error) {
             title = "네트워크 연결 오류"
             body = "단말기와 네트워크간의 연결을 확인해주세요"
            CUSTOM_DIALOG_ACTIVITY_TITLE_TEXT_VIEW.text = title
            CUSTOM_DIALOG_ACTIVITY_BODY_TEXT_VIEW.text = body
            dlg.show()
            CUSTOM_DIALOG_ACTIVITY_SUMMIT_BUTTON.setOnClickListener { dlg.dismiss() }
            CUSTOM_DIALOG_ACTIVITY_CANCEL_BUTTON.setOnClickListener { dlg.dismiss() }
        } else if (ID === info_error) {
             title = "로그인 실패"
             body = "아이디 또는 비밀번호를 다시 확인하세요.\n등록되지 않은 아이디이거나, \n아이디 또는 비밀번호를 잘못 입력하셨습니다."
            CUSTOM_DIALOG_ACTIVITY_TITLE_TEXT_VIEW.text = title
            CUSTOM_DIALOG_ACTIVITY_BODY_TEXT_VIEW.text = body
            dlg.show()
            CUSTOM_DIALOG_ACTIVITY_SUMMIT_BUTTON.setOnClickListener {
                dlg.dismiss()
            }
        } else if (ID === "LOG_OUT") {
             title = "LOGOUT"
             body = "로그아웃 하시겠습니까?"
            CUSTOM_DIALOG_ACTIVITY_TITLE_TEXT_VIEW.text = title
            CUSTOM_DIALOG_ACTIVITY_BODY_TEXT_VIEW.text = body
            CUSTOM_DIALOG_ACTIVITY_CANCEL_BUTTON.visibility = View.VISIBLE
            dlg.show()

            CUSTOM_DIALOG_ACTIVITY_SUMMIT_BUTTON.setOnClickListener {
                System.exit(0)
                dlg.dismiss()
            }
            CUSTOM_DIALOG_ACTIVITY_CANCEL_BUTTON.setOnClickListener {
                dlg.dismiss()
            }
        } else if (ID === "FINISH") {
            title = "EXIT"
            body = "종료 하시겠습니까?"
            CUSTOM_DIALOG_ACTIVITY_TITLE_TEXT_VIEW.text = title
            CUSTOM_DIALOG_ACTIVITY_BODY_TEXT_VIEW.text = body
            CUSTOM_DIALOG_ACTIVITY_CANCEL_BUTTON.visibility = View.VISIBLE
            dlg.show()

            CUSTOM_DIALOG_ACTIVITY_SUMMIT_BUTTON.setOnClickListener {
                dlg.dismiss()
                ActivityCompat.finishAffinity(Activity())
                System.runFinalization()
                System.exit(0)

            }
            CUSTOM_DIALOG_ACTIVITY_CANCEL_BUTTON.setOnClickListener {
                dlg.dismiss()
            }
        } else if( ID == "EZCHAIN_TRANS_COMLETE_ERROR"){
            title = "오류"
            body = "작업완료는 이지체인 또는 재고실사 프로그램에서 가능합니다."
            CUSTOM_DIALOG_ACTIVITY_TITLE_TEXT_VIEW.text = title
            CUSTOM_DIALOG_ACTIVITY_BODY_TEXT_VIEW.text = body
            dlg.show()

            CUSTOM_DIALOG_ACTIVITY_SUMMIT_BUTTON.setOnClickListener {
                dlg.dismiss()
            }

        }

    }

}




