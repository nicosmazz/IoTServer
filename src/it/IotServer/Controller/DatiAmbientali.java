package it.IotServer.Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import it.IotServer.Model.DatoAmbientale;
import it.IotServer.Model.Risposta;
import it.IotServer.Utility.GoogleMail;
import it.IotServer.Utility.Notification;
import it.IotServer.Utility.PostgreSql;

@Path("datiAmbientali")
public class DatiAmbientali {

	// Questo servizio permette di salvare una lettura effettuata da un beacon, al servizio va passato un json contenente i dati letti.//
	@POST
	@Path("newDatoAmbientale")
	@Consumes("application/json")
	@Produces("application/json")
	public Risposta insertDatoAmbientale(DatoAmbientale dato) {
		boolean ins = false;

		try (Connection conn = PostgreSql.getConnection()) {
			PreparedStatement pstmt = conn.prepareStatement(
					"INSERT INTO DATO_AMBIENTALE(beacon, batteria, temperatura, lux, x_accellerazione, y_accellerazione, z_accellerazione, timestamp, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			pstmt.setString(1, dato.getMacAdd());
			pstmt.setDouble(2, dato.getBatteria());
			pstmt.setDouble(3, dato.getTemperatura());
			pstmt.setDouble(4, dato.getLux());
			pstmt.setDouble(5, dato.getxAcc());
			pstmt.setDouble(6, dato.getyAcc());
			pstmt.setDouble(7, dato.getzAcc());
			pstmt.setTimestamp(8, Timestamp.valueOf(dato.getTimestamp()));
			pstmt.setString(9, dato.getUsername());
			pstmt.executeUpdate();
			pstmt.close();
			ins = true;
			// controllo livello batteria beacon
			if (dato.getBatteria() <= MyServlet.batteryThreshold) {
				Statement query = conn.createStatement();
				//ResultSet resSet = query.executeQuery("SELECT token_value FROM token where username = 'admin'");
				ResultSet resSet = query.executeQuery("SELECT email FROM utente where username = 'admin'");

				if (resSet.next()) {
					//Notification.pushFCMNotification(resSet.getString("token_value"), "Batteria", dato.getMacAdd());
					PreparedStatement prepStat = conn.prepareStatement("SELECT posizione, x, y FROM beacon where macaddress = ?");
					prepStat.setString(1, dato.getMacAdd());
					ResultSet resSet2 = prepStat.executeQuery();
					if(resSet2.next()){
						String message = "La batteria del Beacon posizionato in: " + resSet2.getString("posizione") + " alle cordinate, x:" 
								+ resSet2.getInt("x")+ ", y:" +resSet2.getInt("y") + " sta per esaurirsi." + '\n' 
								+"E' consigliata la sua sostituzione";
								GoogleMail.send(resSet.getString("email"),"Batteria Beacon Scarsa",message);
					}
					
					prepStat.close();
					resSet2.close();
				}
				query.close();
				resSet.close();
			}

			// controllo temperatura
			if (dato.getTemperatura() >= MyServlet.temperatureThreshold) {
				Statement query = conn.createStatement();
				ResultSet resSet = query.executeQuery("SELECT token_value FROM token");

				while (resSet.next()) {
					Notification.pushFCMNotification(resSet.getString("token_value"), "Incendio", dato.getMacAdd());
				}

				query.close();
				resSet.close();
			}

			// controllo illuminazione
			if (dato.getLux() <= MyServlet.lightThreshold) {
				Statement query = conn.createStatement();
				ResultSet resSet = query.executeQuery("SELECT token_value FROM token");
				
					while (resSet.next()) {
						Notification.pushFCMNotification(resSet.getString("token_value"), "Illuminazione", dato.getMacAdd());
					}

				query.close();
				resSet.close();
			}

			// controllo accellerometro
			if ((dato.getxAcc() >= MyServlet.xAccThreshold || dato.getxAcc() <= (-MyServlet.xAccThreshold))
					|| (dato.getyAcc() >= MyServlet.yAccThreshold || dato.getyAcc() <= (-MyServlet.yAccThreshold))
					|| (dato.getzAcc() >= MyServlet.zAccThreshold || dato.getzAcc() <= (-MyServlet.zAccThreshold))) {
				Statement query = conn.createStatement();
				ResultSet resSet = query.executeQuery("SELECT token_value FROM token");
				
					while (resSet.next()) {
						Notification.pushFCMNotification(resSet.getString("token_value"), "Terremoto", dato.getMacAdd());
					}

				query.close();
				resSet.close();
			}
			conn.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new Risposta(String.valueOf(ins));
	}

	// Questo servizio permette di recuperare, in formato Json, la lettura più recente effettuata su un Beacon. Necessita che gli venga passato il macAddress del Beacon//
	@GET
	@Path("getLastData/{id}")
	@Produces("application/json")
	public DatoAmbientale getLastData(@PathParam("id") String macAdd) {
		DatoAmbientale lastData = new DatoAmbientale();
		try (Connection conn = PostgreSql.getConnection()) {
			PreparedStatement pstmt = conn.prepareStatement("SELECT beacon, batteria, temperatura, lux, x_accellerazione, y_accellerazione, "
					+ "z_accellerazione, timestamp FROM DATO_AMBIENTALE where id = " + "(select MAX(id) from dato_ambientale where beacon = ?);");
			pstmt.setString(1, macAdd);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				lastData = new DatoAmbientale(rs.getString("beacon"), rs.getInt("batteria"), rs.getDouble("temperatura"), rs.getDouble("lux"), rs.getDouble("x_accellerazione"),
						rs.getDouble("y_accellerazione"), rs.getDouble("z_accellerazione"), String.valueOf(rs.getTimestamp("timestamp")));
			}
			conn.close();
			pstmt.close();
			rs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lastData;
	}
}
