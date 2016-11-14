package com.deadlymachine.soundrecorder.fragments;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.deadlymachine.soundrecorder.R;
import com.deadlymachine.soundrecorder.interfaces.BackHandlerInterface;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by men_in_black007 on 14/11/16.
 */

public class RecordingsFragment extends Fragment {

    private BackHandlerInterface mBackHandlerInterface;
    private MediaPlayer mMediaPlayer = null;
    private File outDir = new File(Environment.getExternalStorageDirectory() + File.separator + "SoundRecorder");
    private ArrayList mArrayList = new ArrayList();
    private Collection<File> recordingCollection = FileUtils.listFiles(outDir, new String[]{"mp3"}, false);
    private ArrayAdapter<String> mArrayAdapter;
    private boolean isMediaPlaying = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recordings, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mBackHandlerInterface = (BackHandlerInterface) getActivity();
        mBackHandlerInterface.setSelectedFragment(this);
        final ListView mListView = (ListView) view.findViewById(R.id.listView);
        mArrayList.addAll(recordingCollection);
        mArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, mArrayList);
        mListView.setAdapter(mArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isMediaPlaying) {
                    String name = parent.getItemAtPosition(position).toString();
                    Uri uri = Uri.parse(name);
                    try {
                        isMediaPlaying = true;
                        mMediaPlayer = new MediaPlayer();
                        mMediaPlayer.setDataSource(getContext(), uri);
                        mMediaPlayer.setVolume(7.0f, 7.0f);
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
                        onMediaPlayerCompletion();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "MediaPlayer is currently playing", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onMediaPlayerCompletion() {
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isMediaPlaying = false;
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mMediaPlayer.release();
            }
        });
    }

    public boolean onBackPressed() {
        return false;
    }
}