package com.example.pimz.jetnavigator

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceActivity
import android.util.Log


class PreferenceActivity :  PreferenceActivity(), Preference.OnPreferenceClickListener,
    Preference.OnPreferenceChangeListener {


    private val mListPreference: ListPreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.xml.pref_settings)
        addPreferencesFromResource(R.xml.pref_settings)

        var pAudio = findPreference("AudioName") as Preference
        pAudio.onPreferenceChangeListener = this

        BaseActivity.AudioName = "jinho"

    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        return false
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {

        var value: String = newValue as String

        if (preference == mListPreference) {
            var ListPref: ListPreference = preference as ListPreference
            var index: Int = ListPref.findIndexOfValue(value)
            mListPreference!!.summary = if (index >= 0) ListPref.entries[index] else null
            //val AudioName = getSharedPreferences("pref", Activity.MODE_PRIVATE)
            var audiopref = getSharedPreferences("audiopref",0)
            var mAudiopref = audiopref.edit()
            mAudiopref.putString("AudioName",  ListPref.entries[index].toString())
            mAudiopref.commit()
            Log.d("asdasdasdasd1111", ListPref.entries[index].toString())
        }
        return false
    }

}