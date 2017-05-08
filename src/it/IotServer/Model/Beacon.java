package it.IotServer.Model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Beacon implements Serializable {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private String macAdd;
	@SuppressWarnings("unused")
	private String posizione;
	
	public Beacon() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Beacon(String macAdd, String posizione) {
		this.macAdd = macAdd;
		this.posizione = posizione;
	}

}
