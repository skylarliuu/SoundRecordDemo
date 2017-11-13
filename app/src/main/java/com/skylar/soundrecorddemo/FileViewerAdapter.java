package com.skylar.soundrecorddemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.skylar.soundrecorddemo.fragment.PlayFragment;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/9/11.
 */

public class FileViewerAdapter extends RecyclerView.Adapter<FileViewerAdapter.RecordViewHolder> implements OnDatabaseChangeListner{

    private DBHelper mDatabase;
    private Context mContext;
    private LinearLayoutManager mllm;

    public FileViewerAdapter(Context context, LinearLayoutManager llm) {
        super();
        mContext = context;
        mDatabase = new DBHelper(mContext);
        mDatabase.setDatabaseChangeListner(this);
        mllm = llm;
    }

    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("test","onCreateViewHolder:");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,parent,false);
        mContext = parent.getContext();
        RecordViewHolder viewHolder = new RecordViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecordViewHolder holder, final int position) {
        Log.i("test","onBindViewHolder:");
        RecordItem item = getItem(position);
        long length = item.getLength();
        Log.i("test","length:"+length);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(length);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(length) - TimeUnit.MILLISECONDS.toSeconds(minutes);

        holder.fileName.setText(item.getName());
        Log.i("test","minutes:"+minutes);
        Log.i("test","seconds:"+seconds);
        Log.i("test","item.getTime():"+item.getTime());
        holder.fileLength.setText(String.format("%02d:%02d",minutes,seconds));
        holder.addedTime.setText(DateUtils.formatDateTime(mContext,item.getTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR));
        holder.cardView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                PlayFragment fragment = PlayFragment.newInstance(mDatabase.getItemAtPosition(position));
                FragmentTransaction transaction = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
                fragment.show(transaction,"play_dialog");
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                ArrayList<String> entrys = new ArrayList<String>();
                entrys.add(mContext.getString(R.string.dialog_file_share));
                entrys.add(mContext.getString(R.string.dialog_file_rename));
                entrys.add(mContext.getString(R.string.dialog_file_delete));

                final CharSequence[] items = entrys.toArray(new CharSequence[entrys.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(mContext.getString(R.string.dialog_title_options));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){
                            shareFileDialog(position);
                        }else if(i == 1){
                            renameFileDialog(position);
                        }else if(i == 2){
                            deleteFileDialog(position);
                        }
                    }
                });

                builder.setCancelable(true);
                builder.setNegativeButton(mContext.getString(R.string.dialog_action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.i("test","getItemCount:"+mDatabase.getCount());
        return mDatabase.getCount();
    }

    private RecordItem getItem(int position) {
        return mDatabase.getItemAtPosition(position);
    }

    @Override
    public void addRecord() {
        notifyItemInserted(getItemCount() - 1);
        mllm.scrollToPosition(getItemCount()-1);
    }

    @Override
    public void renameRecord() {

    }

    @Override
    public void removeRecord() {

    }


    public static class RecordViewHolder extends RecyclerView.ViewHolder{
        private TextView fileName;
        private TextView fileLength;
        private TextView addedTime;
        private CardView cardView;

        public RecordViewHolder(View itemView) {
            super(itemView);

            fileName = (TextView) itemView.findViewById(R.id.fileName);
            fileLength = (TextView) itemView.findViewById(R.id.fileLength);
            addedTime = (TextView) itemView.findViewById(R.id.addedTime);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }

    private void deleteFileDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.dialog_title_delete));
        builder.setMessage(mContext.getString(R.string.dialog_text_delete));
        builder.setCancelable(true);
        builder.setPositiveButton(mContext.getString(R.string.dialog_action_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                remove(position);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(mContext.getString(R.string.dialog_action_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void renameFileDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.dialog_title_rename));

        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_rename_file,null);
        final EditText editText = (EditText) view.findViewById(R.id.new_name);

        builder.setCancelable(true);
        builder.setPositiveButton(mContext.getString(R.string.dialog_action_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newName = editText.getText().toString().trim() + ".mp4";
                rename(position,newName);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(mContext.getString(R.string.dialog_action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();;
            }
        });
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void rename(int position, String newName) {
        String newPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundRecordDemo/" + newName;

        File file = new File(newPath);
        if(file.exists() && !file.isDirectory()){
            Toast.makeText(mContext,
                    String.format(mContext.getString(R.string.toast_file_exists), newName),
                    Toast.LENGTH_SHORT).show();
        }else{
            File old = new File(getItem(position).getPath());
            old.renameTo(file);
            mDatabase.renameRecord(getItem(position),newName,newPath);
            notifyItemChanged(position);
        }

    }

    private void shareFileDialog(int position) {
        Intent intent  = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(getItem(position).getPath())));
        intent.setType("audio/mp4");
        mContext.startActivity(Intent.createChooser(intent,mContext.getString(R.string.send_to)));
    }

    private void remove(int position){
        File file = new File(getItem(position).getPath());
        file.delete();

        Toast.makeText(
                mContext,
                String.format(
                        mContext.getString(R.string.toast_file_delete),
                        getItem(position).getName()
                ),
                Toast.LENGTH_SHORT
        ).show();

        mDatabase.removeRecord(getItem(position).getId());
        notifyItemRemoved(position);
    }
}
