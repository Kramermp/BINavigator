package binavigator.backend.texteditor;

import binavigator.backend.BINavController;
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
	private ParenthesesPainter parenthesesPainter;
	private TextLineNumber textLineNumber;
//	private CaretLinePainter linePainter;
//	private CharacterCountPainter ccp;
	private TextEditorPainter tep;

	//Font Settings
	private TextColorTheme textColorTheme;
	private Font font = new Font(Font.MONOSPACED, Font.PLAIN, 25);

	//State Variables
	private boolean textLineNumbersEnabled = true;
	private boolean parenthesesPainterEnabled = true;
	private boolean infoPanelEnabled = true;
	private int tabSize = 4;

	//* Simple Text Editor Controller
	public TextEditorController() {
		try {

			textEditorPanel = new TextEditorPanel(this);
			textEditorPanel.setup();

			textEditorPanel.getTextPane().getHighlighter().addHighlight(0, 0, new TextEditorPainter(this, this.getTextPane()));

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
			this.tep = new TextEditorPainter(this, this.getTextPane());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void configurePanel() {

		TextEditorListener listener = new TextEditorListener(this);
		getTextPane().addKeyListener(listener);
		getTextPane().addFocusListener(listener);
		getTextPane().addCaretListener(listener);

	}



	public void caretMoved() {
		int[] highlightArgs = getHighLightArgs();

		tep.resetHighlight(highlightArgs[0], highlightArgs[1]);
	}

	public int[] getHighLightArgs() {
		int openIndex = checkForOpenParentheses(getTextPane().getCaretPosition());
		int closeIndex = checkForCloseParentheses(getTextPane().getCaretPosition());


		if (openIndex >=0 && closeIndex >= 0 && openIndex < closeIndex) {
//			System.out.println("On open and close");
			//Do Nothing
		}else if (openIndex < 0 && closeIndex < 0){
//			System.out.println("Not on a Parentheses");
			//Do Nothing
		} else if (openIndex >= 0 && openIndex > closeIndex){
			System.out.println("On an Open Parentheses");
			closeIndex = searchForCloseParentheses(openIndex);
		} else //noinspection ConstantConditions
			if (closeIndex >= 0 && closeIndex > openIndex) {
				System.out.println("On a Close Parentheses");
				openIndex = searchForOpenParentheses(closeIndex);
			}

//		System.out.println("Open: " + openIndex + " Close: " + closeIndex);

		return new int[]{openIndex, closeIndex};
	}

	public void findParentheses() {
		int openIndex  = searchForOpenParentheses(textEditorPanel.getTextPane().getCaretPosition());
		int closeIndex = searchForCloseParentheses(textEditorPanel.getTextPane().getCaretPosition());


		tep.resetHighlight(openIndex, closeIndex);
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

	private int searchForOpenParentheses(int startIndex) {
		int countNeeded = 1;
		int i;

		i = startIndex;
		while(i > 0 && countNeeded > 0) {
			i--;
			if(getTextPane().getText().charAt(i) == ')') {
				countNeeded++;
			} else if (getTextPane().getText().charAt(i) == '(') {
				countNeeded--;
			}
		}

		return i;
	}

	private int searchForCloseParentheses(int startIndex) {
		int countNeeded = 1;
		int i = startIndex;
		int lastIndex = getTextPane().getText().length() - 1;

		while(i < lastIndex && countNeeded > 0) {
			i++;
			if(getTextPane().getText().charAt(i) == '(') {
				countNeeded++;
			} else if (getTextPane().getText().charAt(i) == ')') {
				countNeeded--;
			}
		}

		return i;
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

	private String getInfoString() {
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

	public int  getTabSize() {
		return tabSize;
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
}
