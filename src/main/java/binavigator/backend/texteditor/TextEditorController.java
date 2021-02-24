package binavigator.backend.texteditor;

import binavigator.backend.BINavController;
import binavigator.ui.texteditor.SqlStyledDocument;
import binavigator.ui.texteditor.TextEditorPanel;
import binavigator.ui.texteditor.TextLineNumber;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;

public class TextEditorController {
	private BINavController parentController;

	private TextEditorPanel textEditorPanel;

	private boolean displayLineNumbers = true;

	public TextEditorController(BINavController biNavController) {
		this.parentController = biNavController;

		try {
			textEditorPanel = new TextEditorPanel(this);
			textEditorPanel.setup();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}


	public Component getTextEditorPanel() {

		return textEditorPanel;
	}

	public SqlStyledDocument getSqlStyledDocument() {
		return parentController.getSqlStyledDocument();
	}

	public JTextPane getNewSqlPane() {
		return parentController.getNewSqlPane();
	}

	public void caretMoved() {
		parentController.caretMoved();
	}

	public BINavController getParentController() {
		return parentController;
	}
}
