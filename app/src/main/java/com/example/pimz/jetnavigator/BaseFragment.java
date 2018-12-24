package com.example.pimz.jetnavigator;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import java.text.DecimalFormat;


public class BaseFragment extends Fragment {

    String AudioName = "origin";
    SoundPool sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    public final String TAG = getClass().getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.v(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    public static String toNumFormat(String num) {
        if (num.equals(""))
            num = "0";

        int number = Integer.parseInt(num.replace(",", ""));

        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(number);
    }

    public static String toString(String num) {
        if (num.equals(""))
            num = "0";

        String  number = String.valueOf(num.replace(",", ""));


        return number;
    }

    public void doPlayAudio(String AudioState){
        SharedPreferences preferences = getContext().getSharedPreferences("audiopref", 0);
        AudioName = preferences.getString("AudioName", "origin");
        int resID = getContext().getResources().getIdentifier(AudioName+"_"+AudioState, "raw", getContext().getPackageName());
        //sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        int sound_load = sp.load(getContext(),resID,1);
        int waitLimit = 1000;
        int waitCounter = 0;
        int throttle = 10;

        while(sp.play(sound_load, 0.7f, 0.7f, 1, 0, 1.f) == 0 && waitCounter < waitLimit){
            waitCounter++; SystemClock.sleep(throttle);
        }



    }
}