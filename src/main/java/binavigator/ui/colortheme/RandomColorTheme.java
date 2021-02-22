package binavigator.ui.colortheme;

import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.Random;

public class RandomColorTheme extends TextColorTheme{
	private static Random rng = new Random();

	private static Color getRandomColor() {
		return (new Color(rng.nextInt(255), rng.nextInt(255), rng.nextInt(255)));
	}

	public RandomColorTheme(WindowTheme windowTheme) {
		super(windowTheme);
		super.name = "Random";
	}

	@Override
	public Color getTextColor() {
		return getRandomColor();
	}

	@Override
	public Color getCommentColor() {
		return getRandomColor();
	}

	@Override
	public Color getStringColor() {
		return getRandomColor();
	}

	@Override
	public Color getKeyWordColor() {
		return getRandomColor();
	}

	@Override
	public Color getMiscColor() {
		return getRandomColor();
	}

	@Override
	public Color getSecondaryColor() {
		return getRandomColor();
	}

	@Override
	public Color getNumberColor() {
		return getRandomColor();
	}

	@Override
	public Color getParenthesesHiLightColor() {
		return getRandomColor();
	}

	@Override
	public Color getLineHiLight() {
		return getRandomColor();
	}
}
