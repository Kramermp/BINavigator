package binavigator.ui.colortheme;

import binavigator.backend.BINavController;

import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.xml.soap.Text;
import java.awt.*;

public abstract class TextColorTheme {
	protected BINavController parentController;
	protected String name;

	protected StyleContext cont = StyleContext.getDefaultStyleContext();

	protected AttributeSet keyWordSyle;
	protected AttributeSet defaultStyle;
	protected AttributeSet commentStyle;
	protected AttributeSet stringStyle;
	protected AttributeSet secondaryStyle;
	protected AttributeSet miscStyle;
	protected AttributeSet numberStyle;

	public abstract Color getTextColor();
	public abstract Color getCommentColor();
	public abstract Color getStringColor();
	public abstract Color getKeyWordColor();
	public abstract Color getMiscColor();
	public abstract Color getSecondaryColor();
	public abstract Color getNumberColor();
	public abstract Color getParenthesesHiLightColor();

	public void updateStyles() {
		keyWordSyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, getKeyWordColor());
		defaultStyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, getTextColor());
		commentStyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, getCommentColor());
		stringStyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, getStringColor());
		secondaryStyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, getSecondaryColor());
		miscStyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, getMiscColor());
		numberStyle = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, getNumberColor());
	}

	public abstract Color getLineHiLight();

	public TextColorTheme() {
		// Do Nothing
	}

	public TextColorTheme (BINavController parentController) {
		this.parentController = parentController;
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

	public AttributeSet getMiscStyle() {
		return miscStyle;
	}

	public AttributeSet getSecondaryStyle() {
		return secondaryStyle;
	}

	public AttributeSet getNumberStyle() {
		return numberStyle;
	}

	public String getName() {
		return name;
	}
}
