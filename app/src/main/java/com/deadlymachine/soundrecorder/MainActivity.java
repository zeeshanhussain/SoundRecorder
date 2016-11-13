package com.deadlymachine.soundrecorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SoundRecorder";
    private TextView mRecorderStatus;
    private MediaRecorder mMediaRecorder = null;
    private MediaPlayer mMediaPlayer;
    private String outputFile = null;
    private Button mStartButton, mStopButton;
    private boolean isPermissionGranted = false;
    private boolean isStopPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO}, 1);
        mRecorderStatus = (TextView) findViewById(R.id.recorderStatus);
        mStartButton = (Button) findViewById(R.id.button1);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPermissionGranted) {
                    try {
                        initializeMedia();
                        mMediaRecorder.prepare();
                        mMediaRecorder.start();
                        mRecorderStatus.setText("Listening..");
                    } catch (IllegalStateException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No Permissions Granted", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mStopButton = (Button) findViewById(R.id.button2);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPermissionGranted) {
                    if (mMediaRecorder != null) {
                        isStopPressed = true;
                        mMediaRecorder.stop();
                        mMediaRecorder.release();
                        mRecorderStatus.setText("Playing..");
                        mMediaRecorder = null;
                        try {
                            mMediaPlayer = new MediaPlayer();
                            mMediaPlayer.setDataSource(outputFile);
                            mMediaPlayer.setVolume(7.0f, 7.0f);
                            mMediaPlayer.prepare();
                            mMediaPlayer.start();
                            onMediaPlayerCompletion();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No Permissions Granted", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (isStopPressed) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
        } else {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder.reset();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    private void onMediaPlayerCompletion() {
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mRecorderStatus.setText("Start Recording");
            }
        });
    }

    private void initializeMedia() {
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.mp3";
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setOutputFile(outputFile);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    isPermissionGranted = true;
                    mRecorderStatus.setText("Start Recording");
                    mRecorderStatus.setVisibility(View.VISIBLE);
                } else {
                    isPermissionGranted = false;
                    mRecorderStatus.setText("No Permission Granted");
                    mRecorderStatus.setTextColor(Color.RED);
                    mRecorderStatus.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
