package binavigator.ui.texteditor;

import binavigator.backend.texteditor.TextEditorController;

import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class CharacterCountPainter implements Highlighter.HighlightPainter {
	private TextEditorController controller;
	private int height  = 0;

	private Rectangle line = new Rectangle();

	private Color characterLineColor = Color.RED;

	public CharacterCountPainter(TextEditorController controller) {
		this.controller = controller;


//		try {
//			controller.getTextPane().getHighlighter().addHighlight(0, 0, this);
//		} catch (BadLocationException e) {
//			e.printStackTrace();
//		}

	}


	@Override
	public void paint(Graphics g, int i, int i1, Shape shape, JTextComponent jTextComponent) {
		g.setColor(characterLineColor);

		g.fillRect(line.x, line.y, line.width, line.height);
	}

	public void refreshCharacterCountLine() {
		controller.getTextPane().paintImmediately(line);
	}

	public void fillCharacterCountLineShape(Graphics g) {
		//Paint Character Line
		//Update Character Count line
		FontMetrics fm = controller.getTextPane()
				.getGraphics()
				.getFontMetrics(
						controller.getFont());
		int characterWidth = fm.stringWidth(" ");
		int maxCharacterWidth = characterWidth * controller.getCharacterCountLimit();

		line.x = maxCharacterWidth;
		line.width = 3;
		line.y = 0;
		line.height = controller.getTextPane().getHeight();
		g.setColor(characterLineColor);
		g.fillRect(line.x, line.y, line.width, line.height);
	}

	public void repaint() {
		controller.getTextPane().repaint(line);
	}
}
