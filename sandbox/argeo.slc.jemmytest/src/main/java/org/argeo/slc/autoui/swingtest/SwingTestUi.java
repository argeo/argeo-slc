package org.argeo.slc.autoui.swingtest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class SwingTestUi {
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("HelloWorldSwing");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add the ubiquitous "Hello World" label.
		final JLabel label = new JLabel("Hello World");
		frame.getContentPane().add(label);
		
		final JButton button = new JButton("Button");
		frame.getContentPane().add(button);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				button.setText("Pressed!!");

			}});

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
