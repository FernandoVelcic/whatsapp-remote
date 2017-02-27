package com.soxeon.whatsappremote;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import org.java_websocket.WebSocket;

import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Server extends WebSocketServer {
	
	public Server(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		// this.sendToAll("new connection: " + handshake.getResourceDescriptor());
		// conn.getRemoteSocketAddress().getAddress().getHostAddress();
		Log.i("[SERVER]", "New connection: " + conn);
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		Log.i("[SERVER]", "Closed connection: " + conn);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		JSONObject request = null;
		String action = null;

		try {
			request = new JSONObject(message);
			action = request.getString("action");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (action.equals("requestContacts")) {
			conn.send(WhatsApp.getContactList());
		} else if (action.equals("requestChats")) {
			conn.send(WhatsApp.getChatList());
		} else if (action.equals("sendMessage")) {
			WhatsApp.sendMessage(request);
		} else if (action.equals("requestMessages")) {
			conn.send(WhatsApp.getMessages(request));
		} else if (action.equals("requestAvatar")) {
			conn.send(WhatsApp.getAvatar(request));
		}else {
			Log.i("[SERVER]", "Unknown action: " + action);
		}
	}

	@Override
	public void onError(WebSocket conn, Exception e) {
		e.printStackTrace();
		if (conn != null) {
			// some errors like port binding failed may not be assignable to
			// a specific websocket
		}
	}

	public void sendToAll(String text) {
		Collection<WebSocket> con = connections();
		synchronized (con) {
			for (WebSocket c : con) {
				c.send(text);
			}
		}
	}
}