package it.IotServer.Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.lang3.RandomStringUtils;

import it.IotServer.Model.Risposta;
import it.IotServer.Model.Utente;
import it.IotServer.Utility.PostgreSql;

@Path("utenti")
public class Utenti {

	// Questo servizio permette l'aggiunta di un nuvo utente sul db. Neccisita che gli venga passato un Json contente i dati dell'utente//
	@SuppressWarnings("resource")
	@POST
	@Path("newuser")
	@Consumes("application/json")
	@Produces("application/json")
	public Risposta insertUser(Utente utente) {
		try (Connection conn = PostgreSql.getConnection()) {

			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO UTENTE(nome, cognome, email, username, password) VALUES (?, ?, ?, ?, ?)");
			pstmt.setString(1, utente.getNome());
			pstmt.setString(2, utente.getCognome());
			pstmt.setString(3, utente.getEmail());
			pstmt.setString(4, utente.getUsername());
			pstmt.setString(5, utente.getPassword());
			pstmt.executeUpdate();

			pstmt = conn.prepareStatement("SELECT * FROM token WHERE token_value=?");
			pstmt.setString(1, utente.getToken());
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				pstmt = conn.prepareStatement("UPDATE token SET username=? WHERE token_value=?");
				pstmt.setString(1, utente.getUsername());
				pstmt.setString(2, utente.getToken());
				pstmt.executeUpdate();
			}
			pstmt.close();
			conn.close();
			rs.close();

			return new Risposta("true");
		} catch (Exception ex) {
			ex.printStackTrace();
			return new Risposta("false");
		}
	}

	// Questo servizio permette di verificare l'esistenza o meno di un determinato utente//
	@GET
	@Path("username/{user}/password/{pass}")
	public Risposta validateUser(@PathParam("user") String username, @PathParam("pass") String pass) {

		boolean utente = false;

		try (Connection conn = PostgreSql.getConnection()) {
			ResultSet rs;

			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM UTENTE WHERE USERNAME=? AND PASSWORD=?");
			pstmt.setString(1, username);
			pstmt.setString(2, pass);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				utente = true;
			}
			rs.close();
			pstmt.close();
			conn.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new Risposta(String.valueOf(utente));
	}

	@SuppressWarnings("resource")
	@POST
	@Path("saveToken")
	@Consumes("application/json")
	@Produces("application/json")
	public Risposta saveToken(Utente ut) {
		boolean inserito = false;
		String user = ut.getUsername();
		String token = ut.getToken();

		try (Connection conn = PostgreSql.getConnection()) {
			ResultSet rs;

			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM token WHERE USERNAME=?");
			pstmt.setString(1, user);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				pstmt = conn.prepareStatement("UPDATE token SET token_value=? WHERE username=?");
				pstmt.setString(1, token);
				pstmt.setString(2, user);
				pstmt.executeUpdate();
				inserito = true;
			} else {
				pstmt = conn.prepareStatement("INSERT INTO token(username, token_value) VALUES (?, ?)");
				pstmt.setString(1, user);
				pstmt.setString(2, token);
				pstmt.executeUpdate();
				inserito = true;
			}
			
			rs.close();
			pstmt.close();
			conn.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new Risposta(String.valueOf(inserito));
	}
//questo servizio dovrà essere richiamato al primo avvio dell'app. Fornisce uno username provvisorio con cui salvare il token. Tale username sarà sostituito con quello dell'utente quando si registra
	@GET
	@Path("getNewUsername")
	@Produces("application/json")
	public Risposta getNewUsername() {
		String username = null;
		try (Connection conn = PostgreSql.getConnection()) {
			boolean trovato = false;
			PreparedStatement pstmt;
			ResultSet rs;

			do {
				username = RandomStringUtils.randomAlphabetic(50);
				pstmt = conn.prepareStatement("SELECT * FROM token WHERE USERNAME=?");
				pstmt.setString(1, username);
				rs = pstmt.executeQuery();
				if (!rs.next()) {
					trovato = true;
				}
			} while (!trovato);
			rs.close();
			conn.close();
			pstmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new Risposta(username);
	}
}
