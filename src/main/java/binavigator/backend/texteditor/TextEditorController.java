package binavigator.backend.texteditor;

import binavigator.backend.BINavController;
import binavigator.backend.utils.CharArrayUtil;
import binavigator.ui.colortheme.Monokai;
import binavigator.ui.colortheme.RandomColorTheme;
import binavigator.ui.colortheme.TextColorTheme;
import binavigator.ui.colortheme.WindowTheme;
import binavigator.ui.texteditor.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;
import java.awt.*;

@SuppressWarnings({"ALL", "StatementWithEmptyBody"})
public class TextEditorController {
	//Controller
	private BINavController parentController;

	//UI Components
	private TextEditorPanel textEditorPanel;
	private final SqlStyledDocument sqlDoc = new SqlStyledDocument(this);
	private InfoPanel infoPanel;
	private TextLineNumber textLineNumber;
	private TextEditorPainter tePainter;

	//Font Settings
	private TextColorTheme textColorTheme;
	private Font font = new Font(Font.MONOSPACED, Font.PLAIN, 25);

	//State Variables
	private boolean textLineNumbersEnabled = true;
	private boolean parenthesesPainterEnabled = true;
	private boolean infoPanelEnabled = true;
	private boolean characterCountLineEnabled = true;
	private int tabSize = 4;
	private int characterCountLimit = 80;

	//* Simple Text Editor Controller
	public TextEditorController() {
		try {

			textEditorPanel = new TextEditorPanel(this);
			textEditorPanel.setup();

			textEditorPanel.getTextPane().getHighlighter().addHighlight(0, 0, new TextEditorPainter(this, this.getTextPane()));
			textEditorPanel.add(new InfoPanel(), BorderLayout.SOUTH);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public TextEditorController(BINavController biNavController) {

		this.parentController = biNavController;
		this. textColorTheme =  new Monokai(this);

		try {

			textEditorPanel = new TextEditorPanel(this);
			setFont(font);
			textEditorPanel.setup();
			configurePanel();
			this.tePainter = new TextEditorPainter(this, this.getTextPane());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void configurePanel() {

		TextEditorListener listener = new TextEditorListener(this);
		getTextPane().addKeyListener(listener);
		getTextPane().addFocusListener(listener);
		getTextPane().addCaretListener(listener);

		infoPanel = new InfoPanel();
		textEditorPanel.add(infoPanel, BorderLayout.SOUTH);
	}

	public void repaintTextEditor() {
		int[] highlightArgs = getHighLightArgs();
		try {
			infoPanel.setCaretInfo((getCaretColumn() + 1), getCaretPosition() + 1);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		tePainter.resetHighlight(highlightArgs[0], highlightArgs[1]);
	}

	public int[] getHighLightArgs() {

		int openIndex = checkForOpenParentheses(getTextPane().getCaretPosition());
		if(sqlDoc.getDocMap().isInComment(openIndex)) {
			openIndex = -1;
		}
		int closeIndex = checkForCloseParentheses(getTextPane().getCaretPosition());
		if(sqlDoc.getDocMap().isInComment(closeIndex)) {
			closeIndex = -1;
		}

		char[] searchArray = textEditorPanel.getTextPane().getText().toCharArray();

		if (openIndex >=0 && closeIndex >= 0 && openIndex < closeIndex) {
//			System.out.println("On open and close");
			//Do Nothing
		}else if (openIndex < 0 && closeIndex < 0){
//			System.out.println("Not on a Parentheses");
			//Do Nothing
		} else if (openIndex >= 0 && openIndex > closeIndex){
			System.out.println("On an Open Parentheses");
			closeIndex = CharArrayUtil.findCloseParentheses(openIndex, searchArray, sqlDoc.getDocMap());
		} else //noinspection ConstantConditions
			if (closeIndex >= 0 && closeIndex > openIndex) {
				System.out.println("On a Close Parentheses");
				openIndex = CharArrayUtil.findOpenParentheses(closeIndex, searchArray, sqlDoc.getDocMap());
			}

		System.out.println("Open: " + openIndex + " Close: " + closeIndex);

		if(openIndex < 0) {
			openIndex = 0;
		}

		if (closeIndex >= searchArray.length) {
			closeIndex = searchArray.length - 1;
		}
		return new int[]{openIndex, closeIndex};
	}

	public void findParentheses() {
		char[] searchArray = textEditorPanel.getTextPane().getText().toCharArray();
		int openIndex  = CharArrayUtil.findOpenParentheses(textEditorPanel.getTextPane().getCaretPosition(), searchArray, sqlDoc.getDocMap());
		int closeIndex = CharArrayUtil.findCloseParentheses(textEditorPanel.getTextPane().getCaretPosition(), searchArray, sqlDoc.getDocMap());


		tePainter.resetHighlight(openIndex, closeIndex);
	}

	private int checkForOpenParentheses(int startPosition) {
		int textLength = getTextPane().getText().length();
		//noinspection StatementWithEmptyBody
		if (startPosition >= textLength || startPosition < 0) {
			// Do Nothing
		} else if (getTextPane().getText().charAt(startPosition) == '(') {
			return startPosition;
		}

		if (startPosition - 1 >= textLength || startPosition - 1 < 0) {
			return -1;
		} else if (getTextPane().getText().charAt(startPosition - 1) == '(') {
			return (startPosition - 1);
		}

		return -1;
	}

	private int checkForCloseParentheses(int startPosition) {
		int textLength = getTextPane().getText().length();
		if (startPosition >= textLength || startPosition < 0) {
			// Do Nothing;
		} else if (getTextPane().getText().charAt(startPosition) == ')') {
			return startPosition;
		}

		if (startPosition - 1 >= textLength || startPosition - 1 < 0) {
			return -1;
		} else if (getTextPane().getText().charAt(startPosition - 1) == ')') {
			return (startPosition - 1);
		}

		return -1;
	}

	public void highlightText() {
		textEditorPanel.highlightText();
	}

	public void refreshUi() {
		textColorTheme.updateStyles();
		highlightText();
		repaintTextEditor();
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

	public Color getParenthesesPaintColor() {
		return new Color(textColorTheme.getParenthesesHiLightColor().getRed(),
				textColorTheme.getParenthesesHiLightColor().getGreen(),
				textColorTheme.getParenthesesHiLightColor().getBlue(),
				25);
	}

	public Font getFont() {
		return font;
	}

	private int getCaretColumn() throws BadLocationException {
		return getCaretPosition() - getRowStart(getCaretPosition());
	}

	private int getCaretPosition() {
		return getTextPane().getCaretPosition();
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

	public int  getTabSize() {
		return tabSize;
	}

	public int getCharacterCountLimit() {
		return this.characterCountLimit;
	}

	public boolean getCharacterCountLineEnabled() {
		return this.characterCountLineEnabled;
	}

	// ** SETTERS **
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
	}

	public void setTextLineNumbersEnabled(boolean state) {
		textLineNumbersEnabled = state;
	}

	public void setParenthesesPainterEnabled(boolean state) {
		parenthesesPainterEnabled = state;
	}

	@SuppressWarnings("unused")
	public void setInfoPanelEnabled(boolean state) {
		infoPanelEnabled = state;
	}

	public void setTabSize(int newTabSize) {
		this.tabSize = newTabSize;
		textEditorPanel.setTabSize(newTabSize);
	}

	public void setCharacterCountLimit(int limit) {
		this.characterCountLimit = limit;
		repaintTextEditor();
	}

	public void setChararcterCountLineEnabled(boolean chararcterCountLine) {
		this.characterCountLineEnabled = chararcterCountLine;
		repaintTextEditor();
	}


}
