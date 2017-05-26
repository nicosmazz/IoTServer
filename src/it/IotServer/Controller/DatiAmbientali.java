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
			pstmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
			pstmt.setString(9,"mike");
			pstmt.executeUpdate();
			pstmt.close();
			ins = true;
			// controllo livello batteria beacon
			if (dato.getBatteria() <= 20) {
				Statement query = conn.createStatement();
				ResultSet resSet = query.executeQuery("SELECT token_value FROM token where username = 'nicosmazz'");
				Statement query2 = conn.createStatement();
				ResultSet resSet2 = query2.executeQuery("SELECT posizione FROM beacon where macaddress='" + dato.getMacAdd() + "';");

				if (resSet.next()) {
					if (resSet2.next()) {
						Notification.pushFCMNotification(resSet.getString("token_value"), "Livello Batteria Beacon Basso",
								"Attenzione la batteria del beacon " + dato.getMacAdd() + " in posizione " + resSet2.getString("posizione") + " � quasi scarica");
					}
				}
				query.close();
				query2.close();
				resSet.close();
				resSet2.close();
			}
			
			if(dato.getTemperatura() >= 50){
				Statement query = conn.createStatement();
				ResultSet resSet = query.executeQuery("SELECT token_value FROM token");
				Statement query2 = conn.createStatement();
				ResultSet resSet2 = query2.executeQuery("SELECT posizione FROM beacon where macaddress='" + dato.getMacAdd() + "';");
				if (resSet2.next()) {
					while (resSet.next()){
						Notification.pushFCMNotification(resSet.getString("token_value"), "Pericolo Incendio",
								"Attenzione pericolo incendio in " + resSet2.getString("posizione"));
					}
				}
				
				query.close();
				query2.close();
				resSet.close();
				resSet2.close();
			}
			
			if(dato.getLux() <= 0){
				Statement query = conn.createStatement();
				ResultSet resSet = query.executeQuery("SELECT token_value FROM token");
				Statement query2 = conn.createStatement();
				ResultSet resSet2 = query2.executeQuery("SELECT posizione FROM beacon where macaddress='" + dato.getMacAdd() + "';");
				if (resSet2.next()) {
					while (resSet.next()){
						Notification.pushFCMNotification(resSet.getString("token_value"), "Problema illuminazione",
								"Attenzione assenza di illuminazione in " + resSet2.getString("posizione"));
					}
				}
				
				query.close();
				query2.close();
				resSet.close();
				resSet2.close();
			}
			
			if( (dato.getxAcc() >= 1.5 || dato.getxAcc() <= (-1.5) ) || (dato.getyAcc() >= 1.5 || dato.getyAcc() <= (-1.5) ) 
					|| (dato.getzAcc() >= 1.5 || dato.getzAcc() <= (-1.5) ) ){
				Statement query = conn.createStatement();
				ResultSet resSet = query.executeQuery("SELECT token_value FROM token");
				Statement query2 = conn.createStatement();
				ResultSet resSet2 = query2.executeQuery("SELECT posizione FROM beacon where macaddress='" + dato.getMacAdd() + "';");
				if (resSet2.next()) {
					while (resSet.next()){
						Notification.pushFCMNotification(resSet.getString("token_value"), "Pericolo Terremoto",
								"Attenzione rilevato terremoto in " + resSet2.getString("posizione"));
					}
				}
				
				query.close();
				query2.close();
				resSet.close();
				resSet2.close();
			}

			conn.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new Risposta(String.valueOf(ins));
	}

	// Questo servizio permette di recuperare, in formato Json, la lettura pi� recente effettuata su un Beacon. Necessita che gli venga passato il macAddress del Beacon//
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
						rs.getDouble("y_accellerazione"), rs.getDouble("z_accellerazione"), rs.getString("timestamp"));
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