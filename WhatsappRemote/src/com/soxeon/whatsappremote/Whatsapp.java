package com.soxeon.whatsappremote;

import java.io.File;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.CommandCapture;

import android.util.Log;

import org.java_websocket.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class WhatsApp {
	private static String WHATSAPP_DB_PATH = null;
	private static String WHATSAPP_PATH = null;
	private static int msg_count = 1;
	private static long start_timestamp_s = System.currentTimeMillis() / 1000L;

	public static void setDir(String Dir) {
		WHATSAPP_PATH = Dir + "/";
		WHATSAPP_DB_PATH = WHATSAPP_PATH + "databases/";
	}

	public static String getContactList() {
		JSONObject json = new JSONObject();
		JSONArray jsonArray;
		
		String query = "SELECT jid, display_name FROM wa_contacts WHERE is_whatsapp_user = 1";
		String[] rows = {"phone","name"};

		jsonArray = RootToolsE.sqlite3.Query(WHATSAPP_DB_PATH + "wa.db", query, rows);

		try {
			json.put("contacts", jsonArray);
			json.put("action", "respondContacts");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json.toString();
	}

	public static String getChatList() {
		JSONObject json = new JSONObject();
		JSONArray jsonArray;
		
		String query = "SELECT key_remote_jid, message_table_id FROM chat_list ORDER BY message_table_id DESC";
		String[] rows = {"phone", "last_message_id"};
		
		jsonArray = RootToolsE.sqlite3.Query(WHATSAPP_DB_PATH + "msgstore.db", query, rows);
	
		try {
			json.put("chats", jsonArray);
			json.put("action", "respondChats");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jsonArray.toString();
	}
	
	public static void sendMessage(JSONObject request)
	{
		String phone = null;
		String message = null;
		
		try {
			phone = request.getString("phone");
			message = request.getString("message");
		} catch(JSONException e) {
			e.getStackTrace();
		}
		
		long timestamp_ms = System.currentTimeMillis();
		
		String key_id = start_timestamp_s + "-" + msg_count++;
		
		String query = "INSERT INTO messages (key_remote_jid, key_from_me, key_id, status, needs_push, data, timestamp, media_url, media_mime_type, media_wa_type, media_size, media_name, media_hash, media_duration, origin, latitude, longitude, thumb_image, remote_resource, received_timestamp, send_timestamp, receipt_server_timestamp, receipt_device_timestamp, raw_data, recipient_count) VALUES('" + phone + "',1,'" + key_id + "',0,0,'" + message + "'," + timestamp_ms + ",NULL,NULL,'0',0,NULL,NULL,0,0,0.0,0.0,NULL,NULL," + timestamp_ms + ",-1,-1,-1,NULL,NULL)";
		
		Stop();
		RootToolsE.sqlite3.QueryNoResult(WHATSAPP_DB_PATH + "msgstore.db", query);
		Start();
	}
	
	public static String getMessages(JSONObject request)
	{
		String phone = null;
		int count = 0;
		
		try {
			phone = request.getString("phone");
			count = request.getInt("count");
		} catch(JSONException e) {
			e.getStackTrace();
		}
		
		JSONObject json = new JSONObject();
		JSONArray jsonArray;
		
		String query = "SELECT status, data, timestamp, origin, remote_resource, received_timestamp, receipt_device_timestamp FROM messages WHERE key_remote_jid = '" + phone + "' ORDER BY received_timestamp DESC LIMIT " + count;
		String rows[] = {"status", "message", "timestamp", "origin", "phone", "received_timestamp", "receipt_device_timestamp"};
		
		jsonArray = RootToolsE.sqlite3.Query(WHATSAPP_DB_PATH + "msgstore.db", query, rows);
		
		try {
			json.put("messages", jsonArray);
			json.put("phone", phone);
			json.put("action", "respondMessages");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json.toString();
	}
	
	public static String getAvatar(JSONObject request)
	{
		String phone = null;
		
		try {
			phone = request.getString("phone");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSONObject json = new JSONObject();
		
		String imageFile = WHATSAPP_PATH + "files/Avatars/" + phone + ".j";

		CommandCapture command = new CommandCapture(0, "cat " + imageFile);
		RootToolsE.Execute(command);
		RootToolsE.commandWait(command);
		//imageFile = Base64.encodeToString(command.toString().getBytes(), Base64.encodeFromFile(arg0));
		imageFile = command.toString();
		
		//cat imageFile | ./base64 --encode
		//imageFile = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
		
		try {
			json.put("photo", imageFile);
			json.put("phone", phone);
			json.put("action", "respondAvatar");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json.toString();
	}

	private static void Start() {
		RootToolsE.Execute("am startservice -n com.whatsapp/com.whatsapp.messaging.MessageService -a com.whatsapp.MessageService.START");
	}

	private static void Stop() {
		RootToolsE.Execute("am startservice -n com.whatsapp/com.whatsapp.messaging.MessageService -a com.whatsapp.MessageService.STOP_NO_RESTART");
	}
}
