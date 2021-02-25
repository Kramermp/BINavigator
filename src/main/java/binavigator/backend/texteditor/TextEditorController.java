package binavigator.backend.texteditor;

import binavigator.backend.BINavController;
import binavigator.ui.LinePainter;
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
	private CharacterCountPainter ccp;

	//Font Settings
	private TextColorTheme textColorTheme;
	private Font font = new Font(Font.MONOSPACED, Font.PLAIN, 25);

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
			setFont(font);
			textEditorPanel.setup();
			configurePanel();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void configurePanel() {

		configureCharacterCountPainter();
		configureParenthesesPainter();
		configureTextLineNumbers();
		configureInfoPanel();
//		linePainter = new LinePainter(textEditorPanel.getTextPane(), textEditorPanel.getBackground());

		TextEditorListener listener = new TextEditorListener(this);
		getTextPane().addKeyListener(listener);
		getTextPane().addFocusListener(listener);
		getTextPane().addCaretListener(listener);


	}

	private void configureParenthesesPainter() {
		if(parenthesesPainterEnable == true) {
			parenthesesPainter = new ParenthesesPainter(getTextPane(), this);
			parenthesesPainter.setColor(textColorTheme.getParenthesesHiLightColor());
			parenthesesPainter.setAlpha(150);
		} else {
			parenthesesPainter = null;
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

	private void configureInfoPanel() {
		if(infoPanelEnabled == true) {
			infoPanel = new InfoPanel(getInfoString());
			textEditorPanel.add(infoPanel, BorderLayout.SOUTH);
		} else {
			infoPanel = null;
		}
	}

	private void configureCharacterCountPainter() {
		ccp = new CharacterCountPainter(this);
	}

	public void initializeUI() {
//		infoPanel.setCaretInfo(getInfoString());

		try {
			resetHighlight();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		ccp.drawLine();
	}

	public void drawComponents() {
	}

	public void caretMoved() {
		infoPanel.setCaretInfo(getInfoString());
		ccp.drawLine();
		try {
			resetHighlight();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void resetHighlight() throws BadLocationException {
		int openIndex = checkForOpenParentheses(getTextPane().getCaretPosition());
		int closeIndex = checkForCloseParentheses(getTextPane().getCaretPosition());


		if (openIndex >=0 && closeIndex >= 0 && openIndex < closeIndex) {
			System.out.println("On open and close");
		}else if (openIndex < 0 && closeIndex < 0){
			System.out.println("Not on a Parentheses");
		} else if (openIndex >= 0 && openIndex > closeIndex){
			System.out.println("On an Open Parentheses");
			closeIndex = searchForCloseParentheses(openIndex);
		} else if (closeIndex >= 0 && closeIndex > openIndex) {
			System.out.println("On a Close Parentheses");
			openIndex = searchForOpenParentheses(closeIndex);
		}

		System.out.println("Open: " + openIndex + " Close: " + closeIndex);

		parenthesesPainter.resetHighlight(openIndex, closeIndex);
	}

	public void findParentheses() {
		int openIndex  = searchForOpenParentheses(textEditorPanel.getTextPane().getCaretPosition());
		int closeIndex = searchForCloseParentheses(textEditorPanel.getTextPane().getCaretPosition());

		parenthesesPainter.resetHighlight(openIndex, closeIndex);
	}

	private int checkForOpenParentheses(int startPostion) {
		int textLength = getTextPane().getText().length();
		if (startPostion >= textLength || startPostion < 0) {
			// Do Nothing
		} else if (getTextPane().getText().charAt(startPostion) == '(') {
			return startPostion;
		}

		if (startPostion - 1 >= textLength || startPostion - 1 < 0) {
			return -1;
		} else if (getTextPane().getText().charAt(startPostion - 1) == '(') {
			return (startPostion - 1);
		}

		return -1;
	}

	private int checkForCloseParentheses(int startPostion) {
		int textLength = getTextPane().getText().length();
		if (startPostion >= textLength || startPostion < 0) {
			// Do Nothing;
		} else if (getTextPane().getText().charAt(startPostion) == ')') {
			return startPostion;
		}

		if (startPostion - 1 >= textLength || startPostion - 1 < 0) {
			return -1;
		} else if (getTextPane().getText().charAt(startPostion - 1) == ')') {
			return (startPostion - 1);
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
//		if(this.textLineNumber != null) {
//			this.textLineNumber.setFont(font);
//		}
//		if(this.infoPanel != null) {
//			this.infoPanel.setFont(font);
//		}
	}
}
