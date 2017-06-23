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
	public Risposta sendAlertFromJavaApplication(@PathParam("titolo") String titolo, @PathParam("luogo") String luogo) {
		boolean ris;
		if(luogo.equals("null")){
			luogo="";
		}
		try (Connection conn = PostgreSql.getConnection()) {
			Statement query = conn.createStatement();
			ResultSet resSet = query.executeQuery("SELECT token_value FROM token");
			while(resSet.next()){
				String tit;
				String text;
				if(titolo.equals("Incendio1")){
					tit = "Allarme Incendio";
					text ="Rilevato Incendio nell'edificio, apire l'app per la procedura di evacuazione";
				} else if(titolo.equals("Terremoto1")){
					tit = "Allarme Terremoto";
					text = "Rilevato terremoto, apire l'app per la procedura di evacuazione";
				} else{
					tit = "Mancanza di Illuminazione";
					text = "Rilevato guasto all'illuminazione, aprire l'app per rilevare la posizione del guasto";
				}
				
				Notification.pushFCMNotification(resSet.getString("token_value"), tit, text, titolo, luogo);
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
