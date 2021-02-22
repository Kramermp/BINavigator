package binavigator.ui;

import binavigator.backend.BINavController;

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

public class ParenthesesPainter implements Highlighter.HighlightPainter, CaretListener, MouseListener, MouseMotionListener {
//	public ParenthesesPainter(JTextPane textPane, Color background) {
//
//	}

	private JTextComponent component;

	private BINavController parentController = null;

	private Color color;

	private Rectangle lastView;

	/*
	 *  The line color will be calculated automatically by attempting
	 *  to make the current selection lighter by a factor of 1.2.
	 *
	 *  @param component  text component that requires background line painting
	 */
	public ParenthesesPainter(JTextComponent component)
		{

			this(component, null);
			setLighter(component.getSelectionColor());
		}

		/*
		 *  Manually control the line color
		 *
		 *  @param component  text component that requires background line painting
		 *  @param color      the color of the background line
		 */
	public ParenthesesPainter(JTextComponent component, BINavController parentController)
		{
			this.component = component;
			this.parentController = parentController;
			setColor( parentController.getParenthesePaintColor());

			//  Add listeners so we know when to change highlighting

			component.addCaretListener( this );
			component.addMouseListener( this );
			component.addMouseMotionListener( this );

			//  Turn highlighting on by adding a dummy highlight

			try
			{
				component.getHighlighter().addHighlight(0, 0, this);
			}
			catch(BadLocationException ble) {}
		}

		/*
		 *	You can reset the line color at any time
		 *
		 *  @param color  the color of the background line
		 */
		public void setColor(Color color)
		{
			this.color = color;
		}

		/*
		 *  Calculate the line color by making the selection color lighter
		 *
		 *  @return the color of the background line
		 */
		public void setLighter(Color color)
		{
			int red   = Math.min(255, (int)(color.getRed() * 1.1));
			int green = Math.min(255, (int)(color.getGreen() * 1.1));
			int blue  = Math.min(255, (int)(color.getBlue() * 1.1));
			setColor(new Color(red, green, blue));
		}

	public Rectangle getCurrentView(JTextComponent component) {
		Rectangle rec = null;
		rec = new Rectangle();

		if(!(component.getText().charAt(component.getCaretPosition()) == '(' || (component.getCaretPosition() != 0 && component.getText().charAt(component.getCaretPosition() - 1) == '(') )) {
			return rec;
		}

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

		return rec;
	}


	//  Paint the background highlight

		public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c)
		{
//			try
//			{

			Rectangle rec = rec = getCurrentView(c);
				if(c.getText().charAt(c.getCaretPosition()) == '(' || (c.getCaretPosition() != 0 && c.getText().charAt(c.getCaretPosition() - 1) == '(') ) {
					System.out.println("Sittting on opening parentheses");
					g.setColor(Color.BLUE);

					g.fillRect(rec.x, rec.y, rec.width, rec.height);
					System.out.println(rec.width);
					System.out.println(rec.height);
					System.out.println(rec.y);

				} else if(c.getText().charAt(c.getCaretPosition()) == ')'){
					System.out.println("Sittting on closing parentheses");

				}

				if (lastView == null)
					lastView = rec;
//			}
//			catch(BadLocationException ble) {System.out.println(ble);}
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
					Rectangle currentView = getCurrentView(component);

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

		//  Implement CaretListener

		public void caretUpdate(CaretEvent e)
		{
			resetHighlight();
		}

		//  Implement MouseListener

		public void mousePressed(MouseEvent e)
		{
			resetHighlight();
		}

		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}

		//  Implement MouseMotionListener

		public void mouseDragged(MouseEvent e)
		{
			resetHighlight();
		}

		public void mouseMoved(MouseEvent e) {}

}
