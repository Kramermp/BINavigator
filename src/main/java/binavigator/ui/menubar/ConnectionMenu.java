package binavigator.ui.menubar;

import binavigator.backend.BINavController;

import javax.swing.*;

public class ConnectionMenu extends JMenu {
	BINavController parentController = null;

	public ConnectionMenu() {
		super();
	}

	public ConnectionMenu(BINavController parentController) {
		super("Connection");
		this.parentController = parentController;
	}
}
