package com.tenten;

import android.R;
import android.content.*;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

public class prev extends ImageView {

    public int mControlStyle;
	AudioManager manager;
    Handler mHandler;
    public int puke;

    public prev(final Context c, final AttributeSet a)
    {
        super(c, a);
        setImageResource(R.drawable.ic_media_previous);

		manager = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
        mControlStyle = Settings.System.getInt(c.getContentResolver(), "mControlStyle", 0);
        puke = Settings.System.getInt(c.getContentResolver(), "poke", 0);

        mHandler = new Handler();
        SettingsObserver settingsObserver = new SettingsObserver(mHandler);
        settingsObserver.observe();

        WhatStyle();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new MusicControl(c, a).sendMediaButtonEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
            }
        });
    }

    class SettingsObserver extends ContentObserver {
        SettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            ContentResolver resolver = getContext().getContentResolver();
            resolver.registerContentObserver(Settings.System.getUriFor(
                    "mControlStyle"), false, this);
            resolver.registerContentObserver(Settings.System.getUriFor(
                    "poke"), false, this);
        }

        public void onChange(boolean selfChange) {
            if(mControlStyle == 0)
                poke();
        }
    }

	public void WhatStyle()
	{
		if(mControlStyle == 1)
			setVisibility(GONE);			
		else if(mControlStyle == 2)
			setVisibility(VISIBLE);
	}

    public void poke()
    {
        if(puke == 1)
            setVisibility(VISIBLE);
        else
            setVisibility(GONE);
    }

}
