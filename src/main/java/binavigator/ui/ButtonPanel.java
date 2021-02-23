package binavigator.ui;

import binavigator.backend.BINavController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonPanel extends JPanel {
	BINavController parentController;

	public ButtonPanel(BINavController parentController) {
		super();
		this.parentController = parentController;

		buildButtonPanel();
	}

	private void buildButtonPanel() {
//		this.setBackground(Color.red);
//		new JButton("Run")
//		this.setPreferredSize(new Dimension(50, 50));
		this.setLayout(new BorderLayout());
		JButton btn = new RunBtn(this );
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				parentController.runSql();
			}
		});

		this.add(btn, BorderLayout.WEST);
	}

}
