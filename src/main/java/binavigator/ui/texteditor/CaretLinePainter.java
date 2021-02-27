package binavigator.ui.texteditor;

import binavigator.backend.texteditor.TextEditorController;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/*
 *  Track the movement of the Caret by painting a background line at the
 *  current caret position.
 */
public class CaretLinePainter implements Highlighter.HighlightPainter {

	private JTextComponent component;

	private Color color;

	private Rectangle lastView;

	private Rectangle currentView;

	/*
	 *  The line color will be calculated automatically by attempting
	 *  to make the current selection lighter by a factor of 1.2.
	 *
	 *  @param component  text component that requires background line painting
	 */
	public CaretLinePainter(TextEditorController textEditorController) {
		this(textEditorController.getTextPane(), null);
		setLighter(component.getSelectionColor());
	}

	/*
	 *  Manually control the line color
	 *
	 *  @param component  text component that requires background line painting
	 *  @param color      the color of the background line
	 */
	public CaretLinePainter(JTextComponent component, Color color) {
		this.component = component;
		setColor( color );

		//  Add listeners so we know when to change highlighting

		//  Turn highlighting on by adding a dummy highlight

//		try {
//			component.getHighlighter().addHighlight(0, 0, this);
//		}
//		catch(BadLocationException ble) {}
	}

	/*
	 *	You can reset the line color at any time
	 *
	 *  @param color  the color of the background line
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/*
	 *  Calculate the line color by making the selection color lighter
	 *
	 *  @return the color of the background line
	 */
	public void setLighter(Color color) {
		int red   = Math.min(255, (int)(color.getRed() * 1.1));
		int green = Math.min(255, (int)(color.getGreen() * 1.1));
		int blue  = Math.min(255, (int)(color.getBlue() * 1.1));
		setColor(new Color(red, green, blue));
	}

	//  Paint the background highlight

	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {

	}

	public void refreshActiveLine() {
		try {
			int offset =  component.getCaretPosition();
			Rectangle currentView = component.modelToView(offset);

			//  Remove the highlighting from the previously highlighted line

			System.out.println(currentView.width);
			if (lastView.y != currentView.y) {
				component.repaint(0, lastView.y, component.getWidth(), lastView.height);
				component.repaint(0, currentView.y, component.getWidth(), currentView.height);
				lastView = currentView;
			}
		}
		catch(BadLocationException ble) {}
	}

	public void fillActiveLineShape(Graphics g, Rectangle[] characterShape) {
		try {
			currentView = component.modelToView(component.getCaretPosition());
			currentView.x = component.getMargin().left;
			currentView.width = component.getWidth();

			g.setColor( color );

			Area area = new Area(currentView);
			for(int i = 0; i < characterShape.length; i++) {
				area.subtract(new Area(characterShape[i]));
			}

			currentView = area.getBounds();
			g.fillRect(currentView.x, currentView.y, currentView.width, currentView.height);

			if (lastView == null)
				lastView = currentView;
		}
		catch(BadLocationException ble) {System.out.println(ble);}
	}

}
