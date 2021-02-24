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
	//Log
	private Log log = LogFactory.getLog(this.getClass());

	//Controller
	private TextEditorController controller = null;

	//UI Components
   	private JTextPane textPane =  null;
	private SqlStyledDocument doc = null;
	private JScrollPane jScrollPane = null;
	private JPanel infoPanel = null;

	//Unused for now
	private int lineCount = 1;

	public TextEditorPanel(final TextEditorController textEditorController) throws BadLocationException {
		super();
		this.controller = textEditorController;
		this.setLayout(new BorderLayout());

		doc = this.controller.getSqlStyledDocument();

		textPane = this.controller.getNewSqlPane();

		jScrollPane = new JScrollPane(textPane);
		jScrollPane.setBorder(null);
		add(jScrollPane);
	}

	public void setup() {
		this.setFont(controller.getFont());
		this.textPane.setText("SELECT\nTestTable.TestColumn1,\nTestTable.TestColumn2\nFROM\nTestTable\nWhere\nTestColumn2 = \"test\"");
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

	//Getters
	public JScrollPane getjScrollPane() {
		return jScrollPane;
	}

	public JTextPane getTextPane() {
		return textPane;
	}

	public String getTextContent() {
		return textPane.getText();
	}

	//Setters
	@Override
	public void setFont(Font font){
		super.setFont(font);
		if (textPane != null) {
			this.textPane.setFont(font);
		}
	}

}
