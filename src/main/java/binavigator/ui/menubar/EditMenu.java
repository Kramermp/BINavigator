package binavigator.ui.menubar;

import binavigator.backend.BINavController;

import javax.swing.*;

public class EditMenu extends JMenu {
	private BINavController parentController = null;

	public EditMenu() {

	}


	public EditMenu(BINavController parentController) {
		super("Edit");
		this.parentController = parentController;
	}
}
