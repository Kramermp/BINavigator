package binavigator.ui;

import javax.swing.*;
import javax.tools.Tool;
import java.awt.*;

public class RunBtn extends JButton {
	JPanel parentContrainer;

	public RunBtn(final ButtonPanel parentContainer) {
		super("Run");
		this.parentContrainer = parentContainer;
	}
}
