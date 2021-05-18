

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

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
	private short delay_timer;
	private short sound_timer;
	
	// stack and stack pointer
	private short stack[];
	private short stackPointer;

	// hex based keypad
	private short[] key = new short[16];



	// font set for rendering
	final int FONTSET_SIZE = 0x50;
	private static short[] fontset = new short[]{
		0xF0,0x90,0x90,0x90,0xF0,
		0x20,0x60,0x20,0x20,0x70,
		0xF0,0x10,0xF0,0x80,0xF0,
		0xF0,0x10,0xF0,0x10,0xF0,
		0x90,0x90,0xF0,0x10,0x10,
		0xF0,0x80,0xF0,0x10,0xF0,
		0xF0,0x80,0xF0,0x90,0xF0,
		0xF0,0x10,0x20,0x40,0x40,
		0xF0,0x90,0xF0,0x90,0xF0,
		0xF0,0x90,0xF0,0x10,0xF0,
		0xF0,0x90,0xF0,0x90,0x90,
		0xE0,0x90,0xE0,0x90,0xE0,
		0xF0,0x80,0x80,0x80,0xF0,
		0xE0,0x90,0x90,0x90,0xE0,
		0xF0,0x80,0xF0,0x80,0xF0,
		0xF0,0x80,0xF0,0x80,0x80
	};

	// Chip constructor
	public Chip(String path) throws FileNotFoundException {
		Scanner scan = new Scanner(new File(path));
		
		for (int i = 0x000; i < FONTSET_SIZE; i++) {
			memory[i+ 0x050] = fontset[i];
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

		return (high << 8 | low);
	}
	// decodes opcode
	public void decode(short opcode){
		short nibble0 = ((opcode & 0xF000) >> 12);
		short nibble1 = ((opcode & 0x0F00) >> 8);
		short nibble2 = ((opcode & 0x00F0) >> 4);
		short nibble3 = (opcode & 0x000F);
		V[0xF] = ((opcode & 0xF000) >> 16);

		switch(nibble0) {
			case 0:
				//use if statements instead of switch


			case 1:
				pc = (opcode & 0x0FFF);
				break;
			case 2:
				pc = (opcode & 0x0FFF);
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
				V[nibble1] = (opcode & 0x00FF);
				break;
			case 7:
				V[nibble1] += (opcode & 0x00FF);
				break;
			case 8:
				switch(nibble3) {
					case 0:
						V[nibble1] = V[nibble2];
						break;
					case 1:
						V[nibble1] = (V[nibble1] | V[nibble2]);
						break;
					case 2:
						V[nibble1] = (V[nibble1] & V[nibble2]);
						break;
					case 3:
						V[nibble1] = (V[nibble1] ^ V[nibble2]);
						break;
					case 4:
						if (V[nibble1] + V[nibble2] > 0xFF) {
							V[0xF] = 1; // carry
						}		
						else {
							V[0xF] = 0;
						}	
						V[nibble1] += V[nibble2];
						break;
					case 5:
						if (V[nibble1] - V[nibble2] <  0x00) {
							V[0xF] = 1; // borrow
						}		
						else {
							V[0xF] = 0;
						}	
						V[nibble1] -= V[nibble2];
						break;
					case 6:
						
						break;
					case 7:

						break;
					case 8: //needs to be case E

						break;
				}
				
			case 9:

				break;
				
		}
	}

 	public static void main(String[] args) {
		
		String path = null;

		if (args.length > 1) {
			path = args[1];
		}
		else {
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
