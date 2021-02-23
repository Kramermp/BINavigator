package binavigator.ui;

import javax.swing.*;
import javax.tools.Tool;
import java.awt.*;

public class RunBtn extends JButton {
	JPanel parentContrainer;

	public RunBtn(final ButtonPanel parentContainer) {
		this.parentContrainer = parentContainer;
		this.setSize(25, 25);
		this.setIcon(new Icon() {

			@Override
			public void paintIcon(Component component, Graphics graphics, int i, int i1) {
				graphics.setColor(Color.GREEN);
				int heightMarginFactor = 1 / 20; //5%
				int widthMarginFactor = 1 / 20; //5%
				int heightMargin = (int) (component.getHeight() * heightMarginFactor);;
				int widthMargin = (int) (component.getWidth() * widthMarginFactor);

				int targetHieight = component.getHeight() - (2 * heightMargin);
				int targetWidth = component.getWidth() - (2 * widthMargin);

				Polygon triangle = new Polygon();

				triangle.addPoint((int) Math.floor(component.getWidth() - (component.getWidth() * .9)) , (int) (component.getHeight() * .1));
				triangle.addPoint((int) Math.floor(component.getWidth() - (component.getWidth() * .9)), (int) (component.getHeight() * .9));
				triangle.addPoint((int) Math.floor(component.getWidth() * .9), component.getHeight() / 2);
				graphics.fillPolygon(triangle);
			}

			@Override
			public int getIconWidth() {
				return (int) (10);
			}

			@Override
			public int getIconHeight() {
				return (int) parentContainer.getHeight();
			}
		});
	}
}
