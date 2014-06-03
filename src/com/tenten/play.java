package com.tenten;

import android.R;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;


public class play extends ImageView {

    public int mControlStyle;
    AudioManager manager;
    Context cc;

    public play(final Context c, final AttributeSet a)
    {
        super(c, a);
        cc = c;
        manager = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
        mControlStyle = Settings.System.getInt(c.getContentResolver(), "mControlStyle", 0);
        whaticon();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new MusicControl(c, a).sendMediaButtonEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                whaticon2();
            }
        });
    }

    public void whaticon()
    {
        if(manager.isMusicActive())
            setImageResource(R.drawable.ic_media_pause);
        else
            setImageResource(R.drawable.ic_media_play);
    }

    public void whaticon2()
    {
        if(!manager.isMusicActive())
        {
            setImageResource(R.drawable.ic_media_pause);
            if(mControlStyle == 0)
                Settings.System.putInt(cc.getContentResolver(), "poke", 1);
        }

        else
        {
            setImageResource(R.drawable.ic_media_play);
            if(mControlStyle == 0)
                Settings.System.putInt(cc.getContentResolver(), "poke", 0);
        }
    }
}
