package it.IotServer.Controller;


import java.sql.Connection;
import java.sql.ResultSet;

import java.sql.Statement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import it.IotServer.Model.Risposta;
import it.IotServer.Utility.Notification;
import it.IotServer.Utility.PostgreSql;


@Path("alert")
public class Alert {

	@GET
	@Path("titolo/{titolo}/luogo/{luogo}")
	public Risposta validateUser(@PathParam("titolo") String titolo, @PathParam("luogo") String luogo) {
		boolean ris;
		if(luogo.equals("null")){
			luogo="";
		}
		try (Connection conn = PostgreSql.getConnection()) {
			Statement query = conn.createStatement();
			ResultSet resSet = query.executeQuery("SELECT token_value FROM token");
			while(resSet.next()){
				Notification.pushFCMNotification(resSet.getString("token_value"), titolo, luogo);
			}
			query.close();
			resSet.close();
			ris = true;
		} catch (Exception e) {
			e.printStackTrace();
			ris = false;
		} 
		
		return new Risposta(String.valueOf(ris));
	}
	
}
