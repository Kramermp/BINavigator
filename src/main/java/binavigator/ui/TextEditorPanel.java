package binavigator.ui;

import binavigator.ui.colortheme.Monokai;
import binavigator.ui.colortheme.TextColorTheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import static java.lang.System.lineSeparator;

public class TextEditorPanel extends JPanel {
   	private JTextPane textPane =  null;
	private JPanel lineCounterArea = null;
	private TextLineNumber rowHeaders  = null;
	private int lineCount = 1;
	private TextColorTheme textColorTheme = null;
	private SqlStyledDocument doc;

	private JScrollPane jScrollPane;

	final StyleContext cont = StyleContext.getDefaultStyleContext();
//	final AttributeSet keywordAttr; = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, textColorTheme.getKeyWordColor());
//	final AttributeSet defaultAttr = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, textColorTheme.getTextColor());

	public TextEditorPanel(TextColorTheme textColorTheme) {
		super();
		this.textColorTheme = textColorTheme;
		this.setLayout(new BorderLayout());

		doc = new SqlStyledDocument(textColorTheme, this);

		textPane = new JTextPane(doc);
		new LinePainter(textPane, this.getBackground());
		textPane.setText("SELECT testColumn \n FROM sampleTable --Sample Comment \n WHERE Test=\"test\"");
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

	public void setTextColorThem(TextColorTheme textColorTheme) {
		this.textColorTheme = textColorTheme;
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
