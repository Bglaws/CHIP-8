
import java.nio.file.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

		byte[] file = new byte[4000];
		try {
			file = Files.readAllBytes(new File(path).toPath());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		for (int i = 0x000; i < FONTSET_SIZE; i++) {
			memory[i + 0x050] = fontset[i];
		}

		// Rom is read in at 0x200
		int j = 0x200;
		for (int index = 0; index < file.length; index++) {

			memory[j++] = file[index];
		}
		// stack implemented at the end of memory
		stackPointer = 0xF00;
		// ROM loaded in at this address
		pc = 0x200;

	}

	public Chip() {
		for (int i = 0x000; i < FONTSET_SIZE; i++) {
			memory[i + 0x050] = fontset[i];
		}

		// stack implemented at the end of memory
		stackPointer = 0xF00;
		// ROM loaded in at this address
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

					case 0x0:
						Arrays.fill(gfx, (short) 0);
						System.out.println("clearscr");
						return;
					case 0xE:
						short highByte = memory[stackPointer++];
						short lowByte = memory[stackPointer++];
						pc = (short) ((highByte << 8) + lowByte);
						System.out.println("ret");
						return;
				}

			case 1:
				pc = (short) (opcode & 0x0FFF);
				System.out.printf("jmp 0x%X\n", pc);
				return;
			case 2:
				// current pc is set to the top of the stack then pc set to nnn
				short lowByte = (short) (pc & 0x00FF);
				short highByte = (short) ((pc & 0xFF00) >> 8);
				memory[stackPointer--] = lowByte;
				memory[stackPointer--] = highByte;
				pc = (short) (opcode & 0x0FFF);
				System.out.printf("call 0x%X\n", pc);
				return;
			case 3:
				if (V[nibble1] == (opcode & 0x00FF)) {
					pc += 2;
				}
				System.out.printf("skip if %X", V[nibble1], " == %X\n", (opcode & 0x00FF));
				return;
			case 4:
				if (V[nibble1] != (opcode & 0x00FF)) {
					pc += 2;
				}
				System.out.printf("skip if %X", V[nibble1], " != %X\n", (opcode & 0x00FF));
				return;
			case 5:
				if (V[nibble1] == V[nibble2]) {
					pc += 2;
				}
				System.out.printf("skip if %X", V[nibble1], " == %X\n", V[nibble2]);
				return;
			case 6:
				V[nibble1] = (short) (opcode & 0x00FF);
				System.out.printf("set %X", V[nibble1], " = %X\n", (opcode & 0x00FF));
				return;
			case 7:
				V[nibble1] += (opcode & 0x00FF);
				System.out.printf("set %X", V[nibble1], " += %X\n", (opcode & 0x00FF));
				return;
			case 8:
				switch (nibble3) {
					case 0:
						V[nibble1] = V[nibble2];
						System.out.printf("set %X", V[nibble1], " = %X\n", V[nibble2]);
						return;
					case 1:
						V[nibble1] = (short) (V[nibble1] | V[nibble2]);
						System.out.printf("set %X", V[nibble1], " OR= %X\n", V[nibble2]);
						return;
					case 2:
						V[nibble1] = (short) (V[nibble1] & V[nibble2]);
						System.out.printf("set %X", V[nibble1], " &= %X\n", V[nibble2]);
						return;
					case 3:
						V[nibble1] = (short) (V[nibble1] ^ V[nibble2]);
						System.out.printf("set %X", V[nibble1], " XOR= %X\n", V[nibble2]);
						return;
					case 4:
						if (V[nibble1] + V[nibble2] > 0xFF) {
							V[0xF] = 1; // carry
						} else {
							V[0xF] = 0;
						}
						V[nibble1] = (short) ((V[nibble2] + V[nibble1]) % 256);
						System.out.printf("set %X", V[nibble1], " += %X.", V[nibble2], "flag = %X\n", V[0xF]);
						return;
					case 5:
						// HERE!!!
						if (V[nibble1] - V[nibble2] < 0x00) {
							V[0xF] = 0; // borrow
						} else {
							V[0xF] = 1;
						}
						V[nibble1] = (short) ((V[nibble2] - V[nibble1]) % 256);
						System.out.printf("set %X", V[nibble1], " -= %X.", V[nibble2], "flag = %X\n", V[0xF]);
						return;
					case 6:
						short leastSigBit = (short) (V[nibble1] & 0x01);
						V[0xF] = leastSigBit;
						V[nibble1] >>= 1;
						System.out.printf("str %X", leastSigBit, " in VF. set %X", V[nibble1], " >>= 1\n");
						return;
					case 7:
						if (V[nibble2] - V[nibble1] < 0xFF) {
							V[0xF] = 0; // borrow
						} else {
							V[0xF] = 1;
						}
						V[nibble1] = (short) ((V[nibble2] - V[nibble1]) % 256);
						System.out.printf("set %X", V[nibble1], " = %X.", V[nibble2], " - %X.", V[nibble1],
								"flag = %X\n", V[0xF]);
						return;
					case 0xE:
						short mostSigBit = (short) (V[nibble1] & 0xF);
						V[0xF] = mostSigBit;
						V[nibble1] <<= 1;
						System.out.printf("str %X", mostSigBit, " in VF. set %X", V[nibble1], " <<= 1\n");
						return;
				}

			case 9:
				if (V[nibble1] != V[nibble2]) {
					pc += 2;
				}
				System.out.printf("skip if %X", V[nibble1], " != %X\n", V[nibble2]);
				return;
			case 0xA:
				I = (short) (opcode & 0x0FFF);
				System.out.printf("set \n", I);
				return;
			case 0xB:
				pc = (short) (V[0] + (opcode & 0x0FFF));
				System.out.printf("jmp %X\n", pc);
				return;
			case 0xC:
				V[nibble1] = (short) ((int) java.lang.Math.random() & (opcode & 0x00FF));
				System.out.println("set Vx = rand");
				return;
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
					// loops through 8 bits of one row, since sprites are 8 bits (pixels) wide.
					// Sprites can have a height between 0 and 15.
					for (int xL = 0; xL < 8; xL++) {

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
				System.out.printf("draw \n");
				return;
			case 0xE:
				switch (opcode & 0x00FF) {
					case 0x9E:
						if (key[V[nibble1]] != 0) {
							pc += 2;
						}
						System.out.printf("skip if %X", key[V[nibble1]], " != 0\n");
						return;
					case 0xA1:
						if (key[V[nibble1]] == 0) {
							pc += 2;
						}
						System.out.printf("skip if %X", key[V[nibble1]], " == 0\n");
						return;
				}

			case 0xF:
				switch (opcode & 0x00FF) {
					case 0x07:
						V[nibble1] = delayTimer;
						System.out.printf("set %X", V[nibble1], " = %X\n", delayTimer);
						return;
					case 0x0A:
						for (int i = 0; i < key.length; i++) {
							if (key[i] == 1) {
								V[nibble1] = key[i];
								System.out.printf("Key pressed\n");
								return;
							}
						}
						pc -= 2; // pc -= 2 so that program counter does not progress to next instruction
						System.out.printf("await key press\n");
						return;
					case 0x15:
						delayTimer = V[nibble1];
						System.out.printf("set DT %X", delayTimer, " = %X\n", V[nibble1]);
						return;
					case 0x18:
						soundTimer = V[nibble1];
						System.out.printf("set ST %X", soundTimer, " = %X\n", V[nibble1]);
						return;
					case 0x1E:
						I += V[nibble1];
						System.out.printf("add, %d", I, " += %X\n", V[nibble1]);
						return;
					case 0x29:
						// set I to the location of the sprite for the character in Vx
						I = V[nibble1];
						System.out.printf("set %d", I, " = %X\n", V[nibble1]);
						return;
					case 0x33:
						// Stores BCD of Vx (first 3 bits) in memory starting at I
						// (first bit at I, second I + 1, third I + 2)
						memory[I] = (short) (V[nibble1] / 100);
						memory[I + 1] = (short) ((V[nibble1] / 10) % 10);
						memory[I + 2] = (short) ((V[nibble1] / 100) % 10);
						System.out.printf("set BCD %X", V[nibble1], " in %X\n", memory[I]);
						return;
					case 0x55:
						// store V0 to Vx in memory starting at address I
						for (int i = 0; i <= nibble1; i++) {
							memory[I + i] = V[i];
						}
						System.out.printf("reg_dump %X", V[0], " to %X\n", V[nibble1]);
						return;
					case 0x65:
						// Fills V0 to Vx with values from memory starting at address I
						for (int i = 0; i <= nibble1; i++) {
							V[i] = memory[I + i];
						}
						System.out.printf("reg_load %X", V[0], " to %X\n", V[nibble1]);
						return;
				}
		}
		System.out.println("Error: invalid opcode");
	}

	public static void main(String[] args) {

		String path = null;
		Chip chip = null;

		if (args.length > 0) {
			path = args[0];
		} else {
			System.out.println("no file path provided");
			System.exit(1);
		}
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

	// helper methods
	public short getPC() {
		return this.pc;
	}

}
