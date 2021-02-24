package binavigator.backend.texteditor;

import binavigator.backend.BINavController;
import binavigator.ui.colortheme.Monokai;
import binavigator.ui.colortheme.RandomColorTheme;
import binavigator.ui.colortheme.TextColorTheme;
import binavigator.ui.colortheme.WindowTheme;
import binavigator.ui.texteditor.InfoPanel;
import binavigator.ui.texteditor.SqlStyledDocument;
import binavigator.ui.texteditor.TextEditorPanel;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;
import java.awt.*;

public class TextEditorController {
	private BINavController parentController;

	private TextEditorPanel textEditorPanel;
	private SqlStyledDocument sqlDoc = new SqlStyledDocument(this);

	private boolean displayLineNumbers = true;
	private InfoPanel infoPanel = new InfoPanel(" ");

	private TextColorTheme textColorTheme;
	private Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);

	public TextEditorController(BINavController biNavController) {
		this.parentController = biNavController;
		this. textColorTheme =  new Monokai(this);

		try {
			textEditorPanel = new TextEditorPanel(this);
			textEditorPanel.setup();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public Component getTextEditorPanel() {

		return textEditorPanel;
	}

	public SqlStyledDocument getSqlStyledDocument() {
		return  this.sqlDoc;
	}

	public JTextPane getNewSqlPane() {
		return new JTextPane(getSqlStyledDocument());
	}

	public void caretMoved() {
		infoPanel.setCaretInfo(getInfoString());
	}

	public BINavController getParentController() {
		return parentController;
	}

	public JTextComponent getTextPane() {
		return textEditorPanel.getTextPane();
	}

	public int getRowStart(int offset) throws BadLocationException {
		return Utilities.getRowStart(textEditorPanel.getTextPane(), offset);
	}

	public int getRowEnd(int offset) throws BadLocationException {
		return Utilities.getRowEnd(textEditorPanel.getTextPane(), offset);
	}

	public TextColorTheme getTextColorTheme() {
		return this.textColorTheme;
	}

	public String getInfoString() {
		try {
			return "Col: " + String.format("%03d", (getCaretColumn() + 1)) + " Char: " + String.format("%4d", getCaretPosition() + 1);
		} catch (BadLocationException e) {
			e.printStackTrace();
			return 	"Col: " + String.format("%03d", 0) + " Char: " + String.format("%4d", 0);
		}
	}

	public void repaintDocument() {
		textEditorPanel.repaintDocument();
	}

	public int getCaretPosition() {
		return getTextPane().getCaretPosition();
	}

	public int getCaretColumn() throws BadLocationException {
		return getCaretPosition() - getRowStart(getCaretPosition());
	}

	public Font getFont() {
		return font;
	}

	public Color getParenthesePaintColor() {
		return new Color(textColorTheme.getParenthesesHiLightColor().getRed(),
				textColorTheme.getParenthesesHiLightColor().getGreen(),
				textColorTheme.getParenthesesHiLightColor().getBlue(),
				25);
	}

	public void refresh() {
		textColorTheme.updateStyles();
		repaintDocument();
		caretMoved();
	}

	public void setTextColorTheme(String textColorTheme) {
		switch (textColorTheme.toUpperCase()) {
			case "MONOKAI":
				this.textColorTheme = new Monokai((this));
				return;
			case "RANDOM":
				this.textColorTheme = new RandomColorTheme(this);
				return;
			default:
				this.textColorTheme = new Monokai(this);
		}
	}

	public WindowTheme getWindowTheme() {
		return parentController.getWindowTheme();
	}
}
