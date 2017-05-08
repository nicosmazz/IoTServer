package it.IotServer.Utility;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class Notification {

	public final static String AUTH_KEY_FCM = " AAAAA9Y3gjI:APA91bF-80iC47IgM70Ybur-KvkhUJ70mbrEZ3iJRAJK-XeZ2b7DhiTzjY2jL0aSpuGZA2zXn2p8S19-_yokFWsE862YWPfthGqk3wjVgNI2Zw0nu3XDPTmLWWb-uaK6Dyq3QdrqN6kk ";
	public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

	// userDeviceIdKey is the device id you will query from your database

	public static void pushFCMNotification(String userDeviceIdKey, String title, String body) throws Exception {

			String authKey = AUTH_KEY_FCM; // You FCM AUTH key
			String FMCurl = API_URL_FCM;
			System.out.println(userDeviceIdKey);
			URL url = new URL(FMCurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.setDoOutput(true);

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "key=" + authKey);
			conn.setRequestProperty("Content-Type", "application/json");

			JSONObject json = new JSONObject();
			json.put("to", userDeviceIdKey.trim());
			JSONObject info = new JSONObject();
			info.put("title", title); // Notification title
			info.put("body", body); // Notification body
			json.put("notification", info);

			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(json.toString());
			wr.flush();
			conn.getInputStream();
	}
}
