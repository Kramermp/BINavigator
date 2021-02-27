package binavigator.ui.texteditor;

import binavigator.backend.texteditor.TextEditorController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

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

	//States
	private int tabSize = 4;

	//Unused for now
	private int lineCount = 1;

	public TextEditorPanel(final TextEditorController textEditorController) throws BadLocationException {
		super();
		System.setProperty("awt.useSystemAAFontSettings","off");
		System.setProperty("swing.aatext", "false");
		this.controller = textEditorController;
		this.setLayout(new BorderLayout());

		doc = this.controller.getSqlStyledDocument();

		textPane = this.controller.getNewSqlPane();

		jScrollPane = new JScrollPane(textPane);
		jScrollPane.setBorder(null);
		add(jScrollPane);
	}

	public void setup() {
		buildTabs();
		textPane.setText("SELECT\nTestTable.TestColumn1,\nTestTable.TestColumn2\nFROM\nTestTable\nWhere\nTestColumn2 = \"test\"");
	}


	private void buildTabs(){
		StyleContext sc = StyleContext.getDefaultStyleContext();
		TabStop[] test = new TabStop[50];
		int spaceWidth  = getFontMetrics((getFont())).stringWidth(" ");
		int charWidth  = getFontMetrics(getFont()).stringWidth("T");
		SqlStyledDocument doc = (SqlStyledDocument) textPane.getDocument();

		for(int i = 0; i < 50; i++) {
			System.out.println("Space" + spaceWidth);
			System.out.println("Char " + charWidth);
			test[i] = new TabStop((spaceWidth * (i) * tabSize ) ) ;
			System.out.println(test[i].getPosition());
		}
		TabSet tabs = new TabSet( test );
		AttributeSet paraSet = sc.addAttribute(sc.getEmptySet(), StyleConstants.TabSet, tabs);

		doc.setParagraphAttributes(0, textPane.getText().length(), paraSet, false);
	}

	public void highlightText() {
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

	public void setTabSize(int tabSize) {
		this.tabSize = tabSize;
		buildTabs();
	}

}
