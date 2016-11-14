package com.deadlymachine.soundrecorder;

import android.app.ListActivity;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Listfiles extends ListActivity {

    private List<String> fileList = new ArrayList<String>();
    private ListView listRecordings;
    private MediaPlayer mMediaPlayer = null;
    private boolean isPlaying=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView = (ListView) findViewById(R.id.list_item);
        final MediaPlayer myMediaPlayer = MediaPlayer.create(this, R.raw.audio);
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/SoundRecorder");
        File[] files = root.listFiles();
        fileList.clear();
        for (File file : files) {
            fileList.add(file.getPath());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                 this, 
                 android.R.layout.list_item,
                 fileList );
         listRecordings.setAdapter(arrayAdapter);
    }
    @Override  
    protected void onListItemClick(ListView l, View v, int pos, long id) {  
        super.onListItemClick(l, v, pos, id);
        Object o = this.getListAdapter().getItem(pos);
        String fileName = o.toString();
        if(!isPlaying){
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(outputFile);
            mMediaPlayer.setVolume(7.0f, 7.0f);
            mMediaPlayer.prepare();
            isPlaying=true;
            mMediaPlayer.start();
            onMediaPlayerCompletion();
        } else{
            isPlaying=false;
            mMediaPlayer.stop();
        }
    }  
}
