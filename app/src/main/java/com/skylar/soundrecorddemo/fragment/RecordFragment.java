package com.skylar.soundrecorddemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.skylar.soundrecorddemo.R;
import com.skylar.soundrecorddemo.RecordService;

import java.io.File;

/**
 * Created by Administrator on 2017/9/7.
 */

public class RecordFragment extends Fragment {

    private Chronometer mChronometer;
    private FloatingActionButton mRecordButton;
    private TextView mStatusText;

    private boolean mStartRecording = true;
    private int mStatusCount = 0;

    public static RecordFragment newInstance(){
//        Bundle bundle = new Bundle();
//        bundle.putString("ARGUMENT","argument");
        RecordFragment fragment = new RecordFragment();
//        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //通过argument进行通信，数据不会因为fragment的销毁而丢失
//        Bundle bundle = getArguments();
//        String argument = bundle.getString("ARGUMENT");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //fragment中必须得用inflate三个参数的这个方法，不然会报错
        View view = inflater.inflate(R.layout.fragment_record,container,false);
        mChronometer = (Chronometer) view.findViewById(R.id.chronometer);
        mRecordButton = (FloatingActionButton) view.findViewById(R.id.fab);
        mStatusText = (TextView) view.findViewById(R.id.statusText);

        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
            }
        });

        return view;
    }


    private void onRecord(boolean startRecording) {

        Intent intent = new  Intent(getActivity(), RecordService.class);

        if(startRecording){//开始录音

            mRecordButton.setImageResource(R.drawable.ic_media_stop);
            Toast.makeText(getActivity(),R.string.toast_recording_start,Toast.LENGTH_SHORT).show();

            //创建文件夹
            File file = new File(Environment.getExternalStorageDirectory()+"/SoundRecordDemo");
            if(!file.exists()){
                file.mkdir();
            }

            //显示计时器
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            //更改录音状态的文字
            mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if(mStatusCount == 0){
                        mStatusText.setText(getString(R.string.record_in_progress) + ".");
                    }else if(mStatusCount == 1){
                        mStatusText.setText(getString(R.string.record_in_progress) + "..");
                    }else if(mStatusCount == 2){
                        mStatusCount = -1;
                        mStatusText.setText(getString(R.string.record_in_progress) + "...");
                    }
                    mStatusCount++;
                }
            });

            getActivity().startService(intent);
            //保持屏幕常亮
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mStatusText.setText(getString(R.string.record_in_progress) + ".");


        }else{
            mRecordButton.setImageResource(R.drawable.ic_mic_white_36dp);

            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mStatusText.setText(R.string.record_prompt);

            getActivity().stopService(intent);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

    }
}
