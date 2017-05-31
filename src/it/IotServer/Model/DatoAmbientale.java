package it.IotServer.Model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class DatoAmbientale {
	
	private String macAdd;
	private double batteria;
	private double temperatura;
	private double lux;
	private double xAcc;
	private double yAcc;
	private double zAcc;
	private String timestamp;
	private String username;
	
	public DatoAmbientale() {
		super();
	}
	
	public DatoAmbientale(String macAdd, double batteria, double temperatura, double lux, double xAcc, double yAcc, double zAcc, String timestamp, String username) {
		this.macAdd = macAdd;
		this.batteria = batteria;
		this.temperatura = temperatura;
		this.lux = lux;
		this.xAcc = xAcc;
		this.yAcc = yAcc;
		this.zAcc = zAcc;
		this.timestamp = timestamp;
		this.username = username;
	}

	public DatoAmbientale(String macAdd, double batteria, double temperatura, double lux, double xAcc, double yAcc, double zAcc, String timestamp) {
		this.macAdd = macAdd;
		this.batteria = batteria;
		this.temperatura = temperatura;
		this.lux = lux;
		this.xAcc = xAcc;
		this.yAcc = yAcc;
		this.zAcc = zAcc;
		this.timestamp = timestamp;
	}
	
	public String getUsername() {
		return username;
	}

	public String getMacAdd() {
		return macAdd;
	}

	public double getBatteria() {
		return batteria;
	}
	
	public double getTemperatura() {
		return temperatura;
	}

	public double getLux() {
		return lux;
	}

	public double getxAcc() {
		return xAcc;
	}

	public double getyAcc() {
		return yAcc;
	}

	public double getzAcc() {
		return zAcc;
	}

	public String getTimestamp() {
		return timestamp;
	}
}
