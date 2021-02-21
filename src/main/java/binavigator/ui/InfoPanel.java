package binavigator.ui;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.*;

public class InfoPanel extends JPanel {

	JLabel caretInfo = new JLabel("Col: XXX Char: XXXX");

	public InfoPanel() {
		super();
	}

	public void setCaretInfo(String text) {
		caretInfo.setText(text);
	}


}
