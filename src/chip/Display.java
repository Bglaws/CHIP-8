
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.*;

public class Display {

	/*
	 * windowInit method is static for testing purposes but will be changed to non
	 * static so that Chip class can make a display object
	 */
	public Display(short[] gfx) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		JFrame gameWindow = new JFrame("CHIP-8");
		gameWindow.setSize(screenSize);
		gameWindow.setVisible(true);

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		panel.setLayout(new GridLayout(64, 32));

		gameWindow.add(panel, BorderLayout.CENTER);
		gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// gameWindow.pack();
	}

	// public static void main(String[] args) {
	// Display d = new Display();
	// }
}
