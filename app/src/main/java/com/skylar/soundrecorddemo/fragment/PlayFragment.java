package com.skylar.soundrecorddemo.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.skylar.soundrecorddemo.R;
import com.skylar.soundrecorddemo.RecordItem;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/9/13.
 */

public class PlayFragment extends DialogFragment {

    private RecordItem mRecordItem;
    private static final String ARG = "argument";

    private TextView mCurrentProgress,mDuration;
    private SeekBar mSeekBar;
    private FloatingActionButton mPlayFab;
    private MediaPlayer mMediaPlayer;

    private Handler mHandler = new Handler();
    private long minute,seconds;
    private boolean isPlay = false;

    public static PlayFragment newInstance(RecordItem recordItem){
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG,recordItem);
        PlayFragment fragment = new PlayFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mRecordItem = arguments.getParcelable(ARG);

        long duration = mRecordItem.getLength();
        minute = TimeUnit.MILLISECONDS.toMinutes(duration);
        seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MILLISECONDS.toSeconds(minute);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_play,null);
        TextView fileName = (TextView) view.findViewById(R.id.fileName);
        mCurrentProgress = (TextView) view.findViewById(R.id.currentProgress);
        mDuration = (TextView) view.findViewById(R.id.duration);
        mPlayFab = (FloatingActionButton) view.findViewById(R.id.playFab);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekBar);

        mDuration.setText(String.format("%02d:%02d",minute,seconds));//总时长
        fileName.setText(mRecordItem.getName());

        ColorFilter filter = new LightingColorFilter(getResources().getColor(R.color.primary),getResources().getColor(R.color.primary));
        mSeekBar.getProgressDrawable().setColorFilter(filter);
        mSeekBar.getThumb().setColorFilter(filter);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                Log.i("test","onProgressChanged");
                Log.i("test","fromUser"+fromUser);
                if(fromUser){//用户用手拖动改变进度
                    long minute = TimeUnit.MILLISECONDS.toMinutes(i);
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(i) - TimeUnit.MILLISECONDS.toSeconds(minute);
                    mCurrentProgress.setText(String.format("%02d:%02d",minute,seconds));

                    if(mMediaPlayer == null){
                        preparePlay(i);
                    }else{
                        mMediaPlayer.seekTo(i);
                        mHandler.removeCallbacks(mRunnable);

                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("test","onStartTrackingTouch");
                if(mMediaPlayer != null){
                    mHandler.removeCallbacks(mRunnable);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("test","onStopTrackingTouch");
                 if(mMediaPlayer != null){
                     updateSeekBar();
                 }
            }
        });

        mPlayFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPlay(isPlay);
                isPlay = !isPlay;
            }
        });

        builder.setView(view);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(Dialog.BUTTON_NEGATIVE).setEnabled(false);
        dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
        dialog.getButton(Dialog.BUTTON_NEUTRAL).setEnabled(false);

        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mMediaPlayer != null){
            stopPlay();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mMediaPlayer != null){
            stopPlay();
        }
    }

    private void preparePlay(int i) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mRecordItem.getPath());
            mMediaPlayer.prepare();
            mSeekBar.setMax(mMediaPlayer.getDuration());
            mMediaPlayer.seekTo(i);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopPlay();
            }
        });

    }

    private void onPlay(boolean isPlay) {
        if(isPlay){
            //暂停
            pausePlay();
        }else{
            if(mMediaPlayer == null){
                //开始播放
                startPlay();
            }else{
                //恢复播放
                resumePlay();
            }
        }
    }


    private void startPlay() {
        mPlayFab.setImageResource(R.drawable.ic_media_pause);
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mRecordItem.getPath());
            mMediaPlayer.prepare();
            mSeekBar.setMax(mMediaPlayer.getDuration());
            Log.i("test","mMediaPlayer.getDuration():"+mMediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mMediaPlayer.start();
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopPlay();
            }
        });

        updateSeekBar();

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void resumePlay() {
        mPlayFab.setImageResource(R.drawable.ic_media_pause);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.start();
        updateSeekBar();

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void pausePlay() {
        mPlayFab.setImageResource(R.drawable.ic_media_play);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.pause();

    }

    private void stopPlay(){
        isPlay = !isPlay;

        mPlayFab.setImageResource(R.drawable.ic_media_play);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;

        mSeekBar.setProgress(mSeekBar.getMax());
        mCurrentProgress.setText(mDuration.getText());

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }

    private void updateSeekBar(){
        mHandler.postDelayed(mRunnable,1000);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            int pos = mMediaPlayer.getCurrentPosition();
            Log.i("test","pos:"+pos);
            long minute = TimeUnit.MILLISECONDS.toMinutes(pos);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(pos) - TimeUnit.MILLISECONDS.toSeconds(minute);
            mCurrentProgress.setText(String.format("%02d:%02d",minute,seconds));
            mSeekBar.setProgress(pos);

            updateSeekBar();//循环，每一秒更新一次
        }
    };
}
