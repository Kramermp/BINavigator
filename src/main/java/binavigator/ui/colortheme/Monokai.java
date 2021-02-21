package binavigator.ui.colortheme;

import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;

public class Monokai extends TextColorTheme {
	private static final Color WHITE = Color.decode("#F8F8F2");
	private static final Color RED = Color.decode("#F92672");
	private static final Color DARK_GRAY = Color.decode("#75715E");
	private static final Color ORANGE = Color.decode("#FD971F");
	private static final Color LIGHT_ORANGE = Color.decode("#E69F66");
	private static final Color YELLOW = Color.decode("#E6DB74");
	private static final Color GREEN = Color.decode("#A6E22E");
	private static final Color BLUE = Color.decode("#66D9EF");
	private static final Color PURPLE = Color.decode("#AE81FF");

 	public Monokai(WindowTheme windowTheme) {
		super(windowTheme);
	}

	@Override
	public Color getTextColor() {
		if(super.getWindowTheme() == WindowTheme.DARK){
			return this.WHITE;
		}

		return this.DARK_GRAY;

	}

	@Override
	public Color getCommentColor() {
		return this.LIGHT_ORANGE;
	}

	@Override
	public Color getStringColor() {
		return this.YELLOW;
	}

	@Override
	public Color getKeyWordColor() {
		return this.RED;
	}

	@Override
	public void updateStyles() {
		keyWordSyle = super.cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, getKeyWordColor());
		defaultStyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, getTextColor());
		commentStyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, getCommentColor());
		stringStyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, getStringColor());
	}

	@Override
	public Color getLineHiLight() {
		return this.BLUE;
	}

}
