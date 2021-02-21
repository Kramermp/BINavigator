package binavigator;


import binavigator.backend.BINavController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

/**
 * Entry Point for the BINavigator Application
 */
public class BINavigator {
	private static Logger log = LogManager.getLogger(BINavigator.class);

	public static void main(String[] args) {
		log.debug("Launching BINavigator");
		try {
			new BINavController();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
