package binavigator.backend;

public class OracleCloudConnection {
	private static String connectionString = OracleCloudConnection.loadConnectionStringFromFile();
	private static String connectionFile = "conf/connection.ini";

	public OracleCloudConnection() {

	}

	public static String loadConnectionStringFromFile() {

		return "Test String";
	}
}
