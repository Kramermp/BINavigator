package binavigator.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import static java.lang.System.lineSeparator;

public class NavPanel extends JPanel {
   	JTextArea textArea = new JTextArea();
   	Document document = textArea.getDocument();
	private JPanel lineCounterArea = null;
	LineNumberingTextArea rowHeaders  = null;
	private int lineCount = 1;


	public NavPanel() {
		super();
		this.setLayout(new BorderLayout());
		JScrollPane textAreaPane = new JScrollPane(textArea);
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent documentEvent) {
				System.out.println("InsertUpdate!");
				rowHeaders.updateLineNumbers();
			}

			public void removeUpdate(DocumentEvent documentEvent) {
				System.out.println("RemoveUpdate!");
				rowHeaders.updateLineNumbers();
			}

			public void changedUpdate(DocumentEvent documentEvent) {
				System.out.println("changedUpdate!");
				rowHeaders.updateLineNumbers();
			}


		});
		rowHeaders =  new LineNumberingTextArea(textArea);
		textAreaPane.setRowHeaderView(rowHeaders);
		this.add(textAreaPane);

		textArea.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent focusEvent) {
				System.out.println("Text Area Gained Focus");
			}

			public void focusLost(FocusEvent focusEvent) {
				System.out.println("Text Area Lost Focus");
			}
		});

	}

	public String getTextContent() {
		return textArea.getText();
	}

	public void openNewDocument() {
		textArea.setText("");
	}

	public void appendToDocwLn(String stringToAppend) {
		textArea.append(stringToAppend);
		textArea.append(System.lineSeparator());
	}

	private class LineNumberingTextArea extends JTextArea
	{
		private JTextArea textArea;

		public LineNumberingTextArea(JTextArea textArea){
			this.textArea = textArea;
			setEditable(false);
		}

		public void updateLineNumbers() {
			String lineNumbersText = getLineNumbersText();
			setText(lineNumbersText);
		}

		private String getLineNumbersText()
		{
			StringBuilder stringBuilder = new StringBuilder();
			int lineCount = textArea.getLineCount() + 1;
			for(int i =  1; i < lineCount; i++) {
				stringBuilder.append(i);
				stringBuilder.append(lineSeparator());
			}

			return stringBuilder.toString();
		}
	}

}
