package com.example.pimz.jetnavigator;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.PointMobile.PMSyncService.BluetoothChatService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.text.DecimalFormat;
import java.util.Random;


public class BaseActivity extends AppCompatActivity {

    static BluetoothChatService mChatService;
    static BluetoothAdapter mBluetoothAdapter;

    static Boolean WH_FLAG = true;

    AppCompatDialog progressDialog;
    SoundPool sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    static String AudioName = "origin";


    private static final boolean D = true;
    public static final int MESSAGE_BARCODE = 2;
    protected final String TAG = getClass().getSimpleName();


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(URLFactory.serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (D)
            Log.e(TAG, "--- ON PAUSE ---");
    }


    public void progressON(Activity activity, String message) {
        Random random = new Random();
        int rand = (random.nextInt(3) + 1);
        String File_name = "progress_loading_image";
        int resID = getApplicationContext().getResources().getIdentifier(File_name + rand, "drawable", getApplicationContext().getPackageName());

        if (activity == null || activity.isFinishing()) {
            return;
        }


        if (progressDialog != null && progressDialog.isShowing()) {
            progressSET(message);
        } else {

            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.activity_progress_dialog);
            ImageView imageView = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
            imageView.setImageResource(resID);
            progressDialog.show();
            Log.d("PROGRESS", "11111111111111");
        }


        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
        img_loading_frame.setBackgroundResource(resID);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }


    }

    public void progressSET(String message) {

        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }


        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }

    }

    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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

        String number = String.valueOf(num.replace(",", ""));

        return number;
    }

    public void doPlayAudio(String AudioState) {

        SharedPreferences preferences = getSharedPreferences("audiopref", 0);
        AudioName = preferences.getString("AudioName", null);
        if (AudioName == null)
            AudioName = "origin";

        int resID = getApplicationContext().getResources().getIdentifier(AudioName + "_" + AudioState, "raw", getApplicationContext().getPackageName());
        int sound_load = sp.load(this, resID, 1);
        sp.play(sound_load, 0.7f, 0.7f, 1, 0, 1.f);


        int waitLimit = 300;
        int waitCounter = 0;
        int throttle = 10;

        while (sp.play(sound_load, 0.7f, 0.7f, 1, 0, 1.f) == 0 && waitCounter < waitLimit) {
            waitCounter++;
            SystemClock.sleep(throttle);
        }

    }

    public void doPlayAudioCount(int a) throws InterruptedException {
        // a= 258
        int b = a / 100; //2
        int c = a % 100; // 58
        int d = c % 10; //8
        int e = (c - d); //50

        if (b > 0) {
            doPlayAudio("cnt_" + (b * 100));
            Thread.sleep(100);
        }
        if (e > 0) {
            doPlayAudio("cnt_" + e);
            Thread.sleep(100);
        }
        if (d > 0) {
            doPlayAudio("cnt_" + d);
            Thread.sleep(100);
        }
        if (a == 0)
            doPlayAudio("cnt_0");
    }

}

