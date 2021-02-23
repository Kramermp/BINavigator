package binavigator.backend;

import binavigator.ui.*;
import binavigator.ui.colortheme.WindowTheme;
import binavigator.ui.colortheme.TextColorTheme;
import binavigator.ui.colortheme.Monokai;
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
	private TextColorTheme textColorTheme;

	private SqlStyledDocument sqlDoc = new SqlStyledDocument(this);
	private Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);

	private InfoPanel infoPanel;
	private ParenthesesPainter parenthesesPainter;

	private int tabSize = 8;


	public BINavController() throws UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, BadLocationException {
		textColorTheme = new Monokai(this);
		UIManager.setLookAndFeel(new FlatDarculaLaf());
		frame = new BINavigatorFrame();
		panel = new TextEditorPanel(this);
		NavMenuBar menuBar = new NavMenuBar(this);
		infoPanel = new InfoPanel(" ");
		TextLineNumber textLineNumber = new TextLineNumber(panel.getTextPane(), this);

		panel.addInfoPanel(infoPanel);
		panel.addTextLineNumber(textLineNumber);

		frame.setExtendedState( frame.getExtendedState()| JFrame.MAXIMIZED_BOTH );
		frame.setJMenuBar(menuBar);
		frame.add(panel);
		frame.add(new ButtonPanel(this), BorderLayout.NORTH);

		frame.setVisible(true);

	}

	public void setup() {
		//Hard Coding because there is a weird multi thread thing goign on here i think
		infoPanel.setCaretInfo("Col: " + String.format("%03d", 1) + " Char: " + String.format("%4d", 1));
		setFont(this.font);

		parenthesesPainter = new ParenthesesPainter(panel.getTextPane(), this);
		panel.getTextPane().addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent keyEvent) {

			}

			@Override
			public void keyPressed(KeyEvent keyEvent) {

			}

			@Override
			public void keyReleased(KeyEvent keyEvent) {
				if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT || keyEvent.getKeyCode() == KeyEvent.VK_LEFT || keyEvent.getKeyCode() == KeyEvent.VK_UP ||
						keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
					parenthesesPainter.resetHighlight();
				}
			}
		});
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
		textColorTheme.updateStyles();
		panel.repaintDocument();
		caretMoved();
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
		infoPanel.setCaretInfo(getInfoString());
	}

	public String getInfoString() {
		try {
			return "Col: " + String.format("%03d", (getCaretColumn() + 1)) + " Char: " + String.format("%4d", getCaretPosition() + 1);
		} catch (BadLocationException e) {
			e.printStackTrace();
			return 	"Col: " + String.format("%03d", 0) + " Char: " + String.format("%4d", 0);
		}
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

	public int getCaretPosition() {
		return panel.getTextPane().getCaretPosition();
	}

	public int getCaretColumn() throws BadLocationException {
		return getCaretPosition() - getRowStart(getCaretPosition());
	}

	public void setFont(Font font) {
		this.font = font;
		panel.setFont(font);
		panel.getTextPane().setFont(font);
	}

	public Font getFont() {
		return font;
	}

	public void setTabSize(int tabSize) {
		this.tabSize = tabSize;
	}

	public int getTabSize() {
		return this.tabSize;
	}

	public Color getParenthesePaintColor() {
		return new Color(textColorTheme.getParenthesesHiLightColor().getRed(),
				textColorTheme.getParenthesesHiLightColor().getGreen(),
				textColorTheme.getParenthesesHiLightColor().getBlue(),
				25);
	}

	public void runSql() {
		log.info("Running Sql");
		log.info(panel.getTextPane().getText());
	}

	static class CustomTabParagraphView extends ParagraphView {

		public CustomTabParagraphView(Element elem) {
			super(elem);
		}

		public float nextTabStop(float x, int tabOffset) {
			TabSet tabs = getTabSet();
			if(tabs == null) {
				// a tab every 72 pixels.
				return (float)(getTabBase() + (((int)x / 8 + 1) * 8));
			}

			return super.nextTabStop(x, tabOffset);
		}

	}

}
