package binavigator.backend;

import binavigator.backend.net.OracleCloudConnection;
import binavigator.backend.texteditor.TextEditorController;
import binavigator.ui.*;
import binavigator.ui.colortheme.WindowTheme;
import binavigator.ui.colortheme.TextColorTheme;
import binavigator.ui.colortheme.Monokai;
import binavigator.ui.texteditor.*;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

public class BINavController {
	private Log log = LogFactory.getLog(this.getClass());
	private BINavigatorFrame frame = null;
	private TextEditorPanel panel = null;

	private WindowTheme windowTheme = WindowTheme.DARK;

	private TextEditorController textEditorController;


	public BINavController() throws UnsupportedLookAndFeelException  {

		UIManager.setLookAndFeel(new FlatDarculaLaf());
		frame = new BINavigatorFrame();
		this.textEditorController =  new TextEditorController(this);
		NavMenuBar menuBar = new NavMenuBar(this);

		frame.setExtendedState( frame.getExtendedState()| JFrame.MAXIMIZED_BOTH );
		frame.setJMenuBar(menuBar);

		frame.add(textEditorController.getTextEditorPanel());
		frame.add(new ButtonPanel(this), BorderLayout.NORTH);

		frame.setVisible(true);

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

		this.windowTheme = windowTheme;
		refresh();
	}

	private void refresh() {
		textEditorController.refresh();
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

	public WindowTheme getWindowTheme() {
		return windowTheme;
	}

	public SqlStyledDocument getSqlStyledDocument() {
		return textEditorController.getSqlStyledDocument();
	}

	public int getRowStart(int offset) throws BadLocationException {
		return Utilities.getRowStart(textEditorController.getTextPane(), offset);
	}

	public int getRowEnd(int offset) throws BadLocationException {
		return Utilities.getRowEnd(textEditorController.getTextPane(), offset);
	}


	public int getCaretPosition() {
		return textEditorController.getTextPane().getCaretPosition();
	}


	public void runSql() {
		log.info("Running Sql");
		log.info(textEditorController.getTextPane().getText());
		OracleCloudConnection occ =  new OracleCloudConnection();
	}

	public void setTextColorTheme(String textColorTheme) {
		textEditorController.setTextColorTheme(textColorTheme);
	}

}
