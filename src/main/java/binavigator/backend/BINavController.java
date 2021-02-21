package binavigator.backend;

import binavigator.ui.*;
import binavigator.ui.colortheme.WindowTheme;
import binavigator.ui.colortheme.TextColorTheme;
import binavigator.ui.colortheme.Monokai;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.io.*;

public class BINavController {
	private BINavigatorFrame frame = null;
	private TextEditorPanel panel = null;
	private NavMenuBar menuBar = null;

	private WindowTheme windowTheme = WindowTheme.DARK;
	private TextColorTheme textColorTheme = new Monokai(windowTheme);

	private SqlStyledDocument sqlDoc = new SqlStyledDocument(this);

	private InfoPanel infoPanel;


	public BINavController() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, BadLocationException {
		UIManager.setLookAndFeel(new FlatDarculaLaf());
		frame = new BINavigatorFrame();
		panel = new TextEditorPanel(this);
		menuBar = new NavMenuBar(this);
		infoPanel = new InfoPanel();

		panel.addInfoPanel(infoPanel);

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

		textColorTheme.setWindowTheme(windowTheme);
		panel.repaintDocument();
		infoPanel.validate();

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

	public void setTextColorTheme(TextColorTheme textColorTheme) {
		this.textColorTheme = textColorTheme;
		panel.setTextColorTheme(this.textColorTheme);
		panel.repaintDocument();
	}

	public WindowTheme getWindowTheme() {
		return windowTheme;
	}

	public void caretMoved() {

		infoPanel.setCaretInfo("Caret Move Detected");
	}

	public SqlStyledDocument getSqlStyledDocument() {
		return sqlDoc;
	}

	public int getRowStart(int offset) throws BadLocationException {
		return Utilities.getRowStart(panel.getTextPane(), offset);
	}

	public int getRowEnd(int offset) throws BadLocationException {
		return Utilities.getRowEnd(panel.getTextPane(), offset);
	}

	public JTextPane getNewSqlPane() {
		return new JTextPane(sqlDoc);
	}
}
