package com.example.proscend.goodobaby;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerService extends Service {

    private ServerSocket m_serverSocket = null;

    private HandlerThread server_thread;

    private Handler serverHandler = new Handler();

    private static final String DB_FILE = "event1.db",DB_TABLE = "event";

    private SQLiteDatabase mEventDbRW;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {

        EventDbHelper evenDbHp =new EventDbHelper(getApplicationContext(), DB_FILE,null, 1);
        evenDbHp.sCreateTableCommand = "CREATE TABLE " + DB_TABLE + "(" +"_id INTEGER PRIMARY KEY," + "event TEXT NOT NULL," +"date TEXT,"+ "time TEXT);";
        mEventDbRW = evenDbHp.getWritableDatabase();

        try {
            // open a server socket
            int clientPort = 5001;
            m_serverSocket = new ServerSocket(clientPort);
            // start server thread to do socket-accept tasks
            server_thread = new HandlerThread("ServerHandler");
            // stay for work
            server_thread.start();
            serverHandler = new Handler(server_thread.getLooper());
            serverHandler.postDelayed(serverRunnable, 1000);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        try {
            m_serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverHandler.removeCallbacks(serverRunnable);
        server_thread.quit();
        super.onDestroy();
    }

    private Runnable serverRunnable = new Runnable() {
        @Override
        public void run() {
            String msg = tcpServer();
            serverHandler.post(this);
            if(msg != "Connect error."){

                ContentValues newRow = new ContentValues();
                System.out.println("msg=" + msg);
                String[] split=msg.split(",");
                //newRow.put("time", split[0]);
                newRow.put("event", split[1]);
                System.out.println("event="+  split[1]);
                msg = split[1];
                split=split[0].split(" ",5);
                for(String s : split){
                    System.out.println(s);

                }
                System.out.println("date="+split[1]+" "+split[2]+" "+split[4]);
                System.out.println("time="+  split[3]);

                newRow.put("date", split[1] + " " + split[2] + " " + split[4]);
                newRow.put("time", split[3]);

                mEventDbRW.insert(DB_TABLE, null, newRow);
                msg += "              " + split[3];

                notification(msg);
            }
        }
    };

    private String tcpServer(){

        DataInputStream netInputStream;
        DataOutputStream netOutputStream;
        String strTxt = "goodoBaby";
        byte [] sendbyte = new byte[20];
        byte [] readbyte = new byte[1023];
        byte [] readlen  = new byte[10];
        int msglen;
        String resultmsg;

        try {
            System.out.println("Waitting for client connection......");

            Socket serverSocket = m_serverSocket.accept();

            Log.d("accept", "Accepted connection from " + serverSocket.getInetAddress().getHostAddress());

            netInputStream = new DataInputStream(serverSocket.getInputStream());
            netOutputStream = new DataOutputStream(serverSocket.getOutputStream());
            //read message length first
            netInputStream.read(readlen);
            Log.d("availabe length", Integer.toString(netInputStream.available()));
            resultmsg = new String(readlen, 0, 2);
            msglen = Integer.parseInt(resultmsg);
            Log.d("msglen", Integer.toString(msglen));

            //send text length first
            //msglen = strTxt.length();
            //sendbyte = Integer.toString(msglen).getBytes();
            netOutputStream.write(readlen, 0, readlen.length);
            Log.d("send length", readlen.toString());

            //read message
            //if (msglen > 0){
            netInputStream.read(readbyte);
            Log.d("readbyte", readbyte.toString());
            resultmsg = new String(readbyte, 0, msglen);
            Log.d("resultmsg", resultmsg);

            //send text
            //sendbyte = strTxt.getBytes();
            netOutputStream.write(readbyte, 0, readbyte.length);
            Log.d("send byte", readbyte.toString());


            //Log.d("readlen", resultmsg);


            // close streams
            netInputStream.close();
            netOutputStream.close();

            // close server socket
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            resultmsg = "Connect error.";
        }
        return  resultmsg;

    }


    private void notification(String msg){

        //取得Notification服務
        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //設定當按下這個通知之後要執行的activity
        Intent notifyIntent = new Intent(ServerService.this,MainActivity.class);
        notifyIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent appIntent=PendingIntent.getActivity(ServerService.this,0,
                notifyIntent,0);
        Notification notification = new Notification();
        //設定出現在狀態列的圖示
        notification.icon=R.drawable.baby_sleep;
        //顯示在狀態列的文字
        notification.tickerText="notification on status bar.";
        //會有通知預設的鈴聲、振動、light
        notification.defaults=Notification.DEFAULT_ALL;
        //設定通知的標題、內容
        notification.setLatestEventInfo(ServerService.this,"goodoBaby",msg,appIntent);
        //送出Notification
        notificationManager.notify(0,notification);
    }

}