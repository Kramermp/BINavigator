package binavigator.ui.menubar;

import binavigator.backend.BINavController;
import binavigator.ui.colortheme.WindowTheme;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class ViewMenu extends JMenu {
	BINavController parentController = null;

	public ViewMenu(BINavController parentController) {
		super("View");
		this.parentController = parentController;
		this.setMnemonic(KeyEvent.VK_V);

		buildThemeSelector();
	}

	public void buildThemeSelector() {
		JMenu selectThemeBtn =  new JMenu("Select WindowTheme");

		JMenuItem darkThemBtn = new JMenuItem("Dark");
		darkThemBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				parentController.setTheme(WindowTheme.DARK);
			}
		});
		selectThemeBtn.add(darkThemBtn);

		JMenuItem lightThemeBtn = new JMenuItem("Light");
		lightThemeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				parentController.setTheme(WindowTheme.LIGHT);
			}
		});
		selectThemeBtn.add(lightThemeBtn);

		this.add(selectThemeBtn);
	}


}
