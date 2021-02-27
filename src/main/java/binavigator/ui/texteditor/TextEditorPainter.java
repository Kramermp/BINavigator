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
	private CharacterCountPainter cpp;
	private JTextComponent component;

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
		this.pp = new ParenthesesPainter(textEditorController.getTextPane());

		//  Add listeners so we know when to change highlighting

		//  Turn highlighting on by adding a dummy highlight

		try
		{
			component.getHighlighter().addHighlight(0, 0, this);
		}
		catch(BadLocationException ble) {}
	}

	public void updateCharacterShape(JTextComponent component, int beginIndex, int endIndex) throws BadLocationException {
		System.out.println("Updating Character Shape;");

		//Update Parentheses Shape
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
		return  textComponent.getGraphics().getFontMetrics(controller.getFont());
	}


	//  Paint the background highlight

	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c)
	{
		fillActiveLineShaep(g);
		fillParenthesesShape(g);
		fillCharacterCountLineShape(g);
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
				Graphics g = component.getGraphics();


				//Painting Parentheses
				calculateParenthesesShape(startIndex, endIndex);

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

//				refreshActiveLine();
				refreshParenthesesShape();
				refreshCharacterCountLine();


				//Used to Paint Active Line



			}
		});


	}

	private void refreshCharacterCountLine() {
		component.paintImmediately(line);
	}

	public void setColor(Color color) {
		this.color = color;
		if(alpha == 0){
			this.hilightColor = hilightColor;
		} else {
			this.hilightColor = new Color(color.getRed(), color.getBlue(), color.getGreen(), alpha);
		}
	}


	public void fillParenthesesShape(Graphics g) {
		//Paint Parentheses
		g.setColor(hilightColor);
		for(int i = 0; i < this.currentShapes.length; i++) {
			if(currentShapes[i] != null)
				g.fillRect(currentShapes[i].x, currentShapes[i].y, currentShapes[i].width, currentShapes[i].height);
		}

	}

	public void fillCharacterCountLineShape(Graphics g) {
		//Paint Character Line
		//Update Character Count line
		FontMetrics fm = controller.getTextPane()
				.getGraphics()
				.getFontMetrics(
						controller.getFont());
		int characterWidth = fm.stringWidth(" ");
		int maxCharacterWidth = characterWidth * lineCount;

		line.x = maxCharacterWidth;
		line.width = 3;
		line.y = 0;
		line.height = controller.getTextPane().getHeight();
		g.setColor(characterLineColor);
		g.fillRect(line.x, line.y, line.width, line.height);
	}

	public void fillActiveLineShaep(Graphics g) {
		try
		{
			Rectangle r = component.modelToView(component.getCaretPosition());
			g.setColor( color );
			g.fillRect(component.getMargin().left, r.y, component.getWidth(), r.height);

			if (lastView == null)
				lastView = r;
		}
		catch(BadLocationException ble) {System.out.println(ble);}
	}

	public void calculateParenthesesShape(int startIndex, int endIndex) {
		try {
			updateCharacterShape(component, startIndex, endIndex);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void refreshParenthesesShape() {
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
	}

	public void refreshActiveLine() {
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

}
