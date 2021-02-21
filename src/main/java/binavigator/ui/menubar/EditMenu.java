package binavigator.ui.menubar;

import binavigator.backend.BINavController;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class EditMenu extends JMenu {
	private BINavController parentController = null;

	public EditMenu() {
		super();
		this.setMnemonic(KeyEvent.VK_E);
	}


	public EditMenu(BINavController parentController) {
		super("Edit");
		this.parentController = parentController;
		this.setMnemonic(KeyEvent.VK_E);
	}

}
