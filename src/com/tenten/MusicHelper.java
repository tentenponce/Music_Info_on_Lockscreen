package com.tenten;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;

public class MusicHelper extends View {
    public AudioManager am;

    public String mTrack = null;
    public static long mSongId = 0;

    public MusicHelper(final Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        IntentFilter iF = new IntentFilter();
        iF.addAction("com.android.music.playstatechanged");
        iF.addAction("com.android.music.metachanged");
        mContext.registerReceiver(mMusicReceiver, iF);
        IntentFilter iF2 = new IntentFilter();
        iF2.addAction("android.hardware.usb.action.USB_STATE");
        mContext.registerReceiver(usb, iF2);
    }


    public BroadcastReceiver mMusicReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            mTrack = intent.getStringExtra("track");
            mSongId = intent.getLongExtra("id", 0);
            updateInfo();
        }
    };

    public BroadcastReceiver usb = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Boolean sb = intent.getExtras().getBoolean("connected");
            Settings.System.putInt(mContext.getContentResolver(), "usb" , sb ? 1:0);
        }
    };

    public void updateInfo() {
        Settings.System.putString(mContext.getContentResolver(), "trackname", mTrack);
        Settings.System.putLong(mContext.getContentResolver(), "id", mSongId);
    }
}
