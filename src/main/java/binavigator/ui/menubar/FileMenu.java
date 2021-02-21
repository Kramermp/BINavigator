package binavigator.ui.menubar;

import binavigator.backend.BINavController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

public class FileMenu extends JMenu {
	BINavController parentController = null;

	public FileMenu(BINavController biNavController) {
		super("File");
		this.parentController = biNavController;
		this.setMnemonic(KeyEvent.VK_F);

		JMenuItem saveBtn = new JMenuItem("Save");
		saveBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent actionEvent) {
				System.out.println("Action Performed,");
				JFileChooser jfc = new JFileChooser();

				int option = jfc.showSaveDialog( new JDialog());
				if (option == JFileChooser.APPROVE_OPTION) {
					File selectedFile = jfc.getSelectedFile();
					parentController.saveToFile(selectedFile);
				}
			}
		});

		JMenuItem openBtn = new JMenuItem("Open");
		openBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent actionEvent) {
				System.out.println("Action Performed,");
				JFileChooser jfc = new JFileChooser();

				int option = jfc.showOpenDialog( new JDialog());
				if (option == JFileChooser.APPROVE_OPTION) {
					File selectedFile = jfc.getSelectedFile();
					parentController.loadFile(selectedFile);
				}
			}

		});

		this.add(saveBtn);
		this.add(openBtn);

		JMenuItem exitBtn = new JMenuItem("Exit");
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				parentController.exitSafely();
			}
		});
		this.add(exitBtn);
	}
}
