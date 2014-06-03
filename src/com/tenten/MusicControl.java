package com.tenten;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.LinearLayout;

public class MusicControl extends LinearLayout {

    AudioManager manager;
    public int mBehavior;
    Handler mHandler;
    Context cc;
    public MusicControl(Context c, AttributeSet a) {
        super(c, a);
        cc = c;
        manager = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
        mBehavior = Settings.System.getInt(c.getContentResolver(), "mBehavior", 0);
        mHandler = new Handler();
        SettingsObserver settingsObserver = new SettingsObserver(mHandler);
        settingsObserver.observe();
        whatbehavior();
    }
    class SettingsObserver extends ContentObserver {
        SettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            ContentResolver resolver = getContext().getContentResolver();
            resolver.registerContentObserver(Settings.System.getUriFor(
                    "mBehavior"), false, this);
        }

        public void onChange(boolean selfChange) {
            whatbehavior();
        }
    }

    public void whatbehavior()
    {
        if(mBehavior == 0)
        {
            setVisibility(VISIBLE);
        }
        else if(mBehavior == 1)
        {
            setVisibility(GONE);
        }
        else if(mBehavior == 2)
        {
            if(manager.isMusicActive())
                setVisibility(VISIBLE);
            else
                setVisibility(GONE);
        }
    }

    public void sendMediaButtonEvent(int code) {
        long eventtime = SystemClock.uptimeMillis();

        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, code, 0);
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        getContext().sendOrderedBroadcast(downIntent, null);

        Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, code, 0);
        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
        getContext().sendOrderedBroadcast(upIntent, null);
    }
}
