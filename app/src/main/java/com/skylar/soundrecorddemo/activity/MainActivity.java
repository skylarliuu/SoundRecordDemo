package com.skylar.soundrecorddemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.skylar.soundrecorddemo.R;
import com.skylar.soundrecorddemo.fragment.FileViewerFragment;
import com.skylar.soundrecorddemo.fragment.RecordFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabStrip);

        setSupportActionBar(toolbar);

        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        tabStrip.setViewPager(viewPager);


    }

    private class MyAdapter extends FragmentPagerAdapter {

        private String[] titles = {getString(R.string.tab_title_record),
                getString(R.string.tab_title_saved_recordings)};

         public MyAdapter(FragmentManager fm){
             super(fm);
         }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                return RecordFragment.newInstance();
            }else{
                return FileViewerFragment.newInstance();
            }

//            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:

                startActivity(new Intent(this,SettingActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
