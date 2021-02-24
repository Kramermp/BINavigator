package binavigator.ui.texteditor;

import binavigator.backend.BINavController;
import binavigator.backend.texteditor.TextEditorController;
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
	private BINavController parentController = null;
	private TextEditorController textEditorController;
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

	private InfoPanel infoPanel;

	private Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);


	public TextEditorPanel(BINavController biNavController) throws BadLocationException {
		super();
		this.parentController = biNavController;
		this.setLayout(new BorderLayout());

		doc = this.parentController.getSqlStyledDocument();

		textPane = this.parentController.getNewSqlPane();
//		new LinePainter(textPane, this.getBackground());

		textPane.setText("SELECT testColumn \nFROM sampleTable --Sample Comment \nWHERE Test=\"test\" \nAND " +
				"1 = \"TEST\" \nAND TESTColmn2 IN ( SELECT TestColumn2 \n\tFROM TESTTable2);"
		);

		textPane.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent caretEvent) {
				parentController.caretMoved();
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

	public TextEditorPanel(final TextEditorController textEditorController) throws BadLocationException {
		super();
		this.textEditorController = textEditorController;
		this.setLayout(new BorderLayout());

		doc = this.textEditorController.getSqlStyledDocument();

		textPane = this.textEditorController.getNewSqlPane();
//		new LinePainter(textPane, this.getBackground());

		textPane.setText("Created Correctly;"
		);



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
		if(parentController == null) {
			jScrollPane.setRowHeaderView(new TextLineNumber(getTextPane(), textEditorController.getParentController()));
		} else {
			jScrollPane.setRowHeaderView(new TextLineNumber(getTextPane(), parentController));
		}
		addInfoPanel(new InfoPanel( " "));
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
		this.add(infoPanel, BorderLayout.SOUTH);
	}

	public void addTextLineNumber(TextLineNumber textLineNumber) {
		System.out.println("Adding Text Line Numbers");
		jScrollPane.setRowHeaderView(textLineNumber);
	}

}
