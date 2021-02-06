package binavigator.backend;

import binavigator.ui.BINavigatorFrame;
import binavigator.ui.NavMenuBar;
import binavigator.ui.NavPanel;
import binavigator.ui.Theme;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.io.*;

public class BINavController {
	private BINavigatorFrame frame = null;
	private NavPanel panel = null;
	private NavMenuBar menuBar = null;

	public BINavController() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
		UIManager.setLookAndFeel(new FlatDarculaLaf());
		frame = new BINavigatorFrame();
		panel = new NavPanel();
		menuBar = new NavMenuBar(this);

		//loadColorPallete();
		frame.setExtendedState( frame.getExtendedState()| JFrame.MAXIMIZED_BOTH );
		frame.setJMenuBar(menuBar);
		frame.add(panel);
		frame.setVisible(true);
		frame.validate();
		frame.repaint();
	}

	public void exitSafely() {
		System.exit(0);
	}

	public void setTheme(Theme theme) {
		try {
			switch (theme) {
				case DARK:
					System.out.println("Setting Theme to Dark");
					UIManager.setLookAndFeel(new FlatDarculaLaf());
					break;
				case LIGHT:
					System.out.println("Setting Theme to Light");
					UIManager.setLookAndFeel(new FlatLightLaf());
					break;
			}
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		SwingUtilities.updateComponentTreeUI(frame);
		frame.validate();
		frame.repaint();
	}


	public void loadFile(File file) {
		System.out.println("Loading File: " + file.getAbsolutePath());
		StringBuilder sb = new StringBuilder();
		panel.openNewDocument();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while (br.ready()) {
				panel.appendToDocwLn(br.readLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

		public void saveToFile(File file ) {
		System.out.println("Saving to File: " + file.getAbsolutePath());
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(getTextContent().getBytes());
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getTextContent() {
		return panel.getTextContent();
	}
}
