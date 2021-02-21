package binavigator.ui;

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

/**
 * TextEditor Panel is the panel Containg Text Editor and its deails i.e. Line Numbers
 */
public class TextEditorPanel extends JPanel {
	Log log = LogFactory.getLog(this.getClass());

   	private JTextPane textPane =  null;
	private JPanel lineCounterArea = null;
	private TextLineNumber rowHeaders  = null;
	private int lineCount = 1;
	private TextColorTheme textColorTheme = null;
	private SqlStyledDocument doc;

	private JScrollPane jScrollPane;

	private JLabel caretInfo = null;

	final StyleContext cont = StyleContext.getDefaultStyleContext();


	public TextEditorPanel(TextColorTheme textColorTheme) throws BadLocationException {
		super();
		this.textColorTheme = textColorTheme;
		this.setLayout(new BorderLayout());

		doc = new SqlStyledDocument(this);

		textPane = new JTextPane(doc);
		new LinePainter(textPane, this.getBackground());
//		new ParenthesesPainter(textPane, this.getBackground(), doc.getCurrentFont());
		textPane.setText("SELECT testColumn \nFROM sampleTable --Sample Comment \nWHERE Test=\"test\" \nAND " +
				"1 = \"TEST\" \nAND TESTColmn2 IN ( SELECT TestColumn2 \n\tFROM TESTTable2);"
		);

		textPane.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent caretEvent) {
				try {
					caretInfo.setText(getCaretInfoString());
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		});

		jScrollPane = new JScrollPane(textPane);
		add(jScrollPane);

		rowHeaders =  new TextLineNumber(textPane, this.textColorTheme);
		jScrollPane.setRowHeaderView(rowHeaders);

		textPane.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent focusEvent) {
				System.out.println("Text Area Gained Focus");
			}

			public void focusLost(FocusEvent focusEvent) {
				System.out.println("Text Area Lost Focus");
			}
		});


		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(this.getBackground().darker());

		caretInfo = new JLabel(getCaretInfoString());
		bottomPanel.add(caretInfo);

		this.add(bottomPanel, BorderLayout.SOUTH);

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

	private int getCaretColumn() throws BadLocationException {
		return textPane.getCaretPosition() - Utilities.getRowStart(textPane, textPane.getCaretPosition());
	}

	private String getCaretInfoString() throws BadLocationException {
		return "Column " + String.format("%03d", getCaretColumn()) + " Char: " + String.format("%04d", textPane.getCaretPosition()) ;
	}

	public JTextPane getTextPane() {
		return textPane;
	}

	//	private class LineNumberingTextArea extends JTextArea
//	{
//		private JTextPane jTextPane;
//
//		public LineNumberingTextArea(JTextPane jTextPane){
//			this.jTextPane = jTextPane;
//			setEditable(false);
//		}
//
//		public void updateLineNumbers() {
//			System.out.println("Updating Line Numbers");
//			String lineNumbersText = getLineNumbersText();
//			setText(lineNumbersText);
//		}
//
//		private String getLineNumbersText()
//		{
//			StringBuilder stringBuilder = new StringBuilder();
//			int lineCount = calculateLineCount();
//
//			for(int i =  1; i < lineCount; i++) {
//				stringBuilder.append(i);
//				stringBuilder.append(lineSeparator());
//			}
//
//			return stringBuilder.toString();
//		}
//
//	}

}
