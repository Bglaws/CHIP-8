
import java.io.FileNotFoundException;

import org.junit.Test;

public class ChipTest {

	@Test
	void testDisplayClear() {
		Chip chip = new Chip();

		chip.decode((short) 0x00E0);
		assert chip.getPC() == 0x0E0;
	}

	@Test
	void testFlowReturn() {
		Chip chip = new Chip();
		chip.decode((short) 0x00EE);
		assert chip.getPC() == 0x0EE;
	}

	@Test
	void testJmp() {
		Chip chip = new Chip();
		chip.decode((short) 0x1111);
		assert chip.getPC() == 0x111;
	}

	@Test
	void testCall() {
		Chip chip = new Chip();
		chip.decode((short) 0x2);
	}

	public static void main(String[] args) {

		String path = "../../roms/snake.ch8";
		Chip chip = null;

		// if (args.length > 0) {
		// path = args[0];
		// } else {
		// System.out.println("no file path provided");
		// System.exit(1);
		// }
		try {
			chip = new Chip(path);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

		while (true) {
			short instr = chip.fetch();
			chip.decode(instr);
		}
	}

}
