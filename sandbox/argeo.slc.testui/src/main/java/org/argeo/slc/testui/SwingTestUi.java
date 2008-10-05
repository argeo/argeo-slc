package org.argeo.slc.testui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SwingTestUi {
	private static void createAndShowGUI(boolean exitOnClose) {
		// Create and set up the window.
		final JFrame frame = new JFrame("HelloWorldSwing");
		if (exitOnClose)
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new GridLayout(4, 1));

		// "Hello World" label.
		final JLabel label = new JLabel("Hello World");
		frame.getContentPane().add(label);

		// Change label button
		{
			final JButton button = new JButton("Button");
			frame.getContentPane().add(button);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (label.getText().equals("Hello World"))
						label.setText("Pressed!!");
					else
						label.setText("Hello World");
				}
			});
		}

		// Start dialog button
		{
			final JButton buttonStart = new JButton("Start");
			frame.getContentPane().add(buttonStart);
			buttonStart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JDialog dialog = new JDialog(frame, "TestDialog");
					JLabel label = new JLabel("Dialog open");
					dialog.getContentPane().add(label);
					dialog.pack();
					dialog.setVisible(true);
				}
			});
		}

		// Complex Panel
		{
			JPanel complexPanel = new JPanel();
			complexPanel.setLayout(new GridLayout(1, 2));
			final JTextField textTime = new JTextField(Long.toString(System
					.currentTimeMillis()));
			complexPanel.add(textTime);
			JButton buttonTime = new JButton("Now!");
			buttonTime.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					textTime.setText(Long.toString(System.currentTimeMillis()));
				}
			});
			complexPanel.add(buttonTime);
			frame.getContentPane().add(complexPanel);
		}

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		boolean noExitOnClose = false;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("noExitOnClose")) {
				noExitOnClose = true;
			}
		}

		final boolean exitOnClose = !noExitOnClose;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI(exitOnClose);
			}
		});
	}

}
