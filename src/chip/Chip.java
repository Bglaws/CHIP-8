
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class Chip {

	private short opcode;
	private short[] memory = new short[4096];
	private short[] V = new short[16];

	// Index register & Program counter, which can have a value from 0x000 to 0xFFF
	private short I;
	private short pc;

	// Not sure what memory map is but it goes here

	private short[] gfx = new short[64 * 32];

	// timers
	private short delayTimer;
	private short soundTimer;

	// stack pointer
	private short stackPointer;

	// hex based keypad
	private short[] key = new short[16];

	// font set for rendering
	final int FONTSET_SIZE = 0x50;
	private static short[] fontset = new short[] { 0xF0, 0x90, 0x90, 0x90, 0xF0, 0x20, 0x60, 0x20, 0x20, 0x70, 0xF0,
			0x10, 0xF0, 0x80, 0xF0, 0xF0, 0x10, 0xF0, 0x10, 0xF0, 0x90, 0x90, 0xF0, 0x10, 0x10, 0xF0, 0x80, 0xF0, 0x10,
			0xF0, 0xF0, 0x80, 0xF0, 0x90, 0xF0, 0xF0, 0x10, 0x20, 0x40, 0x40, 0xF0, 0x90, 0xF0, 0x90, 0xF0, 0xF0, 0x90,
			0xF0, 0x10, 0xF0, 0xF0, 0x90, 0xF0, 0x90, 0x90, 0xE0, 0x90, 0xE0, 0x90, 0xE0, 0xF0, 0x80, 0x80, 0x80, 0xF0,
			0xE0, 0x90, 0x90, 0x90, 0xE0, 0xF0, 0x80, 0xF0, 0x80, 0xF0, 0xF0, 0x80, 0xF0, 0x80, 0x80 };

	// Chip constructor
	public Chip(String path) throws FileNotFoundException {
		Scanner scan = new Scanner(new File(path));

		for (int i = 0x000; i < FONTSET_SIZE; i++) {
			memory[i + 0x050] = fontset[i];
		}

		// Rom is read in at 0x200
		int j = 0x200;
		while (scan.hasNextByte()) {
			memory[j] = scan.nextByte();
			j++;
		}
		pc = 0x200;

	}

	// emulation cycle
	public void emulateCycle() {

		opcode = fetch();
		decode(opcode);

	}

	// fetches opcode
	public short fetch() {
		short high = memory[pc++];
		short low = memory[pc++];

		return (short) (high << 8 | low);
	}

	// decodes opcode
	public void decode(short opcode) {
		short nibble0 = (short) ((opcode & 0xF000) >> 12);
		short nibble1 = (short) ((opcode & 0x0F00) >> 8);
		short nibble2 = (short) ((opcode & 0x00F0) >> 4);
		short nibble3 = (short) (opcode & 0x000F);
		V[0xF] = (short) ((opcode & 0xF000) >> 16);

		switch (nibble0) {

			case 0:
				switch (nibble3) {

					case 0:
						if (V[nibble3] == 0x0) {
							Arrays.fill(gfx, (short) 0);
						}
						break;
					case 0xE:
						if (V[nibble3] == 0xE) {
							stackPointer--;
						}
						break;
				}

			case 1:
				pc = (short) (opcode & 0x0FFF);
				break;
			case 2:
				pc = (short) (opcode & 0x0FFF);
				break;
			case 3:
				if (V[nibble1] == 0x00FF) {
					pc += 2;
				}
				break;
			case 4:
				if (V[nibble1] != (opcode & 0x00FF)) {
					pc += 2;
				}
				break;
			case 5:
				if (V[nibble1] == V[nibble2]) {
					pc += 2;
				}
				break;
			case 6:
				V[nibble1] = (short) (opcode & 0x00FF);
				break;
			case 7:
				V[nibble1] += (opcode & 0x00FF);
				break;
			case 8:
				switch (nibble3) {
					case 0:
						V[nibble1] = V[nibble2];
						break;
					case 1:
						V[nibble1] = (short) (V[nibble1] | V[nibble2]);
						break;
					case 2:
						V[nibble1] = (short) (V[nibble1] & V[nibble2]);
						break;
					case 3:
						V[nibble1] = (short) (V[nibble1] ^ V[nibble2]);
						break;
					case 4:
						if (V[nibble1] + V[nibble2] > 0xFF) {
							V[0xF] = 1; // carry
						} else {
							V[0xF] = 0;
						}
						V[nibble1] = (short) ((V[nibble2] + V[nibble1]) % 256);
						break;
					case 5:
						if (V[nibble1] - V[nibble2] < 0x00) {
							V[0xF] = 1; // borrow
						} else {
							V[0xF] = 0;
						}
						V[nibble1] = (short) ((V[nibble2] - V[nibble1]) % 256);
						break;
					case 6:
						V[0xF] = (short) (V[nibble1] & 0x01);
						V[nibble1] >>= 1;
						break;
					case 7:
						if (V[nibble2] - V[nibble1] < 0xFF) {
							V[0xF] = 1; // borrow
						} else {
							V[0xF] = 0;
						}
						V[nibble1] = (short) ((V[nibble2] - V[nibble1]) % 256);
						break;
					case 0xE:
						V[0xF] = (short) (V[nibble1] & 0xF);
						V[nibble1] <<= 1;
						break;
				}

			case 9:
				if (V[nibble1] != V[nibble2]) {
					pc += 2;
				}
				break;
			case 0xA:
				I = (short) (opcode & 0x0FFF);
				break;
			case 0xB:
				pc = (short) (V[0] + (opcode & 0x0FFF));
				break;
			case 0xC:
				V[nibble1] = (short) ((int) java.lang.Math.random() & (opcode & 0x00FF));
				break;
			case 0xD:

				short x = V[nibble1];
				short y = V[nibble2];
				short height = nibble3;
				short pixel;

				// carry flag is reset so it can be used to signal collision
				V[0xF] = 0;
				for (int yL = 0; yL < height; yL++) { // loop over each row

					// fetches pixel from memory starting from I
					pixel = memory[I + yL];
					for (int xL = 0; xL < 8; xL++) { // loops through 8 bits of one row

						// 0x80 = 1000 0000
						// by incrementing xL, every bit will be checked
						// if current pixel is already on, check for collision
						if ((pixel & (0x80 >> xL)) != 0) {

							// if current pixel AND display pixel both set to 1,
							// a collision has occured
							if (gfx[(x + xL + ((y + yL) * 64))] == 1) {
								V[0xF] = 1;
								gfx[x + xL + ((y + yL) * 64)] ^= 1;
							}

						}

					}
					// drawFlag = true;
				}
				break;
			case 0xE:
				switch (opcode & 0x00FF) {
					case 0x9E:
						if (key[V[nibble1]] != 0) {
							pc += 2;
						}
						break;
					case 0xA1:
						if (key[V[nibble1]] == 0) {
							pc += 2;
						}
						break;
				}

			case 0xF:
				switch (opcode & 0x00FF) {
					case 0x07:
						V[nibble1] = delayTimer;
						break;
					case 0x0A:
						for (int i = 0; i < key.length; i++) {
							if (key[i] == 1) {
								V[nibble1] = key[i];
								break;
							}
						}
						pc -= 2;
						break;
					case 0x15:
						delayTimer = V[nibble1];
						break;
					case 0x18:
						soundTimer = V[nibble1];
						break;
					case 0x1E:
						I += V[nibble1];
						break;
					case 0x29:
						I = V[nibble1];
						break;
					case 0x33:

						break;
					case 0x55:
						break;
					case 0x65:
						break;
				}
			default:
				System.out.println("Error: invalid opcode");
		}
	}

	public static void main(String[] args) {

		String path = null;

		if (args.length > 1) {
			path = args[1];
		} else {
			System.out.print("no file path provided");
			System.exit(1);
		}
		try {
			Chip chip = new Chip(path);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

	}

}
