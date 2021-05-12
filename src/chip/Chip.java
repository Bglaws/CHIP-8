

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Chip {

	private short opcode;
	private byte[] memory = new byte[4096];
	private byte[] V = new byte[16];

	// Index register & Program counter, which can have a value from 0x000 to 0xFFF
	private byte I;
	private short pc;

	// Not sure what memory map is but it goes here
	

	private byte[] gfx = new byte[64 * 32];

	// timers
	private byte delay_timer;
	private byte sound_timer;
	
	// stack and stack pointer
	private byte stack[];
	private byte stackPointer;

	// hex based keypad
	private byte[] key = new byte[16];



	// font set for rendering
	final int FONTSET_SIZE = 0x50;
	private static byte[] fontset = new byte[]{
		(byte)0xF0,(byte)0x90,(byte)0x90,(byte)0x90,(byte)0xF0,
		(byte)0x20,(byte)0x60,(byte)0x20,(byte)0x20,(byte)0x70,
		(byte)0xF0,(byte)0x10,(byte)0xF0,(byte)0x80,(byte)0xF0,
		(byte)0xF0,(byte)0x10,(byte)0xF0,(byte)0x10,(byte)0xF0,
		(byte)0x90,(byte)0x90,(byte)0xF0,(byte)0x10,(byte)0x10,
		(byte)0xF0,(byte)0x80,(byte)0xF0,(byte)0x10,(byte)0xF0,
		(byte)0xF0,(byte)0x80,(byte)0xF0,(byte)0x90,(byte)0xF0,
		(byte)0xF0,(byte)0x10,(byte)0x20,(byte)0x40,(byte)0x40,
		(byte)0xF0,(byte)0x90,(byte)0xF0,(byte)0x90,(byte)0xF0,
		(byte)0xF0,(byte)0x90,(byte)0xF0,(byte)0x10,(byte)0xF0,
		(byte)0xF0,(byte)0x90,(byte)0xF0,(byte)0x90,(byte)0x90,
		(byte)0xE0,(byte)0x90,(byte)0xE0,(byte)0x90,(byte)0xE0,
		(byte)0xF0,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0xF0,
		(byte)0xE0,(byte)0x90,(byte)0x90,(byte)0x90,(byte)0xE0,
		(byte)0xF0,(byte)0x80,(byte)0xF0,(byte)0x80,(byte)0xF0,
		(byte)0xF0,(byte)0x80,(byte)0xF0,(byte)0x80,(byte)0x80
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
		pc = (byte) 0x200;
		
	}

	// emulation cycle
	public void emulateCycle() {

		opcode = fetch();
		decode(opcode);
		

	}
	// fetches opcode
	public short fetch() {
		byte high = memory[pc++];
		byte low = memory[pc++];

		return (short) (high << 8 | low);
	}
	// decodes opcode
	public void decode(short opcode){
		byte nibble0 = (byte) ((opcode & 0xF000) >> 12);
		byte nibble1 = (byte) ((opcode & 0x0F00) >> 8);
		byte nibble2 = (byte) ((opcode & 0x00F0) >> 4);
		byte nibble3 = (byte) (opcode & 0x000F);

		switch(nibble0) {
			case 0:
				//use if statements instead of switch


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
				V[nibble1] = (byte) (opcode & 0x00FF);
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
						V[nibble1] = (byte) (V[nibble1] | V[nibble2]);
						break;
					case 2:
						V[nibble1] = (byte) (V[nibble1] & V[nibble2]);
						break;
					case 3:
						V[nibble1] = (byte) (V[nibble1] ^ V[nibble2]);
						break;
					case 4:

						break;
					case 5:

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
