package binavigator.ui.colortheme;

import java.awt.*;

public class Monokai extends TextColorTheme {
	private static final Color WHITE = Color.decode("#F8F8F2");
	private static final Color RED = Color.decode("#F92672");
	private static final Color DARK_GRAY = Color.decode("#75715E");
	private static final Color ORANGE = Color.decode("#FD971F");
	private static final Color Light_Orange = Color.decode("#E69F66");
	private static final Color YELLOW = Color.decode("#E6DB74");
	private static final Color GREEN = Color.decode("#A6E22E");
	private static final Color BLUE = Color.decode("#66D9EF");
	private static final Color Purple = Color.decode("#AE81FF");


	@Override
	public Color getTextColor() {
		return this.WHITE;
	}

	@Override
	public Color getCommentColor() {
		return this.DARK_GRAY;
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
	public Color getLineHiLight() {
		return this.BLUE;
	}
}
