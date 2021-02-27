package binavigator.ui.menubar;

import binavigator.backend.BINavController;
import binavigator.ui.colortheme.Monokai;
import binavigator.ui.colortheme.RandomColorTheme;
import binavigator.ui.colortheme.WindowTheme;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

public class ViewMenu extends JMenu {
	Log log = LogFactory.getLog(this.getClass());

	BINavController parentController = null;
	JMenuItem textThemeBtn;

	public ViewMenu(BINavController parentController) {
		super("View");
		this.parentController = parentController;
		this.setMnemonic(KeyEvent.VK_V);

		buildThemeSelector();
		buildTabMenu();
	}

	public void buildThemeSelector() {
		textThemeBtn =  new JMenu("Select Text Color Theme");
		populateTextThemeBtn();
		this.add(textThemeBtn);

		JMenu windowThemeBtn =  new JMenu("Select Window Theme");

		JMenuItem darkThemBtn = new JMenuItem("Dark");
		darkThemBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				parentController.setTheme(WindowTheme.DARK);
			}
		});
		windowThemeBtn.add(darkThemBtn);

		JMenuItem lightThemeBtn = new JMenuItem("Light");
		lightThemeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				parentController.setTheme(WindowTheme.LIGHT);
			}
		});
		windowThemeBtn.add(lightThemeBtn);

		this.add(windowThemeBtn);
	}

	//TODO: Implement a method that can expand right now just hardcoded MVP
	private void populateTextThemeBtn() {
		int supportedTextThemeCount = 2;
		JMenuItem textBtn = new JMenuItem("Monokai");
		textBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				log.info("Setting Text Theme to Monokai");
				parentController.setTextColorTheme("MONOKAI");
			}
		});

		textThemeBtn.add(textBtn);

		JMenuItem textBtn2 = new JMenuItem("Random");
		textBtn2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				log.info("Setting Text Theme to Random");
				parentController.setTextColorTheme("RANDOM");
			}
		});

		textThemeBtn.add(textBtn2);
	}

	private void buildTabMenu() {
		JMenu tabSizeBtn = new JMenu("Tab Size");

		JMenuItem twoSpaceBtn = new JMenuItem(("2 Spaces"));
		twoSpaceBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				parentController.setTabSize(2);
			}
		});

		JMenuItem fourSpaceBtn = new JMenuItem(("4 Spaces"));
		fourSpaceBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				parentController.setTabSize(4);
			}
		});



		JMenuItem eightSpaceBtn = new JMenuItem(("8 Spaces"));
		eightSpaceBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				parentController.setTabSize(8);
			}
		});

		tabSizeBtn.add(twoSpaceBtn);
		tabSizeBtn.add(fourSpaceBtn);
		tabSizeBtn.add(eightSpaceBtn);

		this.add(tabSizeBtn);
	}


}
