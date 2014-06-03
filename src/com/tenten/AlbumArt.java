package com.tenten;

import android.content.*;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.android.internal.R;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AlbumArt extends ImageView {

    public Handler mHandler;
    public Context mContext;
    public final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
    public final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    public Bitmap mCachedBit = null;
    public AudioManager man;

    public AlbumArt(Context context, AttributeSet attributeSet)  {
        super(context, attributeSet);
        mContext = context;
        mHandler = new Handler();
        SettingsObserver settingsObserver = new SettingsObserver(mHandler);
        settingsObserver.observe();
        man = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int usb = Settings.System.getInt(mContext.getContentResolver(), "usb", 0);
        if(usb == 0 && man.isMusicActive())
            updateInfo();
        else
            setVisibility(GONE);
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
                    "hidealbumart"), false, this);
            resolver.registerContentObserver(Settings.System.getUriFor(
                    "usb"), false, this);
        }

        public void onChange(boolean selfChange) {
            int usb = Settings.System.getInt(mContext.getContentResolver(), "usb", 0);
            if(usb == 0)
                updateInfo();
            else
                setVisibility(GONE);
        }
    }

    public void updateInfo() {
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Media.getContentUri("external"), new String[]{MediaStore.Audio.Media.ALBUM_ID}, null, null, null);
        long song_id = Settings.System.getLong(mContext.getContentResolver(), "id", 0);
        if (cursor.moveToFirst())
        {
            long albumId = cursor.getInt(0);
            Bitmap bm = getArtwork(mContext, song_id, albumId, false);
            if (bm == null) {
                bm = getArtwork(mContext, song_id, -1);
            }

            int hidealbumart = Settings.System.getInt(mContext.getContentResolver(), "hidealbumart", 0);
            if(hidealbumart == 1)
                setVisibility(GONE);
            else{
                if(bm != null){
                    setImageBitmap(bm);
                    setVisibility(VISIBLE);
                }
            }
        }
    }


    public Bitmap getArtwork(Context context, long song_id, long album_id) {
        return getArtwork(context, song_id, album_id, true);
    }

    public Bitmap getArtwork(Context context, long song_id, long album_id,boolean allowdefault) {

        if (album_id < 0) {
            // This is something that is not in the database, so get the album art directly
            // from the file.
            if (song_id >= 0) {
                Bitmap bm = getArtworkFromFile(context, song_id, -1);
                if (bm != null) {
                    return bm;
                }
            }
            if (allowdefault) {
                return getDefaultArtwork(context);
            }
            return null;
        }

        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, sBitmapOptions);
            } catch (FileNotFoundException ex) {

                Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                if (bm != null) {
                    if (bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                        if (bm == null && allowdefault) {
                            return getDefaultArtwork(context);
                        }
                    }
                } else if (allowdefault) {
                    bm = getDefaultArtwork(context);
                }
                return bm;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ignored) {
                }
            }
        }

        return null;
    }

    private Bitmap getArtworkFromFile(Context context, long songid, long albumid) {
        Bitmap bm = null;

        if (albumid < 0 && songid < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }

        try {
            if (albumid < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            } else {
                Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            }
        } catch (IllegalStateException ignored) {
        } catch (FileNotFoundException ignored) {
        }
        if (bm != null) {
            mCachedBit = bm;
        }
        return bm;
    }

    private Bitmap getDefaultArtwork(Context context) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeStream(
                context.getResources().openRawResource(R.drawable.platlogo), null, opts);
    }
}


