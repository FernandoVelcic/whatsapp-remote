package com.soxeon.whatsappremote;

import java.net.UnknownHostException;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.util.Log;

public class ServerService extends Service {
	private static final int SERVER_PORT = 8080;
	private Server WebServer;

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		// [Thread] Server initialization
		try {
			WebServer = new Server(SERVER_PORT);
			WebServer.start();
			Log.d("[DEBUG]", "WebServer started on port: " + WebServer.getPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		Log.d("[DEBUG]", "ServerService started.");
	}

	@Override
	public void onDestroy() { // this.stopSelf();
		super.onDestroy();
		try {
			WebServer.stop();
		} catch (Exception e) {
		}
		Log.d("[DEBUG]", "ServerService destroyed.");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
