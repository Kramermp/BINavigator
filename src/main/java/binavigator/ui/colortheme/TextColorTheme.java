package binavigator.ui.colortheme;

import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.xml.soap.Text;
import java.awt.*;

public abstract class TextColorTheme {
	private WindowTheme windowTheme = WindowTheme.DARK;

	protected StyleContext cont = StyleContext.getDefaultStyleContext();

	protected AttributeSet keyWordSyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, getKeyWordColor());
	protected AttributeSet defaultStyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, getTextColor());
	protected AttributeSet commentStyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, getCommentColor());
	protected AttributeSet stringStyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, getStringColor());

	public abstract Color getTextColor();
	public abstract Color getCommentColor();
	public abstract Color getStringColor();
	public abstract Color getKeyWordColor();
	public abstract void updateStyles();

	public abstract Color getLineHiLight();

	public TextColorTheme (WindowTheme windowTheme) {
		this.windowTheme = windowTheme;
	}

	public WindowTheme getWindowTheme() {
		return windowTheme;
	}

	public void setWindowTheme(WindowTheme windowTheme) {
		this.windowTheme = windowTheme;
		this.updateStyles();
	}

	public AttributeSet getKeyWordSyle() {
		return keyWordSyle;
	}

	public AttributeSet getCommentStyle() {
		return commentStyle;
	}

	public AttributeSet getDefaultStyle() {
		return defaultStyle;
	}

	public AttributeSet getStringStyle() {
		return stringStyle;
	}

}
