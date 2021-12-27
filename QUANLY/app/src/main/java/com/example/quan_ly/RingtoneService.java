package com.example.quan_ly;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class RingtoneService extends Service
{
    private Ringtone ringtone;
    private MediaPlayer player;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        boolean isUrgent = intent.getExtras().getBoolean("isUrgent");
        Uri ringtoneUri = isUrgent ?
            getCustomRingtoneUri() :
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Log.d("Sa ringtone", "onStartCommand | " + isUrgent);
        Log.d("Sa ringtone", "uri | " + ringtoneUri.toString());

//        // Option 1:
//        this.ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
//        ringtone.play();
        // Option 2:
        player = MediaPlayer.create(this, ringtoneUri);
        player.setLooping(isUrgent);
        player.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
//        ringtone.stop();
        player.stop();
    }

    private Uri getCustomRingtoneUri() {
        Resources resources = getApplicationContext().getResources();
        int resourceId = R.raw.manager_alarm;
        return new Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(resourceId))
            .appendPath(resources.getResourceTypeName(resourceId))
            .appendPath(resources.getResourceEntryName(resourceId))
            .build();
    }
}
