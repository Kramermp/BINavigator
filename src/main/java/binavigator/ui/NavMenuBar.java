package binavigator.ui;

import binavigator.backend.BINavController;
import binavigator.ui.menubar.EditMenu;
import binavigator.ui.menubar.FileMenu;
import binavigator.ui.menubar.ViewMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NavMenuBar extends JMenuBar {
	private JMenu fileMenu = null;
	private BINavController parentController = null;
	private JMenu editMenu;
	private JMenu viewMenu;

	public NavMenuBar(BINavController parentController) {
		super();
		this.parentController = parentController;
		buildFileMenu();
		buildEditMenu();
		buildViewMenu();
	}

	private void buildFileMenu() {

		this.add(new FileMenu(parentController));
	}

	private void buildEditMenu() {

		this.add(new EditMenu(parentController));
	}

	private void buildViewMenu() {

		this.add(new ViewMenu(parentController));
	}

}
