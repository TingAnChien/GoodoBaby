package com.example.proscend.goodobaby;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class MainActivity extends ActionBarActivity {

    private ImageView mImgViewSleep,mImgViewAwake,mImgViewEscape;

    private Switch swtDect;

    private Button btnRecord;

    private String event_msg;

    private String serverIP="192.168.2.107";

    public static boolean ServiceOn = true;

    public static boolean detectOn = true;

    private static final int settingcode=1;

    protected static final int REFRESH_DATA = 0x0000001;

    private GestureDetector changePage;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case REFRESH_DATA:
                    event_msg = msg.obj.toString();
                    //textview.setText(event_msg);
                    Toast.makeText(MainActivity.this, event_msg, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        changePage = new GestureDetector(new SwipeGestureDetector());
        changePage = new GestureDetector(new SwipeGestureDetector());

        // start a service
        if(ServiceOn){
            Intent intent = new Intent(MainActivity.this, ServerService.class);
            startService(intent);
        }

        setupViewComponent();
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
            System.out.println("in setting");
            SettingClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void SettingClick(){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SettingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("serviceon", ServiceOn);
        bundle.putString("serverip", serverIP);
        intent.putExtras(bundle);

        startActivityForResult(intent, settingcode);
        //MainActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //super.onActivityResult(requestCode, resultCode, data);
        //System.out.println("in result");
        //Toast.makeText(MainActivity.this, " onActivityResult" + serverIP, Toast.LENGTH_LONG).show();
        switch(requestCode){
            case settingcode:
                ServiceOn = data.getExtras().getBoolean("serviceon");
                serverIP = data.getExtras().getString("serverip");
                //Toast.makeText(MainActivity.this, "Change Server IP : " + serverIP, Toast.LENGTH_LONG).show();
                //System.out.println("change ip");
                break;
        }
    }

    private void setupViewComponent(){

        btnRecord = (Button)findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(btnRecordOnClick);
        swtDect = (Switch)findViewById(R.id.switchDect);
        if(detectOn)
            swtDect.setChecked(true);
        else
            swtDect.setChecked(false);

        swtDect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String msg;
                if (isChecked) {
                    msg = "021";
                    detectOn = true;
                }
                else {
                    msg = "020";
                    detectOn = false;
                }
                // start a client thread to send message
                Thread t = new Thread(new sendRunnable(msg));
                t.start();
            }
        });


        mImgViewSleep = (ImageView)findViewById(R.id.imgViewSleep);
        Resources res = getResources();
        //Drawable drawImg;
        //if(state==0) {
        Drawable drawImg = res.getDrawable(R.drawable.baby_sleep);
        mImgViewSleep.setBackgroundDrawable(drawImg);

        //}
        /*else if(state==1) {
            Drawable drawImg2 = res.getDrawable(R.drawable.baby_awake);
            mImgViewAwake.setBackgroundDrawable(drawImg);
        }
        else{
            Drawable drawImg3 = res.getDrawable(R.drawable.baby_escape);
            mImgViewEscape.setBackgroundDrawable(drawImg);
        }
        */

    }


    private Button.OnClickListener btnRecordOnClick = new Button.OnClickListener(){
        public void onClick(View v){

            Intent intent = new Intent();
            intent.setClass(MainActivity.this,ListActivity.class);

            startActivity(intent);
            MainActivity.this.finish();
        }
    };

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String tcpClient(String strTxt){
        InetAddress serverIp;

        DataInputStream  netInputStream;
        DataOutputStream netOutputStream;
        byte [] sendbyte;
        byte [] readbyte = new byte[1023];
        byte [] readlen  = new byte[10];
        int msglen;
        String resultmsg = "";

        try {
            System.out.println("Waitting to connect......");
            serverIp = InetAddress.getByName(serverIP);

            int serverPort=5001;

            Socket clientSocket=new Socket(serverIp,serverPort);

            netInputStream  = new DataInputStream(clientSocket.getInputStream());
            netOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            //send text length first
            msglen = strTxt.length();
            sendbyte = Integer.toString(msglen).getBytes();
            netOutputStream.write(sendbyte, 0, sendbyte.length);

            // read from server
            netInputStream.read(readlen);

            //send text
            sendbyte = strTxt.getBytes();
            netOutputStream.write(sendbyte, 0, sendbyte.length);

            //read message length first
            netInputStream.read(readlen);
            Log.d("readlen", readlen.toString());

            //read message
            msglen = netInputStream.available();
            if (msglen > 0){
                netInputStream.read(readbyte);
                resultmsg = new String(readbyte, 0, msglen);
                Log.d("resultmsg", resultmsg);
            }

            clientSocket.close();
        } catch (IOException e) {
            return "Connect error~~";
        }
        return resultmsg;

    }


    private class sendRunnable implements Runnable {
        String strTxt = null;
        public sendRunnable(String msg) {
            this.strTxt = msg;
        }
        @Override
        public void run() {
            String result = tcpClient(strTxt);
            System.out.println(result);
            //mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (changePage.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void onLeftSwipe() {

        Intent intent = new Intent();
        intent.setClass(MainActivity.this,ListActivity.class);

        startActivity(intent);
        MainActivity.this.finish();

    }

    private void onRightSwipe() {

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
                    MainActivity.this.onLeftSwipe();

                    // Right swipe
                } else if (-diff > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    MainActivity.this.onRightSwipe();
                }
            } catch (Exception e) {
                Log.e("YourActivity", "Error on gestures");
            }
            return false;
        }
    }

}
