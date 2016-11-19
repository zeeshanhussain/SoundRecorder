package com.deadlymachine.soundrecorder;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.deadlymachine.soundrecorder.fragments.RecordingsFragment;
import com.deadlymachine.soundrecorder.interfaces.BackHandlerInterface;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


import java.util.Locale;


public class SoundRecorderActivity extends AppCompatActivity implements BackHandlerInterface {

    private static final String TAG = "SoundRecorder";
    private static final SimpleDateFormat mFileFormat = new SimpleDateFormat("yyyy-MM-dd kk.mm.ss", Locale.getDefault());
    private RecordingsFragment mRecordingsFragment;
    private String mFileName = null;
    private TextView mRecorderStatus;
    private MediaRecorder mMediaRecorder = null;
    private MediaPlayer mMediaPlayer = null;
    private Chronometer mChronometer = null;
    private AudioManager mAudioManager;
    private File outDir;
    private String outputFile = null;
    private Button mRecordButton, mStopButton, mPlayButton;
    private boolean isPermissionGranted = false;
    private boolean isStopPressed = false;
    private boolean isMediaRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_soundrecorder);
        ActivityCompat.requestPermissions(SoundRecorderActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO}, 1);
        outDir = new File(Environment.getExternalStorageDirectory() + File.separator + "SoundRecorder");
        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mRecorderStatus = (TextView) findViewById(R.id.recorderStatus);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mRecordButton = (Button) findViewById(R.id.button1);
        mStopButton = (Button) findViewById(R.id.button2);
        mPlayButton = (Button) findViewById(R.id.button3);
        final Intent intent = new Intent(SoundRecorderActivity.this, RecorderService.class);
        mRecordButton.setClickable(true);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPermissionGranted) {
                    try {
                        mChronometer.setBase(SystemClock.elapsedRealtime());
                        mChronometer.start();
                        mRecordButton.setClickable(true);
                        mStopButton.setClickable(true);
                        mPlayButton.setVisibility(View.GONE);
                        isMediaRecording = true;
                        SoundRecorderActivity.this.startService(intent);
                        mRecorderStatus.setText("Listening");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SoundRecorderActivity.this, "No Permissions Granted", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mStopButton.setClickable(false);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    stopService(intent);
                    mMediaRecorder = null;
                    isStopPressed = true;
                    isMediaRecording = false;
                    mChronometer.stop();
                    mChronometer.setBase(SystemClock.elapsedRealtime());
                    mRecorderStatus.setText("Stopped");
                    mPlayButton.setVisibility(View.VISIBLE);

            }
        });
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAudioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                    mRecordButton.setClickable(false);
                    mStopButton.setClickable(false);
                    mRecorderStatus.setText("Playing");
                    try {
                        mMediaPlayer = new MediaPlayer();
                        mMediaPlayer.setDataSource(outputFile);
                        mMediaPlayer.setVolume(7.0f, 7.0f);
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
                        onMediaPlayerCompletion();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Your device is in Silent mode", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "onDestroy");
//        if (mMediaPlayer != null && isStopPressed) {
//            mMediaPlayer.stop();
//            mMediaPlayer.reset();
//            mMediaPlayer.release();
//        }
//    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        // If you minimise the app, stop the recorder (Haxxx for now)
//        if (mMediaRecorder != null || isMediaRecording) {
//            if (!isStopPressed) {
//                mMediaRecorder.stop();
//                mChronometer.stop();
//                mChronometer.setBase(SystemClock.elapsedRealtime());
//                mMediaRecorder.release();
//                mMediaRecorder = null;
//                setOnCompletion();
//            }
//        }

    }

    @Override
    public void onBackPressed() {
        if (mRecordingsFragment != null && !mRecordingsFragment.onBackPressed()) {
            super.onBackPressed();
            mRecordingsFragment = null;
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("SoundRecorder");
            alertDialog.setMessage("Do you want to exit?");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SoundRecorderActivity.super.onBackPressed();
                }
            });
            alertDialog.setNegativeButton("No", null);
            alertDialog.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent intent = new Intent(SoundRecorderActivity.this, about_me.class);
                startActivity(intent);
                return true;
            case R.id.viewRecording:
                if (isPermissionGranted) {
                    RecordingsFragment recordingsFragment = new RecordingsFragment();
                    showFragment(recordingsFragment, "recordingsFragment");
                } else {
                    Toast.makeText(SoundRecorderActivity.this, "No Permissions Granted", Toast.LENGTH_SHORT).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setOnCompletion() {
        mRecordButton.setClickable(true);
        mStopButton.setClickable(false);
        mPlayButton.setVisibility(View.GONE);
        mRecorderStatus.setText("Start Recording");
    }

    private void onMediaPlayerCompletion() {
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                setOnCompletion();
            }
        });
    }


//    private void initializeMedia() {
//        mFileName = mFileFormat.format(new Date()) + ".mp3";
//        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundRecorder/" + mFileName;
//        mMediaRecorder = new MediaRecorder();
//        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
//        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        mMediaRecorder.setOutputFile(outputFile);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    isPermissionGranted = true;
                    mRecorderStatus.setText("Start Recording");
                    if (!outDir.exists()) {
                        Log.d(TAG, "Creating directory");
                        outDir.mkdirs();
                    } else {
                        Log.d(TAG, "Directory present");
                    }
                } else {
                    isPermissionGranted = false;
                    mRecorderStatus.setText("No Permission Granted");
                    mRecorderStatus.setTextColor(Color.RED);
                }
                break;
        }
    }

    public void showFragment(Fragment fragment, String Tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.recorderActivity, fragment, Tag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void setSelectedFragment(RecordingsFragment fragment) {
        this.mRecordingsFragment = fragment;
    }
}
