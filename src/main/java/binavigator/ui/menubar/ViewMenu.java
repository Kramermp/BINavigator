package binavigator.ui.menubar;

import binavigator.backend.BINavController;
import binavigator.ui.Theme;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewMenu extends JMenu {
	BINavController parentController = null;

	public ViewMenu(BINavController parentController) {
		super("View");
		this.parentController = parentController;
		buildThemeSelector();
	}

	public void buildThemeSelector() {
		JMenu selectThemeBtn =  new JMenu("Select Theme");

		JMenuItem darkThemBtn = new JMenuItem("Dark");
		darkThemBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				parentController.setTheme(Theme.DARK);
			}
		});
		selectThemeBtn.add(darkThemBtn);

		JMenuItem lightThemeBtn = new JMenuItem("Light");
		lightThemeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				parentController.setTheme(Theme.LIGHT);
			}
		});
		selectThemeBtn.add(lightThemeBtn);

		this.add(selectThemeBtn);
	}


}
