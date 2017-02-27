package com.soxeon.whatsappremote;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;

public final class RootToolsE
{
	public static void ChangeDir(String dir)
	{
		Execute("cd " + dir);
	}
	
	public static void ChangeMode(String file, int mode)
	{
		Execute("chmod " + mode + " " + file);
	}

	public static void Execute(CommandCapture commands)
	{
		try {
			RootTools.getShell(true).add(commands);
		} catch (Exception e) {
		}
	}
	
	public static void Execute(String command) {
		CommandCapture commands = new CommandCapture(0, command);

		Execute(commands);
	}
	
	//FIXME
	//Reference: http://code.google.com/p/roottools/issues/detail?id=51
    private static void commandWait(Command cmd) {
    	while(!cmd.isFinished())
    	{
	        synchronized (cmd) {
	            try {
	                if (!cmd.isFinished()) {
	                    cmd.wait(100);
	                }
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
    	}
    }
	
	public static class sqlite3 {
		public static JSONArray Query(String file, String query, final String[] Rows) {
			final JSONArray jsonArray = new JSONArray();

			CommandCapture command = new CommandCapture(0, "./sqlite3 " + file + " \"" + query + "\"") {
				@Override
				public void output(int id, String line) {
					if(line.length() == 0) return;
					
					JSONObject jsonObject = new JSONObject();
					String[] RowResults = line.split("\\|");

					try {
						for(int i = 0; i < RowResults.length; i++) {
							jsonObject.put(Rows[i], RowResults[i]);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					jsonArray.put(jsonObject);
				}
			};

			Execute(command);
			
			commandWait(command);

			return jsonArray;
		}

		public static void QueryNoResult(String file, String query) {
			Execute("./sqlite3 " + file + " \"" + query + "\"");
		}
	}
}