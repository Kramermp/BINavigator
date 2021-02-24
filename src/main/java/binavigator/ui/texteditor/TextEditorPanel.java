package binavigator.ui.texteditor;

import binavigator.backend.BINavController;
import binavigator.backend.texteditor.TextEditorController;
import binavigator.ui.LinePainter;
import binavigator.ui.colortheme.TextColorTheme;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * TextEditor Panel is the panel Containg Text Editor and its deails i.e. Line Numbers
 */
public class TextEditorPanel extends JPanel {
	private TextEditorController controller;
	Log log = LogFactory.getLog(this.getClass());

   	private JTextPane textPane =  null;
	private TextLineNumber rowHeaders  = null;
	private int lineCount = 1;
	private TextColorTheme textColorTheme = null;
	private SqlStyledDocument doc;
	private JScrollPane jScrollPane;

	final StyleContext cont = StyleContext.getDefaultStyleContext();

	private InfoPanel infoPanel;


	private ParenthesesPainter parenthesesPainter;

	public TextEditorPanel(final TextEditorController textEditorController) throws BadLocationException {
		super();
		this.controller = textEditorController;
		this.setLayout(new BorderLayout());

		doc = this.controller.getSqlStyledDocument();

		textPane = this.controller.getNewSqlPane();
		new LinePainter(textPane, this.getBackground());

		textPane.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent caretEvent) {
				textEditorController.caretMoved();
			}
		});

		jScrollPane = new JScrollPane(textPane);
		jScrollPane.setBorder(null);
		add(jScrollPane);

		textPane.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent focusEvent) {
				System.out.println("Text Area Gained Focus");
			}

			public void focusLost(FocusEvent focusEvent) {
				System.out.println("Text Area Lost Focus");
			}
		});

	}

	public void setup() {
		this.setFont(controller.getFont());
		jScrollPane.setRowHeaderView(new TextLineNumber(getTextPane(), controller));

		this.infoPanel = new InfoPanel(" ");
		addInfoPanel(infoPanel);
		textPane.setText("SELECT\nTestTable.TestColumn1,\nTestTable.TestColumn2\nFROM\nTestTable\nWhere\nTestColumn2 = \"test\"");
		infoPanel.setCaretInfo("Col: " + String.format("%03d", 1) + " Char: " + String.format("%4d", 1));

		parenthesesPainter = new ParenthesesPainter(controller.getTextPane(), controller);

		controller.getTextPane().addKeyListener(new KeyListener() {
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

	public void repaintDocument() {
		JViewport viewport = jScrollPane.getViewport();
		Point startPoint = viewport.getViewPosition();
		Dimension size = viewport.getExtentSize();
		Point endPoint =  new Point(startPoint.x + size.width, startPoint.y + size.height);

		int start = textPane.viewToModel(startPoint);
		int end = textPane.viewToModel(endPoint);

		doc.paintDocument(start, end);
	}

	public String getTextContent() {
		return textPane.getText();
	}

	public void openNewDocument() {
		textPane.setText("");
	}

	public void appendToDocwLn(String stringToAppend) {
		try {
			textPane.getDocument().insertString(textPane.getDocument().getLength(), stringToAppend + System.lineSeparator(), textPane.getCharacterAttributes());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	private int findLastNonWordChar (String text, int index) {
		while (--index >= 0) {
			if (String.valueOf(text.charAt(index)).matches("\\W")) {
				break;
			}
		}
		return index;
	}

	private int findFirstNonWordChar (String text, int index) {
		while (index < text.length()) {
			if (String.valueOf(text.charAt(index)).matches("\\W")) {
				break;
			}
			index++;
		}
		return index;
	}

	private int calculateLineCount() {
		return textPane.getText().split("\n").length + 1;
	}

	public void setTextColorTheme(TextColorTheme textColorTheme) {
		log.info("Setting Text Color Theme to " + textColorTheme.getName());
		this.textColorTheme = textColorTheme;
	}

	public TextColorTheme getTextColorTheme() {
		return textColorTheme;
	}

	public Font getDocumentFont() {
		return doc.getCurrentFont();
	}

	public JTextPane getTextPane() {
		return textPane;
	}

	public void addInfoPanel(InfoPanel infoPanel) {
		infoPanel = new InfoPanel( " ");
		this.add(infoPanel, BorderLayout.SOUTH);
	}

	public void addTextLineNumber(TextLineNumber textLineNumber) {
		System.out.println("Adding Text Line Numbers");
		jScrollPane.setRowHeaderView(textLineNumber);
	}

}
