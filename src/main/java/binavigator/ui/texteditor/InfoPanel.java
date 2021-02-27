package binavigator.ui.texteditor;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.*;

public class InfoPanel extends JPanel {

	private JLabel caretInfo;

	public static final String DEFAULT_STRING = "";

	public InfoPanel() {
		super();
		this.caretInfo = new JLabel(" ");
		this.setCaretInfo(0, 0);
		this.setLayout(new BorderLayout());
		this.add(caretInfo, BorderLayout.WEST);
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if(caretInfo != null){
			caretInfo.setFont(font);
		}
	}


	public void setCaretInfo(int columnPosition, int characterPostion) {
		System.out.println(columnPosition+ " " + characterPostion);
		caretInfo.setText("Col: " + String.format("%03d", columnPosition)  + " Char: " + String.format("%4d", characterPostion));
	}
}
