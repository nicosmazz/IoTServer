package it.IotServer.Model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Risposta {
	
	@SuppressWarnings("unused")
	private String response;

	public Risposta(String response) {
		this.response = response;
	}

	public Risposta() {
		super();
	}

}
