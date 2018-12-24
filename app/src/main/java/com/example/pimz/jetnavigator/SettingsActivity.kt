package com.example.pimz.jetnavigator

import android.content.Context
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity:BaseActivity(){
    var Audio:String? = null
    var chk1:Boolean = false
    var chk2:Boolean = false
    var chk3:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)





        var pref = getSharedPreferences("audiopref", 0)
        SETTING_ACTIVITY_RADIO1.isChecked = pref.getBoolean("chk1", false)
        SETTING_ACTIVITY_RADIO2.isChecked = pref.getBoolean("chk2", false)
        SETTING_ACTIVITY_RADIO3.isChecked = pref.getBoolean("chk3", false)

        SETTING_ACTIVITY_RADIO1.setOnClickListener {
            doSaveAudioPref()
            doPlayAudio("result_normal")

        }
        SETTING_ACTIVITY_RADIO2.setOnClickListener {
            doSaveAudioPref()
            doPlayAudio("result_normal")

        }
        SETTING_ACTIVITY_RADIO3.setOnClickListener {
            doSaveAudioPref()
            doPlayAudio("result_normal")

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        doSaveAudioPref()
    }

    private fun doSaveAudioPref(){
        when {
            SETTING_ACTIVITY_RADIO1.isChecked-> {
                Audio = "origin"
                chk1 = true
            }
            SETTING_ACTIVITY_RADIO2.isChecked -> {
                Audio = "mijin"
                chk2 = true
            }
            SETTING_ACTIVITY_RADIO3.isChecked -> {
                Audio = "jinho"
                chk3 = true
            }
        }
        Log.d("asdasdasdasd1111", Audio.toString())
        Log.d("asdasdasdasd1111", chk1.toString())
        Log.d("asdasdasdasd1111", chk2.toString())
        Log.d("asdasdasdasd1111", chk3.toString())

        var audiopref = getSharedPreferences("audiopref",Context.MODE_PRIVATE)
        var mAudiopref = audiopref.edit()
        mAudiopref.putString("AudioName",  Audio)
        mAudiopref.putBoolean("chk1", chk1)
        mAudiopref.putBoolean("chk2", chk2)
        mAudiopref.putBoolean("chk3", chk3)
        mAudiopref.commit()
    }
}