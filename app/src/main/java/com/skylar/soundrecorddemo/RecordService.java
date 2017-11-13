package com.skylar.soundrecorddemo;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2017/9/10.
 */
public class RecordService extends Service {

    private String mFileName = null;
    private String mFilePath = null;

    private MediaRecorder mMediaRecorder = null;
    private long mStartTime;
    private long mLength;

    private DBHelper mDatabase;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = new DBHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //开始录音
        startRecord();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //停止录音，保存录音
        if(mMediaRecorder!=null){
            stopRecord();
        }
        super.onDestroy();

    }

    private void stopRecord() {
        mMediaRecorder.stop();
        mLength = System.currentTimeMillis() - mStartTime;
        mMediaRecorder.release();

        Toast.makeText(this,getString(R.string.toast_recording_finish)+mFilePath,Toast.LENGTH_SHORT).show();

        mDatabase.addRecord(mFileName,mFilePath,mLength);

        mMediaRecorder=null;

    }

    private void startRecord() {
        //新建存储录音的文件名称和路径
        setFileNameAndPath();

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(mFilePath);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setAudioChannels(1);

        if(PrefrenceUtil.getPrefHighQuality(this)){
            mMediaRecorder.setAudioSamplingRate(44100);
            mMediaRecorder.setAudioEncodingBitRate(192000);
        }

        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaRecorder.start();
        //记录开始录音的时间
        mStartTime = System.currentTimeMillis();

    }

    private void setFileNameAndPath() {
        int count = 0;
        File file;
        do{
            count ++;
            mFileName = getString(R.string.default_file_name) + "_" + (mDatabase.getCount()+ count)+ ".mp4";
            Log.i("test","setFileNameAndPath mDatabase.getCount():"+mDatabase.getCount());
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundRecordDemo/" + mFileName;
            file = new File(mFilePath);
        }while(file.exists() && !file.isDirectory()); //文件存在且不是文件夹，则一直循环

    }
}
