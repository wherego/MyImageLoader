package com.example.alan.imageloader;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final static String urlPic = "http://img2.3lian.com/2014/f2/110/d/";
    private MyImageLoader imageLoader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageLoader = MyImageLoader.build(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList<String> needToReq = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            needToReq.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            needToReq.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        ActivityCompat.requestPermissions(MainActivity.this,
                needToReq.toArray(new String[needToReq.size()]), 1);

        GridView gridView = (GridView) findViewById(R.id.gridView);
        ImageAdapter imageAdapter = new ImageAdapter();
        gridView.setAdapter(imageAdapter);
        gridView.setOnTouchListener(imageAdapter);
        gridView.setOnScrollListener(imageAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ImageAdapter extends BaseAdapter implements View.OnTouchListener, AbsListView.OnScrollListener{
        private VelocityTracker velocityTracker;
        private boolean isGridTooSlow = true;
        private boolean isGridIdle = true;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            velocityTracker.addMovement(event);
            velocityTracker.computeCurrentVelocity(1000);
            float yVelocity = velocityTracker.getYVelocity();
            if (Math.abs(yVelocity) < 5000 || !event.equals(MotionEvent.ACTION_UP)) {
                isGridTooSlow = true;
            } else {
                isGridTooSlow = false;
            }
            return false;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                isGridIdle = true;
                notifyDataSetChanged();
                Log.i(TAG, "onScrollChanged: SCROLL_STATE_IDLE");
            } else {
                isGridIdle = false;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }

        class ViewHolder {
            public ImageView imageView;

        }

        private ArrayList<String> mUrList = new ArrayList<>();

        @Override
        public int getCount() {
            return mUrList.size();
        }

        @Override
        public String getItem(int position) {
            return mUrList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_grid, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imvItem);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ImageView imageView = viewHolder.imageView;
            final String tag = (String) imageView.getTag();
            final String uri = getItem(position);
            if (!uri.equals(tag)) {
                //set default image before finish load.
                imageView.setImageDrawable(Resources.getSystem().getDrawable(android.R.drawable.ic_menu_gallery));
            }
            imageView.setTag(uri);
            int pxHeight = getResources().getDimensionPixelSize(R.dimen.bitmap_height);
            if (isGridTooSlow || isGridIdle) {
                imageLoader.bindBitmap(uri, imageView, (int) (pxHeight * 1.5), pxHeight);
            }
            return convertView;
        }

        public ImageAdapter() {
            super();
            velocityTracker = VelocityTracker.obtain();
            for(int i = 0; i < 50; i++) {
               mUrList.add(urlPic + (i + 1) + ".jpg" );
            }
        }

    }
}
