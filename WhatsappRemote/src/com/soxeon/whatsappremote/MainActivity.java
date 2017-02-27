package com.soxeon.whatsappremote;

import java.io.FileOutputStream;
import java.io.InputStream;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.stericson.RootTools.*;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Root status
        if (RootTools.isAccessGiven()) {
        	TextView statusText = (TextView)findViewById(R.id.textStatus);
        	statusText.setText("YES");
        	statusText.setTextColor(Color.GREEN);
        }
        
        //IP Address
        WifiManager wifiMgr = (WifiManager)getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        
        if(ip != 0)
        {
            String ipAddress = Formatter.formatIpAddress(ip);
            
            TextView IpAddressText = (TextView)findViewById(R.id.textIpAddress);
            IpAddressText.setText(ipAddress);
            IpAddressText.setTextColor(Color.BLACK);
        }
        
		//WhatsApp
		try {
			String WHATSAPP_PATH = getPackageManager().getPackageInfo("com.whatsapp",0).applicationInfo.dataDir;
			WhatsApp.setDir(WHATSAPP_PATH);
		} catch (NameNotFoundException e) {
			e.getStackTrace();
		}
		
		//RootTools
		RootTools.installBinary(getApplicationContext(), R.raw.sqlite3, "sqlite3", "755");
		RootToolsE.ChangeDir(getFilesDir().getPath());
	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void startClicked(View view) {
    	view.setClickable(false);
    	findViewById(R.id.buttonStop).setClickable(true);
    	
    	startService(new Intent(this, ServerService.class));
    }

    public void stopClicked(View view) {
    	view.setClickable(false);
    	findViewById(R.id.buttonStart).setClickable(true);
    	
    	stopService(new Intent(this, ServerService.class));
    }

}
