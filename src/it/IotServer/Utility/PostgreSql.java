package it.IotServer.Utility;

import java.sql.Connection;
import java.sql.DriverManager;

public class PostgreSql {

	public static Connection getConnection() throws Exception {

		String DRIVER = "org.postgresql.Driver";
		String URL = "jdbc:postgresql://localhost:5432/IoTApp";

		Connection conn = null;
		while (conn == null) {
			try {
				Class.forName(DRIVER);
				conn = DriverManager.getConnection(URL, "postgres", "2374674");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return conn;
	}
}