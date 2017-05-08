package it.IotServer.Utility;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Listener extends Thread {

	private Connection conn;
	private org.postgresql.PGConnection pgconn;

	public Listener(Connection conn) throws SQLException {
		this.conn = conn;
		this.pgconn = (org.postgresql.PGConnection) conn;
		Statement stmt = conn.createStatement();
		stmt.execute("LISTEN beacon_change");
		stmt.close();
	}

	public void run() {
		while (true) {
			try {
				// issue a dummy query to contact the backend
				// and receive any pending notifications.
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT 1");
				rs.close();
				stmt.close();

				org.postgresql.PGNotification notifications[] = pgconn.getNotifications();
				if (notifications != null) {
					for (int i = 0; i < notifications.length; i++) {
						System.out.println("Got notification: " + notifications[i].getName());
						// TODO aggiungere invio notifica per cambiamenti posizioni beacon
						try {
							Statement query = conn.createStatement();
							ResultSet resSet = query.executeQuery("SELECT token_value FROM token ");

							while (resSet.next()) {
								Notification.pushFCMNotification(resSet.getString("token_value"), "Beacon Cambiati", "Attenzione sono state fatte modifiche alle posizoni dei Beacon");
							}
							query.close();
							resSet.close();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}

				// wait a while before checking again for new
				// notifications
				Thread.sleep(500);
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}
}