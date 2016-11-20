package com.deadlymachine.soundrecorder.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.deadlymachine.soundrecorder.R;
import com.deadlymachine.soundrecorder.interfaces.BackHandlerInterface;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


/**
 * Created by men_in_black007 on 14/11/16.
 */

public class RecordingsFragment extends Fragment {

    private static final String TAG = "RecordingsFragment";
    private BackHandlerInterface mBackHandlerInterface;
    private MediaPlayer mMediaPlayer = null;
    private File outDir = new File(Environment.getExternalStorageDirectory() + File.separator + "SoundRecorder");
    private ArrayList mArrayList = new ArrayList();
    private Collection<File> recordingCollection = FileUtils.listFiles(outDir, new String[]{"mp3"}, false);
    private ArrayAdapter<String> mArrayAdapter;
    private ListView mListView;
    private CharSequence options[] = new CharSequence[]{"Play", "Rename", "Delete", "Share"};
    private boolean isMediaPlaying = false;
    private boolean isFragmentOpened = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recordings, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        setFragmentOpened(true);
        mBackHandlerInterface = (BackHandlerInterface) getActivity();
        mBackHandlerInterface.setSelectedFragment(this);
        mListView = (ListView) view.findViewById(R.id.listView);
        mArrayList.addAll(recordingCollection);
        Collections.sort(mArrayList);
        mArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, mArrayList);
        mListView.setAdapter(mArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String name = parent.getItemAtPosition(position).toString();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Options");
                alertDialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                mStartMedia(name);
                                break;
                            case 1:
                                showRenameDialog(name);
                                break;
                            case 2:
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                                alertDialog.setTitle("Delete Recording");
                                alertDialog.setMessage("Do you want to Delete this Recording?");
                                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        File file = new File(name);
                                        boolean deleted = file.delete();
                                        mArrayList.remove(position);
                                        mArrayAdapter.notifyDataSetChanged();
                                        Log.d(TAG, "File Path: " + name + ", Deleted: " + String.valueOf(deleted));
                                    }
                                });
                                alertDialog.setNegativeButton("No", null);
                                alertDialog.show();
                                break;
                            case 3:
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + name));
                                shareIntent.setType("audio/mp3");
                                startActivity(Intent.createChooser(shareIntent, "Share audio File"));
                        }
                    }
                });
                alertDialog.show();
            }
        });
    }

    private void mStartMedia(String name) {
        if (!isMediaPlaying) {
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

    private void showRenameDialog(final String name) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.rename_recording, null);
        dialogBuilder.setView(dialogView);

        final EditText newFileName = (EditText) dialogView.findViewById(R.id.newFileName);

        dialogBuilder.setTitle("Rename");
        dialogBuilder.setMessage("Enter new file name");
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String oldName = name.substring(34, name.length());
                String newName = newFileName.getText().toString() + ".mp3";
                Log.d("RecordingsFragment", oldName + " " + newName);
                renameFile(oldName, newName);
                updateList();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", null);
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void renameFile(String oldName, String newName) {
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "SoundRecorder");
        if (dir.exists()) {
            File from = new File(dir, oldName);
            File to = new File(dir, newName);
            if (from.exists())
                from.renameTo(to);
        }
    }

    private void updateList() {
        // Haxx
        mArrayAdapter.clear();
        Collection<File> recordingCollections = FileUtils.listFiles(outDir, new String[]{"mp3"}, false);
        mArrayList.addAll(recordingCollections);
        Collections.sort(mArrayList);
        mArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, mArrayList);
        mListView.setAdapter(mArrayAdapter);
    }

    public boolean onBackPressed() {
        return false;
    }

    public boolean isFragmentOpened() {
        return isFragmentOpened;
    }

    public void setFragmentOpened(boolean fragmentOpened) {
        isFragmentOpened = fragmentOpened;
    }
}