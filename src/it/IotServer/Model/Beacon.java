package it.IotServer.Model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Beacon implements Serializable {

	private static final long serialVersionUID = 1L;

	private String macAdd;
	private String posizione;
	private String piano;
	private int x;
	private int y;
	
	public Beacon(String macAdd, String posizione, String piano, int x, int y) {
		this.macAdd = macAdd;
		this.posizione = posizione;
		this.piano = piano;
		this.x = x;
		this.y = y;
	}

	public String getMacAdd() {
		return macAdd;
	}

	public String getPosizione() {
		return posizione;
	}

	public String getPiano() {
		return piano;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	

}
