package binavigator;


import binavigator.backend.BINavController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.text.BadLocationException;

/**
 * Entry Point for the BINavigator Application
 */
public class BINavigator {
	private static Logger log = LogManager.getLogger(BINavigator.class);

	public static void main(String[] args) {
		log.debug("Launching BINavigator");
		try {
			new BINavController();
		}  catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
}
