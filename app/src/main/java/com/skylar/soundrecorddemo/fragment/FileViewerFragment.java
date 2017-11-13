package com.skylar.soundrecorddemo.fragment;


import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skylar.soundrecorddemo.FileViewerAdapter;
import com.skylar.soundrecorddemo.R;

/**
 * Created by Administrator on 2017/9/11.
 */

public class FileViewerFragment extends Fragment {

    public static FileViewerFragment newInstance(){
        FileViewerFragment fragment = new FileViewerFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        observer.startWatching();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file,container,false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);//设置每个item的大小一致，这样recyclerView就不用去计算每个item的size

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setReverseLayout(true);//将从数据库读取出来的新数据放置在前面（本来应该是旧-新的顺序，这个方法就可以变成新-旧的顺序）
        llm.setStackFromEnd(true);

        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        FileViewerAdapter adapter = new FileViewerAdapter(getActivity(),llm);
        recyclerView.setAdapter(adapter);

        return view;
    }

    //观察路径下的文件的动态（删除文件）
    FileObserver observer = new FileObserver(Environment.getExternalStorageDirectory().toString() + "/SoundRecordDemo") {
        @Override
        public void onEvent(int event, String file) {
             if(event == FileObserver.DELETE){
                 Log.i("test","delete file:"+file);//delete file:My Recording_1.mp4
                 //用户在文件存储里删除了文件，则更新数据库和recyclerView
             }
        }
    };
}
