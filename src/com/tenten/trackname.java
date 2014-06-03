package com.tenten;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.provider.Settings;
import android.util.AttributeSet;
import android.widget.TextView;


public class trackname extends TextView {

    public String trakname;
    public Handler mHandler;
    AudioManager man;
    int hidetrackname;
    Context cc;

    public trackname(final Context context, AttributeSet attrs) {
        super(context, attrs);
        cc = context;
        mHandler = new Handler();
        SettingsObserver settingsObserver = new SettingsObserver(mHandler);
        settingsObserver.observe();
        updateInfo();
    }

    class SettingsObserver extends ContentObserver {
        SettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            ContentResolver resolver = getContext().getContentResolver();
            resolver.registerContentObserver(Settings.System.getUriFor(
                    "trackname"), false, this);
            resolver.registerContentObserver(Settings.System.getUriFor(
                    "hidetrackname"), false, this);
        }

        public void onChange(boolean selfChange) {
            updateInfo();

        }
    }

    public void updateInfo() {
        trakname = Settings.System.getString(mContext.getContentResolver(), "trackname");
        hidetrackname = Settings.System.getInt(mContext.getContentResolver(), "hidetrackname", 0);
        man = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        setText(trakname);

        if(hidetrackname == 1)
            setVisibility(GONE);
        else{
			if(man.isMusicActive())
				setVisibility(VISIBLE);
			else
				setVisibility(GONE);
		}
    }
}
