package binavigator.ui;

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

	private Color color;

	private Font font;

	private Rectangle lastView;

	/*
	 *  The line color will be calculated automatically by attempting
	 *  to make the current selection lighter by a factor of 1.2.
	 *
	 *  @param component  text component that requires background line painting
	 */
	public ParenthesesPainter(JTextComponent component)
		{

			this(component, null, null);
			setLighter(component.getSelectionColor());
		}

		/*
		 *  Manually control the line color
		 *
		 *  @param component  text component that requires background line painting
		 *  @param color      the color of the background line
		 */
	public ParenthesesPainter(JTextComponent component, Color color, Font font)
		{
			this.component = component;
			setColor( color );
			setFont(font);

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

		public void setFont(Font font) {
			this.font = font;
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

		//  Paint the background highlight

		public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c)
		{
			try
			{
				int rowStart = Utilities.getRowStart(component, c.getCaretPosition());
				int column = (c.getCaretPosition() - rowStart);
				FontMetrics fm = g.getFontMetrics(font);

				System.out.println("Current Column: " + column);

				Rectangle r = c.modelToView(c.getCaretPosition());
				g.setColor(color);
				g.fillRect(0, r.y, c.getWidth(), r.height);

				if (lastView == null)
					lastView = r;
			}
			catch(BadLocationException ble) {System.out.println(ble);}
		}

		/*
		 *   Caret position has changed, remove the highlight
		 */
		private void resetHighlight()
		{
			//  Use invokeLater to make sure updates to the Document are completed,
			//  otherwise Undo processing causes the modelToView method to loop.

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						int offset =  component.getCaretPosition();
						Rectangle currentView = component.modelToView(offset);

						//  Remove the highlighting from the previously highlighted line
						if (lastView.y != currentView.y)
						{
							component.repaint(0, lastView.y, component.getWidth(), lastView.height);
							lastView = currentView;
						}
					}
					catch(BadLocationException ble) {}
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
