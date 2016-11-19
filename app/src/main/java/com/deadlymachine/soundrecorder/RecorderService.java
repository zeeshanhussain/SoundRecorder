package com.deadlymachine.soundrecorder;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zeeshan on 18/11/16.
 */

public class RecorderService extends Service {

    private static final SimpleDateFormat mFileFormat = new SimpleDateFormat("yyyy-MM-dd kk.mm.ss", Locale.getDefault());
    private MediaRecorder mMediaRecorder = null;
    private String mFileName = null;
    private String outputFile = null;
    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private int mElapsedSeconds = 0;
    private OnTimerChangedListener onTimerChangedListener = null;
    private Timer mTimer = null;
    private TimerTask mIncrementTimerTask = null;



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mMediaRecorder != null) {
            stopRecording();
        }

        super.onDestroy();
    }

    public void startRecording() {

        try {

            startForeground(1, createNotification());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stopRecording() {

        if (mIncrementTimerTask != null) {
            mIncrementTimerTask.cancel();
            mIncrementTimerTask = null;
        }
        startForeground(0,createNotification());


    }

    private Notification createNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_play)
                        .setContentTitle(getString(R.string.notification_recording))
                        .setUsesChronometer(true)
                        .setOngoing(true);

        mBuilder.setContentIntent(PendingIntent.getActivities(getApplicationContext(), 0,
                new Intent[]{new Intent(getApplicationContext(), SoundRecorderActivity.class)}, 0));

        return mBuilder.build();
    }

    public interface OnTimerChangedListener {
        void onTimerChanged(int seconds);
    }

    }

