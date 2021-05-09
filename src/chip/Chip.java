package chip;

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
	

	private byte[] gfx = new byte[64 * 32];

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
	final short[] fontset = {
		0xF0, 0x90, 0x90, 0x90, 0xF0,		// 0
		0x20, 0x60, 0x20, 0x20, 0x70,		// 1
		0xF0, 0x10, 0xF0, 0x80, 0xF0,		// 2
		0xF0, 0x10, 0xF0, 0x10, 0xF0,		// 3
		0x90, 0x90, 0xF0, 0x10, 0x10,		// 4
		0xF0, 0x80, 0xF0, 0x10, 0xF0,		// 5
		0xF0, 0x80, 0xF0, 0x90, 0xF0,		// 6
		0xF0, 0x10, 0x20, 0x40, 0x40,		// 7
		0xF0, 0x90, 0xF0, 0x90, 0xF0,		// 8
		0xF0, 0x90, 0xF0, 0x10, 0xF0,		// 9
		0xF0, 0x90, 0xF0, 0x90, 0x90,		// A
		0xE0, 0x90, 0xE0, 0x90, 0xE0,		// B
		0xF0, 0x80, 0x80, 0x80, 0xF0,		// C
		0xE0, 0x90, 0x90, 0x90, 0xE0,		// D
		0xF0, 0x80, 0xF0, 0x80, 0xF0,		// E
		0xF0, 0x80, 0xF0, 0x80, 0x80		// F
	};



	public Chip(String path) throws FileNotFoundException {
		Scanner scan = new Scanner(new File(path));
		
		for (int i = 0x000; i < FONTSET_SIZE; i++) {
			memory[i+ 0x050] = fontset[i];
		}
		

		int j = 0x200;
		while (scan.hasNextByte()) {
			memory[j] = scan.nextByte();
			j++;
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
