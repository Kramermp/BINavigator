package binavigator.ui.texteditor;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.*;

public class InfoPanel extends JPanel {

	private JLabel caretInfo;

	public InfoPanel(String string) {
		super();
		this.caretInfo = new JLabel(string);
		this.setLayout(new BorderLayout());
		this.add(caretInfo, BorderLayout.WEST);
	}

	public void setCaretInfo(String text) {
		caretInfo.setText(text);
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if(caretInfo != null){
			caretInfo.setFont(font);
		}
	}


}
