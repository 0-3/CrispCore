package network.reborn.core.Util;

import json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

public class UUIDAPI {
	public static HashMap<String, String> cache = new HashMap<String, String>();

	public static String getNameMCAPI(String uuid) {
		String r = "";
		if (cache.containsKey(uuid)) {
			r = cache.get(uuid);
		} else {
			try {
				URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.replaceAll("-", ""));
				Scanner scan = new Scanner(url.openStream());
				String str = new String();
				while (scan.hasNext()) {
					str += scan.nextLine();
				}
				scan.close();
				JSONObject obj = new JSONObject(str);
				r = obj.getString("name");

			} catch (IOException e) {
				e.printStackTrace();
				r = "ERROR";
			}
			if (!r.equals("ERROR")) {
				if (!cache.containsKey(uuid) && !cache.containsValue(r)) {
					cache.put(uuid, r);
				}
			}
		}
		return r;
	}

	/*
	 * public static String getUUIDMCAPI(String name) { String r = ""; try { URL
	 * url = new URL("https://mcapi.ca/uuid/player/" + name); Scanner scan = new
	 * Scanner(url.openStream()); String str = new String(); while
	 * (scan.hasNext()) { str += scan.nextLine(); } scan.close(); JSONObject obj
	 * = new JSONObject(str); r = obj.getString("uuid");
	 * 
	 * } catch (IOException e) { e.printStackTrace(); } return r; }
	 */

}