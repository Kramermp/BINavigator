package binavigator.ui.texteditor;

import binavigator.backend.BINavController;
import binavigator.backend.texteditor.TextEditorController;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class ParenthesesPainter implements Highlighter.HighlightPainter {

	private JTextComponent component;

	private TextEditorController parentController = null;

	private Rectangle lastView;

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
//	public ParenthesesPainter(JTextComponent component, BINavController parentController) {
//		this.component = component;
//		this.parentController = parentController;
//
//		//  Add listeners so we know when to change highlighting
//
//		component.addCaretListener( this );
//		component.addMouseListener( this );
//		component.addMouseMotionListener( this );
//
//		//  Turn highlighting on by adding a dummy highlight
//
//		try
//		{
//			component.getHighlighter().addHighlight(0, 0, this);
//		}
//		catch(BadLocationException ble) {}
//	}

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

	public Rectangle getCurrentView(JTextComponent component) throws BadLocationException {
		Rectangle rec = null;
		rec = new Rectangle();

		if(checkForOpenParentheses(component) ) {
			rec.width = component.getWidth();
			rec.x = component.getMargin().left;
			FontMetrics fm = component.getGraphics().getFontMetrics(parentController.getFont());
			rec.y  = (fm.getHeight() * (component.getText().substring(0, component.getCaretPosition()).split(("\n")).length - 1)) + component.getMargin().top  ;
			rec.height = fm.getHeight();

			int countNeeded = 1;
			int i = 0;
			for(i = component.getCaretPosition() + 1; i < component.getText().length() && countNeeded > 0; i++) {
				if(component.getText().charAt(i) == ')') {
					countNeeded--;
				} else if (component.getText().charAt(i) == '(') {
					countNeeded++;
				}
			}

			int lineCount = component.getText().substring(component.getCaretPosition(), i).split("\n", - 1).length;
			rec.height = rec.height * lineCount;
		} else  if (checkForCloseParenthese(component)){

			rec.width = component.getWidth();

			FontMetrics fm = component.getGraphics().getFontMetrics(parentController.getFont());
			String leadingString = component.getText().substring(Utilities.getRowStart(component, component.getCaretPosition()), component.getCaretPosition());
			rec.x = component.getMargin().left + fm.stringWidth(leadingString);
			rec.y  = (fm.getHeight() * (component.getText().substring(0, component.getCaretPosition()).split(("\n")).length)) + component.getMargin().top  ;
			rec.height = fm.getHeight();

			int countNeeded = 1;
			int i = component.getCaretPosition();

			//Need because of the backwards search in check
			if (i != component.getText().length() && component.getText().charAt(i) == ')') {
				i = component.getCaretPosition() - 1;
			} else {//if (component.getText().charAt(i - 1) == ')')

				i = component.getCaretPosition() - 2;
			}

			// IDE complain so self assigning
			for(i = i; i > 0  && countNeeded > 0; i--) {
				if(component.getText().charAt(i) == '(') {
					countNeeded--;
				} else if (component.getText().charAt(i) == ')') {
					countNeeded++;
				}
			}

			int lineCount = component.getText().substring(i, component.getCaretPosition()).split("\n", - 1).length;

			rec.y-=(rec.height * lineCount);
			rec.height =(rec.height * lineCount); //Drawing from bottom up
		} else {
			rec = new Rectangle(0,0,0,0);
		}

		return rec;
	}


	public void updateCharacterShape(JTextComponent component, int beginIndex, int endIndex) throws BadLocationException {
		lastShape = currentShapes;
		System.out.println("Get from character " + beginIndex + " - " + endIndex);
		if(beginIndex  == endIndex) {
//			return new Polygon();
		}

		String[] lines = component.getText().substring(beginIndex, endIndex).split("\n", - 1);

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

	private Rectangle getSingleLineShape(JTextComponent component, int beginIndex, int endIndex) {
		System.out.println("Painting Single Line");
		Rectangle rec = null;

		try {
			FontMetrics fm = getFontMetric(component);
			rec = new Rectangle();

			//X Position = Margin + size of SubString from line start to Begin
			rec.x = component.getMargin().left + fm.stringWidth(component.getText().substring(Utilities.getRowStart(component, beginIndex), beginIndex));
			rec.width = fm.stringWidth(component.getText().substring(beginIndex, endIndex));

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
			int lineOffset = (component.getText().substring(0, beginIndex).split("\n", -1).length - 1 );

			// First And Last Line have to Be handled specially
			recs[0] = new Rectangle();
			recs[0].x = component.getMargin().left + fm.stringWidth(component.getText().substring(Utilities.getRowStart(component, beginIndex), beginIndex));
			recs[0].width = component.getWidth();

			recs[0].y = component.getMargin().top + lineOffset * fm.getHeight();
			recs[0].height = fm.getHeight();

			for(int i = 1; i < recs.length - 2; i++) {
				recs[i].x = component.getMargin().left;
				recs[i].width = component.getWidth();

				recs[i].y = component.getMargin().top + ((lineOffset + i) * fm.getHeight());
				recs[i].height = fm.getHeight();
			}

			recs[recs.length - 1] = new Rectangle();

			recs[recs.length - 1].x = component.getMargin().left;
			recs[recs.length - 1].width = component.getMargin().left + fm.stringWidth(component.getText().substring(Utilities.getRowStart(component, beginIndex), beginIndex));

			recs[recs.length - 1].y = component.getMargin().top + ((lineOffset + recs.length - 1) * fm.getHeight());
			recs[recs.length - 1].height = fm.getHeight();
		}

		return recs;
	}

	private FontMetrics getFontMetric(JTextComponent textComponent) {
		return  textComponent.getGraphics().getFontMetrics(component.getFont());
	}


	//  Paint the background highlight

		public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c)
		{
			Rectangle rec = null;

			g.setColor(Color.GREEN);
			for(int i = 0; i < this.currentShapes.length; i++) {
				g.fillRect(currentShapes[i].x, currentShapes[i].y, currentShapes[i].width, currentShapes[i].height);
			}

			try {
				rec = getCurrentView(c);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

			if(checkForOpenParentheses(c)) {
					System.out.println("Sittting on opening parentheses");
					g.setColor(parentController.getParenthesePaintColor());

					g.fillRect(rec.x, rec.y, rec.width, rec.height);
					System.out.println(rec.width);
					System.out.println(rec.height);
					System.out.println(rec.y);

				} else if(checkForCloseParenthese(c)){
					System.out.println("Sittting on closing parentheses");
					g.setColor(parentController.getParenthesePaintColor());

					g.fillRect(rec.x, rec.y, rec.width, rec.height);
					System.out.println(rec.width);
					System.out.println(rec.height);
					System.out.println(rec.y);
				}

				if (lastView == null)
					lastView = rec;
		}

		/*
		 *   Caret position has changed, remove the highlight
		 */
		public void resetHighlight()
		{
			//  Use invokeLater to make sure updates to the Document are completed,
			//  otherwise Undo processing causes the modelToView method to loop.

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					System.out.println("Reseting Highlight");


					try {
						updateCharacterShape(component, 10, 20);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					lastShape = new Rectangle[]{new Rectangle()};
					if(lastShape.length == 0 || currentShapes.length == 0 || lastShape[0].x !=  currentShapes[0].x || currentShapes[0].y != lastShape[0].y) {
						for(int i = 0; i < currentShapes.length; i++) {
							component.paintImmediately(currentShapes[i]);
						}
						for(int i = 0; i < lastShape.length; i++) {
							component.paintImmediately(lastShape[i]);
						}
					}

					Rectangle currentView = null;
					try {
						currentView = getCurrentView(component);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}

					//  Remove the highlighting from the previously highlighted line
					if (!lastView.equals(currentView)) {
						System.out.println("Repainting");

						System.out.println(currentView.width);
						component.validate();
						component.paintImmediately(lastView);
						component.paintImmediately(currentView);
						component.validate();

						lastView = currentView;
					}
				}
			});
		}

		public void getOpenIndex(JTextComponent component) {

		}

		public void getCloseIndex(JTextComponent component) {

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

}
