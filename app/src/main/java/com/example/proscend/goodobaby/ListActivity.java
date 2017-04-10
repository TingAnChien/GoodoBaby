package com.example.proscend.goodobaby;

/**
 * Created by proscend on 2015/7/27.
 */

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by proscend on 2015/7/24.
 */
public class ListActivity  extends android.app.ListActivity{
    private TextView mTxtResult;
    private ArrayAdapter<String> adapter;
    private List<String> allEvent;
    private Button btnLisback,btnLisAnalyze;
    private GestureDetector changePage;

    private static final String DB_FILE = "event1.db",DB_TABLE = "event";

    private SQLiteDatabase mEventDbRW;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        changePage = new GestureDetector(new SwipeGestureDetector());

        EventDbHelper evenDbHp =new EventDbHelper(getApplicationContext(), DB_FILE,null, 1);
        evenDbHp.sCreateTableCommand = "CREATE TABLE " + DB_TABLE + "(" +"_id INTEGER PRIMARY KEY," + "event TEXT NOT NULL," +"date TEXT,"+ "time TEXT);";
        mEventDbRW = evenDbHp.getWritableDatabase();

        setupViewComponent();
    }

    private void setupViewComponent(){
        mTxtResult = (TextView)findViewById(R.id.txtlist);
        btnLisback = (Button)findViewById(R.id.btnLisback);
        btnLisback.setOnClickListener(btnLisbackOnClick);

        btnLisAnalyze = (Button)findViewById(R.id.btnLisanalyz);
        btnLisAnalyze.setOnClickListener(btnLisanalyzeOnClick);

        allEvent = new ArrayList<String>();
        allEvent.add("hello1");
        allEvent.add("hello2");

        Cursor c =null;
        c = mEventDbRW.query(true, DB_TABLE, new String[]{"event","date","time"},null,null,null,null,null,null);
        if(c.getCount() == 0){
            Toast.makeText(ListActivity.this, "沒有資料", Toast.LENGTH_LONG).show();
        }
        else{
            c.moveToFirst();
            allEvent.add(c.getString(0)+"                              "+c.getString(2));
            while(c.moveToNext())
                allEvent.add(c.getString(0)+"                              "+c.getString(2));
        }

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,allEvent);

        setListAdapter(adapter);
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(listviewOnItemClkLis);

    }

    private Button.OnClickListener btnLisbackOnClick = new Button.OnClickListener(){
        public void onClick(View v){
            Intent intent = new Intent();
            intent.setClass(ListActivity.this,MainActivity.class);

            startActivity(intent);
            ListActivity.this.finish();


        }

    };


    private Button.OnClickListener btnLisanalyzeOnClick = new Button.OnClickListener(){
        public void onClick(View v){
            Intent intent = new Intent();
            intent.setClass(ListActivity.this,RecordActivity.class);

            startActivity(intent);
            ListActivity.this.finish();


        }

    };



    AdapterView.OnItemClickListener listviewOnItemClkLis = new AdapterView.OnItemClickListener(){
        public  void onItemClick(AdapterView<?>parent,View view,int position,long id){

            mTxtResult.setText(((TextView) view).getText());

        }



    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (changePage.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void onLeftSwipe() {

        Intent intent = new Intent();
        intent.setClass(ListActivity.this,RecordActivity.class);

        startActivity(intent);
        ListActivity.this.finish();

    }

    private void onRightSwipe() {
        // Do something
        Intent intent = new Intent();
        intent.setClass(ListActivity.this,MainActivity.class);

        startActivity(intent);
        ListActivity.this.finish();
    }

    // Private class for gestures
    private class SwipeGestureDetector
            extends GestureDetector.SimpleOnGestureListener {
        // Swipe properties, you can change it to make the swipe
        // longer or shorter and speed
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 200;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            try {
                float diffAbs = Math.abs(e1.getY() - e2.getY());
                float diff = e1.getX() - e2.getX();

                if (diffAbs > SWIPE_MAX_OFF_PATH)
                    return false;

                // Left swipe
                if (diff > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    ListActivity.this.onLeftSwipe();

                    // Right swipe
                } else if (-diff > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    ListActivity.this.onRightSwipe();
                }
            } catch (Exception e) {
                Log.e("YourActivity", "Error on gestures");
            }
            return false;
        }
    }



}




