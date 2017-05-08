package it.IotServer.Controller;
import java.sql.Connection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import it.IotServer.Utility.Listener;
import it.IotServer.Utility.Notifier;
import it.IotServer.Utility.PostgreSql;

@WebListener
public class MyAppServletContextListener implements ServletContextListener {
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("ServletContextListener destroyed");
	}

        //Run this before web application is started
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("ServletContextListener started");
		// Create two distinct connections, one for the notifier
        // and another for the listener to show the communication
        // works across connections 
		try{
			 @SuppressWarnings("resource")
				Connection lConn = PostgreSql.getConnection();
		        @SuppressWarnings("resource")
				Connection nConn = PostgreSql.getConnection();

		        // Create two threads, one to issue notifications and
		        // the other to receive them.

		        Listener listener = new Listener(lConn);
		        Notifier notifier = new Notifier(nConn);
		        listener.start();
		        notifier.start();
		} catch (Exception ex){
			ex.printStackTrace();
		}
       
	}
}
