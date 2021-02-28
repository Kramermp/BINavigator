package binavigator.ui.texteditor;

import binavigator.backend.texteditor.TextEditorController;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;
import java.awt.*;

public class TextEditorPainter extends JComponent implements Highlighter.HighlightPainter {
	private TextEditorController controller;
	private ParenthesesPainter pp;
	private CharacterCountPainter ccp;
	private JTextComponent component;
	private CaretLinePainter clp;

	private int alpha = 100;
	private Color color = Color.YELLOW;
	private Color activeLineColor = Color.BLUE;
	private Color hilightColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	private Color characterLineColor  = Color.RED;

	private Rectangle[] lastShape = new Rectangle[]{};

	private Rectangle[] currentShapes = new Rectangle[]{};

	private int lineCount = 80;
	private int height  = 0;

	private Rectangle line = new Rectangle();

	private Rectangle lastView;

	public TextEditorPainter(TextEditorController textEditorController, JTextComponent component) {
		this.component = component;
		this.controller = textEditorController;
		this.activeLineColor = component.getBackground().brighter();
		this.pp = new ParenthesesPainter(textEditorController.getTextPane(), textEditorController);
		this.ccp = new CharacterCountPainter(textEditorController);
		this.clp = new CaretLinePainter(textEditorController);

		try
		{
			component.getHighlighter().addHighlight(0, 0, this);
		}
		catch(BadLocationException ble) {}
	}

	private FontMetrics getFontMetric(JTextComponent textComponent) {
		return  textComponent.getGraphics().getFontMetrics(controller.getFont());
	}


	//  Paint the background highlight

	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c)
	{
		clp.fillActiveLineShape(g, pp.getCharacterShape());
		pp.fillParenthesesShape(g);

		if(controller.getCharacterCountLineEnabled()) {
			ccp.fillCharacterCountLineShape(g);
		}
	}

	/*
	 *   Caret position has changed, remove the highlight
	 */
	public void resetHighlight(final int startIndex, final int endIndex)
	{
		//  Use invokeLater to make sure updates to the Document are completed,
		//  otherwise Undo processing causes the modelToView method to loop.

		Thread test  = new Thread(){
			public void run() {

				//Painting Parentheses
				try {
					pp.updateShape(startIndex, endIndex);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}

				clp.refreshActiveLine();
				pp.refreshParenthesesShape();


				if(controller.getCharacterCountLineEnabled()) {
					ccp.refreshCharacterCountLine();
				} else {
					ccp.repaint();
				}
			}
		};

		test.run();

	}

	public void setColor(Color color) {
		this.color = color;
		if(alpha == 0){
			this.hilightColor = hilightColor;
		} else {
			this.hilightColor = new Color(color.getRed(), color.getBlue(), color.getGreen(), alpha);
		}
	}

}
