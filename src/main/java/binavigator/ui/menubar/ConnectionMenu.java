package binavigator.ui.menubar;

import binavigator.backend.BINavController;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class ConnectionMenu extends JMenu {
	BINavController parentController = null;

	public ConnectionMenu() {
		super();
		this.setMnemonic(KeyEvent.VK_C);
	}

	public ConnectionMenu(BINavController parentController) {
		super("Connection");
		this.parentController = parentController;
		this.setMnemonic(KeyEvent.VK_C);
	}
}
