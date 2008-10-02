package org.argeo.slc.testui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class SwingTestUi {
	private static void createAndShowGUI(boolean exitOnClose) {
		// Create and set up the window.
		JFrame frame = new JFrame("HelloWorldSwing");
		if (exitOnClose)
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new GridLayout(2, 1));

		// Add the ubiquitous "Hello World" label.
		final JLabel label = new JLabel("Hello World");
		frame.getContentPane().add(label);

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
