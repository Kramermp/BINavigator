package binavigator.ui.texteditor;

import binavigator.backend.BINavController;
import binavigator.backend.texteditor.TextEditorController;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class ParenthesesPainter implements Highlighter.HighlightPainter {

	private JTextComponent component;

	private TextEditorController parentController = null;

	private int alpha = 100;
	private Color color = Color.YELLOW;
	private Color hilightColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);

	private Rectangle[] lastShape = new Rectangle[]{};

	private Rectangle[] currentShapes = new Rectangle[]{};

	/*
	 *  The line color will be calculated automatically by attempting
	 *  to make the current selection lighter by a factor of 1.2.
	 *
	 *  @param component  text component that requires background line painting
	 */
	public ParenthesesPainter(JTextComponent component)
		{

			this(component, null);
		}

		/*
		 *  Manually control the line color
		 *
		 *  @param component  text component that requires background line painting
		 *  @param color      the color of the background line
		 */

	public ParenthesesPainter(JTextComponent component, TextEditorController parentController) {
		this.component = component;
		this.parentController = parentController;

		//  Add listeners so we know when to change highlighting

		//  Turn highlighting on by adding a dummy highlight

		try
		{
			component.getHighlighter().addHighlight(0, 0, this);
		}
		catch(BadLocationException ble) {}
	}


	public void updateCharacterShape(JTextComponent component, int beginIndex, int endIndex) throws BadLocationException {
		lastShape = currentShapes;
		if(beginIndex  == endIndex || beginIndex < 0 || endIndex < 0) {
			currentShapes = new Rectangle[] {new Rectangle()};
		} else {
			String[] lines = component.getText().substring(beginIndex, endIndex).split("\n", - 1);
			System.out.println("Get from character " + beginIndex + " - " + endIndex);

			switch (lines.length) {
				case 0:
					currentShapes = new Rectangle[] {};
					return;
				case 1:
					currentShapes =  new Rectangle[]{getSingleLineShape(component, beginIndex, endIndex)};
					return;
				default:
					currentShapes = getMultiLineShape(component, beginIndex, endIndex, lines.length);
			}
		}
	}

	private Rectangle getSingleLineShape(JTextComponent component, int beginIndex, int endIndex) {
		System.out.println("Painting Single Line");
		Rectangle rec = null;

		try {
			FontMetrics fm = getFontMetric(component);
			rec = new Rectangle();

			//X Position = Margin + size of SubString from line start to Begin
			rec.x = component.getMargin().left + fm.stringWidth(component.getText().substring(Utilities.getRowStart(component, beginIndex), beginIndex));
			rec.width = fm.stringWidth(component.getText().substring(beginIndex, endIndex + 1));

			rec.y = component.getMargin().top + (component.getText().substring(0, beginIndex).split("\n", -1).length - 1 )* fm.getHeight();
			rec.height = fm.getHeight();

		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		return rec;
	}

	private Rectangle[] getMultiLineShape (JTextComponent component, int beginIndex, int endIndex, int lineCount) throws BadLocationException {
		Rectangle[] recs = new Rectangle[lineCount];
		FontMetrics fm = getFontMetric(component);

		if(lineCount == 0) {
			return new Rectangle[]{};
		} else if (lineCount == 1) {
			return new Rectangle[] {getSingleLineShape(component, beginIndex, endIndex)};
		} else {
			System.out.println("Lines: " + lineCount);
			String[] lines = (component.getText().substring(0, beginIndex).split("\n", -1));
			int lineOffset = lines.length - 1 ;

			// First And Last Line have to Be handled specially
			recs[0] = new Rectangle();
			String firstLine = component.getText().substring(Utilities.getRowStart(component, beginIndex), beginIndex);
			int firstLineCharCount = 0;
			if(firstLine.contains("\t")) {
				firstLineCharCount = charCountWithTabs(firstLine);
			} else {
				firstLineCharCount = firstLine.length();
			}
			recs[0].x = component.getMargin().left + (fm.stringWidth(" ") * firstLineCharCount);
			recs[0].width = component.getWidth();

			recs[0].y = component.getMargin().top + (lineOffset * fm.getHeight());
			recs[0].height = fm.getHeight();

			for(int i = 1; i < recs.length - 1; i++) {
				recs[i] = new Rectangle();
				recs[i].x = component.getMargin().left;
				recs[i].width = component.getWidth();

				recs[i].y = component.getMargin().top + ((lineOffset + i) * fm.getHeight());
				recs[i].height = fm.getHeight();
			}

			recs[recs.length - 1] = new Rectangle();

			String lastLine = component.getText().substring(Utilities.getRowStart(component, endIndex), endIndex + 1 );
			recs[recs.length - 1].x = component.getMargin().left;

			int charCount = 0;
			if(lastLine.contains("\t")) {
				charCount = charCountWithTabs(lastLine);
			} else {
				charCount=lastLine.length();
			}

			//For the life of me cannot figure out why it needs two margins
			recs[recs.length - 1].width = fm.stringWidth(" " ) * charCount;

			recs[recs.length - 1].y = component.getMargin().top + ((lineOffset + recs.length - 1) * fm.getHeight());
			recs[recs.length - 1].height = fm.getHeight();
		}

		return recs;
	}

	public int charCountWithTabs(String lastLine) {
		int charPosition = 0;
		for(int i = 0; i < lastLine.length(); i++) {
			if(lastLine.charAt(i) == '\t') {
				//Tabsize
				charPosition = charPosition + (4-(charPosition%4));
//				System.out.println("TabSize: " + ( 4 - (i%4)));
			} else {
				charPosition++;
			}
		}

		return charPosition;
	}

	private FontMetrics getFontMetric(JTextComponent textComponent) {
		return  textComponent.getGraphics().getFontMetrics(parentController.getFont());
	}


	//  Paint the background highlight

		public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c)
		{
			Rectangle rec = null;

			g.setColor(hilightColor);
			for(int i = 0; i < this.currentShapes.length; i++) {
				if(currentShapes[i] != null)
					g.fillRect(currentShapes[i].x, currentShapes[i].y, currentShapes[i].width, currentShapes[i].height);
			}

		}

		/*
		 *   Caret position has changed, remove the highlight
		 */
		public void resetHighlight(final int startIndex, final int endIndex)
		{
			//  Use invokeLater to make sure updates to the Document are completed,
			//  otherwise Undo processing causes the modelToView method to loop.

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					System.out.println("Reseting Highlight");


					try {
						updateCharacterShape(component, startIndex, endIndex);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}

					for(int i = 0; i < currentShapes.length; i++) {
					if(currentShapes[i] != null) {
						component.paintImmediately(currentShapes[i]);
					} else {
						System.out.println(i + " was null");
					}

				}
					for(int i = 0; i < lastShape.length; i++) {
						component.paintImmediately(lastShape[i]);
					}

					lastShape = currentShapes;

//					Rectangle currentView = null;
//					try {
//						currentView = getCurrentView(component);
//					} catch (BadLocationException e) {
//						e.printStackTrace();
//					}
//
//					//  Remove the highlighting from the previously highlighted line
//					if (!lastView.equals(currentView)) {
//						System.out.println("Repainting");
//
//						System.out.println(currentView.width);
//						component.validate();
//						component.paintImmediately(lastView);
//						component.paintImmediately(currentView);
//						component.validate();
//
//						lastView = currentView;
//					}
				}
			});
		}

		private boolean checkForOpenParentheses(JTextComponent c) {
			int caretPosition = c.getCaretPosition();
			if(caretPosition == 0) {
				caretPosition = 1;
			}

			//If is in the array we check it
			if(!(caretPosition >= c.getText().length() || caretPosition < 0))  {
				if(c.getText().charAt(caretPosition) == '(') {
					return true;
				}
			}

			if(!(caretPosition - 1 < 0 || caretPosition - 1 >= c.getText().length())) {
				if(c.getText().charAt(caretPosition - 1) == '(') {
					return true;
				}
			}

			return false;

		}

		private boolean checkForCloseParenthese(JTextComponent c) {
			int caretPosition = c.getCaretPosition();
			if(caretPosition == 0) {
				caretPosition = 1;
			}

			//If is in the array we check it
			if(!(caretPosition >= c.getText().length() || caretPosition < 0))  {
				if(c.getText().charAt(caretPosition) == ')') {
					return true;
				}
			}

			if(!(caretPosition - 1 < 0 || caretPosition - 1 >= c.getText().length())) {
				if(c.getText().charAt(caretPosition - 1) == ')') {
					return true;
				}
			}

			return false;
		}

		public void setAlpha(int alpha) {
			this.alpha = alpha;
			hilightColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
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
