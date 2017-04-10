package com.example.proscend.goodobaby;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by proscend on 2015/7/29.
 */
public class SettingActivity extends ActionBarActivity {
    private EditText ipMessage;

    private Button ipBtn;

    private Switch swtDect;

    private TextView iptext;

    private TextView serveriptext;

    private String phoneIP;

    private String serverIP;

    private boolean ServiceOn;

    private static final int settingcode=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ipMessage = (EditText) findViewById(R.id.ip_message);

        ipBtn = (Button) findViewById(R.id.ip_btn);

        iptext = (TextView) findViewById((R.id.ip_text));

        serveriptext = (TextView) findViewById(R.id.serverip_text);

        phoneIP = getLocalIpAddress();

        iptext.setText("My IP :" + phoneIP);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ServiceOn = bundle.getBoolean("serviceon");
        serverIP = bundle.getString("serverip");
        serveriptext.setText("Server IP :" + serverIP);


        if (ipBtn != null){
            ipBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == ipBtn) {
                        if (ipMessage != null) {
                            serverIP = ipMessage.getEditableText().toString();
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString("serverip", serverIP);
                            intent.putExtras(bundle);
                            setResult(settingcode, intent);
                            Toast.makeText(SettingActivity.this, "Change Server IP : " + serverIP, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
            });
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putBoolean("serviceon", ServiceOn);
            bundle.putString("serverip", serverIP);
            intent.putExtras(bundle);
            setResult(settingcode, intent);
            finish();
            /*
            System.out.println("in setting~~");
            Intent intent = new Intent();
            intent.setClass(SettingActivity.this, MainActivity.class);

            startActivity(intent);
            SettingActivity.this.finish();*/
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewComponent(){

        swtDect = (Switch)findViewById(R.id.switchDect);
        if(ServiceOn)
            swtDect.setChecked(true);
        else
            swtDect.setChecked(false);
        swtDect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Intent intent = new Intent(SettingActivity.this,ServerService.class);
                    startService(intent);
                    Toast.makeText(SettingActivity.this, "Notification turn on!", Toast.LENGTH_LONG).show();
                    ServiceOn = true;
                }
                else{
                    Intent intent = new Intent(SettingActivity.this,ServerService.class);
                    stopService(intent);
                    Toast.makeText(SettingActivity.this, "Notification turn off!", Toast.LENGTH_LONG).show();
                    ServiceOn = false;
                }

            }
        });
    }


    public String getLocalIpAddress() {
        WifiManager wifi_service = (WifiManager)getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifi_service.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        if(ipAddress != 0) {
            String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
            return ip;
        }
        else
            return null;
    }



}
