package binavigator.backend;

import binavigator.ui.BINavigatorFrame;
import binavigator.ui.NavMenuBar;
import binavigator.ui.TextEditorPanel;
import binavigator.ui.colortheme.WindowTheme;
import binavigator.ui.colortheme.TextColorTheme;
import binavigator.ui.colortheme.Monokai;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.io.*;

public class BINavController {
	private BINavigatorFrame frame = null;
	private TextEditorPanel panel = null;
	private NavMenuBar menuBar = null;
	private TextColorTheme textColorTheme = new Monokai();

	public BINavController() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
		UIManager.setLookAndFeel(new FlatDarculaLaf());
		frame = new BINavigatorFrame();
		panel = new TextEditorPanel(textColorTheme);
		menuBar = new NavMenuBar(this);

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

	public void setTheme(WindowTheme windowTheme) {
		try {
			switch (windowTheme) {
				case DARK:
					System.out.println("Setting WindowTheme to Dark");
					UIManager.setLookAndFeel(new FlatDarculaLaf());
					break;
				case LIGHT:
					System.out.println("Setting WindowTheme to Light");
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

	public TextColorTheme getTextColorTheme() {
		return this.textColorTheme;
	}
}
