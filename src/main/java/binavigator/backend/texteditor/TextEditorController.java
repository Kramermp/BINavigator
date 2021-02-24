package binavigator.backend.texteditor;

import binavigator.backend.BINavController;
import binavigator.ui.LinePainter;
import binavigator.ui.colortheme.Monokai;
import binavigator.ui.colortheme.RandomColorTheme;
import binavigator.ui.colortheme.TextColorTheme;
import binavigator.ui.colortheme.WindowTheme;
import binavigator.ui.texteditor.*;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TextEditorController {
	//Controller
	private BINavController parentController;

	//UI Components
	private TextEditorPanel textEditorPanel;
	private SqlStyledDocument sqlDoc = new SqlStyledDocument(this);
	private InfoPanel infoPanel;
	private ParenthesesPainter parenthesesPainter;
	private TextLineNumber textLineNumber;
	private LinePainter linePainter;

	//Font Settings
	private TextColorTheme textColorTheme;
	private Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);

	//State Varaibles
	private boolean textLineNumbersEnabled = true;
	private boolean parenthesesPainterEnable = true;
	private boolean infoPanelEnabled = true;

	//* Simple Text Editor Controller
	public TextEditorController() {
		try {
			textEditorPanel = new TextEditorPanel(this);
			textEditorPanel.setup();
			configurePanel();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public TextEditorController(BINavController biNavController) {
		this.parentController = biNavController;
		this. textColorTheme =  new Monokai(this);

		try {
			textEditorPanel = new TextEditorPanel(this);
			textEditorPanel.setup();
			configurePanel();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void configurePanel() {

		configureParenthesesPainter();
		configureTextLineNumbers();
		configureInfoPanel();
		linePainter = new LinePainter(textEditorPanel.getTextPane(), textEditorPanel.getBackground());

		TextEditorListener listener = new TextEditorListener(this);



		getTextPane().addKeyListener(listener);
		getTextPane().addFocusListener(listener);
		getTextPane().addCaretListener(listener);

		setFont(font);
	}

	private void configureInfoPanel() {
		if(infoPanelEnabled = true) {
			infoPanel = new InfoPanel(getInfoString());
			textEditorPanel.add(infoPanel, BorderLayout.SOUTH);
		} else {
			infoPanel = null;
		}
	}

	private void configureTextLineNumbers() {
		if(textLineNumbersEnabled) {
			textLineNumber = new TextLineNumber(getTextPane(), this);
			textEditorPanel.getjScrollPane().setRowHeaderView(textLineNumber);
		} else {
			textEditorPanel.getjScrollPane().setRowHeaderView(null);
		}
	}

	private void configureParenthesesPainter() {
		if(parenthesesPainterEnable == true) {
			parenthesesPainter = new ParenthesesPainter(getTextPane());
		} else {
			parenthesesPainter = null;
		}
	}

	public void caretMoved() {
		infoPanel.setCaretInfo(getInfoString());
	}

	public void repaintDocument() {
		textEditorPanel.repaintDocument();
	}

	public void refreshUi() {
		textColorTheme.updateStyles();
		repaintDocument();
		caretMoved();
	}

	//GETTERS
	public Component getTextEditorPanel() {
		return textEditorPanel;
	}

	public SqlStyledDocument getSqlStyledDocument() {
		return  this.sqlDoc;
	}

	public WindowTheme getWindowTheme() {
		return parentController.getWindowTheme();
	}

	public Color getParenthesePaintColor() {
		return new Color(textColorTheme.getParenthesesHiLightColor().getRed(),
				textColorTheme.getParenthesesHiLightColor().getGreen(),
				textColorTheme.getParenthesesHiLightColor().getBlue(),
				25);
	}

	public Font getFont() {
		return font;
	}

	public int getCaretColumn() throws BadLocationException {
		return getCaretPosition() - getRowStart(getCaretPosition());
	}

	public int getCaretPosition() {
		return getTextPane().getCaretPosition();
	}

	public String getInfoString() {
		try {
			return "Col: " + String.format("%03d", (getCaretColumn() + 1)) + " Char: " + String.format("%4d", getCaretPosition() + 1);
		} catch (BadLocationException e) {
			e.printStackTrace();
			return 	"Col: " + String.format("%03d", 0) + " Char: " + String.format("%4d", 0);
		}
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

	public JTextPane getNewSqlPane() {
		return new JTextPane(getSqlStyledDocument());
	}

	//SETTERS
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

	public void setFont(Font font) {
		this.font = font;
		if(this.textEditorPanel != null) {
			this.textEditorPanel.setFont(font);
		}
		if(this.textLineNumber != null) {
			this.textLineNumber.setFont(font);
		}
		if(this.infoPanel != null) {
			this.infoPanel.setFont(font);
		}
	}

	public void resetHighlight() {
		parenthesesPainter.resetHighlight();
	}
}
