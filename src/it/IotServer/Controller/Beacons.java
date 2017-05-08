package it.IotServer.Controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


import it.IotServer.Model.Beacon;
import it.IotServer.Utility.PostgreSql;

@Path("beacons")
public class Beacons {

	//Questo servizio restituisce la lista di Beacon e la loro posizione//
	@GET
	@Produces("Application/json")
	public ArrayList<Beacon> getJason() {
		ArrayList<Beacon> beaconList = new ArrayList<Beacon>();
		try (Connection conn = PostgreSql.getConnection()) {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM beacon");

			while (rs.next()) {
				beaconList.add(new Beacon(rs.getString("macaddress"), rs.getString("posizione")));
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return beaconList;

	}

}
