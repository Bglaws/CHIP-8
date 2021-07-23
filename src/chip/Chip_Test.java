
import java.io.FileNotFoundException;

import org.junit.Test;

public class Chip_Test {

	@Test
	static int test_jmp() {
		int result = 0;

		Chip chip = new Chip();

		// test 1
		chip.decode((short) 0x1111);
		if (chip.getPC() != 0x111) {
			System.out.println("Test 1 failed")
			result = -1;
		}

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