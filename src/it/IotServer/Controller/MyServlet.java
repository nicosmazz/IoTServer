package it.IotServer.Controller;

import java.io.File;
import java.io.FileReader;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@WebListener
public class MyServlet implements ServletContextListener {
	
	public static ServletContext servletContext;
	public static int temperatureThreshold;
	public static int lightThreshold;
	public static int batteryThreshold;
	public static double xAccThreshold;
	public static double yAccThreshold;
	public static double zAccThreshold;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {

			JSONParser jsonParser = new JSONParser();
			servletContext = arg0.getServletContext();
			File file = new File(servletContext.getRealPath("WEB-INF/configurationParameters.json"));
			FileReader reader = new FileReader(file);
			Object object = jsonParser.parse(reader);
			JSONObject jsonObject = (JSONObject) object;

			temperatureThreshold = Integer.valueOf((String) jsonObject.get("temperatureThreshold"));
			lightThreshold = Integer.valueOf((String) jsonObject.get("lightThreshold"));
			xAccThreshold = Double.valueOf((String) jsonObject.get("xAccThreshold"));
			yAccThreshold = Double.valueOf((String) jsonObject.get("yAccThreshold"));
			zAccThreshold = Double.valueOf((String) jsonObject.get("zAccThreshold"));
			batteryThreshold = Integer.valueOf((String) jsonObject.get("batteryThreshold"));
			
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
