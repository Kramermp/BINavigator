package binavigator.backend.net;

import binavigator.backend.net.ParameterStringBuilder;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class OracleCloudConnection {
	private static String connectionString;
	private static String connectionFile = "conf/connection.ini";
	private static String restEndPoint;
	HttpsURLConnection con;

	public OracleCloudConnection() {
		loadConnectionStringFromFile();
		loadRestEndPointFromFile();
		try {
			con = (HttpsURLConnection) new URL(connectionString).openConnection();
			con.setRequestMethod("POST");

			Map<String, String> parameters = new HashMap<>();
			parameters.put("content-type", "application/json");

			con.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(con.getOutputStream());
			out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
			out.flush();
			out.close();

			con.connect();
			System.out.print("Response: " + con.getResponseCode());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void loadConnectionStringFromFile() {
		try {
			String line;
			Scanner scanner = new Scanner(new FileReader(new File(connectionFile)));
			while(scanner.hasNext()) {
				line = scanner.nextLine();
				if(line.contains("url=")) {
					connectionString = line.substring(4);
					System.out.println(connectionString);
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void loadRestEndPointFromFile() {

		try {
			String line;
			Scanner scanner = new Scanner(new FileReader(new File(connectionFile)));
			while(scanner.hasNext()) {
				line = scanner.nextLine();
				if(line.contains("reportname=")) {
					line = line.substring(11);
					restEndPoint = "/v1/reports/" + line + "/run";
					System.out.println(connectionString);
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println("Rest End Point: " + restEndPoint);
	}


}
