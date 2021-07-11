

import javax.swing.JFrame;
import java.awt.*;

public class Display {

	/* windowInit method is static for testing purposes but will be changed
	   to non static so that Chip class can make a display object */
  
	public static void windowInit() {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		JFrame gameWindow = new JFrame("CHIP-8");

		gameWindow.setSize(screenSize);

		gameWindow.setVisible(true);

	}

	public static void main(String[] args) {

		windowInit();

	}
}
